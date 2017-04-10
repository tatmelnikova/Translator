package kazmina.testapp.translator.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import kazmina.testapp.translator.R;
import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;

/**
 * хелпер для работы с базой данных
 */

public class TranslatorDBHelper extends SQLiteOpenHelper implements DBContract{
    private static final int DB_VERSION = 1;
    private final Context mHelperContext;
    private final String TAG = "TranslatorDBHelper";
    private SQLiteDatabase mSQLiteDatabase;

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        mSQLiteDatabase = db;
        /*db.execSQL("DROP TABLE IF EXISTS " + HISTORY );
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITES );
        onCreate(db);
        */
    }

    public TranslatorDBHelper (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mHelperContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        mSQLiteDatabase = db;
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

        String langsSql =
                "CREATE TABLE " + LANGUAGES + "(" +
                    Languages.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Languages.CODE + " STRING NOT NULL, " +
                        Languages.TITLE + " STRING NOT NULL " +
        ")";
        db.execSQL(langsSql);
        loadInterfaceLanguages();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + HISTORY);
        onCreate(db);
    }
    private void loadInterfaceLanguages() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadLanguages();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void loadLanguages() throws IOException {
        final Resources resources = mHelperContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.languages_localisation);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            Gson gson = new Gson();
            LanguageLocalisation localisation = gson.fromJson(reader, LanguageLocalisation.class);
            LinkedHashMap<String, String> langsMap = localisation.getLangs();
            for (Map.Entry<String, String> language : langsMap.entrySet()){
                long insertId = addLanguage(language.getKey(), language.getValue());
                Log.d(TAG, language.getKey() + " " + language.getValue());
                if (insertId < 0) {
                    Log.e(TAG, "unable to add language: " + language.getKey());
                }
            }
        } finally {
            reader.close();
        }
    }

    private long addLanguage(String code, String title) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(Languages.CODE, code);
        initialValues.put(Languages.TITLE, title);
        return mSQLiteDatabase.insert(LANGUAGES, null, initialValues);
    }
}
