package com.robo.cryptoportfolio.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.robo.cryptoportfolio.MainActivity;
import com.robo.cryptoportfolio.Objects.Ticker;
import com.robo.cryptoportfolio.R;
import com.robo.cryptoportfolio.RecyclerViews.TickerAdapter;
import com.robo.cryptoportfolio.Retrofit.RetrofitAPI;
import com.robo.cryptoportfolio.Retrofit.RetrofitClient;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.internal.EverythingIsNonNull;

public class HomeFragment extends Fragment implements ChipGroup.OnCheckedChangeListener
{

    private RecyclerView recyclerView;
    private List<Ticker> tickerList, allTickersFiltered;
    private ChipGroup cryptoChipGroup;
    private TextView pairsCount,sortBtn;
    private View view;
    private TickerAdapter adapter;
    private EditText search;
    private boolean sortByUp = true;
    private AlertDialog dialog;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        search = view.findViewById(R.id.search_field);
        recyclerView = view.findViewById(R.id.recycler_view);
        cryptoChipGroup = view.findViewById(R.id.crypto_chip_group);
        pairsCount = view.findViewById(R.id.pairs_count);
        sortBtn = view.findViewById(R.id.sort_btn);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.loading_dialog);
        dialog = builder.create();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), layoutManager.getOrientation()));
        cryptoChipGroup.setOnCheckedChangeListener(this);
        cryptoChipGroup.check(R.id.usdt_chip);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sortBtn.setOnClickListener(v ->
        {
            if(!sortByUp)
            {
                allTickersFiltered.sort(Ticker.sortDown);
                adapter.notifyDataSetChanged();
                sortByUp = !sortByUp;
                sortBtn.setText(getString(R.string.DN));
                sortBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_downward,0,0,0);
            }
            else
            {
                allTickersFiltered.sort(Ticker.sortUp);
                adapter.notifyDataSetChanged();
                sortByUp = !sortByUp;
                sortBtn.setText(getString(R.string.UP));
                sortBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.arrow_upward,0,0,0);
            }
        });
    }


    private void fetchData(String marketChoice)
    {
        Retrofit retrofit = new RetrofitClient().getInstance();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<List<Ticker>> call = retrofitAPI.getAllTickers();

        dialog.show();

        search.setText(null);

        call.enqueue(new Callback<List<Ticker>>() {
            @EverythingIsNonNull
            @Override
            public void onResponse(Call<List<Ticker>> call, Response<List<Ticker>> response)
            {
                tickerList = response.body();
                MainActivity.getActivity().setTicker(tickerList);

                //Filtering list based on the ticker and displaying its info.
                allTickersFiltered = tickerList.stream().filter(ticker -> ticker.getMarket().matches("[A-Z 0-9]+" + marketChoice)).collect(Collectors.toList());
                adapter = new TickerAdapter(view.getContext(), allTickersFiltered, marketChoice);
                recyclerView.setAdapter(adapter);
                pairsCount.setText(String.format(Locale.US, "Pairs Fetched: %d", adapter.getItemCount()));

                dialog.dismiss();
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<Ticker>> call, Throwable t) {
                dialog.dismiss();
                Snackbar.make(view,"Something went wrong...",Snackbar.LENGTH_SHORT).show();
                System.out.println(t.getMessage());
            }
        });

    }

    @Override
    public void onCheckedChanged(ChipGroup group, int checkedId) {
        if (checkedId == R.id.usdt_chip)
            fetchData(getResources().getString(R.string.usdt_ticker));
        else if (checkedId == R.id.btc_chip)
            fetchData(getResources().getString(R.string.btc_ticker));
        else if (checkedId == R.id.inr_chip)
            fetchData(getResources().getString(R.string.inr_ticker));
    }

}