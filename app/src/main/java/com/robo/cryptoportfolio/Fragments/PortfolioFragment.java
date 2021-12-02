package com.robo.cryptoportfolio.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.robo.cryptoportfolio.HelperClasses.ConvertBalanceToInr;
import com.robo.cryptoportfolio.HelperClasses.SignatureGenerator;
import com.robo.cryptoportfolio.MainActivity;
import com.robo.cryptoportfolio.Objects.Balance;
import com.robo.cryptoportfolio.Objects.MarketDetails;
import com.robo.cryptoportfolio.R;
import com.robo.cryptoportfolio.RecyclerViews.PortfolioAdapter;
import com.robo.cryptoportfolio.Retrofit.RetrofitAPI;
import com.robo.cryptoportfolio.Retrofit.RetrofitClient;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class PortfolioFragment extends Fragment
{

    private View view;
    private SharedPreferences preferences;
    private static String api,secret;

    RecyclerView portfolioRecycler;
    PortfolioAdapter adapter;
    float portfolioSum = 0, buySum=0;
    
    TextView allBuyTotal,portfolioTotal,totalChange;
    CardView portfolioTotalLayout;


    public PortfolioFragment()
    {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_portfolio, container, false);
        preferences = view.getContext().getSharedPreferences(getResources().getString(R.string.shared_pref), Context.MODE_PRIVATE);
        portfolioRecycler = view.findViewById(R.id.portfolio_recycler);
        allBuyTotal = view.findViewById(R.id.all_buy_total);
        portfolioTotal = view.findViewById(R.id.portfolio_total);
        totalChange = view.findViewById(R.id.total_change);
        portfolioTotalLayout = view.findViewById(R.id.portfolio_total_layout);
        portfolioTotalLayout.setVisibility(View.GONE);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkPreferences();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        portfolioRecycler.setLayoutManager(layoutManager);
    }

    private void checkPreferences()
    {
        if(preferences.getString(getResources().getString(R.string.api_key),null) == null)
        {
            Snackbar.make(view,"Set API and Secret Key on settings tab.", Snackbar.LENGTH_LONG).show();
        }
        else
        {
            api = preferences.getString(getResources().getString(R.string.api_key),null);
            secret = preferences.getString(getResources().getString(R.string.secret_key),null);
            requestData();
        }
    }


    private void requestData()
    {
        SignatureGenerator obj = new SignatureGenerator();
        Retrofit retrofit = new RetrofitClient().getInstance();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        ProgressDialog dialog = new ProgressDialog(view.getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        Call<List<MarketDetails>> marketCall = retrofitAPI.getMarketDetails();
        marketCall.enqueue(new Callback<List<MarketDetails>>() {
            @Override
            public void onResponse(Call<List<MarketDetails>> call, Response<List<MarketDetails>> response) {
                MainActivity.getActivity().setMarketDetails(response.body());
            }

            @Override
            public void onFailure(Call<List<MarketDetails>> call, Throwable t) {

            }
        });

        retrofit = new RetrofitClient().getInstanceWithClient(obj.getSignature(secret),api);
        retrofitAPI = retrofit.create(RetrofitAPI.class);
        //Sending timestamp JSON as a body parameter
        Call<List<Balance>> call = retrofitAPI.getAllBalances(obj.getBody());

        call.enqueue(new Callback<List<Balance>>() {
            @Override
            public void onResponse(@NotNull Call<List<Balance>> call, @NotNull retrofit2.Response<List<Balance>> response)
            {
                dialog.dismiss();
                if(response.body() == null)
                {
                    Snackbar.make(view,"API or Secret Key incorrect.",Snackbar.LENGTH_SHORT)
                            .setAction("Fix it", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new SettingsFragment()).commit();
                                }
                            }).show();
                    return;
                }
                portfolioTotalLayout.setVisibility(View.VISIBLE);
                List<Balance> availableBalances = response.body().stream().filter(balance -> Float.parseFloat(balance.getTotalBalance())>0)
                        .collect(Collectors.toList());

                List<Balance> balanceInInr = ConvertBalanceToInr.getBalances(availableBalances);
                adapter = new PortfolioAdapter(view.getContext(),balanceInInr);
                portfolioRecycler.setAdapter(adapter);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        preferences = view.getContext().getSharedPreferences(getResources().getString(R.string.crypto_shared_pref), Context.MODE_PRIVATE);
                        for (Balance balance:balanceInInr)
                        {
                            String price = preferences.getString(String.format("%s_buy_price",balance.getCurrency()),null);
                            if(price != null)
                            {
                                buySum+=Float.parseFloat(price);
                            }
                            float inr = Float.parseFloat(balance.getInrValue());
                            portfolioSum+=inr;
                        }
                        setPortfolioValues(buySum,portfolioSum);
                    }
                }).start();
            }

            @Override
            public void onFailure(@NotNull Call<List<Balance>> call, @NotNull Throwable t) {
                dialog.dismiss();
                Log.i("Response","Failure");
            }
        });

    }

    
    private void setPortfolioValues(float buy,float portfolio)
    {
        if(buy>0)
        {
            float change = ((portfolio - buy)/buy)*100;
            allBuyTotal.setText(String.format(Locale.US,"%s%.2f",getResources().getString(R.string.inr_symbol),buy));
            portfolioTotal.setText(String.format(Locale.US,"%s%.2f",getResources().getString(R.string.inr_symbol),portfolio));
            if(change<0)
            {
                totalChange.setText(String.format(Locale.US,"%.2f%%", change));
                totalChange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_downward,0,0,0);
                totalChange.setTextColor(getResources().getColor(R.color.red));
                totalChange.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_10)));
            }
            else if(change>0)
            {
                totalChange.setText(String.format(Locale.US,"+%.2f%%", change));
                totalChange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_upward,0,0,0);
                totalChange.setTextColor(getResources().getColor(R.color.green));
                totalChange.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_10)));
            }
        }
        else
        {
            allBuyTotal.setText("-");
            portfolioTotal.setText(String.format(Locale.US,"%s%.2f",getResources().getString(R.string.inr_symbol),portfolio));
            totalChange.setText("-");
            totalChange.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_nochange,0,0,0);
            totalChange.setTextColor(getResources().getColor(R.color.blue));
            totalChange.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue_20)));
        }
    }

    //TODO: Use shared preference to save api and secret key with encoding.
   
}