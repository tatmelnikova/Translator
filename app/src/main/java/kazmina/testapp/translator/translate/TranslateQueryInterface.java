package kazmina.testapp.translator.translate;

/**
 *
 */

interface TranslateQueryInterface {
    void runTranslate(String text, String translateDirection);
    void cancel();
}
