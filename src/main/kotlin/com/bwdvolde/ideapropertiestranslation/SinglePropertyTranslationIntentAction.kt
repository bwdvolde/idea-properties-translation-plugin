package com.bwdvolde.ideapropertiestranslation

import com.bwdvolde.ideapropertiestranslation.notifier.PropertyTranslationNotifier
import com.bwdvolde.ideapropertiestranslation.notifier.impl.DefaultPropertyTranslationNotifier
import com.bwdvolde.ideapropertiestranslation.services.translation.TranslationService
import com.intellij.lang.properties.IProperty
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import java.util.*


class SinglePropertyTranslationIntentAction(
        private val translationService: TranslationService = service(),
        private val notifier: PropertyTranslationNotifier = DefaultPropertyTranslationNotifier()
) : PropertyTranslationIntentAction() {

    override fun getText(): String {
        return MyBundle.getMessage("intent.single.property.action.text")
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val property = element.parent as IProperty
        val propertyKey = property.key!!
        val propertyValue = property.value!!

        val propertiesFiles = property.propertiesFile.resourceBundle.propertiesFiles

        val localesThatRequireTranslation = propertiesFiles
                .filter { it.shouldGenerateTranslationForKey(propertyKey) }
                .map { it.locale }

        val application = ApplicationManager.getApplication()
        application.executeOnPooledThread {
            try {
                val translations = localesThatRequireTranslation.map {
                    Translation(
                        key = propertyKey,
                        locale = it,
                        translation = translationService.translate(propertyValue, it),
                    )
                }

                application.invokeLater {
                    WriteCommandAction.runWriteCommandAction(project) {
                        addTranslations(propertiesFiles, translations)
                    }

                    notifier.notifySuccess(project, localesThatRequireTranslation)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                notifier.notifyFailure(project)
            }
        }
    }
}
