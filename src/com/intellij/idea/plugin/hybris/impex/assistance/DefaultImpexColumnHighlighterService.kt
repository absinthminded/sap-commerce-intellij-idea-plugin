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

package com.intellij.idea.plugin.hybris.impex.assistance

import com.intellij.codeInsight.folding.impl.FoldingUtil
import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.codeInsight.highlighting.HighlightUsagesHandler
import com.intellij.idea.plugin.hybris.impex.utils.ImpexPsiUtils
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColors
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import java.util.stream.Collectors

class DefaultImpexColumnHighlighterService : AbstractImpexHighlighterService(), ImpexColumnHighlighterService {

    override fun highlight(editor: Editor) {
        val columns = ImpexPsiUtils.getColumnOfHeaderUnderCaret(editor)

        if (columns.isNullOrEmpty()) {
            clearHighlightedArea(editor)
        } else {
            highlightArea(editor, columns)
        }
    }

    private fun clearHighlightedArea(editor: Editor) {
        val column = editor.getUserData(CACHE_KEY) ?: return

        editor.putUserData(CACHE_KEY, null)
        application.invokeLater {
            modifyHighlightedArea(editor, column)
        }
    }

    private fun highlightArea(editor: Editor, columns: List<PsiElement>) {
        if (isAlreadyHighlighted(editor, columns)) return

        application.invokeLater {
            val currentHighlightedElement = editor.getUserData(CACHE_KEY)

            editor.putUserData(CACHE_KEY, null)
            currentHighlightedElement
                ?.let { modifyHighlightedArea(editor, it) }

            editor.putUserData(CACHE_KEY, columns)
            modifyHighlightedArea(editor, columns, false)
        }
    }

    private fun isAlreadyHighlighted(editor: Editor, column: List<PsiElement>) = editor
        .getUserData(CACHE_KEY) == column

    private fun modifyHighlightedArea(
        editor: Editor,
        columns: List<PsiElement>,
        clear: Boolean = true
    ) {
        val project = editor.project ?: return
        if (project.isDisposed) return

        removeInvalidRangeHighlighters(editor)

        columns.stream()
            .map(PsiElement::getTextRange)
            .filter { textRange -> !FoldingUtil.isTextRangeFolded(editor, textRange) }
            .collect(Collectors.toList())
            .takeUnless { it.isNullOrEmpty() }
            ?.let {
                HighlightUsagesHandler.highlightRanges(
                    HighlightManager.getInstance(project),
                    editor,
                    EditorColors.SEARCH_RESULT_ATTRIBUTES,
                    clear,
                    it
                )
            }
    }

    override fun releaseEditorData(editor: Editor) {
        editor.putUserData(CACHE_KEY, null)
    }

    companion object {
        private val application: Application = ApplicationManager.getApplication()
        private val CACHE_KEY: Key<List<PsiElement>> = Key.create("IMPEX_COLUMN_HIGHLIGHT_CACHE")
    }
}