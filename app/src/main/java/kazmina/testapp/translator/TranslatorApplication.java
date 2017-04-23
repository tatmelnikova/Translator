package kazmina.testapp.translator;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;

import kazmina.testapp.translator.utils.CacheInterceptor;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TranslatorApplication extends Application {
    private static Retrofit sRetrofit;
    private static YandexTranslateApi sYandexTranslateApi;

    @Override
    public void onCreate() {
        super.onCreate();

        File httpCacheDirectory = new File(getApplicationContext().getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new CacheInterceptor(getApplicationContext())).cache(new Cache(httpCacheDirectory, cacheSize)).build();

        sRetrofit = new Retrofit.Builder()
                .baseUrl("https://translate.yandex.net") //Базовая часть адреса
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        sYandexTranslateApi = sRetrofit.create(YandexTranslateApi.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static Retrofit getRetrofit(){
        return sRetrofit;
    }
    public static YandexTranslateApi getApi() {
        return sYandexTranslateApi;
    }


}
