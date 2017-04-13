package kazmina.testapp.translator;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CheckableImageButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import kazmina.testapp.translator.interfaces.LanguageListener;
import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.interfaces.LanguagesUpdater;
import kazmina.testapp.translator.navigation.BottomNavigationListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, LanguagesHolder, LanguageListener {
    private final String TAG = "MainActivity";

    private final String TRANSLATE_FRAGMENT_TAG = "TRANSLATE_FRAGMENT_TAG";
    private final String CHANGE_LANG_FRAGMENT_TAG = "CHANGE_LANG_FRAGMENT_TAG";

    private String mLangFrom = DEFAULT_LANG_FROM;
    private String mLangTo = DEFAULT_LANG_TO;

    private String mLangFromTitle = DEFAULT_LANG_FROM_TITLE;
    private String mLangToTitle = DEFAULT_LANG_TO_TITLE;


    private LanguagesUpdater mLanguagesUpdater = new LangsUpdater();

    private BottomNavigationListener mBottomNavigationListener = new BottomNavigationListener(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mBottomNavigationListener);
        mLanguagesUpdater.update(this);
        restoreFromBundle(savedInstanceState);
        showTranslateFragment();
    }

    private void showTranslateFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment changeLangFragment = fm.findFragmentByTag(CHANGE_LANG_FRAGMENT_TAG);
        if (changeLangFragment != null) ft.remove(changeLangFragment);

        Fragment translateFragment = getSupportFragmentManager().findFragmentByTag(TRANSLATE_FRAGMENT_TAG);
        if (translateFragment == null) {
            Bundle params = new Bundle();
            params.putString(LANG_FROM_VALUE, DEFAULT_LANG_FROM);
            params.putString(LANG_TO_VALUE, DEFAULT_LANG_TO);
            params.putString(LANG_FROM_TITLE, DEFAULT_LANG_FROM_TITLE);
            params.putString(LANG_TO_TITLE, DEFAULT_LANG_TO_TITLE);
            translateFragment = new TranslateFragment();
            translateFragment.setArguments(params);
            ft.add(R.id.fragmentContainer, translateFragment, TRANSLATE_FRAGMENT_TAG);
        }else{
            ft.show(translateFragment);
            ((TranslateFragment)translateFragment).updateLangs(mLangFrom, mLangTo, mLangFromTitle, mLangToTitle);

        }
        ft.commit();
    }

    private void showLangsView(Integer viewID, String selectedLang){
        Bundle params = new Bundle();
        params.putInt(TARGET_VIEW, viewID);
        params.putString(SELECTED_LANG_VALUE, selectedLang);
        ChangeLanguageFragment changeLanguageFragment = new ChangeLanguageFragment();
        changeLanguageFragment.setArguments(params);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(fm.findFragmentByTag(TRANSLATE_FRAGMENT_TAG));
        ft.add(R.id.fragmentContainer, changeLanguageFragment, CHANGE_LANG_FRAGMENT_TAG);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.langFrom:
                showLangsView(R.id.langFrom, mLangFrom);
                break;
            case R.id.langTo:
                showLangsView(R.id.langTo, mLangTo);
                break;
            case R.id.swapLang:
                swapTranslateDirections();
                break;
            case R.id.rotate:
                if (!"1".equals(v.getTag())) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    v.setTag("1");
                }else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    v.setTag("0");
                }
                break;
            default:
                break;
        }
    }

    private void swapTranslateDirections(){
        String tmp = mLangFrom;
        mLangFrom = mLangTo;
        mLangTo = tmp;
        tmp = mLangFromTitle;
        mLangFromTitle = mLangToTitle;
        mLangToTitle = tmp;
        showTranslateFragment();
    }
    @Override
    public void changeLanguage(Integer which, String code, String title) {
        if (which.equals(R.id.langFrom)){
            mLangFrom = code;
            mLangFromTitle = title;
        }
        if (which.equals(R.id.langTo)){
            mLangTo = code;
            mLangToTitle = title;
        }
        showTranslateFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LANG_FROM_VALUE, mLangFrom);
        outState.putString(LANG_FROM_TITLE, mLangFromTitle);
        outState.putString(LANG_TO_VALUE, mLangTo);
        outState.putString(LANG_TO_TITLE, mLangToTitle);
    }


    private void restoreFromBundle(Bundle savedInstanceState){
        if (savedInstanceState != null){
            mLangFrom = savedInstanceState.getString(LANG_FROM_VALUE);
            mLangFromTitle = savedInstanceState.getString(LANG_FROM_TITLE);
            mLangTo = savedInstanceState.getString(LANG_TO_VALUE);
            mLangToTitle = savedInstanceState.getString(LANG_TO_TITLE);
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreFromBundle(savedInstanceState);
    }
}