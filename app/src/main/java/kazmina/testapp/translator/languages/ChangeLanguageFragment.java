package kazmina.testapp.translator.languages;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Date;
import java.util.Locale;

import kazmina.testapp.translator.FragmentCommunicator;
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

public class ChangeLanguageFragment extends Fragment implements  LanguagesHolder {
    private String mCurrentLangCode;
    private String TAG = "ChangeLanguageFragment";
    private int mTargetView;
    private FragmentCommunicator mListener;
    private DBProvider mDBProvider;
    private DBNotificationManager mDBNotificationManager;
    RecyclerView mRecyclerViewLangs;
    private String mLocale;

    private DBNotificationManager.Listener mDbListener = new DBNotificationManager.Listener(){
        @Override
        public void onDataUpdated() {
            populateList();
        }
    };


    public static ChangeLanguageFragment getInstance(int viewID, String selectedLang){
        Bundle params = new Bundle();
        params.putInt(TARGET_VIEW, viewID);
        params.putString(SELECTED_LANG_VALUE, selectedLang);
        ChangeLanguageFragment changeLanguageFragment = new ChangeLanguageFragment();
        changeLanguageFragment.setArguments(params);
        return changeLanguageFragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDBProvider = DBContainer.getProviderInstance(context);
        mDBNotificationManager = DBContainer.getNotificationInstance(context);
        mDBNotificationManager.addListener(mDbListener);
        try {
            mListener = (FragmentCommunicator) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FragmentCommunicator");
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
        mRecyclerViewLangs = (RecyclerView) view.findViewById(R.id.langsRecycler);
        //mRecyclerViewLangs.setOnItemClickListener(this);
        mRecyclerViewLangs.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), mRecyclerViewLangs ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String selectedLangValue = null;
                        String selectedLangTitle = null;
                        Integer selectedLangId = null;
                        try {
                            LanguagesCursorAdapter adapter = (LanguagesCursorAdapter) mRecyclerViewLangs.getAdapter();
                            Cursor c = adapter.getCursor();
                            c.moveToPosition(position);
                            selectedLangTitle =  c.getString(c.getColumnIndex(DBContract.Languages.TITLE));
                            selectedLangValue = c.getString(c.getColumnIndex(DBContract.Languages.CODE));
                            selectedLangId = c.getInt(c.getColumnIndex(DBContract.Languages.ID));
                        }catch (Exception e){
                            Log.d(TAG, "" + e.getMessage());
                        }
                        mDBProvider.setLanguageTimeStamp(selectedLangId, new Date());
                        mListener.onLangSelected(mTargetView, selectedLangValue, selectedLangTitle);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        mRecyclerViewLangs.setAdapter(null);
        populateList();
        return  view;
    }

    private void populateList(){
        mLocale = Locale.getDefault().getLanguage();
        Cursor c = null;
        LanguagesCursorAdapter adapter = new LanguagesCursorAdapter(R.layout.lang_list_item, c, mCurrentLangCode);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewLangs.setLayoutManager(llm);
        mRecyclerViewLangs.setAdapter(adapter);
        mDBProvider.getLanguagesWithUsed(mLocale, new DBProvider.ResultCallback<Cursor>() {
            @Override
            public void onFinished(Cursor result) {
                LanguagesCursorAdapter adapter = (LanguagesCursorAdapter) mRecyclerViewLangs.getAdapter();
                if (adapter == null){
                    adapter = new LanguagesCursorAdapter(R.layout.lang_list_item, result, mCurrentLangCode);
                    //mListViewLangs.setAdapter(new LanguagesAdapter(getContext(), result, 1, mCurrentLangCode));
                    mRecyclerViewLangs.setAdapter(adapter);
                }else{
                    adapter.swapCursor(result);
                }
            }
        });
    }
}
