package com.robo.cryptoportfolio.RecyclerViews;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robo.cryptoportfolio.R;

public class TickerViewHolder extends RecyclerView.ViewHolder
{
    protected TextView cryptoMarket,cryptoPrice,priceChange;
    protected ImageView cryptoSymbol;

    public TickerViewHolder(@NonNull View itemView)
    {
        super(itemView);
        cryptoMarket = itemView.findViewById(R.id.crypto_market);
        cryptoPrice = itemView.findViewById(R.id.crypto_price);
        priceChange = itemView.findViewById(R.id.price_change);
        cryptoSymbol = itemView.findViewById(R.id.symbol);
    }
}
