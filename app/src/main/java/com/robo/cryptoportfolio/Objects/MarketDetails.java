package com.robo.cryptoportfolio.Objects;

import com.google.gson.annotations.SerializedName;

public class MarketDetails
{

    @SerializedName("coindcx_name")
    private String marketName;

    @SerializedName("target_currency_name")
    private String currencyName;

    @SerializedName("target_currency_short_name")
    private String currencyNameShort;

    public String getMarketName() {
        return marketName;
    }

    public String getCurrencyName()
    {
        return currencyName;
    }

    public String getCurrencyNameShort() {
        return currencyNameShort;
    }
}
