package com.intellij.idea.plugin.hybris.impex.assistance;

import com.intellij.idea.plugin.hybris.impex.ImpexLanguage
import com.intellij.idea.plugin.hybris.impex.psi.*
import com.intellij.idea.plugin.hybris.impex.utils.ImpexPsiUtils
import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import com.intellij.util.ObjectUtils

class ImpexColumnBreadcrumbsProvider : BreadcrumbsProvider {

    override fun getLanguages(): Array<Language> = arrayOf(ImpexLanguage.getInstance())

    override fun acceptElement(element: PsiElement) = element is ImpexFullHeaderParameter
        || element is ImpexFullHeaderType
        || element is ImpexAnyHeaderMode

    override fun getElementInfo(psi: PsiElement): String {
        val headerParameter = getLinkedHeaderParameter(psi)
        if (headerParameter != null) return headerParameter.text

        val adjustedPsi = adjustWhiteSpaceAndSeparator(psi)

        val parentParameter = PsiTreeUtil.getParentOfType(adjustedPsi, ImpexFullHeaderParameter::class.java, false)
        if (parentParameter != null) return parentParameter.text

        val mode = PsiTreeUtil.getParentOfType(adjustedPsi, ImpexAnyHeaderMode::class.java, false)
        if (mode != null) return mode.text

        val type = PsiTreeUtil.getParentOfType(adjustedPsi, ImpexFullHeaderType::class.java, false)
        if (type != null) return type.headerTypeName.text

        return "<error> : ${psi.node.elementType} : ${psi.text}"
    }

    override fun getParent(element: PsiElement): PsiElement? {
        val linkedParameter = getLinkedHeaderParameter(element)
        if (linkedParameter != null) return linkedParameter

        val parentParameter = PsiTreeUtil.getParentOfType(element, ImpexFullHeaderParameter::class.java, true)
        if (parentParameter != null) return parentParameter

        if (element is ImpexAnyHeaderMode) return null

        val adjustedPsi = adjustWhiteSpaceAndSeparator(element)
        if (adjustedPsi is ImpexAnyHeaderMode) return adjustedPsi

        if (adjustedPsi is ImpexFullHeaderParameter) {
            val line = getImpexHeaderLine(adjustedPsi)
            return line
                ?.fullHeaderType
                ?: line
                    ?.anyHeaderMode
                ?: line
        }
        if (adjustedPsi is ImpexFullHeaderType) {
            return getImpexHeaderLine(adjustedPsi)
                ?.anyHeaderMode
        }

        return PsiTreeUtil.getParentOfType(
            adjustedPsi,
            ImpexValueGroup::class.java,
            ImpexFullHeaderParameter::class.java,
            ImpexFullHeaderType::class.java,
            ImpexAnyHeaderMode::class.java
        )
    }

    private fun getImpexHeaderLine(adjustedPsi: PsiElement) = PsiTreeUtil
        .getParentOfType(adjustedPsi, ImpexHeaderLine::class.java, false)

    private fun getLinkedHeaderParameter(psi: PsiElement): ImpexFullHeaderParameter? {
        return ImpexPsiUtils.getClosestSelectedValueGroupFromTheSameLine(psi)
            ?.let { ImpexPsiUtils.getHeaderForValueGroup(it) }
            ?.let { ObjectUtils.tryCast(it, ImpexFullHeaderParameter::class.java) }
    }

    private fun adjustWhiteSpaceAndSeparator(psiElement: PsiElement): PsiElement {
        if (psiElement is PsiWhiteSpace) {
            val previousElement = PsiTreeUtil.skipSiblingsBackward(psiElement, PsiWhiteSpace::class.java)
            if (previousElement != null) return previousElement
        } else if (isParameterSeparator(psiElement)) {
            return psiElement.nextSibling
        }
        return psiElement
    }

    private fun isParameterSeparator(psi: PsiElement) = psi.node.elementType == ImpexTypes.PARAMETERS_SEPARATOR
        && psi.nextSibling != null
}