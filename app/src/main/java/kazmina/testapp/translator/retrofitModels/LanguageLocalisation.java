package kazmina.testapp.translator.retrofitModels;

import java.util.LinkedHashMap;
/**
 * класс для хранения списка доступных языков с переводами их названий
 */

public class LanguageLocalisation {
    private LinkedHashMap<String, String> langs;

    public LinkedHashMap<String, String> getLangs() {
        return langs;
    }

    public void setLangsMap(LinkedHashMap<String, String> langs) {
        this.langs = langs;
    }
}
