package kazmina.testapp.translator.db;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.VisibleForTesting;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import kazmina.testapp.translator.retrofitModels.TranslateResult;

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


    public void getFavorites(final String searchText, final ResultCallback<Cursor> callback){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor c = mDBBackend.getFav(searchText);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(c);
                    }
                });
            }
        });
    }

    public void setLanguageTimeStamp(final Integer id, final Date date){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.setLanguageTimeStamp(id, date);
            }
        });
    }

    public void getLanguagesWithUsed(final String locale, final ResultCallback<Cursor> callback){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor c = mDBBackend.getLanguagesWithUsed(locale, 3);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(c);
                    }
                });
            }
        });
    }

    public void checkNeedUpdate(final String locale,  final Integer updateFrequency, final ResultCallback<String> callback ){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Boolean needUpdate = mDBBackend.checkNeedUpdate(locale, updateFrequency);
                    if (needUpdate) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFinished(locale);
                            }
                        });
                    }

            }
        });
    }
    public void updateLanguages(final String locale, final HashMap<String, String> languagesMap, final ResultCallback<Void> callback){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.updateLanguagesList(locale, languagesMap);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(null);
                    }
                });
            }
        });
    }

    public void setLocaleUpdated(final String locale){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.setLocaleUpdated(locale);
            }
        });
    }
    public void insertHistoryItem(final String text, final TranslateResult translateResult) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.insertHistoryItem(text, translateResult.getPlainText(), translateResult.getLangFrom(), translateResult.getLangTo());
                mDBNotificationManager.notifyListeners();
            }
        });
    }


    public void insertFavoritesItem(final String text, final String result, final String langFrom, final String langTo){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.insertFavoritesItem(text, result, langFrom, langTo);
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
    public void getFavoritesId(final String text, final String langFrom, final String langTo, final ResultCallback<Integer> callback){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Integer favId = mDBBackend.getFavoritesID(text, langFrom, langTo);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(favId);
                    }
                });
            }
        });
    }

    public void checkResultValidity(final String text, final TranslateResult translateResult, final ResultCallback<TranslateResult> callback){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean isValid = mDBBackend.resultIsValid(text, translateResult);
                if (isValid) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFinished(translateResult);
                        }
                    });
                }
            }
        });
    }

    public void clearHistory(){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.clearHistory();
                mDBNotificationManager.notifyListeners();
            }
        });
    }

    public void clearFavorites(){
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDBBackend.clearFavorites();
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