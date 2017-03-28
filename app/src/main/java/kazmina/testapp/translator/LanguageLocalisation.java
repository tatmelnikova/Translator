package kazmina.testapp.translator;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * класс для хранения списка доступных языков с переводами их названий
 */

class LanguageLocalisation {


    private String[] dirs;
    private LinkedHashMap<String, String> langs;

     LinkedHashMap<String, String> getLangs() {
        return langs;
    }

    public void setLangsMap(LinkedHashMap<String, String> langs) {
        this.langs = langs;
    }

    public void setDirs(String[] dirs){this.dirs = dirs;}
    public String[] getDirs(){return dirs;}

}
