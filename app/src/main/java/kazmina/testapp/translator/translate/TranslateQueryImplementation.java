package kazmina.testapp.translator.translate;

import java.lang.annotation.Annotation;

import kazmina.testapp.translator.YandexTranslateApi;
import kazmina.testapp.translator.api.APIError;
import kazmina.testapp.translator.retrofitModels.TranslateResult;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * отправляет запросы к апи переводчика
 */

class TranslateQueryImplementation implements TranslateQueryInterface {
    private TranslateResultHandler mTranslateResultHandler;
    private Call<TranslateResult> mCall;
    TranslateQueryImplementation(TranslateResultHandler handler) {
        super();
       mTranslateResultHandler = handler;
    }

    @Override
    public void runTranslate(final String text, final String translateDirection) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://translate.yandex.net") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        final YandexTranslateApi api = retrofit.create(YandexTranslateApi.class);
        mCall = api.getTranslate(text, translateDirection);
        mCall.enqueue(new Callback<TranslateResult>() {
            @Override
            public void onResponse(Call<TranslateResult> call, Response<TranslateResult> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        TranslateResult translateResult = response.body();
                        mTranslateResultHandler.processResult(text, translateResult);
                    }
                }else{
                    try {
                        //  mTranslateResultHandler.handleError(null, response.errorBody().string());
                        Converter<ResponseBody, APIError> errorConverter =
                                retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
                        APIError error = errorConverter.convert(response.errorBody());
                        mTranslateResultHandler.handleError(error.getStatusCode(), error.getMessage());
                        //mTranslateResultHandler.handleError(null, response.errorBody().string());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<TranslateResult> call, Throwable t) {
                mTranslateResultHandler.handleError(null,null);
            }
        });
    }

    @Override
    public void cancel() {
        if (mCall != null) mCall.cancel();
    }
}
