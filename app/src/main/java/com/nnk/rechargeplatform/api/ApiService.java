package com.nnk.rechargeplatform.api;

import com.nnk.rechargeplatform.Constants;
import com.nnk.rechargeplatform.utils.Logg;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiService {
    private static ApiService instance;
    private Retrofit retrofit;
    private Retrofit fileRetrofit;
    private HttpLoggingInterceptor loggingInterceptor;
    private OkHttpClient okHttpClient;

    //单例
    public static ApiService getInstance() {
        if (instance == null) {
            synchronized (ApiService.class) {
                if (instance == null) {
                    instance = new ApiService();
                }
            }
        }
        return instance;
    }

    private ApiService() {
        //新建log拦截器
       loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Logg.d("okhttp", message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(false)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HOST)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        fileRetrofit=  new Retrofit.Builder()
                .baseUrl(Constants.HOST_FILE)
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

    public <T> T createService(Class<T> service) {
        return retrofit.create(service);
    }

    public <T> T createFileService(Class<T> service) {
        return fileRetrofit.create(service);
    }


}

