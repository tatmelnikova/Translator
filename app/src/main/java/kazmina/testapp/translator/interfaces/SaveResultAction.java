package kazmina.testapp.translator.interfaces;

import android.content.Context;
import android.util.Log;

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
    private boolean mSaveImmediate = false;


    public SaveResultAction(Context context) {
        super();
        mContext = context.getApplicationContext();
    }

    @Override
    public boolean processResult(String text, TranslateResult translateResult) {
        mText = text;
        mTranslateResult = translateResult;
        mSaved = false;
        if (mSaveImmediate) saveHistoryItem();
        return true;
    }

    /**
     * @param immediate = true, если нужно сохранять результат перевода сразу при готовности
     */
    public void setSaveImmediate(boolean immediate){
        mSaveImmediate = immediate;
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
