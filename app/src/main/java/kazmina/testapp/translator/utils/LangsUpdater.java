package kazmina.testapp.translator.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Locale;

import kazmina.testapp.translator.TranslatorApplication;
import kazmina.testapp.translator.YandexTranslateApi;
import kazmina.testapp.translator.db.DBContainer;
import kazmina.testapp.translator.db.DBProvider;
import kazmina.testapp.translator.interfaces.LanguagesUpdaterInterface;
import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * поддерживает актуальность списка языков в базе данных
 */

public class LangsUpdater implements LanguagesUpdaterInterface {
    final String TAG = "LangsUpdater";
    final Integer mUpdateFrequency = 1;


    /**
     * проверяет, пора ли обновлять список языков для текущей локали
     * ResultCallback запускается только в том случае, если после последнего обновления прошло
     * больше mUpdateFrequency дней
     * @param context контекст для провайдера БД
     */
    public void checkNeedUpdate(final Context context){
        String locale = Locale.getDefault().getLanguage();
        DBProvider provider = DBContainer.getProviderInstance(context);
        provider.checkNeedUpdate(locale,  mUpdateFrequency, new DBProvider.ResultCallback<String>() {
            @Override
            public void onFinished(String locale) {
                update(locale, context);
            }
        });
    }

    /**
     * обновляет список языков для указанной локали
     * после обновления списка записывает в БД дату обновления локали
     * @param locale локаль, для которой обновляем список языков
     * @param context  контекст для провайдера БД
     */
    private void update(final String locale, Context context) {
        YandexTranslateApi api = TranslatorApplication.getApi();
        final DBProvider provider = DBContainer.getProviderInstance(context);
        api.getLanguages(locale).enqueue(new Callback<LanguageLocalisation>() {
            @Override
            public void onResponse(Call<LanguageLocalisation> call, Response<LanguageLocalisation> response) {
                if (response.body() == null){
                    Log.d(TAG, "body is null");
                }else{
                    LanguageLocalisation languageLocalisation = response.body();
                    provider.updateLanguages(locale, languageLocalisation.getLangs(), new DBProvider.ResultCallback<Void>() {
                        @Override
                        public void onFinished(Void v) {
                            provider.setLocaleUpdated(locale);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<LanguageLocalisation> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

    }
}
