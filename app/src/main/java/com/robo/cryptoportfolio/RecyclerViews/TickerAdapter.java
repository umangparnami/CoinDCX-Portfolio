package com.robo.cryptoportfolio.RecyclerViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.PictureDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.robo.cryptoportfolio.Objects.Ticker;
import com.robo.cryptoportfolio.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TickerAdapter extends RecyclerView.Adapter<TickerViewHolder> implements Filterable
{

    protected Context context;
    protected List<Ticker> tickerList;
    private final List<Ticker> tickerListFull;
    protected String marketChoice;

    public TickerAdapter(Context context, List<Ticker> tickerList, String marketChoice)
    {
        this.context = context;
        this.tickerList = tickerList;
        this.marketChoice = marketChoice;
        tickerListFull = new ArrayList<>(tickerList);
    }

    @NonNull
    @Override
    public TickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.ticker_item_layout, parent, false);
        return new TickerViewHolder(view);
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    @Override
    public void onBindViewHolder(@NonNull TickerViewHolder holder, int position) {
        Ticker ticker = tickerList.get(position);

        holder.cryptoMarket.setText(ticker.getMarket());

        String url = String.format("https://cdn.coindcx.com/static/coins/%s.svg",ticker.getMarket().split(marketChoice)[0].toLowerCase());

        setCryptoImage(url,holder.cryptoSymbol);


        // Striping trailing zeros in the string
        String price = ticker.getLastPrice().contains(".") ?
                ticker.getLastPrice().replaceAll("0*$","").replaceAll("\\.$","") :
                ticker.getLastPrice();
        if(marketChoice.equals(context.getResources().getString(R.string.usdt_ticker)))
            holder.cryptoPrice.setText(String.format("%s%s", context.getResources().getString(R.string.usd_symbol) ,price));

        else if(marketChoice.equals(context.getResources().getString(R.string.btc_ticker)))
            holder.cryptoPrice.setText(String.format("%s%s", context.getResources().getString(R.string.btc_symbol) ,price));

        else if(marketChoice.equals(context.getResources().getString(R.string.inr_ticker)))
            holder.cryptoPrice.setText(String.format("%s%s", context.getResources().getString(R.string.inr_symbol) ,price));

        float priceChange = Float.parseFloat(ticker.getPriceChange());

        if(priceChange<0)
        {
            holder.priceChange.setText(String.format(Locale.US,"%.2f%%", priceChange));
            holder.priceChange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_downward,0,0,0);
            holder.priceChange.setTextColor(context.getColor(R.color.red));
            holder.priceChange.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.red_10)));
        }
        else if(priceChange>0)
        {
            holder.priceChange.setText(String.format(Locale.US,"+%.2f%%", priceChange));
            holder.priceChange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_upward,0,0,0);
            holder.priceChange.setTextColor(context.getColor(R.color.green));
            holder.priceChange.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.green_10)));
        }
        else
        {
            holder.priceChange.setText(context.getString(R.string.no_change));
            holder.priceChange.setTextColor(context.getColor(R.color.blue));
            holder.priceChange.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue_20)));
            holder.priceChange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_nochange,0,0,0);
        }

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

    @Override
    public int getItemCount() {
        return tickerList.size();
    }

    @Override
    public Filter getFilter()
    {
        return tickerFilter;
    }

    private final Filter tickerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Ticker> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(tickerListFull);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (Ticker ticker : tickerListFull) {
                    if (ticker.getMarket().toLowerCase().startsWith(pattern)) {
                        filteredList.add(ticker);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tickerList.clear();
            tickerList.addAll((List) results.values);
            System.out.println("Ticker Size "+getItemCount());
            notifyDataSetChanged();
        }
    };

}
