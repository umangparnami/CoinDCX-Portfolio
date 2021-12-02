package com.robo.cryptoportfolio;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.robo.cryptoportfolio.Fragments.HomeFragment;
import com.robo.cryptoportfolio.Fragments.PortfolioFragment;
import com.robo.cryptoportfolio.Fragments.SettingsFragment;
import com.robo.cryptoportfolio.Objects.MarketDetails;
import com.robo.cryptoportfolio.Objects.Ticker;

import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private BottomNavigationView bottomNav;
    private FragmentManager manager;
    private List<Ticker> tickerList;
    private List<MarketDetails> marketDetails;
    private static MainActivity activity = null;
    private Fragment fragment;
    private boolean networkAvailable;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);
        manager = getSupportFragmentManager();
        fragment = new HomeFragment();
        manager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home)
            {
                fragment = new HomeFragment();
            }
            else if(item.getItemId() == R.id.portfolio)
            {
                fragment = new PortfolioFragment();
            }
            else if(item.getItemId() == R.id.settings)
            {
                fragment = new SettingsFragment();
            }
            manager.beginTransaction().replace(R.id.frame_layout,fragment).commit();
            return true;
        });
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        Network network = connectivityManager.getActiveNetwork();
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        networkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        return networkAvailable;
    }

    public static MainActivity getActivity()
    {
        if(activity == null)
        {
            activity = new MainActivity();
            return activity;
        }
        return activity;
    }

    public void setTicker(List<Ticker> tickerList)
    {
        this.tickerList = tickerList;
    }

    public List<Ticker> getTicker()
    {
        return tickerList;
    }

    public List<MarketDetails> getMarketDetails() {
        return marketDetails;
    }

    public void setMarketDetails(List<MarketDetails> marketDetails) {
        this.marketDetails = marketDetails;
    }
}