package com.robo.cryptoportfolio.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.robo.cryptoportfolio.Objects.UserInfo;
import com.robo.cryptoportfolio.R;
import com.robo.cryptoportfolio.Retrofit.RetrofitAPI;
import com.robo.cryptoportfolio.Retrofit.RetrofitClient;
import com.robo.cryptoportfolio.HelperClasses.SignatureGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SettingsFragment extends Fragment {

    private View view;
    private SharedPreferences preferences;
    private TextInputLayout apiLayout,secretLayout;
    private Button doneBtn;
    private String api, secret;
    private TextView name,mobile,email;

    public SettingsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        apiLayout = view.findViewById(R.id.api_layout);
        secretLayout = view.findViewById(R.id.secret_layout);
        doneBtn = view.findViewById(R.id.done_btn_frag);
        name = view.findViewById(R.id.name);
        mobile = view.findViewById(R.id.mobile);
        email = view.findViewById(R.id.email);
        preferences = view.getContext().getSharedPreferences(getResources().getString(R.string.shared_pref), Context.MODE_PRIVATE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkPreferences();
    }

    private void checkPreferences()
    {
        if(preferences.getString(getResources().getString(R.string.api_key),null) == null)
        {
            name.setText(getString(R.string.name));
            email.setText(getString(R.string.email));
            mobile.setText(getString(R.string.mobile));
            doneBtn.setOnClickListener(v -> {
                api = apiLayout.getEditText().getText().toString().trim();
                secret = secretLayout.getEditText().getText().toString().trim();
                if(checkEmptyFields(api,secret))
                {
                    savePreferences(api,secret);
                }
            });
        }
        else
        {
            doneBtn.setOnClickListener(v -> {
                api = apiLayout.getEditText().getText().toString().trim();
                secret = secretLayout.getEditText().getText().toString().trim();
                if(checkEmptyFields(api,secret))
                {
                    savePreferences(api,secret);
                }
            });
            api = preferences.getString(getResources().getString(R.string.api_key),null);
            secret = preferences.getString(getResources().getString(R.string.secret_key),null);
            apiLayout.getEditText().setText(api);
            secretLayout.getEditText().setText(secret);
            getUserDetails();
        }
    }

    private boolean checkEmptyFields(String api,String secret)
    {
        String message = "Field can't be empty.";
        if(api.isEmpty() || secret.isEmpty())
        {
            if(api.isEmpty())
            {
                apiLayout.setError(message);
            }
            else {
                secretLayout.setError(message);
            }
            return false;
        }
        return true;
    }

    private void getUserDetails()
    {
        ProgressDialog dialog = new ProgressDialog(view.getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        SignatureGenerator obj = new SignatureGenerator();
        Retrofit retrofit = new RetrofitClient().getInstanceWithClient(obj.getSignature(secret),api);
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<UserInfo> call = retrofitAPI.getUserInfo(obj.getBody());

        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response)
            {
                dialog.dismiss();
                if(response.body() == null)
                {
                    name.setText(getString(R.string.name));
                    email.setText(getString(R.string.email));
                    mobile.setText(getString(R.string.mobile));
                    Snackbar.make(view,"API or Secret Key incorrect.",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                UserInfo userInfo = response.body();
                name.setText(String.format("%s %s",userInfo.getFirstName(),userInfo.getLastName()));
                mobile.setText(String.format("+91 %s",userInfo.getMobileNumber()));
                email.setText(userInfo.getEmail());
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                dialog.dismiss();
                Log.i("Failure",t.getMessage());
                Snackbar.make(view,"Error occurred.",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void savePreferences(String api,String secret)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getResources().getString(R.string.api_key),api);
        editor.putString(getResources().getString(R.string.secret_key),secret);
        editor.apply();
        Snackbar.make(view,"Keys saved.", Snackbar.LENGTH_SHORT).show();
        checkPreferences();
    }
}