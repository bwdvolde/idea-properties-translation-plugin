import com.bwdvolde.ideapropertiestranslation.SinglePropertyTranslationIntentAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import mock.FakeNotifier
import mock.FakeTranslationService

class TestSinglePropertyTranslation : BasePlatformTestCase() {

    private lateinit var translationService: FakeTranslationService
    private lateinit var notifier: FakeNotifier

    private lateinit var intention: SinglePropertyTranslationIntentAction

    override fun setUp() {
        super.setUp()

        translationService = FakeTranslationService()
        notifier = FakeNotifier()

        intention = SinglePropertyTranslationIntentAction(
                translationService = translationService,
                notifier = notifier
        )
    }

    fun `test translations are added to files that do not have the property yet`() {
        myFixture.configureByFiles(
                "single/messages.properties",
                "single/messages_de.properties",
                "single/messages_fr.properties",
                "single/messages_nl.properties"
        )

        myFixture.launchAction(intention)

        myFixture.checkResult("single/messages_de.properties", "chair=Chair_de", false)
        myFixture.checkResult("single/messages_fr.properties", "chair=Chair_fr", false)
        myFixture.checkResult("single/messages_nl.properties", "chair=Chair_nl", false)
    }

    fun `test successful notification is shown when translation was successful`() {
        myFixture.configureByFiles(
                "single/messages.properties",
                "single/messages_de.properties",
                "single/messages_fr.properties",
                "single/messages_nl.properties"
        )

        myFixture.launchAction(intention)

        assertTrue(notifier.successNotified)
    }

    fun `test failure notification is shown when translation service throws exception`() {
        myFixture.configureByFiles(
                "single/messages.properties",
                "single/messages_de.properties",
                "single/messages_fr.properties",
                "single/messages_nl.properties"
        )
        translationService.failOnTranslate()

        myFixture.launchAction(intention)

        assertTrue(notifier.failureNotified)
    }

    override fun getTestDataPath(): String {
        return "src/test/testData"
    }
}
