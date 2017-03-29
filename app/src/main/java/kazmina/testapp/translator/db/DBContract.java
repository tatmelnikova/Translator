package kazmina.testapp.translator.db;

/**
 * контракт базы данных
 */

public interface DBContract {
    String DB_NAME = "main.sqlite";
    String HISTORY = "history";
    interface History {
        String ID = "_id";
        String TEXT = "text";
        String RESULT = "result";
        String DIRECTION_FROM = "direction_from";
        String DIRECTION_TO = "direction_to";
    }
}
