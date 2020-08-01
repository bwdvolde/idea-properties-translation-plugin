package com.github.bwdvolde.ideapropertiesutil.services.translation.impl

import com.fasterxml.jackson.databind.util.JSONPObject
import com.github.bwdvolde.ideapropertiesutil.services.translation.TranslationService
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.intellij.util.Url
import com.intellij.util.Urls
import com.intellij.util.io.HttpRequests
import com.jetbrains.rd.util.string.print
import jdk.nashorn.internal.objects.Global
import jdk.nashorn.internal.parser.JSONParser
import java.util.*

class DummyTranslationServiceImpl : TranslationService {

    override fun translate(text: String, locale: Locale): String {
        val url = Urls.newUrl("https", "", "translate.googleapis.com/translate_a/single", mapOf(
                "client" to "gtx",
                "sl" to "auto",
                "tl" to locale.language,
                "dt" to "t",
                "q" to text
        ))

        val result = HttpRequests.request(url).readString()
        val gson = Gson()
        val translation = gson.fromJson(result, JsonArray::class.java)[0]
                .asJsonArray[0]
                .asJsonArray[0]
                .asString

        println(translation)
        return translation
    }
}
