package kazmina.testapp.translator;

import android.os.Bundle;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import kazmina.testapp.translator.interfaces.LanguageListener;
import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.navigation.BottomNavigationListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, LanguagesHolder, LanguageListener {
    private final String TAG = "MainActivity";

    private final String TRANSLATE_FRAGMENT_TAG = "TRANSLATE_FRAGMENT_TAG";
    private final String CHANGE_LANG_FRAGMENT_TAG = "CHANGE_LANG_FRAGMENT_TAG";

    private String mLangFrom = DEFAULT_LANG_FROM;
    private String mLangTo = DEFAULT_LANG_TO;

    private BottomNavigationListener mBottomNavigationListener = new BottomNavigationListener(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mBottomNavigationListener);
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
            translateFragment = new TranslateFragment();
            translateFragment.setArguments(params);
            ft.add(R.id.fragmentContainer, translateFragment, TRANSLATE_FRAGMENT_TAG);
        }else{
            ((TranslateFragment)translateFragment).updateLangs(mLangFrom, mLangTo);
            ft.show(translateFragment);
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
            default:
                break;
        }
    }

    private void swapTranslateDirections(){
        String tmp = mLangFrom;
        mLangFrom = mLangTo;
        mLangTo = tmp;
        showTranslateFragment();
    }
    @Override
    public void changeLanguage(Integer which, String code) {
        if (which.equals(R.id.langFrom)){
            mLangFrom = code;
        }
        if (which.equals(R.id.langTo)){
            mLangTo = code;
        }
        showTranslateFragment();
    }
}