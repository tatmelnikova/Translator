package kazmina.testapp.translator.translate;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kazmina.testapp.translator.TranslatorApplication;
import kazmina.testapp.translator.YandexTranslateApi;
import kazmina.testapp.translator.translate.TranslateResultHandler;
import kazmina.testapp.translator.retrofitModels.TranslateResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


    TranslateResult getTranslateResult(){
        return mTranslateResult;
    }

    String getTranslateText(){
        return mTranslateText;
    }
    public void setTranslateDirection(String direction){
        mTranslateDirection = direction;
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
                            YandexTranslateApi api = TranslatorApplication.getApi();
                            api.getTranslate(text, mTranslateDirection).enqueue(new Callback<TranslateResult>() {
                                @Override
                                public void onResponse(Call<TranslateResult> call, Response<TranslateResult> response) {
                                    if (response.body() != null) {
                                        mTranslateResult = response.body();
                                        mTranslateText = text;
                                        mTranslateResultHandler.processResult(text, mTranslateResult);
                                    }
                                }

                                @Override
                                public void onFailure(Call<TranslateResult> call, Throwable t) {
                                    Log.d(TAG, t.getMessage());
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
