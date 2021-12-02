package com.robo.cryptoportfolio.HelperClasses;

import android.util.Log;

import com.robo.cryptoportfolio.MainActivity;
import com.robo.cryptoportfolio.Objects.Balance;
import com.robo.cryptoportfolio.Objects.MarketDetails;
import com.robo.cryptoportfolio.Objects.Ticker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ConvertBalanceToInr
{

    public static List<Balance> getBalances(List<Balance> balances)
    {
        List<Balance> balancesWithValue = new ArrayList<>();
        List<Ticker> tickerList = MainActivity.getActivity().getTicker();
        List<MarketDetails> marketDetails = MainActivity.getActivity().getMarketDetails();

        String usdString = tickerList.stream()
                .filter(ticker -> ticker.getMarket().equals("USDTINR"))
                .collect(Collectors.toList()).get(0).getLastPrice();

        float usdPrice = Float.parseFloat(String.format(Locale.US, "%.2f", new BigDecimal(usdString)));


        for(Balance balance:balances)
        {
            if(balance.getCurrency().equals("USDT"))
            {
                String name = marketDetails.stream().filter(market -> market.getCurrencyNameShort().equals("USDT"))
                        .collect(Collectors.toList()).get(0).getCurrencyName();
                float usdtValue = Float.parseFloat(balance.getTotalBalance()) * usdPrice;
                balancesWithValue.add(new Balance(name,balance.getCurrency(),balance.getBalance(),balance.getLockedBalance(),String.valueOf(usdtValue),String.valueOf(usdPrice)));
            }
            else if(tickerList.stream().noneMatch(ticker -> ticker.getMarket().equals(balance.getCurrency() + "USDT")) || balance.getCurrency().equals("INR"))
            {
                Log.i("Breaking Loop","No values available.");
            }
            else
            {
                String currencyPriceString =  tickerList.stream()
                        .filter(ticker -> ticker.getMarket().equals(balance.getCurrency()+"USDT"))
                        .collect(Collectors.toList()).get(0).getLastPrice();

                String name = marketDetails.stream().filter(market -> market.getCurrencyNameShort().equals(balance.getCurrency()))
                        .collect(Collectors.toList()).get(0).getCurrencyName();

                float currencyPriceInUsd = Float.parseFloat(currencyPriceString.contains(".") ?
                        currencyPriceString.replaceAll("0*$","").replaceAll("\\.$","") :
                        currencyPriceString);

                float currencyQuantity = Float.parseFloat(balance.getTotalBalance());

                float currencyPriceInInr = currencyQuantity * currencyPriceInUsd * usdPrice;

                if(currencyPriceInInr>=1)
                {
                    balancesWithValue.add(new Balance(name,balance.getCurrency(),balance.getBalance(),balance.getLockedBalance(),String.valueOf(currencyPriceInInr),String.valueOf(currencyPriceInUsd * usdPrice)));
                }
            }
        }
        return balancesWithValue;
    }

}
