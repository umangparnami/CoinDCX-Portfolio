package com.robo.cryptoportfolio.Retrofit;


import com.robo.cryptoportfolio.Objects.Balance;
import com.robo.cryptoportfolio.Objects.MarketDetails;
import com.robo.cryptoportfolio.Objects.Ticker;
import com.robo.cryptoportfolio.Objects.UserInfo;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitAPI
{
    @GET("ticker")
    Call<List<Ticker>> getAllTickers();

    @GET("v1/markets_details")
    Call<List<MarketDetails>> getMarketDetails();

    @POST("v1/users/balances")
    Call<List<Balance>> getAllBalances(@Body RequestBody body);

    @POST("v1/users/info")
    Call<UserInfo> getUserInfo(@Body RequestBody body);

}
