package com.github.bwdvolde.ideapropertiesutil

import com.github.bwdvolde.ideapropertiesutil.services.translation.TranslationService
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.execution.process.ProcessIOExecutorService
import com.intellij.lang.properties.IProperty
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.lang.properties.psi.impl.PropertyKeyImpl
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import java.util.*


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

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val property = element.parent as IProperty
        val propertyKey = property.key!!
        val propertyValue = property.value!!

        val propertiesFileParent = property.propertiesFile

        val propertiesFiles = propertiesFileParent.resourceBundle.propertiesFiles

        val localesThatRequireTranslation = propertiesFiles
                .filter { it.shouldGenerateTranslationForKey(propertyKey) }
                .map { it.locale }

        ProcessIOExecutorService.INSTANCE.submit {
            val translations = translationService.translate(propertyValue, localesThatRequireTranslation)

            ApplicationManager.getApplication().invokeLater {
                WriteCommandAction.runWriteCommandAction(project) {
                    addTranslations(propertiesFiles, propertyKey, translations)
                    displaySuccessfulNotification(project)
                }
            }
        }

    }

    private fun PropertiesFile.shouldGenerateTranslationForKey(propertyKey: String): Boolean {
        // Default property files doesn't have a locale, can't add a translation for that
        val localeIsUnKnown = locale.language == ""
        val alreadyHasTranslation = findPropertyByKey(propertyKey) != null
        return !(localeIsUnKnown || alreadyHasTranslation)
    }

    private fun addTranslations(propertiesFiles: List<PropertiesFile>, propertyKey: String, translations: Map<Locale, String>) {
        propertiesFiles.forEach {
            val shouldAddTranslation = translations.containsKey(it.locale)
            if (shouldAddTranslation) {
                val translation = translations.getValue(it.locale)
                it.addProperty(propertyKey, translation)
            }
        }
    }

    private val NOTIFICATION_GROUP = NotificationGroup("Property translation", NotificationDisplayType.BALLOON, true)

    private fun displaySuccessfulNotification(project: Project) {
        val notification = NOTIFICATION_GROUP.createNotification("Property translation", "The translations have been added", NotificationType.INFORMATION)
        notification.notify(project)
    }
}
