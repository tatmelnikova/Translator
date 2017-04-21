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

    private final String TAG = "TranslateWatcher";
    private TextChangedListener mTextChangedListener;


    /**
     * @param
     */
    public TranslateWatcher(TextChangedListener listener) {
        super();
        mTextChangedListener = listener;
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
        mTextChangedListener.onTextChanged(text);
    }
}
