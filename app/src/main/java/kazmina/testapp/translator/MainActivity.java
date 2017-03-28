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

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";
    private String langFromID = null;
    private String langToID = null;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_favorites:

                    return true;
                case R.id.navigation_settings:

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
                    showCurrentTranslateDirection();
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

    @Override
    protected void onResume() {
        super.onResume();
        showCurrentTranslateDirection();
    }

    private void showCurrentTranslateDirection(){
        TranslatorApplication app = ((TranslatorApplication) getApplicationContext());
        langFromID = app.getLangFrom();
        langToID = app.getLangTo();
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
        String tmp = langToID;
        app.setLangTo(app.getLangFrom());
        app.setLangFrom(tmp);
        showCurrentTranslateDirection();
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
