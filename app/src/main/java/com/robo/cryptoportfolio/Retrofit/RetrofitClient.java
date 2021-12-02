package com.robo.cryptoportfolio.Retrofit;

import com.robo.cryptoportfolio.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
    private static Retrofit retrofit = null;
    private final String url = BuildConfig.BASE_URL;

    public Retrofit getInstance()
    {
        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit;
        }
        return retrofit;
    }


    public Retrofit getInstanceWithClient(String signature, String api)
    {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request originalRequest = chain.request();
                Request newRequest = originalRequest.newBuilder()
                        .header("X-AUTH-SIGNATURE",signature)
                        .header("X-AUTH-APIKEY",api)
                        .header("Content-Type","application/json")
                        .build();
                return chain.proceed(newRequest);
            }).connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS)
                    .writeTimeout(30,TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            return retrofit;
    }

}
