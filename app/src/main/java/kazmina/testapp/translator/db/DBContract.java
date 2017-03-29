package kazmina.testapp.translator.db;

/**
 * контракт базы данных
 */

interface DBContract {
    String DB_NAME = "main.sqlite";
    String HISTORY = "history";
    interface History {
        String ID = "rowid";
        String TEXT = "text";
        String RESULT = "result";
        String DIRECTION_FROM = "direction_from";
        String DIRECTION_TO = "direction_to";
    }
}
