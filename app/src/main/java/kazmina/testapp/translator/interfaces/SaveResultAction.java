package kazmina.testapp.translator.interfaces;

import android.content.Context;

import kazmina.testapp.translator.TranslateResult;
import kazmina.testapp.translator.db.DbBackend;

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
    public boolean processResult(TranslateResult translateResult) {
        DbBackend backend = new DbBackend(mContext);
        return backend.insertHistoryItem(translateResult.getText()[0],"", translateResult.getLang(), translateResult.getLang());
    }
}
