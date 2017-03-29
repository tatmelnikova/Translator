package kazmina.testapp.translator.interfaces;

import kazmina.testapp.translator.TranslateResult;

/**
 * @todo: header
 */

public interface TranslateResultHandler {
    TranslateResult translateResult = null;
    boolean processResult(TranslateResult translateResult);
}
