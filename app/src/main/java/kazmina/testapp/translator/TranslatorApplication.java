package kazmina.testapp.translator;

import android.app.Application;

import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* основной класс приложения */

public class TranslatorApplication extends Application {
    private Retrofit sRetrofit;
    private static YandexTranslateApi sYandexTranslateApi;
    /**
     * mLanguageLocalisation хранит последний полученный от YandexTranslate список языков в выбранной локали
     */
    private LanguageLocalisation mLanguageLocalisation = null;

    /**
     * DEFAULT_LANG_TO, DEFAULT_LANG_FROM - языки перевода по умолчанию
     */
    private final String DEFAULT_LANG_FROM = "en";
    private final String DEFAULT_LANG_TO = "ru";
    /**
     * mLangFrom - последний выбранный язык, с которого переводим
     */
    private String mLangFrom = DEFAULT_LANG_FROM;
    /**
     * mLangTo - последний выбранный язык, на который переводим
     */
    private String mLangTo = DEFAULT_LANG_TO;

    public String getLangFrom() {
        return mLangFrom;
    }

    public void setLangFrom(String langFrom) {
        mLangFrom = langFrom;
    }

    public String getLangTo() {
        return mLangTo;
    }

    public void setLangTo(String langTo) {
        mLangTo = langTo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sRetrofit = new Retrofit.Builder()
                .baseUrl("https://translate.yandex.net") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        sYandexTranslateApi = sRetrofit.create(YandexTranslateApi.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static YandexTranslateApi getApi() {
        return sYandexTranslateApi;
    }
    public LanguageLocalisation getLanguageLocalisation(){return mLanguageLocalisation;}
    public void setLanguageLocalisation(LanguageLocalisation languageLocalisation){this.mLanguageLocalisation = languageLocalisation;}
}
