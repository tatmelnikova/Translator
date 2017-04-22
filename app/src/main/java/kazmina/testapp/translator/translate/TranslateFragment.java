package kazmina.testapp.translator.translate;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
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
import java.util.Timer;
import java.util.TimerTask;

import kazmina.testapp.translator.FragmentCommunicator;
import kazmina.testapp.translator.R;

import kazmina.testapp.translator.retrofitModels.APIErrorMessages;
import kazmina.testapp.translator.interfaces.LanguagesHolder;
import kazmina.testapp.translator.retrofitModels.TranslateResult;
import kazmina.testapp.translator.utils.CommonUtils;

/**
 * фрагмент основного окна перевода
 */

public class TranslateFragment extends Fragment implements LanguagesHolder, TranslateResultHandler, TextChangedListener, APIErrorMessages, View.OnClickListener{
    private TranslateWatcher mTranslateWatcher;
    private List<TranslateResultHandler> mResultHandlers = null;
    TranslateResult mTranslateResult = null;
    String mTranslateText = null;
    private String TAG = "TranslateFragment";
    private View mView;
    private String mLangFrom;
    private String mLangTo;

    private String mLangFromTitle;
    private String mLangToTitle;
    private SaveResultAction mSaveResultAction;

    private EditText mEditTextTranslate;
    private FragmentCommunicator mListener;
    TranslateQueryInterface mTranslateQuery;
    private final String DELIMETER = "-";
    private final long DELAY = 500;
    private Timer mTimer = new Timer();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FragmentCommunicator) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FragmentCommunicator");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        mView = view;
        mEditTextTranslate = (EditText) view.findViewById(R.id.editTextInput);
        View from = view.findViewById(R.id.langFrom);
        from.setOnClickListener(this);
        View to = view.findViewById(R.id.langTo);
        to.setOnClickListener(this);
        View rotate = view.findViewById(R.id.rotate);
        rotate.setOnClickListener(this);
        View swapLangs = view.findViewById(R.id.swapLang);
        swapLangs.setOnClickListener(this);

        Bundle args = getArguments();
        if (savedInstanceState == null && args != null) {
            saveArguments(args);
        }else if (savedInstanceState != null){
            saveArguments(savedInstanceState);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mTranslateQuery = new TranslateQueryImplementation(this);
        showTranslateDirection();


        ImageView imageButton = (ImageView) mView.findViewById(R.id.imageViewFav);
        TextView resultTextView = (TextView) mView.findViewById(R.id.textViewResult);
        TextView copyrightTextView = (TextView) mView.findViewById(R.id.copyright);
        copyrightTextView.setMovementMethod(LinkMovementMethod.getInstance());
        ShowResultAction showResultAction = new ShowResultAction(resultTextView, copyrightTextView);
        ListenFavoritesAction listenFavoritesAction = new ListenFavoritesAction(getContext(), imageButton);
        mSaveResultAction = new SaveResultAction(getContext());
        mResultHandlers = new ArrayList<>();
        mResultHandlers.add(showResultAction);
        mResultHandlers.add(mSaveResultAction);
        mResultHandlers.add(listenFavoritesAction);
        setWatcher();
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            //mTranslateQuery = new TranslateQueryImplementation(this);
            refreshLangs();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mTranslateQuery.cancel();
        mTranslateQuery = null;
        Log.d(TAG, "onPause");
        mResultHandlers = null;
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


    /**
     * привязывает слушатель к полю ввода текста для перевода
     */
    private void setWatcher(){
        if (mEditTextTranslate != null) {
            mEditTextTranslate.removeTextChangedListener(mTranslateWatcher);
            mTranslateWatcher = new TranslateWatcher(this);
            mEditTextTranslate.addTextChangedListener(mTranslateWatcher);
        }
    }

    /**
     * отображает названия языков перевода
     */
    private void showTranslateDirection(){
            Button langFromButton = (Button) mView.findViewById(R.id.langFrom);
            langFromButton.setText(mLangFromTitle);
            Button langToButton = (Button) mView.findViewById(R.id.langTo);
            langToButton.setText(mLangToTitle);
    }


    void saveArguments(@NonNull  Bundle params){
            mTranslateResult = (TranslateResult) params.getSerializable(TRANSLATE_RESULT);
            mTranslateText = params.getString(TEXT);
            mLangFrom = params.getString(LANG_FROM_VALUE);
            mLangFromTitle = params.getString(LANG_FROM_TITLE);
            mLangTo = params.getString(LANG_TO_VALUE);
            mLangToTitle = params.getString(LANG_TO_TITLE);
    }

    /**
     * @param viewId - ID кнопки, которая была нажата для смены языка (R.id.langFromили R.id.langTo)
     * @param langValue - код языка
     * @param langTitle - локализованное название языка
     */
    public void setLanguage(int viewId, String langValue, String langTitle){
        //проверить второй установленный язык, если для языка 1 уже установлен русский, и для языка
        //2 передан тоже русский, то поменять значения языков местами
        if (viewId == R.id.langFrom){
            if (langValue.equals(mLangTo)){
                swapTranslateDirection();
            }else{
                mLangFrom = langValue;
                mLangFromTitle = langTitle;
            }

        }else if (viewId == R.id.langTo){
            if (langValue.equals(mLangFrom)){
                swapTranslateDirection();
            }else{
                mLangTo = langValue;
                mLangToTitle = langTitle;
            }
        }
        //после установки языка обновить вьюшки с их отображением
        refreshLangs();
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

    @Override
    public void handleError(Integer code, String message) {
        String localisedMessage;
        if (code != null) {
            switch (code) {
                case CODE_INVALID:
                    localisedMessage = getString(MESSAGE_INVALID);
                    break;
                case CODE_BLOCKED:
                    localisedMessage = getString(MESSAGE_BLOCKED);
                    break;
                case CODE_LIMIT:
                    localisedMessage = getString(MESSAGE_LIMIT);
                    break;
                default:
                    localisedMessage = getString(MESSAGE_OTHER);
                    break;
            }
        }else{
            localisedMessage = getString(MESSAGE_EMPTY);
        }

        for (TranslateResultHandler handler : mResultHandlers){
            handler.handleError(code, localisedMessage);
        }
    }

    public void refreshLangs(){
        showTranslateDirection();
        if (mTranslateText != null) runTranslate(mTranslateText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEditTextTranslate = null;
        mView = null;
    }

    public static Fragment getInstance(String langFromValue, String langToValue, String langFromTitle, String langToTitle){
        Fragment translateFragment = new TranslateFragment();
        Bundle params = new Bundle();
        params.putString(LANG_FROM_VALUE, langFromValue);
        params.putString(LANG_TO_VALUE, langToValue);
        params.putString(LANG_FROM_TITLE, langFromTitle);
        params.putString(LANG_TO_TITLE, langToTitle);
        translateFragment.setArguments(params);
        return translateFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.langFrom:
                mListener.onSelectLangButtonClicked(R.id.langFrom, mLangFrom);
                break;
            case R.id.langTo:
                mListener.onSelectLangButtonClicked(R.id.langTo, mLangTo);
                break;
            case R.id.swapLang:
                swapTranslateDirection();
                break;
            case R.id.rotate:
                if (!"1".equals(v.getTag())) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    v.setTag("1");
                }else{
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    v.setTag("0");
                }
                break;
            default:
                break;
        }
    }

    /**
     * меняет направление перевода на обратное
     */
    private void swapTranslateDirection(){
        //если в поле ввода текста для перевода ничего нет, то просто поменяем языки перевода местами
        if (mTranslateText == null ) {
            String tmp = mLangFrom;
            mLangFrom = mLangTo;
            mLangTo = tmp;
            tmp = mLangFromTitle;
            mLangFromTitle = mLangToTitle;
            mLangToTitle = tmp;
            refreshLangs();
        }else{
            //иначе проверим, есть ли уже результат перевода для введенного текста
            //если результата перевода еще нет, то не будем делать ничего
            //Яндекс.Переводчик работает сейчас именно так. варианты - поставить смену языков в
            // очередь дожидаться, пока результат перевода появится? тоже не очень красиво
            if (mTranslateResult != null) {
                String tmp = mLangFrom;
                mLangFrom = mLangTo;
                mLangTo = tmp;
                tmp = mLangFromTitle;
                mLangFromTitle = mLangToTitle;
                mLangToTitle = tmp;
                mTranslateText = mTranslateResult.getPlainText();
                mEditTextTranslate.setText(mTranslateText);
                refreshLangs();
            }
        }

    }

    public void runTranslate(String text){
        String translateDirection = mLangFrom.concat(DELIMETER).concat(mLangTo);
        mTranslateQuery.runTranslate(text, translateDirection);
    }

    @Override
    public void onTextChanged(final String text) {
        mTranslateText = text;
        if (CommonUtils.stringIsEmpty(text)) {
            mTimer.cancel();
            processResult(null, null);
        }else{
            mTimer.cancel();
            mTimer = new Timer();
            mTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            runTranslate(text);
                        }
                    },
                    DELAY
            );
        }
    }
}
