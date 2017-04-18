package kazmina.testapp.translator.translate;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kazmina.testapp.translator.TranslatorApplication;
import kazmina.testapp.translator.YandexTranslateApi;
import kazmina.testapp.translator.api.APIError;
import kazmina.testapp.translator.translate.TranslateResultHandler;
import kazmina.testapp.translator.retrofitModels.TranslateResult;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * слушатель полей ввода текста для перевода
 */

public class TranslateWatcher implements TextWatcher{
    private Timer mTimer = new Timer();
    private final String TAG = "TranslateWatcher";
    private String mTranslateDirection;
    private TranslateResult mTranslateResult = null;
    private String mTranslateText = null;
    private List<TranslateResultHandler> mHandlerList;

    private final long mDelay = 1000;
    private final String mDelimeter = "-";
    private TranslateResultHandler mTranslateResultHandler;

    /**
     * @param langFrom - идентификатор языка, с которого переводим
     * @param langTo - идентификатор языка, на который переводим
     * @param handler - обработчик результата перевода
     */
    public TranslateWatcher(@NonNull String langFrom, @NonNull String langTo, TranslateResultHandler handler) {
        super();
        mTranslateResultHandler = handler;
        mTranslateDirection = langFrom.concat(mDelimeter).concat(langTo);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * @param s - поле ввода, к которому привязан TranslateWatcher
     *
     * отправляет запрос на перевод текста при его изменении, при получении ответа отображает в заданном
     * поле ввода
     */
    @Override
    public void afterTextChanged(Editable s) {
        final String text = s.toString();
        if (text.length() > 0) {
            mTimer.cancel();
            mTimer = new Timer();
            mTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            final Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("https://translate.yandex.net") //Базовая часть адреса
                                    .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                                    .build();
                            final YandexTranslateApi api = retrofit.create(YandexTranslateApi.class);

                            api.getTranslate(text, mTranslateDirection).enqueue(new Callback<TranslateResult>() {
                                @Override
                                public void onResponse(Call<TranslateResult> call, Response<TranslateResult> response) {
                                    if (response.isSuccessful()) {
                                        if (response.body() != null) {
                                            mTranslateResult = response.body();
                                            mTranslateText = text;
                                            mTranslateResultHandler.processResult(text, mTranslateResult);
                                        }
                                    }else{
                                        try {
                                            //  mTranslateResultHandler.handleError(null, response.errorBody().string());
                                            Converter<ResponseBody, APIError> errorConverter =
                                                    retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
                                            APIError error = errorConverter.convert(response.errorBody());
                                            mTranslateResultHandler.handleError(error.getStatusCode(), error.getMessage());
                                            //mTranslateResultHandler.handleError(null, response.errorBody().string());
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<TranslateResult> call, Throwable t) {
                                    mTranslateResultHandler.handleError(null,null);
                                }
                            });
                        }
                    },
                    mDelay
            );
        } else {
            //если поле ввода было очищено, передадим обработчикам в качестве результата перевода null
            mTimer.cancel();
            mTranslateResultHandler.processResult(null, null);
        }
    }
}
