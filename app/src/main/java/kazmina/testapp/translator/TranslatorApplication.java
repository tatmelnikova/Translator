package kazmina.testapp.translator;

import android.app.Application;

import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* основной класс приложения */

public class TranslatorApplication extends Application {
    private Retrofit sRetrofit;
    private static YandexTranslateApi sYandexTranslateApi;

    @Override
    public void onCreate() {
        super.onCreate();
        sRetrofit = new Retrofit.Builder()
                .baseUrl("https://translate.yandex.net") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        sYandexTranslateApi = sRetrofit.create(YandexTranslateApi.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public  Retrofit getRetrofit(){
        return sRetrofit;
    }
    public static YandexTranslateApi getApi() {
        return sYandexTranslateApi;
    }
}
