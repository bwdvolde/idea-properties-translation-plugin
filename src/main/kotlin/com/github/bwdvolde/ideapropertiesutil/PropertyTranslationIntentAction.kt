package com.github.bwdvolde.ideapropertiesutil

import com.github.bwdvolde.ideapropertiesutil.services.translation.TranslationService
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.lang.properties.IProperty
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.lang.properties.psi.impl.PropertyKeyImpl
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.progress.util.BackgroundTaskUtil.computeInBackgroundAndTryWait
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement


class PropertyTranslationIntentAction : PsiElementBaseIntentionAction() {

    private val translationService = service<TranslationService>()

    override fun startInWriteAction(): Boolean {
        return false
    }

    override fun getFamilyName(): String {
        return text
    }

    override fun getText(): String {
        return "Fill in missing translations"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return element is PropertyKeyImpl
    }

    private val NOTIFICATION_GROUP = NotificationGroup("Groovy DSL errors", NotificationDisplayType.BALLOON, true)

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val property = element.parent as IProperty
        val propertyKey = property.key!!
        val propertyValue = property.value!!

        val propertiesFileParent = property.propertiesFile

        val propertiesFiles = propertiesFileParent.resourceBundle.propertiesFiles

        propertiesFiles.forEach { propertiesFile ->
            propertiesFile.addMissingTranslation(propertyKey, propertyValue)
        }

        val notification = NOTIFICATION_GROUP.createNotification("Property translation", "The translations have been added", NotificationType.INFORMATION)
        notification.notify(project)
    }

//    private fun PropertiesFile.addMissingTranslation(propertyKey: String, propertyValue: String) {
//        if (findPropertyByKey(propertyKey) == null) {
//            val result = computeInBackgroundAndTryWait(
//                    {
//                        translationService.translate(propertyValue, locale)
//                    },
//                    { translatedValue ->
//                        println("Got value: $translatedValue")
//                        addProperty(propertyKey, translatedValue)
//                    },
//                    10 * 1000)
//
//            if (result != null) {
//                println("Got value: $result")
//                addProperty(propertyKey, result)
//            }
//
//        }
//    }

    private fun PropertiesFile.addMissingTranslation(propertyKey: String, propertyValue: String) {

        ProgressManager.getInstance()
                .run(object : Backgroundable(project, "Downloading translations", true,
                        PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                    override fun run(indicator: ProgressIndicator) {
                        indicator.isIndeterminate = true
                        val translation = translationService.translate(propertyValue, locale)
                        println(translation)
                    }
                })
    }
}
