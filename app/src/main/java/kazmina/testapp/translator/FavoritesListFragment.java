package kazmina.testapp.translator;

import android.database.Cursor;
import android.util.Log;

import kazmina.testapp.translator.db.DBProvider;

/**
 * фрагмент, содержащий список избранного
 */

public class FavoritesListFragment extends HistoryListFragment {
    private String TAG = "FavoritesListFragment";
    public void refreshHistoryData(){
        Log.d(TAG, "refresh");
        mDBProvider.getFavorites(mSearchText, new DBProvider.ResultCallback<Cursor>() {
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
