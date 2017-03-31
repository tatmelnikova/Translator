package kazmina.testapp.translator;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import kazmina.testapp.translator.db.DBContainer;
import kazmina.testapp.translator.db.DBContract;
import kazmina.testapp.translator.db.DBNotificationManager;
import kazmina.testapp.translator.db.DBProvider;
import kazmina.testapp.translator.navigation.BottomNavigationListener;

/**
 * история переводов
 */

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "HistoryActivity";
    private DBProvider mDBProvider;
    private DBNotificationManager mDBNotificationManager;
    ListView mHistoryList;
    String mSearchText = null;
    private DBNotificationManager.Listener mDbListener = new DBNotificationManager.Listener(){
        @Override
        public void onDataUpdated() {
            refreshHistoryData();
        }
    };
    private BottomNavigationListener mBottomNavigationListener = new BottomNavigationListener(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mDBProvider = DBContainer.getProviderInstance(this);
        mDBNotificationManager = DBContainer.getNotificationInstance(this);
        mDBNotificationManager.addListener(mDbListener);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mBottomNavigationListener);
        mHistoryList = (ListView)findViewById(R.id.historyList);


        refreshHistoryData();

        ListView.OnItemClickListener listener = new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = ((HistoryCursorAdapter)parent.getAdapter()).getCursor();
                c.moveToPosition(position);
                if (c.isNull(c.getColumnIndex(DBContract.History.FAV_ID))){
                    Integer historyID = c.getInt(c.getColumnIndex(DBContract.History.ID));
                    mDBProvider.copyHistoryItemToFavorites(historyID);

                }else{
                    Integer favID = c.getInt(c.getColumnIndex(DBContract.History.FAV_ID));
                    Log.d("TAG", "favid="+favID);
                    mDBProvider.removeFromFavoritesById(favID);
                }
            }
        };
        mHistoryList.setOnItemClickListener(listener);


        SearchView historySearchView = (SearchView) findViewById(R.id.historySearchView);
        historySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchText = newText;
                refreshHistoryData();
                return true;
            }
        });


    }

    public void refreshHistoryData(){

        Log.d(TAG, "refresh");
        mDBProvider.getHistoryWithFav(mSearchText, new DBProvider.ResultCallback<Cursor>() {
            @Override
            public void onFinished(final Cursor result) {
                HistoryCursorAdapter historyCursorAdapter;
                if (mHistoryList.getAdapter() != null) {
                    historyCursorAdapter = ((HistoryCursorAdapter) mHistoryList.getAdapter());
                    historyCursorAdapter.changeCursor(result);
                }else{
                    historyCursorAdapter = new HistoryCursorAdapter(getBaseContext(), result, 1);
                    mHistoryList.setAdapter(historyCursorAdapter);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, v.toString());
    }
}
