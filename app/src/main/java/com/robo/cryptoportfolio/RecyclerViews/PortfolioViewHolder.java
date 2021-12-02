package com.robo.cryptoportfolio.RecyclerViews;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.robo.cryptoportfolio.R;

public class PortfolioViewHolder extends RecyclerView.ViewHolder
{
    ImageView symbol;
    TextView name,currentPrice,quantity,change,buyPrice,buyTotal,currentTotal;
    Button editBtn;

    public PortfolioViewHolder(@NonNull View itemView) {
        super(itemView);
        symbol = itemView.findViewById(R.id.symbol);
        name = itemView.findViewById(R.id.crypto_name);
        currentPrice = itemView.findViewById(R.id.current_price);
        quantity = itemView.findViewById(R.id.quantity);
        change = itemView.findViewById(R.id.change);
        buyPrice = itemView.findViewById(R.id.buy_price);
        buyTotal = itemView.findViewById(R.id.buy_total);
        currentTotal = itemView.findViewById(R.id.current_total);
        editBtn = itemView.findViewById(R.id.edit_buy_price);
    }
}
