package kazmina.testapp.translator.interfaces;

import android.content.Context;

import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;

/**
 *
 */

public interface LanguagesUpdaterInterface {
    void checkNeedUpdate(Context context);
}
