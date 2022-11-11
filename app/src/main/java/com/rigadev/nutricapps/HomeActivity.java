package com.rigadev.nutricapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
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
import com.rigadev.nutricapps.adapter.AdapterFood;
import com.rigadev.nutricapps.databinding.ActivityHomeBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.FoodModel;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements ItemClickListener {

    Context context = this;

    ActivityHomeBinding binding;

    LinearLayoutManager linearLayutFood;
    List<FoodModel> listFood = new ArrayList<>();
    AdapterFood adapterFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRC();

    }

    private void initRC() {
        listFood = new ArrayList<FoodModel>();
        binding.rcMakanan.setHasFixedSize(true);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.rcMakanan.setLayoutManager(layoutManager1);
        adapterFood = new AdapterFood(context, listFood);
        binding.rcMakanan.setAdapter(adapterFood);
        adapterFood.setClickListener(this);
        callFood();
    }


    public void callFood(){
        listFood.clear();
        voidOnLoadFood();
        String url = NetworkState.foodApiUrl+"getFood.php" ;
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

                                        String idFood = users.getString("idFood");
                                        String idMerchant = users.getString("idMerchant");
                                        String foodName = users.getString("foodName");
                                        String ingredient = users.getString("ingredient");
                                        String nutrition = users.getString("nutrition");
                                        String calories = users.getString("calories");
                                        String flavoring = users.getString("flavoring");
                                        String expired = users.getString("expired");
                                        String price = users.getString("price");
                                        String foodPhoto = NetworkState.locatedStorage+ users.getString("foodPhoto");
                                        String status = users.getString("status");

                                        FoodModel dataItem = new FoodModel();
                                        dataItem.setIdFood(idFood);
                                        dataItem.setIdMerchant(idMerchant);
                                        dataItem.setFoodName(foodName);
                                        dataItem.setIngredient(ingredient);
                                        dataItem.setNutrition(nutrition);
                                        dataItem.setCalories(calories);
                                        dataItem.setFlavoring(flavoring);
                                        dataItem.setExpired(expired);
                                        dataItem.setPrice(price);
                                        dataItem.setFoodPhoto(foodPhoto);
                                        dataItem.setStatus(status);
                                        listFood.add(dataItem);
                                    }
                                    adapterFood.notifyDataSetChanged();
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
//                        Log.e("error",error.toString());
//                        MyConfig.showToast(context, error.getLocalizedMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("store", new SessionLogin(getActivity()).getStore() );
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
    }

    void onFoundFood(){
        binding.shimmerMakanan.setVisibility(View.GONE);
        binding.rcMakanan.setVisibility(View.VISIBLE);
    }

    void onNotFoundFood(){
        binding.shimmerMakanan.setVisibility(View.GONE);
        binding.rcMakanan.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view, int position) {

    }
}