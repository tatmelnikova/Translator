package kazmina.testapp.translator.interfaces;

import android.content.Context;

import kazmina.testapp.translator.db.DBBackend;
import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * обработчик для сохранения результата перевода в истории
 */

public class SaveResultAction implements TranslateResultHandler {
    private Context mContext;

    public SaveResultAction(Context context) {
        super();
        mContext = context;
    }

    @Override
    public boolean processResult(String text, TranslateResult translateResult) {
        DBBackend backend = new DBBackend(mContext);
        String[] resultLangs = translateResult.getLang().split("-");
        return backend.insertHistoryItem(text, translateResult.getText()[0], resultLangs[0], resultLangs[1]);
    }
}
