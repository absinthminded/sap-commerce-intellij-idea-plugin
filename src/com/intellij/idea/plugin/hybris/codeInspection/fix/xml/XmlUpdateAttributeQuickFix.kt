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

package com.intellij.idea.plugin.hybris.codeInspection.fix.xml;

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils.message
import com.intellij.idea.plugin.hybris.psi.util.PsiNavigateUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag

class XmlUpdateAttributeQuickFix(private val attributeName: String, private val attributeValue: String) : LocalQuickFix {

    override fun getFamilyName() = message("hybris.inspections.fix.xml.UpdateAttribute", attributeName, attributeValue);

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        when (val currentElement = descriptor.psiElement) {
            is XmlTag -> {
                val xmlAttribute = currentElement.setAttribute(attributeName, attributeValue)
                PsiNavigateUtil.navigate(descriptor, xmlAttribute)
            }

            is XmlAttribute -> {
                currentElement.setValue(attributeValue)
                PsiNavigateUtil.navigate(descriptor, currentElement)
            }

            is XmlElement -> {
                (currentElement.parent as? XmlAttribute)
                    ?.let {
                        it.setValue(attributeValue)
                        PsiNavigateUtil.navigate(descriptor, it)
                    }
            }
        }
    }
}
