/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
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

package com.intellij.idea.plugin.hybris.impex.assistance

import com.intellij.codeInsight.folding.impl.FoldingUtil
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.codeInsight.highlighting.HighlightUsagesHandler
import com.intellij.idea.plugin.hybris.impex.utils.ImpexPsiUtils
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.util.concurrent.ConcurrentHashMap

class DefaultImpexHeaderNameHighlighterService :
    AbstractImpexHighlighterService(),
    ImpexHeaderNameHighlighterService {

    private val highlightedBlocks = mutableMapOf<Editor, PsiElement>()

    override fun highlight(editor: Editor) {
        ImpexPsiUtils.getHeaderOfValueGroupUnderCaret(editor)
            ?.let { highlightArea(editor, it) }
            ?: clearHighlightedArea(editor)
    }

    private fun highlightArea(
        editor: Editor,
        impexFullHeaderParameter: PsiElement
    ) {
        if (isAlreadyHighlighted(editor, impexFullHeaderParameter)) return

        ApplicationManager.getApplication().invokeLater {
            highlightedBlocks.remove(editor)
                ?.let {
                    modifyHighlightedArea(editor, it, true)
                }

            highlightedBlocks[editor] = impexFullHeaderParameter
            modifyHighlightedArea(editor, impexFullHeaderParameter, false)
        }
    }

    private fun clearHighlightedArea(editor: Editor) {
        if (highlightedBlocks.isEmpty()) return

        highlightedBlocks.remove(editor)
            ?.let {
                ApplicationManager.getApplication().invokeLater {
                    modifyHighlightedArea(editor, it, true)
                }
            }
    }

    private fun isAlreadyHighlighted(
        editor: Editor,
        impexFullHeaderParameter: PsiElement
    ) = highlightedBlocks[editor] == impexFullHeaderParameter

    private fun modifyHighlightedArea(
        editor: Editor,
        impexFullHeaderParameter: PsiElement,
        clear: Boolean
    ) {
        val project = editor.project ?: return
        if (project.isDisposed) return

        removeInvalidRangeHighlighters(editor)

        if (isTextRangeNotFolded(editor, impexFullHeaderParameter)) {
            val ranges = mutableListOf<TextRange>()
            ranges.add(impexFullHeaderParameter.textRange)

            HighlightUsagesHandler.highlightRanges(
                HighlightManager.getInstance(project),
                editor,
                EditorColors.SEARCH_RESULT_ATTRIBUTES,
                clear,
                ranges
            )
        }
    }

    private fun isTextRangeNotFolded(editor: Editor, impexFullHeaderParameter: PsiElement) =
        !FoldingUtil.isTextRangeFolded(editor, impexFullHeaderParameter.textRange)

    override fun releaseEditorData(editor: Editor) {
        highlightedBlocks.remove(editor)
    }
}
