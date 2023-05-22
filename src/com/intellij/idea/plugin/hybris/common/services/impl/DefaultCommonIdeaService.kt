/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.intellij.idea.plugin.hybris.common.services.impl

import com.intellij.idea.plugin.hybris.common.HybrisConstants
import com.intellij.idea.plugin.hybris.common.services.CommonIdeaService
import com.intellij.idea.plugin.hybris.project.descriptors.HybrisProjectDescriptor
import com.intellij.idea.plugin.hybris.project.descriptors.PlatformHybrisModuleDescriptor
import com.intellij.idea.plugin.hybris.settings.HybrisDeveloperSpecificProjectSettingsComponent
import com.intellij.idea.plugin.hybris.settings.HybrisRemoteConnectionSettings
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import org.apache.commons.lang3.StringUtils
import java.util.function.Consumer

val regex = Regex("https?://")

class DefaultCommonIdeaService : CommonIdeaService {
    private val commandProcessor: CommandProcessor = CommandProcessor.getInstance()
    override fun isTypingActionInProgress(): Boolean {
        val isTyping = StringUtils.equalsAnyIgnoreCase(
            commandProcessor.currentCommandName, *HybrisConstants.TYPING_EDITOR_ACTIONS
        )
        val isUndoOrRedo = StringUtils.startsWithAny(
            commandProcessor.currentCommandName, *HybrisConstants.UNDO_REDO_EDITOR_ACTIONS
        )
        return isTyping || isUndoOrRedo
    }

    override fun isPotentiallyHybrisProject(project: Project): Boolean {
        val modules = ModuleManager.getInstance(project).modules
        if (modules.isEmpty()) return false

        val moduleNames = modules
            .map { it.name }

        val acceleratorNames = listOf("*cockpits", "*core", "*facades", "*storefront")
        if (matchAllModuleNames(acceleratorNames, moduleNames)) {
            return true
        }

        val webservicesNames = listOf(
            "*${HybrisConstants.EXTENSION_NAME_HMC}",
            HybrisConstants.EXTENSION_NAME_HMC,
            HybrisConstants.EXTENSION_NAME_PLATFORM
        )

        return matchAllModuleNames(webservicesNames, moduleNames)
    }

    private fun matchAllModuleNames(
        namePatterns: Collection<String>,
        moduleNames: Collection<String>
    ): Boolean {
        return namePatterns.all { pattern: String -> matchModuleName(pattern, moduleNames) }
    }

    override fun getPlatformDescriptor(hybrisProjectDescriptor: HybrisProjectDescriptor): PlatformHybrisModuleDescriptor? {
        return hybrisProjectDescriptor.foundModules
            .firstNotNullOfOrNull { it as? PlatformHybrisModuleDescriptor }
    }

    override fun getActiveHacUrl(project: Project): String {
        return HybrisDeveloperSpecificProjectSettingsComponent
            .getInstance(project)
            .getActiveHacRemoteConnectionSettings(project)
            .let { getUrl(it) }
    }

    override fun getActiveSslProtocol(project: Project, settings: HybrisRemoteConnectionSettings?): String {
        val currentSettings = settings ?: HybrisDeveloperSpecificProjectSettingsComponent.getInstance(project)
            .getActiveHacRemoteConnectionSettings(project)

        return currentSettings?.sslProtocol ?: HybrisConstants.DEFAULT_SSL_PROTOCOL
    }

    override fun getHostHacUrl(project: Project, settings: HybrisRemoteConnectionSettings?): String {
        val currentSettings = settings ?: HybrisDeveloperSpecificProjectSettingsComponent.getInstance(project)
            .getActiveHacRemoteConnectionSettings(project)

        return getUrl(currentSettings)
    }

    override fun getSolrUrl(project: Project, settings: HybrisRemoteConnectionSettings?): String {
        var currentSettings = settings
        val sb = StringBuilder()
        if (currentSettings == null) {
            currentSettings = HybrisDeveloperSpecificProjectSettingsComponent.getInstance(project)
                .getActiveSolrRemoteConnectionSettings(project)
        }
        if (currentSettings!!.isSsl) {
            sb.append(HybrisConstants.HTTPS_PROTOCOL)
        } else {
            sb.append(HybrisConstants.HTTP_PROTOCOL)
        }
        sb.append(currentSettings.hostIP)
        sb.append(":")
        sb.append(currentSettings.port)
        sb.append("/")
        sb.append(currentSettings.solrWebroot)
        val result = sb.toString()
        LOG.debug("Calculated host SOLR URL=$result")
        return result
    }

    override fun fixRemoteConnectionSettings(project: Project) {
        val settings = HybrisDeveloperSpecificProjectSettingsComponent.getInstance(project)
        val state = settings.state
        val connectionList = state.remoteConnectionSettingsList
        connectionList.forEach(Consumer {
            prepareSslRemoteConnectionSettings(it)
        })

        if (settings.hacRemoteConnectionSettings.isEmpty()) {
            val newSettings = settings.getDefaultHacRemoteConnectionSettings(project)
            connectionList.add(newSettings)
            state.activeRemoteConnectionID = newSettings.uuid
        }

        if (settings.solrRemoteConnectionSettings.isEmpty()) {
            val newSettings = settings.getDefaultSolrRemoteConnectionSettings(project)
            connectionList.add(newSettings)
            state.activeSolrConnectionID = newSettings.uuid
        }
    }

    private fun prepareSslRemoteConnectionSettings(connectionSettings: HybrisRemoteConnectionSettings) {
        connectionSettings.isSsl = StringUtils.startsWith(connectionSettings.generatedURL, HybrisConstants.HTTPS_PROTOCOL)
        cleanUpRemoteConnectionSettingsHostIp(connectionSettings)
    }

    private fun cleanUpRemoteConnectionSettingsHostIp(connectionSettings: HybrisRemoteConnectionSettings) {
        connectionSettings.hostIP = connectionSettings.hostIP?.replace(regex, "")
    }

    private fun matchModuleName(pattern: String, moduleNames: Collection<String>): Boolean {
        val regex = Regex("\\Q$pattern\\E".replace("*", "\\E.*\\Q"))
        return moduleNames.stream()
            .parallel()
            .anyMatch { it: String -> it.matches(regex) }
    }

    private fun getUrl(settings: HybrisRemoteConnectionSettings?): String {
        val ip = settings!!.hostIP
        val sb = StringBuilder()
        if (settings.isSsl) {
            sb.append(HybrisConstants.HTTPS_PROTOCOL)
        } else {
            sb.append(HybrisConstants.HTTP_PROTOCOL)
        }
        sb.append(ip)
        sb.append(HybrisConstants.URL_PORT_DELIMITER)
        sb.append(settings.port)
        val hac = settings.hacWebroot
        if (StringUtils.isNoneBlank(hac)) {
            sb.append('/')
            sb.append(StringUtils.strip(hac, " /"))
        }
        val result = sb.toString()
        LOG.debug("Calculated hostHacURL=$result")
        return result
    }

    companion object {
        private val LOG = Logger.getInstance(DefaultCommonIdeaService::class.java)
    }
}
