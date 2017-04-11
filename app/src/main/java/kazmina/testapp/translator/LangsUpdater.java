package kazmina.testapp.translator;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Locale;

import kazmina.testapp.translator.db.DBContainer;
import kazmina.testapp.translator.db.DBProvider;
import kazmina.testapp.translator.interfaces.LanguagesUpdater;
import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * поддерживает актуальность списка языков в базе данных
 */

public class LangsUpdater implements LanguagesUpdater{
    final String TAG = "LangsUpdater";
    @Override
    public void update(Context context) {
        YandexTranslateApi api = TranslatorApplication.getApi();
        final String locale = Locale.getDefault().getLanguage();
        final DBProvider provider = DBContainer.getProviderInstance(context);
        api.getLanguages(locale).enqueue(new Callback<LanguageLocalisation>() {
            @Override
            public void onResponse(Call<LanguageLocalisation> call, Response<LanguageLocalisation> response) {
                if (response.body() == null){
                    Log.d(TAG, "body is null");
                }else{
                    LanguageLocalisation languageLocalisation = response.body();
                    provider.updateLanguages(locale, languageLocalisation.getLangs(), new DBProvider.ResultCallback<Cursor>() {
                        @Override
                        public void onFinished(Cursor result) {
                            Log.d(TAG, " langs update finished");
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<LanguageLocalisation> call, Throwable t) {
                //Произошла ошибка
                Log.d(TAG, "api query failure");
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });

    }
}
