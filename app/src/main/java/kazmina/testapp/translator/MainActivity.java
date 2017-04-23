package kazmina.testapp.translator;

import android.content.Context;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import kazmina.testapp.translator.interfaces.FragmentTags;
import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.interfaces.LanguagesUpdaterInterface;
import kazmina.testapp.translator.history.HistoryFragment;
import kazmina.testapp.translator.languages.ChangeLanguageFragment;
import kazmina.testapp.translator.preference.PreferenceFragment;
import kazmina.testapp.translator.translate.TranslateFragment;
import kazmina.testapp.translator.utils.LangsUpdater;


public class MainActivity extends AppCompatActivity implements LanguagesHolder,FragmentTags,  FragmentCommunicator {
    private final String TAG = "MainActivity";
    private LanguagesUpdaterInterface mLanguagesUpdater = new LangsUpdater();

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
                    showFragment(SETTINGS_FRAGMENT_TAG);
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //@todo доделать тесты, привести в порядок стили. и по хорошему переписать все с rxJava
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mBottomNavigationListener);
        //запуск проверки необходимости обновления списка языков
        mLanguagesUpdater.checkNeedUpdate(this);
        if (savedInstanceState == null) showFragment(TRANSLATE_FRAGMENT_TAG);
    }

    /**
     * отображает фрагмент activeFragmentTag и скрывает остальные фрагменты
     * @param activeFragmentTag - тег фрагмента, который будем отображать
     */
    private void showFragment(String activeFragmentTag){
        View langsView = findViewById(R.id.changeLangsContainer);
        langsView.setVisibility(View.GONE);
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
        //если в менеджере фрагментов не найден фрагмент с заданным тегом, создать новый
        if (active == null){
            switch (activeFragmentTag){
                case SHOW_HISTORY_FRAGMENT_TAG:
                    hideKeyboard();
                    active = HistoryFragment.getInstance();
                    break;
                case TRANSLATE_FRAGMENT_TAG:
                    active = TranslateFragment.getInstance(DEFAULT_LANG_FROM, DEFAULT_LANG_TO, DEFAULT_LANG_FROM_TITLE, DEFAULT_LANG_TO_TITLE);
                    break;
                case SETTINGS_FRAGMENT_TAG:
                    hideKeyboard();
                    active = PreferenceFragment.getInstance();
                    break;
                default:
                    break;
            }
        }

        if (active != null && active.isAdded()){
            ft.show(active);
        }else if (active != null ){
            ft.add( R.id.fragmentContainer, active, activeFragmentTag);
        }
        ft.commit();
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * обработчик нажатия кнопки выбора языка. открывает фрагмент со списком языков и передает в него текущий выбранный язык
     * @param viewId - кнопка, для которой будем менять язык
     * @param langValue - текущий выбранный язык
     */
    @Override
    public void onSelectLangButtonClicked(int viewId, String langValue) {
        hideKeyboard();
        ChangeLanguageFragment changeLanguageFragment =  ChangeLanguageFragment.getInstance(viewId, langValue);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(fm.findFragmentByTag(TRANSLATE_FRAGMENT_TAG));
        ft.add(R.id.changeLangsContainer, changeLanguageFragment, CHANGE_LANG_FRAGMENT_TAG);
        ft.commit();
        View langsView = findViewById(R.id.changeLangsContainer);
        langsView.setVisibility(View.VISIBLE);
    }

    /**
     * обработчик выбора языка, выбранный язык передается во фрагмент перевода
     * @param viewId - кнопка, для которой меняем язык
     * @param langValue - код языка
     * @param langTitle - локализованное название языка
     */
    @Override
    public void onLangSelected(int viewId, String langValue, String langTitle) {
        View langsView = findViewById(R.id.changeLangsContainer);
        langsView.setVisibility(View.GONE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        if (allFragments != null && allFragments.size() > 0) {
            for (Fragment fragment : allFragments) {
                if (fragment.isVisible() && !(TRANSLATE_FRAGMENT_TAG.equals(fragment.getTag()))) {
                    ft.hide(fragment);
                }
            }
        }
        TranslateFragment translateFragment = (TranslateFragment) fm.findFragmentByTag(TRANSLATE_FRAGMENT_TAG);
        ft.show(translateFragment);
        translateFragment.setLanguage(viewId, langValue, langTitle);
        ft.commit();
    }
}