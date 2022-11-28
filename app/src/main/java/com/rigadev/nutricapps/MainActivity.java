package com.rigadev.nutricapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rigadev.nutricapps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    Context context = this;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}