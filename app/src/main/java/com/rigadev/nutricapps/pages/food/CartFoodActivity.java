package com.rigadev.nutricapps.pages.food;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rigadev.nutricapps.HomeActivity;
import com.rigadev.nutricapps.LoginActivity;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.adapter.AdapterCart;
import com.rigadev.nutricapps.database.SQLiteHelpers;
import com.rigadev.nutricapps.databinding.ActivityCartFoodBinding;
import com.rigadev.nutricapps.listener.DecreaseClickListener;
import com.rigadev.nutricapps.listener.IncreaseClickListener;
import com.rigadev.nutricapps.model.CartModel;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;
import com.rigadev.nutricapps.util.SessionManager;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFoodActivity extends AppCompatActivity implements DecreaseClickListener, IncreaseClickListener {

    Context context = this;
    ActivityCartFoodBinding binding;

    List<CartModel> listCart = new ArrayList<CartModel>();
    LinearLayoutManager linearLayoutManager;
    AdapterCart adapterCart;

    int printtot=0;
    int priceValue=0;

    int stokValue = 0;
    int diskonValue =0;

    double pricetotSale=0;
    int sumTotalValue=0;
    SQLiteHelpers sqLiteHelpers;

    public static final int REQUEST_MEMBER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ProgressLoadingJIGB.setupLoading = (setup) ->  {
            setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
            setup.message = "Pembuatan Pesanan";//  Center Message
            setup.timer = 0;   // Time of live for progress.
            setup.width = 200; // Optional
            setup.hight = 200; // Optional
        };

        sqLiteHelpers = new SQLiteHelpers(context);

        binding.toolbar.setTitle("Detail Keranjang");
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_white);
        binding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.rcCart.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        binding.rcCart.setLayoutManager(linearLayoutManager);

        adapterCart = new AdapterCart(context, listCart);
        binding.rcCart.setAdapter(adapterCart);
        adapterCart.setDecreaseClickListener(this);
        adapterCart.setIncreaseClickListener(this);

        binding.etAlamatPengiriman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity( new Intent(context, DeliveryChooseAddressActivity.class));
                Intent intent = new Intent(context, DeliveryChooseAddressActivity.class);
                //startActivity(intent);
                startActivityForResult(intent,
                        REQUEST_MEMBER);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sumTotalValue = 0;
        printtot = 0;
        priceValue = 0;
        stokValue = 0;
        initItem();
    }


    @SuppressLint("Range")
    void initItem() {
        listCart.clear();

        Cursor c = sqLiteHelpers.getAllCart();
        Log.d("jumlah", String.valueOf(c.getCount()));

        if (c != null) {
            if (c.getCount()>0){
                binding.linearCartEmpty.setVisibility(View.GONE);
                binding.titleAlamatPengiriman.setVisibility(View.VISIBLE);
                binding.etAlamatPengiriman.setVisibility(View.VISIBLE);
                binding.titleKeranjang.setVisibility(View.VISIBLE);
                binding.rcCart.setVisibility(View.VISIBLE);
                binding.linearTotal.setVisibility(View.VISIBLE);
            }else {
                binding.linearCartEmpty.setVisibility(View.VISIBLE);
                binding.titleAlamatPengiriman.setVisibility(View.GONE);
                binding.etAlamatPengiriman.setVisibility(View.GONE);
                binding.titleKeranjang.setVisibility(View.GONE);
                binding.rcCart.setVisibility(View.GONE);
                binding.linearTotal.setVisibility(View.GONE);
            }


            while (c.moveToNext()) {
                String idCart = c.getString(c.getColumnIndex(SQLiteHelpers.CART_ID));
                String idMenu = c.getString(c.getColumnIndex(SQLiteHelpers.CART_IDFOOD));
                String namaMenu = c.getString(c.getColumnIndex(SQLiteHelpers.CART_NAME));
                String fotoMenu = c.getString(c.getColumnIndex(SQLiteHelpers.CART_PHOTO));
                String jumlah = c.getString(c.getColumnIndex(SQLiteHelpers.CART_QTY));
                String harga = c.getString(c.getColumnIndex(SQLiteHelpers.CART_PRICE));

                stokValue = Integer.parseInt(jumlah);
                priceValue = Integer.parseInt(harga);

                printtot = stokValue * (priceValue);

                CartModel cart = new CartModel(idCart,
                        idMenu,
                        namaMenu,
                        fotoMenu,
                        jumlah,
                        harga,
                        String.valueOf(printtot));

                listCart.add(cart);

                sumTotalValue = sumTotalValue + printtot;
            }
            adapterCart.notifyDataSetChanged();

            binding.textSumTotal.setText("Rp. " +
                    MyConfig.formatNumberComma(String.valueOf(sumTotalValue)));

            binding.btnProsesPesanan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (binding.etAlamatPengiriman.getText().toString().isEmpty()){
                        MyConfig.showToast(context, "Mohon mengisi alamat pengiriman");
                    }else {
                        checkData();
                    }

                }
            });
        } else {
            adapterCart.notifyDataSetChanged();

            binding.linearCartEmpty.setVisibility(View.VISIBLE);
            binding.titleAlamatPengiriman.setVisibility(View.GONE);
            binding.etAlamatPengiriman.setVisibility(View.GONE);
            binding.titleKeranjang.setVisibility(View.GONE);
            binding.rcCart.setVisibility(View.GONE);
            binding.linearTotal.setVisibility(View.GONE);
        }
    }

    @SuppressLint("Range")
    private void checkData() {
        listCart.clear();
        Cursor c = sqLiteHelpers.getAllCart();
        if (c != null) {
            /// inisialisai Array baru
            JSONArray jsonArray = new JSONArray();
            while (c.moveToNext()) {
                String idMenu = c.getString(c.getColumnIndex(SQLiteHelpers.CART_IDFOOD));
                String jumlah = c.getString(c.getColumnIndex(SQLiteHelpers.CART_QTY));
                String harga = c.getString(c.getColumnIndex(SQLiteHelpers.CART_PRICE));

                JSONObject jsonObject = new JSONObject();
                try {
                    //memasukkan object dengan key dan value
                    jsonObject.put("idFood", idMenu);
                    jsonObject.put("qty", jumlah);
                    jsonObject.put("price", harga);
                    //memasukkan objet kedalam array
                    jsonArray.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //konversi array ke string agar mudah dibaca / trace
            final String cartList = jsonArray.toString();
            Log.d("cartList : " , cartList);
            processOrder(cartList);

        }else{
            MyConfig.showToast(context, "Pesanan kosong, mohon memilih menu terlebih dahulu");
        }
    }

    private void processOrder(String cartList) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        ProgressLoadingJIGB.startLoading(context);
        String url = NetworkState.foodApiUrl +"orderFood.php";
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                ProgressLoadingJIGB.finishLoadingJIGB(context);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");
                    if (success == true){
                        sqLiteHelpers.deleteCartAll();
                        MyConfig.showToast(context, message);
                        String foodOrderID = jsonObject.getString("foodOrderID");

                        Intent intent = new Intent(context, FoodPaymentDetailActivity.class);
                        intent.putExtra("foodOrderID", foodOrderID);
                        intent.putExtra("totalPayment", String.valueOf(sumTotalValue));
                        startActivity(intent);
                        finish();

                    }else {
                        new AestheticDialog.Builder(
                                CartFoodActivity.this,
                                DialogStyle.EMOTION,
                                DialogType.ERROR)
                                .setTitle("Error")
                                .setMessage(message)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error_res", error.getLocalizedMessage().toString());
                ProgressLoadingJIGB.finishLoadingJIGB(context);

                new AestheticDialog.Builder(
                        CartFoodActivity.this,
                        DialogStyle.EMOTION,
                        DialogType.ERROR)
                        .setTitle("Error")
                        .setMessage(error.getMessage())
                        .show();

            }
        }){

            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.headers == null)
                {
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                            response.notModified,
                            response.networkTimeMs);
                }

                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUser", new SessionManager(context).getIdUser());
                params.put("totalPayment", String.valueOf(sumTotalValue));
                params.put("cartList", cartList);
                params.put("deliveryAddress", binding.etAlamatPengiriman.getText().toString());
                params.put("token", FirebaseInstanceId.getInstance().getToken());
                return params;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }

    @Override
    public void onClickDecrease(View view, int position, String stokCart) {
        CartModel cart = listCart.get(position);

        int stockInt = Integer.parseInt(stokCart);

        if (stockInt == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Hapus Menu ?");
            builder.setMessage("Apakah anda ingin menghapus menu dari daftar pesanan ?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    sqLiteHelpers.deleteCart(cart.getIdCart());
                    onResume();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SQLiteHelpers.CART_QTY, "1");
                    sqLiteHelpers.updateCart(contentValues, Integer.parseInt(cart.getIdCart()));
                    onResume();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(SQLiteHelpers.CART_QTY, String.valueOf(stokCart));
            sqLiteHelpers.updateCart(contentValues, Integer.parseInt(cart.getIdCart()));

            onResume();
        }
    }


    @Override
    public void onClickIncrease(View view, int position, String stokCart) {
        CartModel cart = listCart.get(position);

        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteHelpers.CART_QTY, String.valueOf(stokCart));
        sqLiteHelpers.updateCart(contentValues, Integer.parseInt(cart.getIdCart()));

        onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_MEMBER :
                if (resultCode == Activity.RESULT_OK){
                    Bundle mExtra = data.getExtras();
                    String address = mExtra.getString("address");
                    binding.etAlamatPengiriman.setText(address);
                }
                break;
        }
    }
}