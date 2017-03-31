package kazmina.testapp.translator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * апи базы данных
 */

public class DBBackend implements DBContract {
    private String TAG = "DBBackend";
    private final TranslatorDBHelper mDBHelper;
    public DBBackend(Context context) {
        mDBHelper = new TranslatorDBHelper(context);
    }
    public DBBackend(TranslatorDBHelper dbOpenHelper) {
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

    /**
     * выбирает историю перевода + ID элемента в избранном, если он там есть
     * @return курсор
     */
    public Cursor getHistoryWithFav(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = null;
        try {
            db.beginTransaction();
            String[] columns = new String[]{History.ID, History.TEXT, History.RESULT, History.DIRECTION_FROM, History.DIRECTION_TO, History.FAV_ID};
            String orderBy = History.ID + " DESC";
            c = db.query(HISTORY_WITH_FAV, columns, null, null, null, null, orderBy);
            if (c != null) {
                c.moveToFirst();
            }
        }catch (Exception e){
            Log.d(TAG, "msg=" + e.getMessage());
        }finally {
            db.endTransaction();
        }
        return c;
    }


    public void showFav(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        String sql = "SeLECT * FROM " + FAVORITES;
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(TAG, "count=" + cursor.getCount());
        while (cursor.moveToNext()) {
            int i = 0;
            while (i < cursor.getColumnCount()) {
                Log.d(TAG, String.valueOf(i) + " = " + cursor.getString(i));
                i++;
            }

        }
        cursor.close();
        db.endTransaction();
    }
    public Cursor getFav(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String[] columns = new String[] { Favorites.ID , Favorites.TEXT, Favorites.RESULT, Favorites.DIRECTION_FROM, Favorites.DIRECTION_TO};
        String orderBy = Favorites.ID + " DESC";
        Cursor c = db.query(FAVORITES, columns, null, null, null, null, orderBy);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    /**
     * добавляет результат перевода в избранное
     * @param text - исходный текст
     * @param result - результат перевода
     * @param from - язык, с которого переводим
     * @param to - язык, на который переводим
     * @return true, если вставка прошла успешно
     */
    public boolean insertHistoryItem(String text, String result, String from, String to){
        /*
        * @todo: проверка на существование записи
        * */
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        boolean inserted = false;
        try {
            db.beginTransaction();
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

    public void copyHistoryItemToFavorites(int itemID){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        String sql = ("INSERT INTO "+ FAVORITES + "("
                    + Favorites.TEXT + ", "
                    + Favorites.RESULT + ", "
                    + Favorites.DIRECTION_FROM + ", "
                    + Favorites.DIRECTION_TO
                + ") "
                + " SELECT "
                    + HISTORY + "."+ History.TEXT + ", "
                    + HISTORY + "."+ History.RESULT + ", "
                    + HISTORY + "."+ History.DIRECTION_FROM + ", "
                    + HISTORY + "."+ History.DIRECTION_TO
                +" FROM " + HISTORY + " WHERE _id=" + itemID);
        db.execSQL(sql);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void removeFromFavoritesByID(int itemID){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        db.delete(FAVORITES, "_id=?", new String[]{String.valueOf(itemID)});
        db.endTransaction();
    }
}
