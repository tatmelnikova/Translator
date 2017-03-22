package kazmina.testapp.translator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:

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
        api.getLanguages(langID).enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                //Данные успешно пришли, но надо проверить response.body() на null
                if (response.body() == null){
                    Log.d(TAG, "body is null");
                }else{
                    TranslatorApplication app = ((TranslatorApplication) getApplicationContext());
                    app.setLanguage(response.body());
                }
            }
            @Override
            public void onFailure(Call<Language> call, Throwable t) {
                //Произошла ошибка
                Log.d(TAG, "api query failure");
                Log.d(TAG, t.getMessage());
                t.printStackTrace();
            }
        });

    }

    private void showLangsView(Integer viewID){

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
            default:
                break;
        }
    }
}
