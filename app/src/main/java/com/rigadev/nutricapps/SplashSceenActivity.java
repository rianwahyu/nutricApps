package com.rigadev.nutricapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.rigadev.nutricapps.databinding.ActivitySplashSceenBinding;
import com.rigadev.nutricapps.util.SessionManager;

public class SplashSceenActivity extends AppCompatActivity {

    Context context = this;
    ActivitySplashSceenBinding binding;

    final Handler handler = new Handler();
    Runnable runnable;
    int timeWait = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivitySplashSceenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        runnable = new Runnable() {
            @Override
            public void run() {
                if (new SessionManager(context).isLoggedin()){
                    Intent o = new Intent(context, HomeActivity.class);
                    startActivity(o);
                    finish();
                }else {
                    Intent o = new Intent(context, MainActivity.class);
                    startActivity(o);
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, timeWait);
    }
}