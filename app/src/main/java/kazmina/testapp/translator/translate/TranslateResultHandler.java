package kazmina.testapp.translator.translate;

import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * интерфейс для обработчиков результата перевода
 */

public interface TranslateResultHandler {
    boolean processResult(String text, TranslateResult translateResult);
    void handleError(Integer code, String message);
}
