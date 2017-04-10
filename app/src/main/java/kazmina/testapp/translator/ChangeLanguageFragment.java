package kazmina.testapp.translator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kazmina.testapp.translator.interfaces.LanguageListener;
import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;

/**
 * фрагмент для отображения списка доступных языков
 */

public class ChangeLanguageFragment extends Fragment implements AdapterView.OnItemClickListener, LanguagesHolder {
    private LanguageLocalisation mLanguageLocalisation;
    private final String LANG_ID_KEY = "LANG_ID";
    private final String LANG_VALUE_KEY = "LANG_VALUE";
    private final String LANG_CHECKED_KEY = "LANG_CHECKED";

    private String mCurrentLangCode;
    private String TAG = "ChangeLanguageFragment";
    private int mTargetView;
    private LanguageListener mListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (LanguageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LanguagesHolder");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurrentLangCode = bundle.getString(SELECTED_LANG_VALUE);
            mTargetView = bundle.getInt(TARGET_VIEW);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_lang, container, false);
        TranslatorApplication app = ((TranslatorApplication) getContext().getApplicationContext());
        mLanguageLocalisation = app.getLanguageLocalisation();
        ListView langsList = (ListView) view.findViewById(R.id.langsList);
        ArrayList<Map<String, Object>> data = new ArrayList<>(
                mLanguageLocalisation.getLangs().size());
        Map<String, Object> m;
        boolean checked;
        int sPosition = -1;
        int counter = 0;
        for(Map.Entry<String, String> lang : mLanguageLocalisation.getLangs().entrySet() ){
            m = new HashMap<>();
            m.put(LANG_ID_KEY, lang.getKey());
            m.put(LANG_VALUE_KEY, lang.getValue());
            checked = (lang.getKey() != null) && (lang.getKey().equals(mCurrentLangCode));
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
        SimpleAdapter sAdapter = new SimpleAdapter(getContext(), data, R.layout.lang_list_item, from, to);
        langsList.setAdapter(sAdapter);
        langsList.setOnItemClickListener(this);
        langsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (sPosition > 0) langsList.setItemChecked(sPosition, true);
        return  view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView langsList = (ListView) getView().findViewById(R.id.langsList);
        SparseBooleanArray checkedItems = langsList.getCheckedItemPositions();
        Log.d(TAG, String.valueOf(checkedItems));
        String selectedLang = new ArrayList<>(mLanguageLocalisation.getLangs().keySet()).get(position);
        mListener.changeLanguage(mTargetView, selectedLang);
    }
}
