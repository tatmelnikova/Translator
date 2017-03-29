package kazmina.testapp.translator.retrofitModels;

/**
 * класс для хранения результата перевода
 */

public class TranslateResult {
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    private String lang;
    private int code;
    private String[] text;
}
