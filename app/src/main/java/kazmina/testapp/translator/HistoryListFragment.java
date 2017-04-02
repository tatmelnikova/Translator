package kazmina.testapp.translator;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import kazmina.testapp.translator.db.DBContainer;
import kazmina.testapp.translator.db.DBContract;
import kazmina.testapp.translator.db.DBNotificationManager;
import kazmina.testapp.translator.db.DBProvider;

/**
 * фрагмент, содержащий список элементов истории
 */

public class HistoryListFragment extends ListFragment {
    private String TAG = "HistoryListFragment";
    DBProvider mDBProvider;
    private DBNotificationManager mDBNotificationManager;
    private String mSearchText = null;
    private DBNotificationManager.Listener mDbListener = new DBNotificationManager.Listener(){
        @Override
        public void onDataUpdated() {
            refreshHistoryData();
        }
    };
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDBProvider = DBContainer.getProviderInstance(context);
        mDBNotificationManager = DBContainer.getNotificationInstance(context);
        mDBNotificationManager.addListener(mDbListener);
        refreshHistoryData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public HistoryListFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        SearchView historySearchView = (SearchView) view.findViewById(R.id.historySearchView);
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
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = ((HistoryCursorAdapter)l.getAdapter()).getCursor();
        c.moveToPosition(position);
        if (c.isNull(c.getColumnIndex(DBContract.History.FAV_ID))){
            Integer historyID = c.getInt(c.getColumnIndex(DBContract.History.ID));
            mDBProvider.copyHistoryItemToFavorites(historyID);

        }else{
            Integer favID = c.getInt(c.getColumnIndex(DBContract.History.FAV_ID));
            mDBProvider.removeFromFavoritesById(favID);
        }
    }

    public void refreshHistoryData(){
        Log.d(TAG, "refresh");
        mDBProvider.getHistoryWithFav(mSearchText, new DBProvider.ResultCallback<Cursor>() {
            @Override
            public void onFinished(final Cursor result) {
                HistoryCursorAdapter historyCursorAdapter;
                if (getListAdapter() != null) {
                    historyCursorAdapter = ((HistoryCursorAdapter) getListAdapter());
                    historyCursorAdapter.changeCursor(result);
                }else{
                    historyCursorAdapter = new HistoryCursorAdapter(getContext(), result, 1);
                    setListAdapter(historyCursorAdapter);
                }

            }
        });
    }
}
