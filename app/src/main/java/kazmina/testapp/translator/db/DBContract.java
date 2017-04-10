package kazmina.testapp.translator.db;

/**
 * контракт базы данных
 */

public interface DBContract {
    String DB_NAME = "main.sqlite";
    String HISTORY = "history";
    String FAVORITES = "favorites";
    String HISTORY_WITH_FAV = "history_with_fav";
    String LANGUAGES = "languages";

    interface History {
        String ID = "_id";
        String TEXT = "text";
        String RESULT = "result";
        String DIRECTION_FROM = "direction_from";
        String DIRECTION_TO = "direction_to";
        String FAV_ID = "fav_id";
    }

    interface Favorites{
        String ID = "_id";
        String TEXT = "text";
        String RESULT = "result";
        String DIRECTION_FROM = "direction_from";
        String DIRECTION_TO = "direction_to";
    }

    interface Languages{
        String ID = "_id";
        String CODE = "code";
        String TITLE = "title";
    }
}
