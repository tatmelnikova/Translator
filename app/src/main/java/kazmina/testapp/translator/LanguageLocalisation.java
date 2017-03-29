package kazmina.testapp.translator;

import java.util.LinkedHashMap;
/**
 * класс для хранения списка доступных языков с переводами их названий
 */

class LanguageLocalisation {
    private LinkedHashMap<String, String> langs;

    LinkedHashMap<String, String> getLangs() {
        return langs;
    }

    public void setLangsMap(LinkedHashMap<String, String> langs) {
        this.langs = langs;
    }
}
