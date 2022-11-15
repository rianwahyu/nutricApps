package com.rigadev.nutricapps.pages.food;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.adapter.AdapterFoodV2;
import com.rigadev.nutricapps.adapter.AdapterHistoryFood;
import com.rigadev.nutricapps.databinding.ActivityHistoryOrderBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.FoodModel;
import com.rigadev.nutricapps.model.HistoryFoodModel;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryOrderActivity extends AppCompatActivity implements ItemClickListener {

    Context context = this;
    ActivityHistoryOrderBinding binding;

    LinearLayoutManager linearLayoutFood;
    List<HistoryFoodModel> listFood = new ArrayList<HistoryFoodModel>();
    AdapterHistoryFood adapterHistoryFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listFood = new ArrayList<HistoryFoodModel>();
        binding.rcMakanan.setHasFixedSize(true);
        linearLayoutFood = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.rcMakanan.setLayoutManager(linearLayoutFood);
        adapterHistoryFood = new AdapterHistoryFood(context, listFood);
        binding.rcMakanan.setAdapter(adapterHistoryFood);
        adapterHistoryFood.setClickListener(this);
        callOrderFood();
    }

    public void callOrderFood(){
        listFood.clear();
        voidOnLoadFood();
        String url = NetworkState.foodApiUrl+"getHistoryOrderFood.php" ;
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("category", response);
                        if(!response.isEmpty()){
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                String message = jsonObject.getString("message");
                                if (success == true){
                                    onFoundFood();
                                    String data = jsonObject.getString("data");
                                    JSONArray jsonarray=new JSONArray(data);
                                    for(int i=0;i<jsonarray.length();i++) {
                                        JSONObject users = jsonarray.getJSONObject(i);

                                        String foodOrderID = users.getString("foodOrderID");
                                        String dateOrder = users.getString("dateOrder");
                                        String totalPayment = users.getString("totalPayment");
                                        String statusPayment = users.getString("statusPayment");
                                        String idUser = users.getString("idUser");
                                        String paymentmethod = users.getString("paymentmethod");
                                        String name = users.getString("name");


                                        HistoryFoodModel dataItem = new HistoryFoodModel();
                                        dataItem.setFoodOrderID(foodOrderID);
                                        dataItem.setDateOrder(dateOrder);
                                        dataItem.setTotalPayment(totalPayment);
                                        dataItem.setStatusPayment(statusPayment);
                                        dataItem.setIdUser(idUser);
                                        dataItem.setPaymentmethod(paymentmethod);
                                        dataItem.setName(name);
                                        listFood.add(dataItem);
                                    }
                                    adapterHistoryFood.notifyDataSetChanged();
                                }else{
                                    onNotFoundFood();
                                    MyConfig.showToast(context, message);
                                }

                            } catch (JSONException e) {
                                onNotFoundFood();
                                Log.e("catchException",e.toString());
                                e.printStackTrace();
                            }
                        }
                        else {
                            Log.e("error","Empty Response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }

    void voidOnLoadFood(){
        binding.shimmerMakanan.setVisibility(View.VISIBLE);
        binding.rcMakanan.setVisibility(View.GONE);
        binding.linearFoodEmpty.setVisibility(View.GONE);
    }

    void onFoundFood(){
        binding.shimmerMakanan.setVisibility(View.GONE);
        binding.rcMakanan.setVisibility(View.VISIBLE);
        binding.linearFoodEmpty.setVisibility(View.GONE);
    }

    void onNotFoundFood(){
        binding.shimmerMakanan.setVisibility(View.GONE);
        binding.rcMakanan.setVisibility(View.GONE);
        binding.linearFoodEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view, int position) {

        HistoryFoodModel hfm = listFood.get(position);

        Intent intent = new Intent(context, DetailFoodOrderActivity.class);
        intent.putExtra("foodOrderID", hfm.getFoodOrderID());
        intent.putExtra("statusPayment", hfm.getStatusPayment());
        intent.putExtra("dateOrder", hfm.getDateOrder());
        intent.putExtra("totalPayment", hfm.getTotalPayment());
        intent.putExtra("name", hfm.getName());
        startActivity(intent);

    }
}