/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2023 EPAM Systems <hybrisideaplugin@epam.com>
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
package com.intellij.idea.plugin.hybris.settings

import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils.message
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel

class HybrisApplicationSettingsConfigurableProvider : ConfigurableProvider() {

    override fun createConfigurable() = SettingsConfigurable()

    class SettingsConfigurable() : BoundSearchableConfigurable(
        "[y] SAP Commerce", "[y] SAP Commerce plugin configuration."
    ) {

        private val state = HybrisApplicationSettingsComponent.getInstance().state

        override fun createPanel() = panel {
            row {
                checkBox(message("hybris.import.settings.import.ootb.modules.read.only.label"))
                    .comment(message("hybris.import.settings.import.ootb.modules.read.only.tooltip"))
                    .bindSelected(state::defaultPlatformInReadOnly)
            }
            row {
                checkBox(message("hybris.project.import.scanExternalModules"))
                    .bindSelected(state::scanThroughExternalModule)
            }
            row {
                checkBox(message("hybris.project.import.followSymlink"))
                    .bindSelected(state::followSymlink)
            }
            row {
                checkBox(message("hybris.project.view.tree.hide.empty.middle.folders"))
                    .bindSelected(state::hideEmptyMiddleFolders)
            }
            row {
                checkBox(message("hybris.project.maven.download.sources.folders"))
                    .bindSelected(state::withMavenSources)
            }
            row {
                checkBox(message("hybris.project.maven.download.javadocs.folders"))
                    .bindSelected(state::withMavenJavadocs)
            }
            row {
                checkBox(message("hybris.project.import.ignore.non.existing.sources"))
                    .bindSelected(state::ignoreNonExistingSourceDirectories)
            }
            row {
                checkBox(message("hybris.project.attach.standard.sources"))
                    .bindSelected(state::withStandardProvidedSources)
            }
            row {
                checkBox(message("hybris.project.import.excludeTestSources"))
                    .bindSelected(state::excludeTestSources)
            }
            row {
                checkBox(message("hybris.project.import.importCustomAntBuildFiles"))
                    .bindSelected(state::importCustomAntBuildFiles)
            }
            row {
                checkBox(message("hybris.ts.items.validation.settings.enabled"))
                    .bindSelected(state::warnIfGeneratedItemsAreOutOfDate)
            }
        }
    }
}
