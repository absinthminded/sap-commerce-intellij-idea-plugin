package com.intellij.idea.plugin.hybris.gradle;

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.gradle.settings.GradleSettings

class GradleSupport {

    fun clearLinkedProjectSettings(project: Project) {
        GradleSettings.getInstance(project).linkedProjectsSettings = emptyList()
    }

    companion object {
        val instance: GradleSupport? = ApplicationManager.getApplication().getService(GradleSupport::class.java)
    }
}