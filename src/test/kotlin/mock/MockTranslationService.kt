package mock

import com.bwdvolde.ideapropertiestranslation.services.translation.TranslationService
import java.lang.RuntimeException
import java.util.*

class MockTranslationService: TranslationService {

    private var shouldFailOnTranslate: Boolean = false

    override fun translate(text: String, locale: Locale): String {
        if (shouldFailOnTranslate) {
            throw RuntimeException()
        }
        return "${text}_${locale.language}"
    }

    override fun translate(text: String, locales: Collection<Locale>): Map<Locale, String> {
        return locales
                .map { it to translate(text, it) }
                .toMap()
    }

    fun failOnTranslate() {
        shouldFailOnTranslate = true
    }
}
