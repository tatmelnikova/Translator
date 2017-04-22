package kazmina.testapp.translator.translate;

import android.content.Context;

import kazmina.testapp.translator.db.DBContainer;
import kazmina.testapp.translator.db.DBProvider;
import kazmina.testapp.translator.retrofitModels.TranslateResult;
import kazmina.testapp.translator.utils.CommonUtils;

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
        mText = CommonUtils.trimString(text);
        mTranslateResult = translateResult;
        mSaved = false;
        if (mSaveImmediate) saveHistoryItem();
        return true;
    }

    @Override
    public void handleError(Integer code, String message) {

    }

    /**
     * @param immediate = true, если нужно сохранять результат перевода сразу при готовности
     */
    public void setSaveImmediate(boolean immediate){
        mSaveImmediate = immediate;
        //после установки флага пробуем сохранить результат перевода, т.к. он мог уже прийти
        saveHistoryItem();
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
