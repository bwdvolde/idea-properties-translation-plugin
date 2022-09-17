package mock

import com.bwdvolde.ideapropertiestranslation.notifier.PropertyTranslationNotifier
import com.intellij.openapi.project.Project
import java.util.*

class FakeNotifier : PropertyTranslationNotifier {

    private var _successNotified = false
    private var _failureNotified = false

    val successNotified
        get() = _successNotified
    val failureNotified
        get() = _failureNotified

    override fun notifySuccess(project: Project, locales: Collection<Locale>) {
        _successNotified = true
    }

    override fun notifyFailure(project: Project) {
        _failureNotified = true
    }
}
