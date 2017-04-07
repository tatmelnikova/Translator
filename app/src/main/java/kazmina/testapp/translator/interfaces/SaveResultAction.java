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
            final DBProvider provider = DBContainer.getProviderInstance(mContext);
            provider.checkResultValidity(mText, mTranslateResult, new DBProvider.ResultCallback<TranslateResult>() {
               @Override
               public void onFinished(TranslateResult result) {
                   provider.insertHistoryItem(mText, result );
                   mSaved = true;
               }
            });
        }
    }
}
