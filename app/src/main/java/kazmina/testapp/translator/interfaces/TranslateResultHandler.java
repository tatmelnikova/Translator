package kazmina.testapp.translator.interfaces;

import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * интерфейс для обработчиков результата перевода
 */

public interface TranslateResultHandler {
    TranslateResult translateResult = null;
    boolean processResult(TranslateResult translateResult);
}
