/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
 * Copyright (C) 2014-2023 EPAM Systems <hybrisideaplugin@epam.com> and contributors
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

package com.intellij.idea.plugin.hybris.impex.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.idea.plugin.hybris.impex.psi.ImpexHeaderLine
import com.intellij.idea.plugin.hybris.impex.psi.ImpexHeaderTypeName
import com.intellij.idea.plugin.hybris.system.type.codeInsight.completion.TSCompletionService
import com.intellij.idea.plugin.hybris.system.type.meta.model.TSMetaType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.jetbrains.annotations.Contract
import org.jetbrains.annotations.Nullable

class ImpexHeaderItemTypeAttributeNameCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val project = parameters.editor.project ?: return

        val psiElementUnderCaret = parameters.position
        val headerTypeName = getHeaderTypeNamePsiElementOfAttribute(psiElementUnderCaret) ?: return

        fillDomAttributesCompletions(project, headerTypeName, result)
    }

    @Nullable
    @Contract("null -> null")
    private fun getHeaderTypeNamePsiElementOfAttribute(headerAttributePsiElement: PsiElement?): ImpexHeaderTypeName? {
        if (headerAttributePsiElement == null || headerAttributePsiElement.node == null) return null

        val impexHeaderLine = PsiTreeUtil.getParentOfType(
            headerAttributePsiElement,
            ImpexHeaderLine::class.java
        )
            ?: return null

        val impexFullHeaderType = impexHeaderLine.fullHeaderType ?: return null

        return impexFullHeaderType.headerTypeName
    }

    private fun fillDomAttributesCompletions(
        project: Project,
        headerTypeName: ImpexHeaderTypeName,
        resultSet: CompletionResultSet
    ) {
        val typeCode = headerTypeName.text

        resultSet.caseInsensitive().addAllElements(
            TSCompletionService.getInstance(project).getCompletions(
                typeCode,
                TSMetaType.META_ITEM,
                TSMetaType.META_ENUM,
                TSMetaType.META_RELATION
            )
        )
    }

    companion object {
        val instance: CompletionProvider<CompletionParameters> =
            ApplicationManager.getApplication().getService(ImpexHeaderItemTypeAttributeNameCompletionProvider::class.java)
    }
}
