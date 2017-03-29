package kazmina.testapp.translator.interfaces;

import android.content.Context;
import android.widget.TextView;


import kazmina.testapp.translator.TranslateResult;

/**
 * обработчик для отображения результата перевода
 */

public class ShowResultAction implements  TranslateResultHandler {
    private Context mContext;
    private TextView mResultView;

    public ShowResultAction(TextView resultView) {
        super();
        mResultView = resultView;
    }

    @Override
    public boolean processResult(TranslateResult translateResult) {
        mResultView.setText("");
        if (translateResult != null){
            for (String textPart : translateResult.getText()){
                mResultView.append(textPart);
            }
        }
        return true;
    }
}
