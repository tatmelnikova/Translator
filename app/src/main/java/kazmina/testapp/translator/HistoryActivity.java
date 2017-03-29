package kazmina.testapp.translator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import kazmina.testapp.translator.db.DBContract;
import kazmina.testapp.translator.db.DbBackend;

/**
 * история переводов
 */

public class HistoryActivity extends AppCompatActivity {
    private final String TAG = "HistoryActivity";
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_favorites:

                    return true;
                case R.id.navigation_settings:

                    return true;
            }
            return false;
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        String[] from = new String[] {DBContract.History.TEXT, DBContract.History.RESULT};
        int[] to = new int[] { R.id.textViewText, R.id.textViewResult};
        DbBackend backend = new DbBackend(getBaseContext());
        Cursor cursor = backend.getTranslateHistory();
        CursorAdapter cursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.history_item, cursor, from, to );
        //cursor.moveToFirst();
        while (cursor.moveToNext() ){

            Log.d( TAG, cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3));
        }
        ListView historyList = (ListView)findViewById(R.id.historyList);
        historyList.setAdapter(cursorAdapter);
    }
}
