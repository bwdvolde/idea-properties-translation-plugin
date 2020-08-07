package com.bwdvolde.ideapropertiestranslation.notifier

import com.intellij.openapi.project.Project
import java.util.*

interface PropertyTranslationNotifier {
    fun notifySuccess(project: Project, locales: Collection<Locale>)
    fun notifyFailure(project: Project)
}
