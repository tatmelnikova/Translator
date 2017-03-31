package kazmina.testapp.translator.db;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.HashSet;

/**
 *
 */
public class DBNotificationManager {

    private HashSet<Listener> mListeners = new HashSet<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mNotifyRunnable = new Runnable() {
        @Override
        public void run() {
            notifyOnUiThread();
        }
    };

    public interface Listener {
        void onDataUpdated();
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

     void notifyListeners() {
        mHandler.removeCallbacks(mNotifyRunnable);
        mHandler.post(mNotifyRunnable);
    }

    private void notifyOnUiThread() {
        for (Listener l : mListeners) l.onDataUpdated();
    }
}