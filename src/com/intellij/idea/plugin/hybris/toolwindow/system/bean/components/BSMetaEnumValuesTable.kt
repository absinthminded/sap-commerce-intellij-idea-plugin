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

package com.intellij.idea.plugin.hybris.toolwindow.system.bean.components

import com.intellij.idea.plugin.hybris.kotlin.yExtensionName
import com.intellij.idea.plugin.hybris.system.bean.meta.model.BSGlobalMetaEnum
import com.intellij.idea.plugin.hybris.system.bean.meta.model.BSMetaEnum
import com.intellij.idea.plugin.hybris.toolwindow.components.AbstractTable
import com.intellij.openapi.project.Project
import com.intellij.util.ui.ListTableModel

class BSMetaEnumValuesTable private constructor(myProject: Project) :
    AbstractTable<BSGlobalMetaEnum, BSMetaEnum.BSMetaEnumValue>(myProject) {

    override fun getSearchableColumnNames() = listOf(COLUMN_VALUE)
    override fun getFixedWidthColumnNames() = listOf(COLUMN_CUSTOM)
    override fun select(item: BSMetaEnum.BSMetaEnumValue) = selectRowWithValue(item.name, COLUMN_VALUE)
    override fun getItems(owner: BSGlobalMetaEnum) = owner.values.values.sortedWith(
        compareBy(
            { !it.isCustom },
            { it.module.name },
            { it.name })
    )
        .toMutableList()

    override fun createModel(): ListTableModel<BSMetaEnum.BSMetaEnumValue> = with(ListTableModel<BSMetaEnum.BSMetaEnumValue>()) {
        columnInfos = arrayOf(
            createColumn(
                name = COLUMN_CUSTOM,
                valueProvider = { attr -> attr.isCustom },
                columnClass = Boolean::class.java,
                tooltip = "Custom"
            ),
            createColumn(
                name = COLUMN_MODULE,
                valueProvider = { attr -> attr.module.yExtensionName() }
            ),
            createColumn(
                name = COLUMN_VALUE,
                valueProvider = { attr -> attr.name }
            )
        )

        this
    }

    companion object {
        private const val serialVersionUID: Long = 6612572661238637911L

        private const val COLUMN_CUSTOM = "C"
        private const val COLUMN_MODULE = "Module"
        private const val COLUMN_VALUE = "Value"

        fun getInstance(project: Project): BSMetaEnumValuesTable = with(BSMetaEnumValuesTable(project)) {
            init()

            this
        }
    }

}