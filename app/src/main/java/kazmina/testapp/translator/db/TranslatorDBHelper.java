package kazmina.testapp.translator.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * хелпер для работы с базой данных
 */

public class TranslatorDBHelper extends SQLiteOpenHelper implements DBContract{
    private static final int DB_VERSION = 1;

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        /*db.execSQL("DROP TABLE IF EXISTS " + HISTORY );
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES );
        onCreate(db);
        */
    }

    public TranslatorDBHelper (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String historySql = "CREATE TABLE " + HISTORY + "(" +
                History.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                History.TEXT + " STRING NOT NULL, " +
                History.RESULT + " STRING NOT NULL, " +
                History.DIRECTION_FROM + " STRING NOT NULL, " +
                History.DIRECTION_TO + " STRING NOT NULL " +
                ")";
        db.execSQL(historySql);
        String favSql =
                "CREATE TABLE " + FAVORITES + "(" +
                        Favorites.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Favorites.TEXT + " STRING NOT NULL, " +
                        Favorites.RESULT + " STRING NOT NULL, " +
                        Favorites.DIRECTION_FROM + " STRING NOT NULL, " +
                        Favorites.DIRECTION_TO + " STRING NOT NULL " +
                        ")";
        db.execSQL(favSql);

        String sql = ("CREATE VIEW " + HISTORY_WITH_FAV + " AS SELECT "+
                HISTORY + "." + History.ID + ", " +  HISTORY + "." + History.TEXT + ", " +  HISTORY + "." + History.RESULT + ", " +
                HISTORY + "." + History.DIRECTION_FROM + ", " +  HISTORY + "." +  History.DIRECTION_TO + ", " +
                FAVORITES + " . " + Favorites.ID + " as " + History.FAV_ID +
                " FROM " + HISTORY +
                " LEFT JOIN " + FAVORITES +
                " ON " + HISTORY + "." + History.TEXT + " = " + FAVORITES + "." + Favorites.TEXT + " AND " +
                HISTORY + "." + History.DIRECTION_FROM  + " = " + FAVORITES + "." + Favorites.DIRECTION_FROM + " AND " +
                HISTORY + "." + History.DIRECTION_TO + " = " + FAVORITES + "." + Favorites.DIRECTION_TO);
        Log.d("db", sql);
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + HISTORY);
        onCreate(db);
    }
}
