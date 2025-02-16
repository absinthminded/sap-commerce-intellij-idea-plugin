/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2023 EPAM Systems <hybrisideaplugin@epam.com> and contributors
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

package com.intellij.idea.plugin.hybris.system.type.lang.folding

import com.intellij.idea.plugin.hybris.system.type.model.*
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.xml.XmlTag

class ItemsXmlFilter : PsiElementFilter {

    override fun isAccepted(element: PsiElement) = when (element) {
        is XmlTag -> when (element.localName) {
            CustomProperties.PROPERTY,
            MapTypes.MAPTYPE,
            AtomicTypes.ATOMICTYPE,
            EnumTypes.ENUMTYPE,
            CollectionTypes.COLLECTIONTYPE,
            Relations.RELATION,
            Relation.SOURCE_ELEMENT,
            Relation.TARGET_ELEMENT,
            ItemTypes.ITEMTYPE,
            ItemType.DEPLOYMENT,
            Attributes.ATTRIBUTE,
            Indexes.INDEX -> true

            else -> false
        }

        else -> false
    }
}