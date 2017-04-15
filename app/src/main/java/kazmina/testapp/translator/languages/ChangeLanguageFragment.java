package kazmina.testapp.translator.languages;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Date;
import java.util.Locale;

import kazmina.testapp.translator.R;
import kazmina.testapp.translator.db.DBContainer;
import kazmina.testapp.translator.db.DBContract;
import kazmina.testapp.translator.db.DBNotificationManager;
import kazmina.testapp.translator.db.DBProvider;
import kazmina.testapp.translator.interfaces.LanguageListener;
import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.languages.LanguagesAdapter;

/**
 * фрагмент для отображения списка доступных языков
 */

public class ChangeLanguageFragment extends Fragment implements AdapterView.OnItemClickListener, LanguagesHolder {
    private String mCurrentLangCode;
    private String TAG = "ChangeLanguageFragment";
    private int mTargetView;
    private LanguageListener mListener;
    private DBProvider mDBProvider;
    private DBNotificationManager mDBNotificationManager;
    ListView mListViewLangs;
    private String mLocale;

    private DBNotificationManager.Listener mDbListener = new DBNotificationManager.Listener(){
        @Override
        public void onDataUpdated() {
            populateList();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDBProvider = DBContainer.getProviderInstance(context);
        mDBNotificationManager = DBContainer.getNotificationInstance(context);
        mDBNotificationManager.addListener(mDbListener);
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
        mListViewLangs = (ListView) view.findViewById(R.id.langsList);
        mListViewLangs.setOnItemClickListener(this);
        populateList();
        return  view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedLangValue = null;
        String selectedLangTitle = null;
        Integer selectedLangId = null;
        try {
            Cursor c = (Cursor) parent.getAdapter().getItem(position);
            selectedLangTitle =  c.getString(c.getColumnIndex(DBContract.Languages.TITLE));
            selectedLangValue = c.getString(c.getColumnIndex(DBContract.Languages.CODE));
            selectedLangId = c.getInt(c.getColumnIndex(DBContract.Languages.ID));
        }catch (Exception e){
            Log.d(TAG, "" + e.getMessage());
        }
        mDBProvider.setLanguageTimeStamp(selectedLangId, new Date());
        mListener.changeLanguage(mTargetView, selectedLangValue, selectedLangTitle);
    }

    private void populateList(){
        mLocale = Locale.getDefault().getLanguage();
        mDBProvider.getLanguages(mLocale, new DBProvider.ResultCallback<Cursor>() {
            @Override
            public void onFinished(Cursor result) {
                CursorAdapter adapter = (CursorAdapter) mListViewLangs.getAdapter();
                if (adapter == null){
                    mListViewLangs.setAdapter(new LanguagesAdapter(getContext(), result, 1, mCurrentLangCode));
                }else{
                    adapter.changeCursor(result);
                }
            }
        });
    }
}
