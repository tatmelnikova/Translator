package kazmina.testapp.translator;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.interfaces.SaveResultAction;
import kazmina.testapp.translator.interfaces.ListenFavoritesAction;
import kazmina.testapp.translator.interfaces.ShowResultAction;
import kazmina.testapp.translator.interfaces.TranslateResultHandler;
import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * фрагмент основного окна перевода
 */

public class TranslateFragment extends Fragment implements LanguagesHolder, TranslateResultHandler{
    private TranslateWatcher mTranslateWatcher;
    private List<TranslateResultHandler> mResultHandlers;
    TranslateResult mTranslateResult = null;
    String mTranslateText = null;
    private String TAG = "TranslateFragment";
    private View mView;
    private String mLangFrom;
    private String mLangTo;

    private String mLangFromTitle;
    private String mLangToTitle;
    private SaveResultAction mSaveResultAction;
    private ShowResultAction mShowResultAction;
    private EditText mEditTextTranslate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        mView = view;
        mEditTextTranslate = (EditText) view.findViewById(R.id.editTextInput);
        return view;
    }


    /**
     * привязывает слушатель к полю ввода текста для перевода
     */
    private void setWatcher(){
        if (mEditTextTranslate != null) {
            mEditTextTranslate.removeTextChangedListener(mTranslateWatcher);
            mTranslateWatcher = new TranslateWatcher(mLangFrom, mLangTo, this);
            mEditTextTranslate.addTextChangedListener(mTranslateWatcher);
        }
    }

    private void showTranslateDirection(){
        if (mView != null) {
            Button langFromButton = (Button) mView.findViewById(R.id.langFrom);
            langFromButton.setText(mLangFromTitle);
            Button langToButton = (Button) mView.findViewById(R.id.langTo);
            langToButton.setText(mLangToTitle);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        outState.putString(TEXT, mTranslateText);
        outState.putSerializable(TRANSLATE_RESULT, mTranslateResult);
        outState.putString(LANG_FROM_VALUE, mLangFrom);
        outState.putString(LANG_FROM_TITLE, mLangFromTitle);
        outState.putString(LANG_TO_VALUE, mLangTo);
        outState.putString(LANG_TO_TITLE, mLangToTitle);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mSaveResultAction.setSaveImmediate(true);
        mResultHandlers = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setWatcher();
        showTranslateDirection();
        ImageView imageButton = (ImageView) mView.findViewById(R.id.imageViewFav);
        TextView resultTextView = (TextView) mView.findViewById(R.id.textViewResult);
        if (mResultHandlers == null) {
            mShowResultAction = new ShowResultAction(resultTextView);
            ListenFavoritesAction listenFavoritesAction = new ListenFavoritesAction(getContext(), imageButton);
            mSaveResultAction = new SaveResultAction(getContext());
            mResultHandlers = new ArrayList<>();
            mResultHandlers.add(mShowResultAction);
            mResultHandlers.add(mSaveResultAction);
            mResultHandlers.add(listenFavoritesAction);

        }
        //при потере фокуса полем ввода текста установить флаг немедленного сохранения результата перевода в истории
        mEditTextTranslate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mSaveResultAction.setSaveImmediate(!hasFocus);
            }
        });
        if (mTranslateResult != null) {
            for (TranslateResultHandler handler : mResultHandlers) {
                handler.processResult(mTranslateText, mTranslateResult);
            }
        }
    }

    void setResultHandlers(Bundle params){
        if (params != null){
            mTranslateResult = (TranslateResult) params.getSerializable(TRANSLATE_RESULT);
            mTranslateText = params.getString(TEXT);
            mLangFrom = params.getString(LANG_FROM_VALUE);
            mLangFromTitle = params.getString(LANG_FROM_TITLE);
            mLangTo = params.getString(LANG_TO_VALUE);
            mLangToTitle = params.getString(LANG_TO_TITLE);
            updateLangs(params);
        }

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            showTranslateDirection();
            setWatcher();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        Bundle args = getArguments();
        if (savedInstanceState == null && args != null) {
            setResultHandlers(args);
        }else if (savedInstanceState != null){
            setResultHandlers(savedInstanceState);
        }
    }

    public void updateLangs(Bundle params){
        String langFrom = params.getString(LANG_FROM_VALUE);
        String langFromTitle = params.getString(LANG_FROM_TITLE);
        String langTo = params.getString(LANG_TO_VALUE);
        String langToTitle = params.getString(LANG_TO_TITLE);
        updateLangs(langFrom, langTo, langFromTitle, langToTitle);
    }

    public void updateLangs(String langFrom, String langTo, String langFromTitle, String langToTitle){
        mLangFrom = langFrom;
        mLangTo = langTo;
        mLangFromTitle = langFromTitle;
        mLangToTitle = langToTitle;
    }

    @Override
    public boolean processResult(String text, TranslateResult translateResult) {
        mTranslateText = text;
        mTranslateResult = translateResult;
        for (TranslateResultHandler handler : mResultHandlers){
            handler.processResult(text, translateResult);
        }
        return true;
    }
}
