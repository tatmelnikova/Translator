package kazmina.testapp.translator.translate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kazmina.testapp.translator.R;
import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * обработчик для копирования текста результата перевода
 */

public class CopyTextAction implements TranslateResultHandler, View.OnClickListener {
    private ImageView mCopy;
    private TranslateResult mTranslateResult;
    private Context mContext;
    public CopyTextAction(ImageView copy, Context context) {
        super();
        mCopy = copy;
        mContext = context;
        mCopy.setOnClickListener(this);
        int color = ResourcesCompat.getColor(mContext.getResources(), R.color.colorDisabled, null);
        mCopy.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public boolean processResult(String text, TranslateResult translateResult) {
        mTranslateResult = translateResult;
        if (translateResult != null) {
            mCopy.setVisibility(View.VISIBLE);
        }else{
            mCopy.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public void handleError(Integer code, String message) {
        mCopy.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(mTranslateResult.getPlainText(), mTranslateResult.getPlainText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, R.string.text_copied, Toast.LENGTH_SHORT).show();
    }
}
