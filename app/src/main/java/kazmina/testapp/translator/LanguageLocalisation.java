package kazmina.testapp.translator;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Fina on 21.03.2017.
 */

public class LanguageLocalisation {


    private String[] dirs;
    protected LinkedHashMap<String, String> langs;

    public LinkedHashMap<String, String> getLangs() {
        return langs;
    }

    public void setLangsMap(LinkedHashMap<String, String> langs) {
        this.langs = langs;
    }

    public void setDirs(String[] dirs){this.dirs = dirs;}
    public String[] getDirs(){return dirs;}

}
