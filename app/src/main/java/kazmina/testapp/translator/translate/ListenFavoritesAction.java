package kazmina.testapp.translator.translate;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;

import kazmina.testapp.translator.R;
import kazmina.testapp.translator.db.DBContainer;
import kazmina.testapp.translator.db.DBProvider;
import kazmina.testapp.translator.retrofitModels.TranslateResult;

/**
 * управляет поведением кнопки добавить в избранное для результата перевода
 */

public class ListenFavoritesAction implements TranslateResultHandler, View.OnClickListener {
    private Context mContext;
    private ImageView mImageViewFav;
    private TranslateResult mTranslateResult;
    private String mText;
    private Integer mFavId = null;
    final String TAG = "ListenFavoritesAction";
    public ListenFavoritesAction(Context context, ImageView imageView) {
        super();
        mContext = context.getApplicationContext();
        mImageViewFav = imageView;
        mImageViewFav.setOnClickListener(this);
    }

    @Override
    public boolean processResult(String text, TranslateResult translateResult) {
        mText = text;
        mTranslateResult = translateResult;
        if (translateResult != null) {
            DBProvider provider = DBContainer.getProviderInstance(mContext);
            provider.getFavoritesId(text, translateResult.getLangFrom(), translateResult.getLangTo(), new DBProvider.ResultCallback<Integer>() {
                @Override
                public void onFinished(Integer result) {
                    mFavId = result;
                    if (result == null) {
                        int colorDisabled = ResourcesCompat.getColor(mContext.getResources(), R.color.colorDisabled, null);
                        mImageViewFav.setColorFilter(colorDisabled, PorterDuff.Mode.MULTIPLY);
                    } else {
                        int colorActive = ResourcesCompat.getColor(mContext.getResources(), R.color.colorPrimary, null);
                        mImageViewFav.setColorFilter(colorActive, PorterDuff.Mode.MULTIPLY);
                    }
                    mImageViewFav.setVisibility(View.VISIBLE);
                }
            });
        }else{
            mImageViewFav.setVisibility(View.INVISIBLE);
        }
        return true;
    }

    @Override
    public void handleError(Integer code, String message) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewFav:
                DBProvider provider = DBContainer.getProviderInstance(mContext);
                if (mFavId != null) {
                    provider.removeFromFavoritesById(mFavId);
                } else {
                    provider.insertFavoritesItem(mText, mTranslateResult.getPlainText(), mTranslateResult.getLangFrom(), mTranslateResult.getLangTo());
                }
                break;
            default:
                break;
        }
        processResult(mText, mTranslateResult);
    }
}
