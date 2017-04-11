package kazmina.testapp.translator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kazmina.testapp.translator.retrofitModels.TranslateResult;

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
     * выбирает историю перевода с фильтрацией по тексту + ID элемента в избранном, если он там есть
     * * @param searchText - текст для поиска
     * @return курсор
     */
     Cursor getHistoryWithFav(String searchText){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = null;
        try {
            db.beginTransaction();
            String[] columns = new String[]{History.ID, History.TEXT, History.RESULT, History.DIRECTION_FROM, History.DIRECTION_TO, History.FAV_ID};
            String orderBy = History.ID + " DESC";
            String where = null; String[] args = null;
            if (searchText != null) {
                where = History.TEXT + " like ? OR " + History.RESULT + "  like ? ";
                args = new String[]{"%".concat(searchText).concat("%")};
            }
            c = db.query(HISTORY_WITH_FAV, columns, where, args, null, null, orderBy);
            if (c != null) {
                c.moveToFirst();
            }
        }catch (Exception e){
            Log.d(TAG, "msg=" + e.getMessage());
        }finally {
            db.endTransaction();
        }
        //showLangs();
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

    /**
     * выборка записей из избранного с фильтрацией по тексту
     * @param searchText - текст для поиска
     * @return курсор
     */
    Cursor getFav(String searchText){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = null;
        try {
            String[] columns = new String[]{History.ID, History.TEXT, History.RESULT, History.DIRECTION_FROM, History.DIRECTION_TO, History.FAV_ID};
            String orderBy = History.ID + " DESC";
            String where = null;
            String[] args = null;
            if (searchText != null) {
                where = "(" + History.TEXT + " like ? OR " + History.RESULT + "  like ? ) AND " + History.FAV_ID + " IS NOT NULL";
                args = new String[]{"%".concat(searchText).concat("%")};
            } else {
                where = History.FAV_ID + " IS NOT NULL";
            }
            c = db.query(HISTORY_WITH_FAV, columns, where, args, null, null, orderBy);
            if (c != null) {
                c.moveToFirst();
            }
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
        }
        return c;
    }

    Cursor getLanguages(String locale){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = null;
        try{
            String[] columns = new String[]{Languages.ID, Languages.CODE, Languages.TITLE};
            String orderBy = Languages.TITLE +" ASC";
            String where = Languages.LOCALE +" = ?";
            String args[] = new String[]{locale};
            c = db.query(LANGUAGES, columns, where, args, null, null, orderBy);
            if (c != null) c.moveToFirst();
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
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

    /**
     * копирует запись из истории в избранное
     * @param itemID - идентификатор нужной записи
     */
    void copyHistoryItemToFavorites(int itemID){
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

    /**
     * @param itemID - идентификатор записи, которую удаляем из избранного
     */
    void removeFromFavoritesByID(int itemID){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(FAVORITES, "_id=?", new String[]{String.valueOf(itemID)});
            db.setTransactionSuccessful();
            db.endTransaction();
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    public void showHistory(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        String sql = "SeLECT * FROM " + HISTORY;
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

    public void showLangs(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        String sql = "SeLECT * FROM " + LANGUAGES;
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(TAG, "langs count=" + cursor.getCount());
        while (cursor.moveToNext()) {
            int i = 0;
            while (i < cursor.getColumnCount()) {
                //Log.d(TAG, String.valueOf(i) + " = " + cursor.getString(i));
                i++;
            }

        }
        cursor.close();
        db.endTransaction();
    }
    boolean resultIsValid(String text, TranslateResult translateResult){
        ResultChecker checker = new ResultChecker();
        return checker.resultIsValid(text, translateResult);
    }

    /**
     * очистка таблицы history
     */
    public void clearHistory(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        db.execSQL("delete from "+ HISTORY);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /**
     * очистка таблицы избранного
     */
    void clearFavorites(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        db.execSQL("delete from "+ FAVORITES);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Cursor updateLanguagesList(String locale, HashMap<String, String> languagesMap){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = null;
        showLangs();
        Time now = new Time(); now.setToNow();
        Log.d(TAG, "update languages from api " + String.valueOf(now.toMillis(true)));
        try{

            List<String> langCodes = new ArrayList<>();
            for(String langCode : languagesMap.keySet()){
                langCodes.add(langCode);
            }
            String codes = "\"" + TextUtils.join("\",\"", langCodes) + "\"";
            Log.d(TAG, codes);
            String[] args = new String[]{locale, codes};
            String where = Languages.LOCALE +" =  \""+ locale+ "\"  AND CODE NOT IN ( "+ codes +" )";
            db.delete(LANGUAGES, where, null);

            showLangs();
            /*
"af","am","ar","az","ba","be","bg","bn","bs","ca","ceb","cs","cy","da","de","el","en","eo","es","et","eu","fa","fi","fr","ga","gd","gl","gu","he","hi","hr","ht","hu","hy","id","is","it","ja","jv","ka","kk","km","kn","ko","ky","la","lb","lo","lt","lv","mg","mhr","mi","mk","ml","mn","mr","mrj","ms","mt","my","ne","nl","no","pa","pap","pl","pt","ro","ru","si","sk","sl","sq","sr","su","sv","sw","ta","te","tg","th","tl","tr","tt","udm","uk","ur","uz","vi","xh","yi","zh"


            String[] columns = new String[]{Languages.ID, Languages.CODE, Languages.TITLE};
            String orderBy = Languages.CODE +" ASC";

            Cursor c = db.query(LANGUAGES, columns, where, args, null, null, orderBy);
            */
        }catch (Exception e){

        }finally {
            addLanguages(locale, languagesMap);
            showLangs();
        }

        return c;
    }

    public void addLanguages(String locale, HashMap<String, String> languagesMap){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            for(Map.Entry<String, String> langEntry : languagesMap.entrySet()) {
                ContentValues values = new ContentValues();
                values.put(Languages.CODE, langEntry.getKey());
                values.put(Languages.TITLE, langEntry.getValue());
                values.put(Languages.LOCALE, locale);
                long insertId = db.insertWithOnConflict(LANGUAGES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (insertId >= 0) {
                    Log.d(TAG, "added " + values.toString());
                }else{
                    Log.d(TAG, "failed " + values.toString());
                }
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
        }finally {
            db.endTransaction();
        }

    }
    /**
     * класс для проверки валидности результата перевода
     */

    private class ResultChecker implements DBContract {
        private final String TAG = "ResultChecker";

        boolean resultIsValid(String text, TranslateResult translateResult){
            return resultHasText(text, translateResult) && resultIsUnique(text, translateResult);
        }
        boolean resultHasText(String text, TranslateResult translateResult){
            boolean result = true;
            if (
                    isEmpty(text)
                    || isEmpty(translateResult.getLang())
                    || isEmpty(translateResult.getPlainText())
                ){
                result = false;
                Log.d(TAG, "result is empty");
            }else{
                Log.d(TAG, "result is not empty");
            }
            return result;
        }

        private boolean isEmpty(String text){
            boolean isEmpty = true;
            if (text != null){
               if (text.matches("\\S+")) isEmpty = false;
            }
            return isEmpty;
        }
        boolean resultIsUnique(String text, TranslateResult translateResult){
            boolean isUnique = true;
            Cursor c = null;
            try {
                SQLiteDatabase db = mDBHelper.getWritableDatabase();
                String[] columns = new String[]{History.ID, History.TEXT, History.RESULT, History.DIRECTION_FROM, History.DIRECTION_TO};
                String where = History.TEXT + " = ? AND " + History.RESULT + "  = ? AND " + History.DIRECTION_FROM + "= ? AND " + History.DIRECTION_TO + " =?";
                String[] args = new String[]{text, translateResult.getPlainText(), translateResult.getLangFrom(), translateResult.getLangTo()};
                c = db.query(HISTORY, columns, where, args, null, null, null);
                if (c.getCount() > 0) {
                    isUnique = false;
                }
                c.close();
            }catch (Exception e){
                Log.d(TAG, ""+e.getMessage());
            }finally {
                if (c!=null) c.close();
            }
            return isUnique;
        }
    }
}
