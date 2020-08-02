package com.bwdvolde.ideapropertiestranslation

import com.bwdvolde.ideapropertiestranslation.services.translation.TranslationService
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.execution.process.ProcessIOExecutorService
import com.intellij.lang.properties.IProperty
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.lang.properties.psi.impl.PropertyKeyImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import java.util.*


class PropertyTranslationIntentAction : PsiElementBaseIntentionAction() {

    private val translationService = service<TranslationService>()
    private val notifier = PropertyTranslationNotifier()

    override fun startInWriteAction(): Boolean {
        return false
    }

    override fun getFamilyName(): String {
        return text
    }

    override fun getText(): String {
        return MyBundle.getMessage("intent.action.text")
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return element is PropertyKeyImpl
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val property = element.parent as IProperty
        val propertyKey = property.key!!
        val propertyValue = property.value!!

        val propertiesFiles = property.propertiesFile.resourceBundle.propertiesFiles

        val localesThatRequireTranslation = propertiesFiles
                .filter { it.shouldGenerateTranslationForKey(propertyKey) }
                .map { it.locale }

        ProcessIOExecutorService.INSTANCE.submit {
            try {
                val translations = translationService.translate(propertyValue, localesThatRequireTranslation)

                ApplicationManager.getApplication().invokeLater {
                    WriteCommandAction.runWriteCommandAction(project) {
                        addTranslations(propertiesFiles, propertyKey, translations)
                    }

                    notifier.notifySuccess(project, propertyKey, localesThatRequireTranslation)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                notifier.notifyFailure(project, propertyKey)
            }
        }

    }

    private fun PropertiesFile.shouldGenerateTranslationForKey(propertyKey: String): Boolean {
        // Default property files doesn't have a locale, can't add a translation for that
        val localeIsUnKnown = locale.language == ""
        // Don't use findProperty here, doesn't seem to work properly
        val alreadyHasTranslation = namesMap.containsKey(propertyKey)
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
}
