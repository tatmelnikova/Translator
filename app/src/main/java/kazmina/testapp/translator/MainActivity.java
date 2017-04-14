package kazmina.testapp.translator;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import kazmina.testapp.translator.interfaces.LanguageListener;
import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.interfaces.LanguagesUpdater;
import kazmina.testapp.translator.history.HistoryFragment;
import kazmina.testapp.translator.languages.ChangeLanguageFragment;
import kazmina.testapp.translator.translate.TranslateFragment;
import kazmina.testapp.translator.utils.LangsUpdater;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, LanguagesHolder, LanguageListener {
    private final String TAG = "MainActivity";

    private final String TRANSLATE_FRAGMENT_TAG = "TRANSLATE_FRAGMENT_TAG";
    private final String CHANGE_LANG_FRAGMENT_TAG = "CHANGE_LANG_FRAGMENT_TAG";
    private final String SHOW_HISTORY_FRAGMENT_TAG = "SHOW_HISTORY";

    private String mLangFrom = DEFAULT_LANG_FROM;
    private String mLangTo = DEFAULT_LANG_TO;

    private String mLangFromTitle = DEFAULT_LANG_FROM_TITLE;
    private String mLangToTitle = DEFAULT_LANG_TO_TITLE;


    private LanguagesUpdater mLanguagesUpdater = new LangsUpdater();

    private BottomNavigationView.OnNavigationItemSelectedListener mBottomNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showFragment(TRANSLATE_FRAGMENT_TAG);
                    return true;
                case R.id.navigation_favorites:
                    showFragment(SHOW_HISTORY_FRAGMENT_TAG);
                    return true;
                case R.id.navigation_settings:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mBottomNavigationListener);mLanguagesUpdater.update(this);
        restoreFromBundle(savedInstanceState);
        showFragment(TRANSLATE_FRAGMENT_TAG);
    }

    private void showFragment(String activeFragmentTag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        if (allFragments != null && allFragments.size() > 0) {
            for (Fragment fragment : allFragments) {
                if (fragment.isVisible() && !(activeFragmentTag.equals(fragment.getTag()))) {
                    ft.hide(fragment);
                }
            }
        }
        Fragment active = fm.findFragmentByTag(activeFragmentTag);
        if (active == null){
            switch (activeFragmentTag){
                case SHOW_HISTORY_FRAGMENT_TAG:
                    active = HistoryFragment.getInstance();
                    break;
                case TRANSLATE_FRAGMENT_TAG:
                    active = TranslateFragment.getInstance(mLangFrom, mLangTo, mLangFromTitle, mLangToTitle);
                    break;
                default:
                    break;
            }
        }else{
            if (activeFragmentTag.equals(TRANSLATE_FRAGMENT_TAG)) {
                ((TranslateFragment)active).updateLangs(mLangFrom, mLangTo, mLangFromTitle, mLangToTitle);
                if (active.isVisible())((TranslateFragment)active).refreshLangs();
            }
        }

        if (active != null && active.isAdded()){
            ft.show(active);
        }else if (active != null ){
            ft.add( R.id.fragmentContainer, active, activeFragmentTag);
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
        showFragment(TRANSLATE_FRAGMENT_TAG);
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
        showFragment(TRANSLATE_FRAGMENT_TAG);
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