package com.bwdvolde.ideapropertiestranslation.services.translation

import java.util.*

interface TranslationService {

    fun translate(text: String, locale: Locale): String
    fun translate(text: String, locales: Collection<Locale>): Map<Locale, String>
}
