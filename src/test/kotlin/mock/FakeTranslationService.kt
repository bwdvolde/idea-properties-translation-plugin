package mock

import com.bwdvolde.ideapropertiestranslation.services.translation.TranslationService
import java.lang.RuntimeException
import java.util.*

class FakeTranslationService: TranslationService {

    private var shouldFailOnTranslate: Boolean = false

    override fun translate(text: String, locale: Locale): String {
        if (shouldFailOnTranslate) {
            throw RuntimeException()
        }
        return "${text}_${locale.language}"
    }

    override fun translate(text: String, locales: Collection<Locale>): Map<Locale, String> {
        return locales.associateWith { translate(text, it) }
    }

    fun failOnTranslate() {
        shouldFailOnTranslate = true
    }
}
