package kazmina.testapp.translator;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * класс для работы ретрофита с апи переводчика
 * key = trnsl.1.1.20170320T074137Z.b79dbc842fbc5526.fd9448c978b6f6ad53587000adc0d56613cd4358
 */

interface YandexTranslateApi {
    @GET("/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20170320T074137Z.b79dbc842fbc5526.fd9448c978b6f6ad53587000adc0d56613cd4358")
    Call<LanguageLocalisation> getLanguages(@Query("ui") String currentLangID);
}
