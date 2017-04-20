package kazmina.testapp.translator.translate;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kazmina.testapp.translator.retrofitModels.TranslateResult;

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

    private final long mDelay = 500;
    private final String mDelimeter = "-";
    private TranslateQueryInterface mTranslateQuery;

    /**
     * @param langFrom - идентификатор языка, с которого переводим
     * @param langTo - идентификатор языка, на который переводим
     * @param translateQuery - интерфейс запроса перевода
     */
    public TranslateWatcher(@NonNull String langFrom, @NonNull String langTo, TranslateQueryInterface translateQuery) {
        super();
        mTranslateQuery = translateQuery;
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
                            mTranslateQuery.runTranslate(text, mTranslateDirection);
                        }
                    },
                    mDelay
            );
        } else {
            //если поле ввода было очищено, передадим обработчикам в качестве результата перевода null
            mTimer.cancel();
            mTranslateQuery.cancel();
        }
    }
}
