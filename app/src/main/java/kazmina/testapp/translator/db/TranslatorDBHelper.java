package kazmina.testapp.translator.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * хелпер для работы с базой данных
 */

public class TranslatorDBHelper extends SQLiteOpenHelper implements DBContract{
    private static final int DB_VERSION = 1;

    public TranslatorDBHelper (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + HISTORY + "(" +
            History.TEXT + " STRING NOT NULL, " +
            History.RESULT + " STRING NOT NULL " +
            History.DIRECTION_FROM + "STRING NOT NULL" +
            History.DIRECTION_TO + "STRING NOT NULL" +
        ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + HISTORY);
        onCreate(db);
    }
}
