package kazmina.testapp.translator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import kazmina.testapp.translator.db.DBBackend;
import kazmina.testapp.translator.db.DBContract;
import kazmina.testapp.translator.db.DBUtils;
import kazmina.testapp.translator.db.TranslatorDBHelper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * тесты для базы данных
 */

@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class DBBackendTest implements DBContract {

    private TranslatorDBHelper helper;
    private SQLiteDatabase db;
    private DBBackend dbBackend;

    @Before
    public void setUp() {
        helper = new TranslatorDBHelper(RuntimeEnvironment.application);
        db = helper.getWritableDatabase();
        dbBackend = new DBBackend(helper);
    }

    @Test
    public void testInsertHistoryItem() {
        dbBackend.insertHistoryItem("мама", "mother", "ru", "en");
        Assert.assertEquals(1, getCount(db, HISTORY));
    }

    @Test
    public void testInsertFavoritesItem(){
        dbBackend.insertFavoritesItem("мама", "mother", "ru", "en");
        Assert.assertEquals(1, getCount(db, FAVORITES));
    }


    @Test
    public void testGetHistoryWithFav(){
        dbBackend.insertHistoryItem("мама", "mother", "ru", "en");
        dbBackend.insertHistoryItem("папа", "father", "ru", "en");
        dbBackend.insertFavoritesItem("мама", "mother", "ru", "en");
        Cursor c = dbBackend.getHistoryWithFav("мама");
        assertNotNull(c);
        c.moveToFirst();
        String text = c.getString(c.getColumnIndex(History.TEXT));
        assertEquals(text, "мама");
        Integer favID = c.getInt(c.getColumnIndex(History.FAV_ID));
        assertNotNull(favID);
    }

    public void testCopyHistoryItemToFavorites(){
        dbBackend.insertHistoryItem("мама", "mother", "ru", "en");
        dbBackend.copyHistoryItemToFavorites(1);
        Assert.assertEquals(1, getCount(db, FAVORITES));

    }
    private int getCount(SQLiteDatabase db, String table) {
        return DBUtils.getResultLongAndClose(
                db.rawQuery("select count(*) from " + table, null)).intValue();
    }

}

