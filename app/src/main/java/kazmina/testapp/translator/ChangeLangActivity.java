package kazmina.testapp.translator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;


/**
 * отображает список доступных для перевода языков с возможностью выбора
 */

public class ChangeLangActivity extends AppCompatActivity implements ListView.OnItemClickListener {
    private final String TAG = "ChangeLangActivity";
    private  String mLangID = null;
    private int mViewFromID;
    private LinkedHashMap<String, String> availableLangs = null;

    private final String LANG_ID_KEY = "LANG_ID";
    private final String LANG_VALUE_KEY = "LANG_VALUE";
    private final String LANG_CHECKED_KEY = "LANG_CHECKED";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_lang);
        mLangID = getIntent().getStringExtra("langID");
        mViewFromID = getIntent().getIntExtra("viewFromID", 0);
        TranslatorApplication app = ((TranslatorApplication) getApplicationContext());
        LanguageLocalisation currentLang = app.getLanguageLocalisation();
        availableLangs = currentLang.getLangs();
        ListView langsList = (ListView) this.findViewById(R.id.langsList);
        ArrayList<Map<String, Object>> data = new ArrayList<>(
                availableLangs.size());
        Map<String, Object> m;
        boolean checked;
        int sPosition = -1;
        int counter = 0;
        for(Map.Entry<String, String> lang : availableLangs.entrySet() ){
            m = new HashMap<>();
            m.put(LANG_ID_KEY, lang.getKey());
            m.put(LANG_VALUE_KEY, lang.getValue());
            checked = (lang.getKey() != null) && (lang.getKey().equals(mLangID));
            m.put(LANG_CHECKED_KEY, checked);
            data.add(m);
            if (checked){
                Log.d(TAG, m.toString());
                sPosition = counter;
            }
            counter++;
        }

        String[] from = {  LANG_VALUE_KEY , LANG_CHECKED_KEY};
        int[] to = { R.id.checkedTextViewLanguage, R.id.checkedTextViewLanguage};
        SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.lang_list_item, from, to);
        langsList.setAdapter(sAdapter);
        langsList.setOnItemClickListener(this);
        langsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (sPosition > 0) langsList.setItemChecked(sPosition, true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Log.d(TAG, String.valueOf(position));
        ListView langsList = (ListView) this.findViewById(R.id.langsList);
        SparseBooleanArray checkedItems = langsList.getCheckedItemPositions();
        Log.d(TAG, String.valueOf(checkedItems));
        mLangID = new ArrayList<>(availableLangs.keySet()).get(position);
        TranslatorApplication app = ((TranslatorApplication) getApplicationContext());

        if (mViewFromID == R.id.langFrom){
            app.setLangFrom(mLangID);
        }else if(mViewFromID == R.id.langTo){
            app.setLangTo(mLangID);
        }
        Log.d(TAG, mLangID);
        finish();

    }
}
