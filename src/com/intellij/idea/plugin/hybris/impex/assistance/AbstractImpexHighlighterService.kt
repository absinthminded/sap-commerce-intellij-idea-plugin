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

import com.intellij.codeInsight.highlighting.HighlightManager
import com.intellij.codeInsight.highlighting.HighlightManagerImpl
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.util.containers.stream
import org.jetbrains.annotations.NotNull

abstract class AbstractImpexHighlighterService : ImpexHighlighterService {

    fun removeInvalidRangeHighlighters(editor: Editor) {
        val project = editor.project ?: return
        val highlightManager = HighlightManager.getInstance(project) as? HighlightManagerImpl ?: return

        highlightManager.getHighlighters(editor).stream()
            .filter { isNotProperRangeHighlighter(it) }
            .forEach { highlighter -> highlightManager.removeSegmentHighlighter(editor, highlighter) }
    }

    private fun isNotProperRangeHighlighter(@NotNull rangeHighlighter: RangeHighlighter) = rangeHighlighter
        .startOffset > rangeHighlighter.endOffset || rangeHighlighter.startOffset < 0
}
