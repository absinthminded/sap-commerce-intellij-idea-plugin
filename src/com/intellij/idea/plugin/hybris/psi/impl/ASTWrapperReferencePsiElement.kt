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

package com.intellij.idea.plugin.hybris.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.idea.plugin.hybris.psi.util.PsiUtils
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import java.io.Serial

abstract class ASTWrapperReferencePsiElement(node: ASTNode) : ASTWrapperPsiElement(node) {

    private var myReference: PsiReferenceBase<out PsiElement>? = null

    abstract fun createReference(): PsiReferenceBase<out PsiElement>?

    override fun getReference() = references
        .firstOrNull()

    override fun getReferences(): Array<PsiReference> {
        if (PsiUtils.shouldCreateNewReference(myReference, text)) {
            myReference = createReference()
        }
        return myReference
            ?.let { arrayOf(it) }
            ?: emptyArray()
    }

    override fun clone(): Any {
        val result = super.clone() as ASTWrapperReferencePsiElement
        result.myReference = null
        return result
    }

    companion object {
        @Serial
        private const val serialVersionUID: Long = 6299332054356373093L
    }
}