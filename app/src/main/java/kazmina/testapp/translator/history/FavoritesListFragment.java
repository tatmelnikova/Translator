package kazmina.testapp.translator.history;

import android.database.Cursor;
import android.util.Log;

import kazmina.testapp.translator.R;
import kazmina.testapp.translator.db.DBProvider;
import kazmina.testapp.translator.history.HistoryCursorAdapter;
import kazmina.testapp.translator.history.HistoryListFragment;

/**
 * фрагмент, содержащий список избранного
 */

public class FavoritesListFragment extends HistoryListFragment {
    private String TAG = "FavoritesListFragment";
    protected final int DIALOG_TITLE = R.string.dialog_delete_favorites_title;
    @Override
    public void clearList() {
       mDBProvider.clearFavorites();

    }

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
                toggleFooter(result.getCount() > 0);
            }
        });
    }

    protected int getTitle(){
        return DIALOG_TITLE;
    }
}
