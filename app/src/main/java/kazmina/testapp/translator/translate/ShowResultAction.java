package kazmina.testapp.translator.translate;

import android.widget.TextView;


import kazmina.testapp.translator.R;
import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * обработчик для отображения результата перевода
 */

public class ShowResultAction implements TranslateResultHandler {
    private TextView mResultText;
    public ShowResultAction(TextView resultView) {
        super();
        mResultText = (TextView) resultView.findViewById(R.id.textViewResult);
    }

    @Override
    public boolean processResult(String text, TranslateResult translateResult) {
        if (text != null && text.length() > 0) {
            mResultText.setText("");
            if (translateResult != null) {
                for (String textPart : translateResult.getText()) {
                    mResultText.append(textPart);
                }
            }
        }else{
            mResultText.setText("");
        }
        return true;
    }
}
