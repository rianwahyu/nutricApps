package com.rigadev.nutricapps.pages.doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.databinding.ActivityDetailDoctorBinding;

public class DetailDoctorActivity extends AppCompatActivity {

    Context context = this;
    ActivityDetailDoctorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setTitle("Detail Dokter");
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_white);
        binding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}