package com.robo.cryptoportfolio.Objects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Locale;

public class Balance
{
    @SerializedName("currency")
    private String currency;

    @SerializedName("balance")
    private String balance;

    @SerializedName("locked_balance")
    private String lockedBalance;

    private String totalBalance;

    private String inrValue;

    private String currentPrice;

    private String buyPrice;

    private String currencyName;


    public Balance(String currencyName,String currency, String balance, String lockedBalance, String inrValue, String currentPrice)
    {
        this.currencyName = currencyName;
        this.currency = currency;
        this.balance = balance;
        this.lockedBalance = lockedBalance;
        this.inrValue = inrValue;
        this.currentPrice = currentPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLockedBalance() {
        return lockedBalance;
    }

    public void setLockedBalance(String lockedBalance) {
        this.lockedBalance = lockedBalance;
    }

    public String getTotalBalance()
    {
        setTotalBalance();
        return totalBalance;
    }

    public void setTotalBalance()
    {
        double temp = Double.parseDouble(balance) + Double.parseDouble(lockedBalance);
        totalBalance = String.format(Locale.US,"%.8f",new BigDecimal(temp));
    }

    public String getInrValue() {
        return inrValue;
    }

    public void setInrValue(String inrValue) {
        this.inrValue = inrValue;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getCurrentTotal()
    {
        return String.valueOf(Float.parseFloat(getTotalBalance()) * Float.parseFloat(getCurrentPrice()));
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Currency %s Total Balance %s InrValue %s Currency Price %s",getCurrency(),getTotalBalance(),getInrValue(),getCurrentPrice());
    }
}
