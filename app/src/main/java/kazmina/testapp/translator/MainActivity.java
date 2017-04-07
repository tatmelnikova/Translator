package kazmina.testapp.translator;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kazmina.testapp.translator.interfaces.SaveResultAction;
import kazmina.testapp.translator.interfaces.ShowResultAction;
import kazmina.testapp.translator.interfaces.TranslateResultHandler;
import kazmina.testapp.translator.navigation.BottomNavigationListener;
import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";
    private TranslateWatcher mTranslateWatcher;
    private List<TranslateResultHandler> mResultHandlers;
    private BottomNavigationListener mBottomNavigationListener = new BottomNavigationListener(this);
    private SaveResultAction mSaveResultAction;
    private EditText mTranslateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mBottomNavigationListener);


        mSaveResultAction = new SaveResultAction(this);
        mTranslateText = (EditText) findViewById(R.id.editTextInput);
        mTranslateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mSaveResultAction.setSaveImmediate(!hasFocus);
            }
        });

        YandexTranslateApi api = TranslatorApplication.getApi();
        String langID = Locale.getDefault().getLanguage();
        Log.d(TAG, langID);
        api.getLanguages(langID).enqueue(new Callback<LanguageLocalisation>() {
            @Override
            public void onResponse(Call<LanguageLocalisation> call, Response<LanguageLocalisation> response) {
                //Данные успешно пришли, но надо проверить response.body() на null
                if (response.body() == null){
                    Log.d(TAG, "body is null");
                }else{
                    TranslatorApplication app = ((TranslatorApplication) getApplicationContext());
                    app.setLanguageLocalisation(response.body());
                    setupCurrentTranslateDirection();
                }
            }
            @Override
            public void onFailure(Call<LanguageLocalisation> call, Throwable t) {
                //Произошла ошибка
                Log.d(TAG, "api query failure");
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });

    }

    /**
     * привязывает слушатель к полю ввода текста для перевода
     */
    private void setWatcher(){
        TranslatorApplication app = ((TranslatorApplication) getApplicationContext());
        mTranslateText.removeTextChangedListener(mTranslateWatcher);
        mTranslateWatcher = new TranslateWatcher( app.getLangFrom(), app.getLangTo(), mResultHandlers);
        mTranslateText.addTextChangedListener(mTranslateWatcher);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCurrentTranslateDirection();
        TextView resultView = (TextView)findViewById(R.id.textViewResult);
        ShowResultAction showResultAction = new ShowResultAction(resultView);

        mResultHandlers = new ArrayList<>();
        mResultHandlers.add(showResultAction);
        mResultHandlers.add(mSaveResultAction);
        setWatcher();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSaveResultAction.setSaveImmediate(true);
        mResultHandlers = null;
    }

    private void setupCurrentTranslateDirection(){
        TranslatorApplication app = ((TranslatorApplication) getApplicationContext());
        String langFromID = app.getLangFrom();
        String langToID = app.getLangTo();
        LanguageLocalisation languageLocalisation = app.getLanguageLocalisation();
        if (languageLocalisation != null) {
            if (langFromID != null) {
                Button langFromButton = (Button) findViewById(R.id.langFrom);
                langFromButton.setText(languageLocalisation.getLangs().get(langFromID));
            }

            if (langToID != null) {
                Button langToButton = (Button) findViewById(R.id.langTo);
                langToButton.setText(languageLocalisation.getLangs().get(langToID));
            }
        }
        setWatcher();
    }
    private void showLangsView(Integer viewID){
        String langID = Locale.getDefault().getLanguage();
        Intent intent = new Intent(this, ChangeLangActivity.class);
        intent.putExtra("langID", langID);
        intent.putExtra("viewFromID", viewID);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);

    }

    private void swapTranslateDirections(){
        TranslatorApplication app = ((TranslatorApplication) getApplicationContext());
        String tmp = app.getLangTo();
        app.setLangTo(app.getLangFrom());
        app.setLangFrom(tmp);
        setupCurrentTranslateDirection();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.langFrom:
                showLangsView(R.id.langFrom);
                break;
            case R.id.langTo:
                showLangsView(R.id.langTo);
                break;
            case R.id.swapLang:
                swapTranslateDirections();
                break;
            default:
                break;
        }
    }
}
