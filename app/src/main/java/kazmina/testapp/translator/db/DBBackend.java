package kazmina.testapp.translator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
     public Cursor getHistoryWithFav(String searchText){
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


    /**
     * ищет запись в таблице избранного по переданному тексту и направлению перевода
     * @param text - текст
     * @param langFrom - язык, с которого переводим
     * @param langTo - язык, на который переводим
     * @return ID найденной записи в таблице избранного, null если ничего не найдено
     */
    Integer getFavoritesID(String text, String langFrom, String langTo){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Integer inFav = null;
        try {
            String[] columns = new String[]{Favorites.ID};
            String orderBy = Favorites.ID + " DESC";

            String where = "(" + Favorites.TEXT + " = ? AND " + Favorites.DIRECTION_FROM + "  = ? ) AND " + Favorites.DIRECTION_TO+ " = ?";
            String[] args = new String[]{text, langFrom, langTo};

            Cursor c = db.query(FAVORITES, columns, where, args, null, null, orderBy);
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    inFav = c.getInt(c.getColumnIndex(Favorites.ID));
                }
                c.close();
            }
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
        }
        return inFav;
    }


    /**
     * выборка списка языков для переданной локали
     * возвращает  продублированные записи последних использованных языков + все языки
     * например:
     * ID   CODE    TITLE       LOCALE  LAST_USED
     * 1    en      Английский  ru      10
     * 2    fr      Французский ru      9
     * 3    al      Албанский   ru      null
     * 4    en      Английский  ru      null
     * 5    fr      Французский ru      null
     * @param locale - код локализации для языков
     * @param used - количество последних использованных языков
     * @return курсор
     */
    Cursor getLanguagesWithUsed(String locale, Integer used){
        if (used == null) used = 3;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = null;
        try {
            String sql =
                    "SELECT * FROM " +
                        "(" +
                            "SELECT " +
                            Languages.ID + ", " +
                            Languages.CODE + ", " +
                            Languages.TITLE + ", " +
                            Languages.LOCALE + ", " +
                            Languages.LAST_USED +
                            " FROM " + LANGUAGES +
                            " WHERE " + Languages.LOCALE  + " = \""+ locale + "\" AND " +Languages.LAST_USED +" IS NOT NULL "+
                            " ORDER BY " + Languages.LAST_USED + " DESC LIMIT " + String.valueOf(used)+
                         ")"+
                    " UNION ALL SELECT * FROM "+
                        "( "+
                            "SELECT " +
                            Languages.ID + ", " +
                            Languages.CODE + ", " +
                            Languages.TITLE + ", " +
                            Languages.LOCALE + ", " +
                            " NULL AS " + Languages.LAST_USED +
                            " FROM " + LANGUAGES +
                            " WHERE " + Languages.LOCALE  + " =  \""+ locale + "\""+
                        ")" +
                    " ORDER BY " + Languages.LAST_USED + " desc, " + Languages.TITLE + " asc "
                    ;
            //если биндить параметры в rawQuery, то итоговый запрос собирается неверно, LIMIT
            //накладывается не на подзапрос, а применяется к результатам внешнего запроса
            //поэтому параметры будут сразу в String sql, не менять!
            c = db.rawQuery(sql,null);
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
        }
        return c;
    }

    /**
     * обновляет дату последнего использования языка
     * @param id - ID языка
     * @param date - дата последнего использования
     */
    void setLanguageTimeStamp(Integer id, Date date){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(Languages.LAST_USED, date.getTime() / 1000); //время в миллисекундах нужно привести к секундам
            db.update(LANGUAGES, values, Languages.ID + " = ?",
                    new String[] { String.valueOf(id) });
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
        }finally {
            db.endTransaction();
        }
    }
    /**
     * добавляет результат перевода в историю
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
     * добавляет результат перевода в избранное
     * @param text - исходный текст
     * @param result - результат перевода
     * @param from - язык, с которого переводим
     * @param to - язык, на который переводим
     * @return true, если вставка прошла успешно
     */
    public boolean insertFavoritesItem(String text, String result, String from, String to){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        boolean inserted = false;
        try {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(Favorites.TEXT, text);
            values.put(Favorites.RESULT, result);
            values.put(Favorites.DIRECTION_FROM, from);
            values.put(Favorites.DIRECTION_TO, to);
            db.insert(FAVORITES, null, values);
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
                Log.d(TAG, String.valueOf(i) + " = " + cursor.getString(i));
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

    /**
     * проверяет, пора ли обновлять список языков для заданной локали
     * @param locale - код локали
     * @param updateFrequency - частота обновления в днях
     * @return true, если локаль нужно обновить
     */
    Boolean checkNeedUpdate( String locale, Integer updateFrequency){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        Boolean needUpdate = true;
        Cursor cursor = null;
        try{
            db.beginTransaction();
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -updateFrequency);
            long shouldBeUpdatedAfter = c.getTimeInMillis() / 1000;
            String[] columns = new String[]{Updates.ID, Updates.LOCALE, Updates.UPDATED};
            String where = Languages.LOCALE + " = ? AND " + Updates.UPDATED + " > ?";
            cursor = db.query(UPDATES, columns, where, new String[]{locale, String.valueOf(shouldBeUpdatedAfter)}, null, null, null);

            if (cursor.moveToFirst()){
               needUpdate = false;
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
        }finally {
            if (cursor != null)cursor.close();
            db.endTransaction();
        }
        return needUpdate;
    }

    /**
     * обновляет список языков для указанной локали
     * @param locale локаль
     * @param languagesMap список языков, полученный от АПИ переводчика
     */
    void updateLanguagesList(String locale, HashMap<String, String> languagesMap){
        showUpdates();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try{
            db.beginTransaction();
            List<String> langCodes = new ArrayList<>();
            for(String langCode : languagesMap.keySet()){
                langCodes.add(langCode);
            }
            String codes = "\"" + TextUtils.join("\",\"", langCodes) + "\"";
            //Log.d(TAG, codes);
            String where = Languages.LOCALE +" =  \""+ locale+ "\"  AND CODE NOT IN ( "+ codes +" )";
            db.delete(LANGUAGES, where, null);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
        }finally {
            db.endTransaction();
        }
        addLanguages(locale, languagesMap);
    }

    /**
     * обновляет дату обновления локали для существующей записи либо вставляет новую запись
     * @param locale код локали
     */
     void setLocaleUpdated(String locale){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String updateTime = String.valueOf(new Date().getTime()/1000);
            ContentValues values = new ContentValues();
            values.put(Updates.UPDATED, updateTime);
            int u = db.update(UPDATES, values, "locale=?", new String[]{locale});
            if (u == 0) {
                values.put(Updates.LOCALE, locale);
                db.insertWithOnConflict(UPDATES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
        }finally {
            db.endTransaction();
        }
        showUpdates();
    }
    private void showUpdates(){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = null;
        try {
            db.beginTransaction();
            c = db.rawQuery("SELECT * FROM " + UPDATES, null);
            while (c.moveToNext()){
                Log.d("0", c.getString(0));
                Log.d("1", c.getString(1));
                Log.d("2", c.getString(2));
            }
        }catch (Exception e){

        }finally {
            if (c!=null) c.close();
            db.endTransaction();
        }
    }

    /** сохраняет переданные языки для указанной локали
     * @param locale код локали
     * @param languagesMap языки в формате en => Английский
     */
    private void addLanguages(String locale, HashMap<String, String> languagesMap){
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            for(Map.Entry<String, String> langEntry : languagesMap.entrySet()) {
                ContentValues values = new ContentValues();
                values.put(Languages.CODE, langEntry.getKey());
                values.put(Languages.TITLE, langEntry.getValue());
                values.put(Languages.LOCALE, locale);
                db.insertWithOnConflict(LANGUAGES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
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
            }
            return result;
        }

        private boolean isEmpty(String text){
            boolean isEmpty = true;
            if (text != null){
               if (!StringUtils.isEmpty(text)) isEmpty = false;
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
