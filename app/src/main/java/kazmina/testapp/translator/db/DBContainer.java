package kazmina.testapp.translator.db;

import android.content.Context;

/**
 *
 */
public class DBContainer {

    private static DBProvider sDBProviderInstance;
    public static DBProvider getProviderInstance(Context context) {
        context = context.getApplicationContext();
        if (sDBProviderInstance == null) {
            sDBProviderInstance = new DBProvider(context);
        }
        return sDBProviderInstance;
    }

    private static DBNotificationManager sDbNotificationInstance;
    public static DBNotificationManager getNotificationInstance(Context context) {
        context = context.getApplicationContext();
        if (sDbNotificationInstance == null) {
            sDbNotificationInstance = new DBNotificationManager();
        }
        return sDbNotificationInstance;
    }
}
