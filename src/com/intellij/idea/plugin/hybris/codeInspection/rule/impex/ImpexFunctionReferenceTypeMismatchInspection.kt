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

package com.intellij.idea.plugin.hybris.codeInspection.rule.impex

import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils.message
import com.intellij.idea.plugin.hybris.impex.psi.ImpexMacroUsageDec
import com.intellij.idea.plugin.hybris.impex.psi.ImpexParameter
import com.intellij.idea.plugin.hybris.impex.psi.ImpexTypes.DOCUMENT_ID
import com.intellij.idea.plugin.hybris.impex.psi.ImpexVisitor
import com.intellij.idea.plugin.hybris.impex.psi.references.ImpexFunctionTSItemReference
import com.intellij.idea.plugin.hybris.system.type.meta.TSMetaModelAccess
import com.intellij.idea.plugin.hybris.system.type.meta.model.*
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.impl.source.tree.LeafPsiElement

class ImpexFunctionReferenceTypeMismatchInspection : LocalInspectionTool() {
    override fun getDefaultLevel(): HighlightDisplayLevel = HighlightDisplayLevel.ERROR
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor = ImpexHeaderLineVisitor(holder)

    private class ImpexHeaderLineVisitor(private val problemsHolder: ProblemsHolder) : ImpexVisitor() {

        override fun visitParameter(parameter: ImpexParameter) {
            if (parameter.firstChild is ImpexMacroUsageDec || (parameter.firstChild as? LeafPsiElement)?.elementType == DOCUMENT_ID) return

            val typeReference = parameter.references
                .find { it is ImpexFunctionTSItemReference }
                ?.let { it as? ImpexFunctionTSItemReference }
                ?.takeIf { it.multiResolve(false).isNotEmpty() }
                ?: return

            val expectedItemType = parameter.referenceItemTypeName ?: return
            val inlineType = typeReference.value
            val metaModelAccess = TSMetaModelAccess.getInstance(parameter.project)
            val referenceMeta = metaModelAccess.findMetaClassifierByName(expectedItemType)
                ?: return

            when (referenceMeta) {
                is TSGlobalMetaItem -> {
                    metaModelAccess.getAll<TSGlobalMetaItem>(TSMetaType.META_ITEM)
                        .any { meta ->
                            meta.allExtends.find { it.name == inlineType } != null
                                // or itself, it will be highlighted as unnecessary via Inspection
                                || meta.name == inlineType
                        }
                        .takeUnless { it }
                        ?.let {
                            problemsHolder.registerProblemForReference(
                                typeReference,
                                ProblemHighlightType.ERROR,
                                message("hybris.inspections.impex.ImpexMismatchFunctionTypeInspection.key",
                                    inlineType,
                                    expectedItemType,
                                    parameter.referenceName ?: "?"
                                ),
                            )
                        }
                }

                is TSGlobalMetaEnum -> registerProblemTypeMismatch(typeReference, inlineType, "Enum")
                is TSGlobalMetaCollection -> registerProblemTypeMismatch(typeReference, inlineType, "Collection")
                is TSGlobalMetaMap -> registerProblemTypeMismatch(typeReference, inlineType, "Map")
                is TSGlobalMetaRelation -> registerProblemTypeMismatch(typeReference, inlineType, "Relation")
                is TSGlobalMetaAtomic -> registerProblemTypeMismatch(typeReference, inlineType, "Atomic")
            }
        }

        private fun registerProblemTypeMismatch(
            typeReference: ImpexFunctionTSItemReference,
            inlineType: String,
            type: String
        ) {
            problemsHolder.registerProblemForReference(
                typeReference,
                ProblemHighlightType.ERROR,
                message("hybris.inspections.impex.ImpexMismatchFunctionTypeInspection.onlyItemType.key", inlineType, type),
            )
        }

    }
}