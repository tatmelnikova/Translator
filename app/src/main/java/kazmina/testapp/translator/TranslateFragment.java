package kazmina.testapp.translator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.interfaces.SaveResultAction;
import kazmina.testapp.translator.interfaces.ShowResultAction;
import kazmina.testapp.translator.interfaces.TranslateResultHandler;
import kazmina.testapp.translator.retrofitModels.LanguageLocalisation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * фрагмент основного окна перевода
 */

public class TranslateFragment extends Fragment implements LanguagesHolder{
    private TranslateWatcher mTranslateWatcher;
    private List<TranslateResultHandler> mResultHandlers;
    private String TAG = "TranslateFragment";
    private View mView;

    private SaveResultAction mSaveResultAction;
    private EditText mTranslateText;

    String mLangFrom;
    String mLangTo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        mTranslateText = (EditText) view.findViewById(R.id.editTextInput);
        //при потере фокуса полем ввода текста установить флаг немедленного сохранения результата перевода в истории
        mTranslateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mSaveResultAction.setSaveImmediate(!hasFocus);
            }
        });

        YandexTranslateApi api = TranslatorApplication.getApi();
        String langID = Locale.getDefault().getLanguage();

        api.getLanguages(langID).enqueue(new Callback<LanguageLocalisation>() {
            @Override
            public void onResponse(Call<LanguageLocalisation> call, Response<LanguageLocalisation> response) {
                //Данные успешно пришли, но надо проверить response.body() на null
                if (response.body() == null){
                    Log.d(TAG, "body is null");
                }else{
                    TranslatorApplication app = ((TranslatorApplication) getContext().getApplicationContext());
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

        mView = view;
        return view;
    }
    /**
     * привязывает слушатель к полю ввода текста для перевода
     */
    private void setWatcher(){
        mTranslateText.removeTextChangedListener(mTranslateWatcher);
        mTranslateWatcher = new TranslateWatcher( mLangFrom, mLangTo, mResultHandlers);
        mTranslateText.addTextChangedListener(mTranslateWatcher);

    }

    private void setupCurrentTranslateDirection(){
        TranslatorApplication app = ((TranslatorApplication) getContext().getApplicationContext());
        LanguageLocalisation languageLocalisation = app.getLanguageLocalisation();
        if (languageLocalisation != null) {
            if (mLangFrom != null) {
                Button langFromButton = (Button) mView.findViewById(R.id.langFrom);
                langFromButton.setText(languageLocalisation.getLangs().get(mLangFrom));
            }

            if (mLangTo != null) {
                Button langToButton = (Button) mView.findViewById(R.id.langTo);
                langToButton.setText(languageLocalisation.getLangs().get(mLangTo));
            }
        }
        setWatcher();
    }



    @Override
    public void onPause() {
        super.onPause();
        mSaveResultAction.setSaveImmediate(true);
        mResultHandlers = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mView != null) {
            TextView resultView = (TextView) mView.findViewById(R.id.textViewResult);
            ShowResultAction showResultAction = new ShowResultAction(resultView);
            mSaveResultAction = new SaveResultAction(getContext());
            mResultHandlers = new ArrayList<>();
            mResultHandlers.add(showResultAction);
            mResultHandlers.add(mSaveResultAction);
            setWatcher();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mLangFrom = args.getString(LANG_FROM_VALUE);
            mLangTo = args.getString(LANG_TO_VALUE);
        }
    }

    public void updateLangs(String langFrom, String langTo){
        mLangFrom = langFrom;
        mLangTo = langTo;
        setupCurrentTranslateDirection();
    }
}
