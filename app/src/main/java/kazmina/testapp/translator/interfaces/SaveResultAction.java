package kazmina.testapp.translator.interfaces;

import android.content.Context;

import kazmina.testapp.translator.db.DBContainer;
import kazmina.testapp.translator.db.DBProvider;
import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * обработчик для сохранения результата перевода в истории
 */

public class SaveResultAction implements TranslateResultHandler {
    private Context mContext;
    private TranslateResult mTranslateResult = null;
    private String mText = null;
    private boolean mSaved = false;
    public SaveResultAction(Context context) {
        super();
        mContext = context;
    }

    @Override
    public boolean processResult(String text, TranslateResult translateResult) {
        mText = text;
        mTranslateResult = translateResult;
        mSaved = false;
        return true;
    }

    public void saveHistoryItem(){
        if (mTranslateResult != null && !mSaved) {
            DBProvider provider = DBContainer.getProviderInstance(mContext);
            String[] resultLangs = mTranslateResult.getLang().split("-");
            provider.insertHistoryItem(mText, mTranslateResult.getText()[0], resultLangs[0], resultLangs[1]);
            mSaved = true;
        }
    }
}
