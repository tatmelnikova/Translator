package kazmina.testapp.translator.translate;

import android.content.Context;

/**
 *
 */

interface TranslateQueryInterface {
    void runTranslate(String text, String translateDirection);
    void cancel();
}
