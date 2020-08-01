package com.github.bwdvolde.ideapropertiesutil.services.translation

import java.util.*

interface TranslationService {

    fun translate(text: String, locale: Locale): String
}
