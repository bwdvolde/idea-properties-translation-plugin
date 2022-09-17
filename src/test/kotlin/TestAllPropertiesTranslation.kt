import com.bwdvolde.ideapropertiestranslation.AllPropertiesTranslationIntentAction
import com.bwdvolde.ideapropertiestranslation.SinglePropertyTranslationIntentAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import mock.FakeNotifier
import mock.FakeTranslationService

class TestAllPropertiesTranslation : BasePlatformTestCase() {

    private lateinit var translationService: FakeTranslationService
    private lateinit var notifier: FakeNotifier

    private lateinit var intention: AllPropertiesTranslationIntentAction

    override fun setUp() {
        super.setUp()

        translationService = FakeTranslationService()
        notifier = FakeNotifier()

        intention = AllPropertiesTranslationIntentAction(
                translationService = translationService,
                notifier = notifier
        )
    }

    fun `test translations are added to files that do not have the property yet`() {
        myFixture.configureByFiles(
                "multiple/messages.properties",
                "multiple/messages_de.properties",
                "multiple/messages_fr.properties",
                "multiple/messages_nl.properties"
        )

        myFixture.launchAction(intention)

        myFixture.checkResult("multiple/messages_de.properties", "furniture.chair=Chair_de\nfurniture.table=Table_de", false)
        myFixture.checkResult("multiple/messages_fr.properties", "furniture.chair=Chair_fr\nfurniture.table=Table_fr", false)
        myFixture.checkResult("multiple/messages_nl.properties", "furniture.chair=Chair_nl\nfurniture.table=Table_nl", false)
    }

    fun `test all files are checked to find keys with at least one translation`() {
        myFixture.configureByFiles(
            "multipleSpread/messages_de.properties",
            "multipleSpread/messages_fr.properties",
        )

        myFixture.launchAction(intention)

        myFixture.checkResult("multipleSpread/messages_de.properties", "action.de=De\naction.fr=Fr_de\n", false)
        myFixture.checkResult("multipleSpread/messages_fr.properties", "action.de=De_fr\naction.fr=Fr\n", false)
    }


    override fun getTestDataPath(): String {
        return "src/test/testData"
    }
}
