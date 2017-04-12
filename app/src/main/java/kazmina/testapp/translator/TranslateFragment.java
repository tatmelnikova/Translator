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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.interfaces.SaveResultAction;
import kazmina.testapp.translator.interfaces.SetFavoritesAction;
import kazmina.testapp.translator.interfaces.ShowResultAction;
import kazmina.testapp.translator.interfaces.TranslateResultHandler;

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
    String mLangFromTitle;
    String mLangToTitle;


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

    private void showTranslateDirection(){
            if (mLangFromTitle != null) {
                Button langFromButton = (Button) mView.findViewById(R.id.langFrom);
                langFromButton.setText(mLangFromTitle);
            }
            if (mLangToTitle != null) {
                Button langToButton = (Button) mView.findViewById(R.id.langTo);
                langToButton.setText(mLangToTitle);
            }
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
            ImageView favIconView = (ImageView) mView.findViewById(R.id.imageViewFav);
            TextView resultTextView = (TextView) mView.findViewById(R.id.textViewResult);
            ShowResultAction showResultAction = new ShowResultAction(resultTextView);
            SetFavoritesAction setFavoritesAction = new SetFavoritesAction(getContext(), favIconView);
            mSaveResultAction = new SaveResultAction(getContext());
            mResultHandlers = new ArrayList<>();
            mResultHandlers.add(showResultAction);
            mResultHandlers.add(mSaveResultAction);
            mResultHandlers.add(setFavoritesAction);
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
            mLangFromTitle = args.getString(LANG_FROM_TITLE);
            mLangToTitle = args.getString(LANG_TO_TITLE);
            showTranslateDirection();
        }
    }

    public void updateLangs(String langFrom, String langTo, String langFromTitle, String langToTitle){
        mLangFrom = langFrom;
        mLangTo = langTo;
        mLangFromTitle = langFromTitle;
        mLangToTitle = langToTitle;
        showTranslateDirection();
        setWatcher();
    }
}
