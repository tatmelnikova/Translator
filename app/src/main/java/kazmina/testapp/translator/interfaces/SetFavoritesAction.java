package kazmina.testapp.translator.interfaces;

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

public class SetFavoritesAction implements TranslateResultHandler {
    private Context mContext;
    private ImageView mImageViewFav;

    public SetFavoritesAction(Context context, ImageView imageViewFav) {
        super();
        mContext = context.getApplicationContext();
        mImageViewFav = imageViewFav;
    }

    @Override
    public boolean processResult(String text, TranslateResult translateResult) {
        if (translateResult != null) {
            DBProvider provider = DBContainer.getProviderInstance(mContext);
            provider.getFavoritesId(text, translateResult.getLangFrom(), translateResult.getLangTo(), new DBProvider.ResultCallback<Integer>() {
                @Override
                public void onFinished(Integer result) {
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
}
