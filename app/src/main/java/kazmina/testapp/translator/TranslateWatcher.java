package kazmina.testapp.translator;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kazmina.testapp.translator.interfaces.TranslateResultHandler;
import kazmina.testapp.translator.retrofitModels.TranslateResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * слушатель полей ввода текста для перевода
 */

class TranslateWatcher implements TextWatcher{
    private Timer mTimer = new Timer();
    private final String TAG = "TranslateWatcher";
    private String mTranslateDirection;
    private List<TranslateResultHandler> mHandlerList;

    private final long mDelay = 1000;
    private final String mDelimeter = "-";


    /**
     * @param langFrom - идентификатор языка, с которого переводим
     * @param langTo - идентификатор языка, на который переводим
     * @param handlerList - список обработчиков результата перевода
     */
    TranslateWatcher(String langFrom, String langTo, List<TranslateResultHandler> handlerList) {
        super();
        mHandlerList = handlerList;
        mTranslateDirection = langFrom.concat(mDelimeter).concat(langTo);
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
                                    if (response.body() != null){
                                        for (TranslateResultHandler handler : mHandlerList){
                                            handler.processResult(response.body());
                                        }
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
    }
}
