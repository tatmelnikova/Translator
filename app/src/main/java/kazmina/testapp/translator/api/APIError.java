package kazmina.testapp.translator.api;

import kazmina.testapp.translator.R;

/**
 * класс для обработки ошибок перевода
 */

public class APIError {
    private int code;
    private String message;

    public APIError() {
    }

    public int getStatusCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setStatusCode(int code){
        this.code = code;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
