package com.github.bwdvolde.ideapropertiesutil.services.translation.impl

import com.github.bwdvolde.ideapropertiesutil.services.translation.TranslationService
import java.util.*

class DummyTranslationServiceImpl : TranslationService {

    override fun translate(text: String, locale: Locale): String {
        return "${text}_${locale.language}"
    }
}
