package com.robo.cryptoportfolio.Objects;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

public class Ticker
{
    @SerializedName("market")
    private String market;

    @SerializedName("last_price")
    private String lastPrice;

    @SerializedName("timestamp")
    private Long timestamp;

    @SerializedName("change_24_hour")
    private String priceChange;

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(String priceChange) {
        this.priceChange = priceChange;
    }

    public static Comparator<Ticker> sortUp = new Comparator<Ticker>() {
        @Override
        public int compare(Ticker o1, Ticker o2) {
            float f1= Float.parseFloat(o1.getPriceChange());
            float f2= Float.parseFloat(o2.getPriceChange());
            return Float.compare(f2,f1);
        }
    };

    public static Comparator<Ticker> sortDown = new Comparator<Ticker>() {
        @Override
        public int compare(Ticker o1, Ticker o2) {
            float f1= Float.parseFloat(o1.getPriceChange());
            float f2= Float.parseFloat(o2.getPriceChange());
            return Float.compare(f1,f2);
        }
    };
}
