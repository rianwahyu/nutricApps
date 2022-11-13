package com.rigadev.nutricapps.pages.food;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.database.SQLiteHelpers;
import com.rigadev.nutricapps.databinding.ActivityFoodDetailBinding;
import com.rigadev.nutricapps.util.MyConfig;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

public class FoodDetailActivity extends AppCompatActivity {

    Context context = this;
    ActivityFoodDetailBinding binding;

    String idFood="", foodName="", ingredient="", nutrition="", calories="",
            flavoring="", expired="", price="", foodPhoto="";

    SQLiteHelpers sqLiteHelpers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sqLiteHelpers = new SQLiteHelpers(context);

        idFood = getIntent().getStringExtra("idFood");
        foodName = getIntent().getStringExtra("foodName");
        ingredient = getIntent().getStringExtra("ingredient");
        nutrition = getIntent().getStringExtra("nutrition");
        calories = getIntent().getStringExtra("calories");
        flavoring = getIntent().getStringExtra("flavoring");
        expired = getIntent().getStringExtra("expired");
        price = getIntent().getStringExtra("price");
        foodPhoto = getIntent().getStringExtra("foodPhoto");

        binding.toolbar.setTitle("Detail Makanan");
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_white);
        binding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Glide.with(context)
                .load(foodPhoto)
                .apply(new RequestOptions().placeholder(R.drawable.food_placeholder).error(R.drawable.food_placeholder))
                .into(binding.imgMenu);

        binding.textNamaMenu.setText(foodName);
        binding.textDeskripsi.setText(ingredient);
        binding.textNutrisi.setText(nutrition);
        binding.textKalori.setText(calories);
        binding.textHarga.setText(MyConfig.formatNumberComma(price));

        binding.btnTambahCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertToCart();
            }
        });
    }

    private void insertToCart() {
        ContentValues cv = new ContentValues();
        cv.put(SQLiteHelpers.CART_IDFOOD, idFood);
        cv.put(SQLiteHelpers.CART_NAME, foodName);
        cv.put(SQLiteHelpers.CART_PHOTO, foodPhoto);
        cv.put(SQLiteHelpers.CART_QTY, "1");
        cv.put(SQLiteHelpers.CART_PRICE, price);
        sqLiteHelpers.insertCart(cv);

        new AestheticDialog.Builder(
                FoodDetailActivity.this,
                DialogStyle.TOASTER,
                DialogType.SUCCESS)
                .setTitle("Sukses")
                .setMessage("Berhasil menambahkan makanan ke keranjang")
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkCart();
    }

    private void checkCart() {
        String countCartString = sqLiteHelpers.countCart();
        int totalCart = Integer.parseInt(countCartString);
        if (totalCart >=1){
            binding.constraintCart.setVisibility(View.VISIBLE);
            binding.constraintCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context, CartFoodActivity.class));
                }
            });
        }else{
            binding.constraintCart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}