package com.rigadev.nutricapps.pages.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.databinding.ActivityMyDiaryBinding;
import com.rigadev.nutricapps.pages.diary.fragment.SectionsPagerDiaryAdapter;

public class MyDiaryActivity extends AppCompatActivity {

    Context context = this;
    ActivityMyDiaryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyDiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerDiaryAdapter sectionsPagerAdapter = new SectionsPagerDiaryAdapter(this, getSupportFragmentManager());
        binding.viewPager.setAdapter(sectionsPagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewPager);

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
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