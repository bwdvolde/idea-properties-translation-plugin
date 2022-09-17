package com.bwdvolde.ideapropertiestranslation

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.lang.properties.psi.PropertiesFile
import com.intellij.lang.properties.psi.impl.PropertyKeyImpl
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import java.util.*

abstract class PropertyTranslationIntentAction : PsiElementBaseIntentionAction() {

    override fun startInWriteAction(): Boolean {
        return true
    }

    override fun getFamilyName(): String {
        return text
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return element is PropertyKeyImpl
    }

    protected fun PropertiesFile.shouldGenerateTranslationForKey(propertyKey: String): Boolean {
        // Default properties file doesn't have a locale, can't add a translation for that
        val localeIsUnKnown = locale.language == ""
        // Don't use findProperty here, doesn't seem to work properly
        val alreadyHasTranslation = namesMap.containsKey(propertyKey)
        return !(localeIsUnKnown || alreadyHasTranslation)
    }

    protected data class ToTranslate(
        val key: String,
        val existingTranslation: String,
        val locale: Locale
    )

    protected data class Translation(
        val key: String,
        val locale: Locale,
        val translation: String,
    )

    protected fun addTranslations(
        propertiesFiles: List<PropertiesFile>,
        translations: Collection<Translation>,
    ) {
        translations.forEach { translation ->
            val propertiesFile = propertiesFiles.first { it.locale == translation.locale }
            propertiesFile.addProperty(translation.key, translation.translation)
        }
    }
}
