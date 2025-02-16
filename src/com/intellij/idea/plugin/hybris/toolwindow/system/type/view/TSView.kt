/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2019 EPAM Systems <hybrisideaplugin@epam.com>
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

package com.intellij.idea.plugin.hybris.toolwindow.system.type.view

import com.intellij.icons.AllIcons
import com.intellij.ide.CommonActionsManager
import com.intellij.ide.IdeBundle
import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils.message
import com.intellij.idea.plugin.hybris.system.type.meta.TSChangeListener
import com.intellij.idea.plugin.hybris.system.type.meta.TSGlobalMetaModel
import com.intellij.idea.plugin.hybris.system.type.meta.TSMetaModelAccess
import com.intellij.idea.plugin.hybris.toolwindow.system.type.components.TSTreePanel
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Disposer
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import java.awt.GridBagLayout

class TSView(val myProject: Project) : SimpleToolWindowPanel(false, true), Disposable {

    private val myItemsViewActionGroup: DefaultActionGroup by lazy(::initItemsViewActionGroup)
    private val mySettings = TSViewSettings.getInstance(myProject)
    private val myTreePane = TSTreePanel(myProject)

    override fun dispose() {
        //NOP
    }

    init {
        installToolbar()

        if (DumbService.isDumb(myProject)) {
            val panel = JBPanel<JBPanel<*>>(GridBagLayout())
            panel.add(JBLabel(message("hybris.toolwindow.ts.suspended.text", IdeBundle.message("progress.performing.indexing.tasks"))))
            setContent(panel)
        }

        Disposer.register(this, myTreePane)
        installSettingsListener()
    }

    private fun installToolbar() {
        val actionsManager = CommonActionsManager.getInstance()
        val toolbar = with(DefaultActionGroup()) {
            add(myItemsViewActionGroup)
            addSeparator()
            add(actionsManager.createExpandAllHeaderAction(myTreePane.tree))
            add(actionsManager.createCollapseAllHeaderAction(myTreePane.tree))
            ActionManager.getInstance().createActionToolbar("HybrisTSView", this, false)
        }
        toolbar.targetComponent = this
        setToolbar(toolbar.component)
    }

    private fun installSettingsListener() {
        with(myProject.messageBus.connect(this)) {
            subscribe(TSViewSettings.TOPIC, object : TSViewSettings.Listener {
                override fun settingsChanged(changeType: TSViewSettings.ChangeType) {
                    myTreePane.update(TSMetaModelAccess.getInstance(myProject).getMetaModel(), changeType)
                }
            })
            subscribe(TSMetaModelAccess.TOPIC, object : TSChangeListener {
                override fun typeSystemChanged(globalMetaModel: TSGlobalMetaModel) {
                    refreshContent(globalMetaModel)
                }
            })
        }
    }

    private fun refreshContent(globalMetaModel: TSGlobalMetaModel) {
        myTreePane.update(globalMetaModel, TSViewSettings.ChangeType.FULL)

        if (content != myTreePane) {
            setContent(myTreePane)
        }
    }

    private fun initItemsViewActionGroup(): DefaultActionGroup = with(
        DefaultActionGroup(
            message("hybris.toolwindow.action.view_options.text"),
            true
        )
    ) {
        templatePresentation.icon = AllIcons.Actions.Show

        addSeparator(message("hybris.toolwindow.action.separator.show"))
        add(ShowOnlyCustomAction(mySettings))
        addSeparator("-- Types --")
        add(ShowMetaAtomicsAction(mySettings))
        add(ShowMetaEnumsAction(mySettings))
        add(ShowMetaCollectionsAction(mySettings))
        add(ShowMetaMapsAction(mySettings))
        add(ShowMetaRelationsAction(mySettings))
        add(ShowMetaItemsAction(mySettings))
        addSeparator("-- Enum --")
        add(ShowMetaEnumValuesAction(mySettings))
        addSeparator("-- Item --")
        add(ShowMetaItemIndexesAction(mySettings))
        add(ShowMetaItemAttributesAction(mySettings))
        add(ShowMetaItemCustomPropertiesAction(mySettings))
        add(GroupItemByParentAction(mySettings))
        this
    }

    companion object {
        private const val serialVersionUID: Long = 74100584202830949L
    }

}
