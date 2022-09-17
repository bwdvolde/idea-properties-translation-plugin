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


class AllPropertiesTranslationIntentAction(
    private val translationService: TranslationService = service(),
    private val notifier: PropertyTranslationNotifier = DefaultPropertyTranslationNotifier()
) : PropertyTranslationIntentAction() {

    override fun getText(): String {
        return MyBundle.getMessage("intent.all.properties.action.text")
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val propertiesFiles = (element.parent as IProperty).propertiesFile.resourceBundle.propertiesFiles

        val keyToExistingTranslation = computeKeysWithExistingTranslation(propertiesFiles)
        val toTranslate = computeWhatToTranslate(keyToExistingTranslation, propertiesFiles)

        val application = ApplicationManager.getApplication()
        application.executeOnPooledThread {
            try {
                val translations = toTranslate.map {
                    Translation(
                        key = it.key,
                        locale = it.locale,
                        translation = translationService.translate(it.existingTranslation, it.locale)
                    )
                }

                application.invokeLater {
                    WriteCommandAction.runWriteCommandAction(project) {
                        addTranslations(propertiesFiles, translations)
                    }
                    notifier.notifySuccess(project, translations.map { it.locale }.toSet())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                notifier.notifyFailure(project)
            }
        }
    }

    private fun computeKeysWithExistingTranslation(propertiesFiles: List<PropertiesFile>) =
        propertiesFiles
            .flatMap { it.properties }
            .filterNotNull()
            .filter { it.key != null && it.value != null }
            .groupBy { it.key!! }
            .mapValues { it.value.map { it.value!! }.first() }

    private fun computeWhatToTranslate(
        keyToExistingTranslation: Map<String, String>,
        propertiesFiles: List<PropertiesFile>
    ) = keyToExistingTranslation
        .flatMap { (key, existingTranslation) ->
            propertiesFiles
                .filter { it.shouldGenerateTranslationForKey(key) }
                .map {
                    ToTranslate(
                        key = key,
                        existingTranslation = existingTranslation,
                        locale = it.locale
                    )
                }
        }
}
