package kazmina.testapp.translator.translate;

import android.view.View;
import android.widget.TextView;


import kazmina.testapp.translator.R;
import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * обработчик для отображения результата перевода
 */

public class ShowResultAction implements TranslateResultHandler {
    private TextView mResultText;
    private TextView mCopyrightText;
    public ShowResultAction(TextView resultView, TextView copyrightView) {
        super();
        mResultText = (TextView) resultView.findViewById(R.id.textViewResult);
        mCopyrightText = copyrightView;
    }

    @Override
    public boolean processResult(String text, TranslateResult translateResult) {
        if (translateResult != null) {
            mResultText.setText("");
            for (String textPart : translateResult.getText()) {
                mResultText.append(textPart);
            }
            mCopyrightText.setVisibility(View.VISIBLE);
        }else{
            mResultText.setText("");
            mCopyrightText.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public void handleError(Integer code, String message) {
        mResultText.setText(message);
    }
}
