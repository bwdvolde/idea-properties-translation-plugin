import com.bwdvolde.ideapropertiestranslation.PropertyTranslationIntentAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import mock.MockNotifier
import mock.MockTranslationService

class TestPropertiesTranslation : BasePlatformTestCase() {

    private lateinit var translationService: MockTranslationService
    private lateinit var notifier: MockNotifier

    private lateinit var intention: PropertyTranslationIntentAction

    override fun setUp() {
        super.setUp()

        translationService = MockTranslationService()
        notifier = MockNotifier()

        intention = PropertyTranslationIntentAction(
                translationService = translationService,
                notifier = notifier
        )
    }

    fun `test translations are added to files that do not have the property yet`() {
        myFixture.configureByFiles(
                "setup1/messages.properties",
                "setup1/messages_de.properties",
                "setup1/messages_fr.properties",
                "setup1/messages_nl.properties"
        )

        myFixture.launchAction(intention)

        myFixture.checkResult("setup1/messages_de.properties", "chair=Chair_de", false)
        myFixture.checkResult("setup1/messages_fr.properties", "chair=Chair_fr", false)
        myFixture.checkResult("setup1/messages_nl.properties", "chair=Chair_nl", false)
    }

    fun `test successful notification is shown when translation was successful`() {
        myFixture.configureByFiles(
                "setup1/messages.properties",
                "setup1/messages_de.properties",
                "setup1/messages_fr.properties",
                "setup1/messages_nl.properties"
        )

        myFixture.launchAction(intention)

        assertTrue(notifier.successNotified)
    }

    fun `test failure notification is shown when translation service throws exception`() {
        myFixture.configureByFiles(
                "setup1/messages.properties",
                "setup1/messages_de.properties",
                "setup1/messages_fr.properties",
                "setup1/messages_nl.properties"
        )
        translationService.failOnTranslate()

        myFixture.launchAction(intention)

        assertTrue(notifier.failureNotified)
    }

    override fun getTestDataPath(): String {
        return "src/test/testData"
    }
}
