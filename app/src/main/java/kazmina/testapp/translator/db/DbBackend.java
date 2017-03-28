package kazmina.testapp.translator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * апи базы данных
 */

public class DbBackend implements DBContract {
    private final TranslatorDBHelper mDBHelper;
    DbBackend(Context context) {
        mDBHelper = new TranslatorDBHelper(context);
    }
    DbBackend(TranslatorDBHelper dbOpenHelper) {
        mDBHelper = dbOpenHelper;
    }
    public Cursor getTranslateHistory() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String[] columns = new String[] { History.ID , History.TEXT, History.RESULT, History.DIRECTION_FROM, History.DIRECTION_TO};
        String orderBy = History.ID + " DESC";
        Cursor c = db.query(HISTORY, columns,
                null, null, null, null, orderBy);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public boolean insertHistoryItem(String text, String result, String from, String to){
        /*
        * @todo: проверка на существование записи
        * */
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        boolean inserted = false;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(History.TEXT, text);
            values.put(History.RESULT, result);
            values.put(History.DIRECTION_FROM, from);
            values.put(History.DIRECTION_TO, to);
            db.insert(HISTORY, null, values);
            db.setTransactionSuccessful();
            inserted = true;
        } catch (Exception e){
            inserted = false;
        }finally {
            db.endTransaction();
        }
        return inserted;
    }
}
