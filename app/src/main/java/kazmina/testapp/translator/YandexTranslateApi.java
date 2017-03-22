package kazmina.testapp.translator;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by Fina on 21.03.2017.
 * key = trnsl.1.1.20170320T074137Z.b79dbc842fbc5526.fd9448c978b6f6ad53587000adc0d56613cd4358
 */

interface YandexTranslateApi {
     //String baseUri = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20170320T074137Z.b79dbc842fbc5526.fd9448c978b6f6ad53587000adc0d56613cd4358& [ui=<код языка>]";
    @GET("/api/v1.5/tr.json/getLangs?key=trnsl.1.1.20170320T074137Z.b79dbc842fbc5526.fd9448c978b6f6ad53587000adc0d56613cd4358")
    Call<Language> getLanguages(@Query("ui") String currentLangID);
}
