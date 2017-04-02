package kazmina.testapp.translator.db;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.VisibleForTesting;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 31/07/2016.
 */
public class DBProvider {

    private final DBBackend mDBBackend;
    private final DBNotificationManager mDBNotificationManager;
    private final CustomExecutor mExecutor;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public interface ResultCallback<T> {
        void onFinished(T result);
    }

    DBProvider(Context context) {
        mDBBackend = new DBBackend(context);
        mDBNotificationManager = DBContainer.getNotificationInstance(context);
        mExecutor = new CustomExecutor();
    }

    @VisibleForTesting
    DBProvider(DBBackend DBBackend,
               DBNotificationManager dbNotificationManager,
               CustomExecutor executor) {
        mDBBackend = DBBackend;
        mDBNotificationManager = dbNotificationManager;
        mExecutor = executor;
    }

    public void getHistoryWithFav(final String searchText, final ResultCallback<Cursor> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor c =  mDBBackend.getHistoryWithFav(searchText);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(c);
                    }
                });
            }
        });
    }

    public void insertHistoryItem(final String text, final String result, final String from, final String to) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.insertHistoryItem(text, result, from, to);
                mDBNotificationManager.notifyListeners();
            }
        });
    }

    public void copyHistoryItemToFavorites(final Integer itemId){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.copyHistoryItemToFavorites(itemId);
                mDBNotificationManager.notifyListeners();
            }
        });
    }

    public void removeFromFavoritesById(final Integer itemId){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.removeFromFavoritesByID(itemId);
                mDBNotificationManager.notifyListeners();
            }
        });
    }
    // TODO: make me multi-threaded!
    class CustomExecutor extends ThreadPoolExecutor {
        CustomExecutor() {
            super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }
}