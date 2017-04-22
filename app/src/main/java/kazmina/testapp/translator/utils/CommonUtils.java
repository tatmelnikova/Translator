package kazmina.testapp.translator.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 *
 */

public class CommonUtils {
    public static boolean checkConnection(Context context){
        boolean isConnected = false;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null){
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        return isConnected;
    }

    /*
    * проверяет, пустой ли текст. если в строке только пробелы, перевод строки либо табуляция, считаем пустым
    * */
    public static boolean stringIsEmpty(String text){
        boolean isEmpty = true;
        if (text != null){
            String tmp = text.replaceAll("\\s", "");
            if (!tmp.isEmpty()){
                isEmpty = false;
            }
        }
        return isEmpty;
    }

    /**
     * удаляет пробельные символы в начале и конце строки
     */
    public static String trimString(String text){
        String newText = null;
        if (text != null){
            newText = text.replaceAll("^(\\s*)", "");
            newText = newText.replaceAll("(\\s*)$", "");
        }
        return newText;
    }
}
