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

package com.intellij.idea.plugin.hybris.toolwindow.system.type.components

import com.intellij.idea.plugin.hybris.system.type.meta.model.TSMetaRelation.TSMetaRelationElement
import com.intellij.openapi.project.Project

class TSMetaRelationElementCustomPropertiesTable private constructor(myProject: Project) : AbstractTSMetaCustomPropertiesTable<TSMetaRelationElement>(myProject) {

    override fun getItems(owner: TSMetaRelationElement) = owner.customProperties.values
        .sortedWith(compareBy(
            { !it.isCustom },
            { it.module.name },
            { it.name })
        )
        .toMutableList()

    companion object {
        private const val serialVersionUID: Long = -7138215848626018593L

        fun getInstance(project: Project): TSMetaRelationElementCustomPropertiesTable = with(TSMetaRelationElementCustomPropertiesTable(project)) {
            init()

            this
        }
    }

}