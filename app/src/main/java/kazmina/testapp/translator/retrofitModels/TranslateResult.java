package kazmina.testapp.translator.retrofitModels;

import java.io.Serializable;

/**
 * класс для хранения результата перевода
 */

public class TranslateResult implements Serializable{
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

    public String getPlainText(){
        StringBuilder builder = new StringBuilder();
        String separator = System.getProperty ("line.separator");
        int counter = 0;
        for(String s : text) {
            builder.append(s);
            counter++;
            if (counter < text.length)builder.append(separator);
        }
        return builder.toString();
    }
    public void setText(String[] text) {
        this.text = text;
    }

    public  String getLangFrom(){
        String[] resultLangs = lang.split("-");
        return resultLangs[0];
    }
    public String getLangTo(){
        String[] resultLangs = lang.split("-");
        return resultLangs[1];
    }
    private String lang;
    private int code;
    private String[] text;
}
