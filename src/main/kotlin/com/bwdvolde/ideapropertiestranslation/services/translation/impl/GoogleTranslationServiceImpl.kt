package com.bwdvolde.ideapropertiestranslation.services.translation.impl

import com.bwdvolde.ideapropertiestranslation.services.translation.TranslationService
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.intellij.util.Urls
import com.intellij.util.io.HttpRequests
import java.lang.RuntimeException
import java.util.*

/**
 * Translation service that uses google translate to obtain translations.
 *
 * Note that google translate is different from google cloud translation
 * Google translate:         free version that can be found at https://translate.google.com/?hl=nl
 * Google cloud translation: paid version that offers better translations and api support
 */
class GoogleTranslationServiceImpl : TranslationService {

    override fun translate(text: String, locale: Locale): String {
        val url = Urls.newUrl("https", "", "translate.googleapis.com/translate_a/single", mapOf(
                "client" to "gtx",
                "sl" to "auto", // Source language
                "tl" to locale.language, // Translation language
                "dt" to "t",
                "q" to text // Text to be translated
        ))

        val result = HttpRequests.request(url).readString()
        val gson = Gson()

        return gson.fromJson(result, JsonArray::class.java)[0]
                .asJsonArray[0]
                .asJsonArray[0]
                .asString
    }

    override fun translate(text: String, locales: Collection<Locale>): Map<Locale, String> {
        return locales
                .map { it to translate(text, it) }
                .toMap()
    }
}
