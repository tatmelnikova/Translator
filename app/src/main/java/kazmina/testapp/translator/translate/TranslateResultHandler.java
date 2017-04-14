package kazmina.testapp.translator.translate;

import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * интерфейс для обработчиков результата перевода
 */

public interface TranslateResultHandler {
    TranslateResult translateResult = null;
    boolean processResult(String text, TranslateResult translateResult);
}
