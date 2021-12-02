package com.robo.cryptoportfolio.RecyclerViews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.robo.cryptoportfolio.Objects.Balance;
import com.robo.cryptoportfolio.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioViewHolder>
{

    private final Context context;
    private final List<Balance> balances;
    private SharedPreferences preferences;


    public PortfolioAdapter(Context context, List<Balance> balances)
    {
        this.context = context;
        this.balances = balances;
    }

    @NonNull
    @Override
    public PortfolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.portfolio_item_layout, parent, false);
        preferences = view.getContext().getSharedPreferences(context.getResources().getString(R.string.crypto_shared_pref), Context.MODE_PRIVATE);
        return new PortfolioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioViewHolder holder, int position)
    {
        Balance balance = balances.get(position);
        String url = String.format("https://cdn.coindcx.com/static/coins/%s.svg",balance.getCurrency().toLowerCase());
        setCryptoImage(url,holder.symbol);


        holder.name.setText(balance.getCurrencyName());
        holder.quantity.setText(String.format(Locale.US,"Qty: %s",getFormattedString(balance.getTotalBalance())));
        holder.currentPrice.setText(String.format("%s%s",context.getString(R.string.inr_symbol),getFormattedString(balance.getCurrentPrice())));
        holder.currentTotal.setText(String.format("%s%s",context.getString(R.string.inr_symbol),getFormattedString(balance.getCurrentTotal())));

        String price = preferences.getString(String.format("%s_buy_price",balance.getCurrency()),null);

        if(price !=null)
        {
            //Calculating buy price and percentage change
            float buyTotal = Float.parseFloat(price);
            holder.buyTotal.setText(String.format("%s%s",context.getString(R.string.inr_symbol),getFormattedString(String.valueOf(buyTotal))));
            float buyPrice = buyTotal / Float.parseFloat(balance.getTotalBalance());
            holder.buyPrice.setText(String.format("%s%s",context.getString(R.string.inr_symbol), getFormattedString(String.valueOf(buyPrice))));
            float change = ((Float.parseFloat(balance.getCurrentTotal())-buyTotal)/buyTotal)*100;
            holder.change.setText(String.format(Locale.US,"%.2f%%",change));
            if(change<0)
            {
                holder.change.setText(String.format(Locale.US,"%.2f%%", change));
                holder.change.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_downward,0,0,0);
                holder.change.setTextColor(context.getColor(R.color.red));
                holder.change.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red_10)));
            }
            else if(change>0)
            {
                holder.change.setText(String.format(Locale.US,"+%.2f%%", change));
                holder.change.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_upward,0,0,0);
                holder.change.setTextColor(context.getColor(R.color.green));
                holder.change.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.green_10)));
            }
            else
            {
                holder.change.setText(context.getString(R.string.no_change));
                holder.change.setTextColor(context.getColor(R.color.blue));
                holder.change.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue_20)));
                holder.change.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_nochange,0,0,0);
            }
        }
        else {
                holder.buyPrice.setText("-");
                holder.buyTotal.setText("-");
                holder.change.setText(context.getString(R.string.no_change));
                holder.change.setTextColor(context.getColor(R.color.blue));
                holder.change.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue_20)));
                holder.change.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_nochange,0,0,0);
            }

        new Thread(new Runnable() {
            @Override
            public void run() {
                holder.editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        createAlert(balance);
                    }
                });
            }
        }).start();

    }

    @Override
    public int getItemCount() {
        return balances.size();
    }


    private void createAlert(Balance balance)
    {
        View price_popup = LayoutInflater.from(context).inflate(R.layout.price_edit_popup,null,false);
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(true);
        dialog.setView(price_popup);
        dialog.show();

        TextView title = dialog.findViewById(R.id.popup_crypto_name);
        TextInputLayout buyPrice = dialog.findViewById(R.id.popup_crypto_buy_price);
        Button doneBtn = dialog.findViewById(R.id.popup_done);
        title.setText(String.format("Enter total buy value for %s",balance.getCurrencyName()));
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = buyPrice.getEditText().getText().toString().trim();
                if(price.isEmpty())
                {
                    buyPrice.setError("Enter price to proceed");
                }
                else
                {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(String.format("%s_buy_price",balance.getCurrency()),price);
                    editor.apply();
                    dialog.dismiss();
                    notifyDataSetChanged();
                }
            }
        });
    }

    private String getFormattedString(String s)
    {
        float num = Float.parseFloat(s);
        DecimalFormat df;
        if(num>1)
        {
            df = new DecimalFormat("#.##");
        }
        else
        {
            df = new DecimalFormat("#.######");
        }
        return df.format(num);
    }

    private void setCryptoImage(String url, ImageView imageView)
    {
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setStrokeWidth(5f);
        progressDrawable.setColorFilter(context.getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
        progressDrawable.setCenterRadius(40f);
        progressDrawable.start();

        RequestBuilder<PictureDrawable> requestBuilder;
        requestBuilder = Glide.with(context)
                .as(PictureDrawable.class)
                .placeholder(progressDrawable);

        requestBuilder.load(url).into(imageView).clearOnDetach();
    }

}
