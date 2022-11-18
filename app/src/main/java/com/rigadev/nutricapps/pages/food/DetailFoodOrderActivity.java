package com.rigadev.nutricapps.pages.food;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
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
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.adapter.AdapteOrderDetail;
import com.rigadev.nutricapps.adapter.AdapterHistoryFood;
import com.rigadev.nutricapps.databinding.ActivityDetailFoodOrderBinding;
import com.rigadev.nutricapps.model.HistoryFoodModel;
import com.rigadev.nutricapps.model.OrderFoodDetailModel;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailFoodOrderActivity extends AppCompatActivity {

    Context context = this;
    ActivityDetailFoodOrderBinding binding;

    String foodOrderID, statusPayment, dateOrder, totalPayment, name, ketStatus;
    LinearLayoutManager linearLayoutManager;
    List<OrderFoodDetailModel> listOrder = new ArrayList<OrderFoodDetailModel>();
    AdapteOrderDetail adapteOrderDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailFoodOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        foodOrderID = getIntent().getStringExtra("foodOrderID");
        statusPayment = getIntent().getStringExtra("statusPayment");
        dateOrder = getIntent().getStringExtra("dateOrder");
        totalPayment = getIntent().getStringExtra("totalPayment");
        name = getIntent().getStringExtra("name");
        ketStatus = getIntent().getStringExtra("ketStatus");

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.textOrderId.setText(foodOrderID);
        binding.textStatusPayment.setText(statusPayment);
        binding.textTanggalOrder.setText(dateOrder);
        binding.textTotalPayment.setText("Rp. "+MyConfig.formatNumberComma(totalPayment));
        binding.textKetStatus.setText(ketStatus);

        listOrder = new ArrayList<OrderFoodDetailModel>();
        binding.rcMakanan.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.rcMakanan.setLayoutManager(linearLayoutManager);
        adapteOrderDetail = new AdapteOrderDetail(context, listOrder);
        binding.rcMakanan.setAdapter(adapteOrderDetail);
        callOrder();

        if (statusPayment.equals("Pending")){
            binding.btnAksi.setText("Bayar Pesanan");
            binding.btnAksi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FoodPaymentDetailActivity.class);
                    intent.putExtra("foodOrderID", foodOrderID);
                    intent.putExtra("totalPayment", String.valueOf(totalPayment));
                    startActivity(intent);
                    finish();
                }
            });
        }else if (statusPayment.equals("Diterima")) {
            binding.btnAksi.setText("");
            binding.btnAksi.setVisibility(View.GONE);
        }else if (statusPayment.equals("Dibayar")){
            binding.btnAksi.setText("");
            binding.btnAksi.setVisibility(View.GONE);
        }else if (statusPayment.equals("Dikirim")){
            binding.btnAksi.setText("Diterima");
            binding.btnAksi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Konfirmasi Penerimaan");
                    builder.setMessage("Apakah anda ingin mengkonfirmasi penerimaan makanan ?");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            processConfirmOrder();
                        }
                    });
                    builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create();
                    builder.show();
                }
            });
        }
    }

    private void callOrder() {
        listOrder.clear();
        String url = NetworkState.foodApiUrl+"getDetailHistoryOrder.php" ;
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

                                    String data = jsonObject.getString("data");
                                    JSONArray jsonarray=new JSONArray(data);
                                    for(int i=0;i<jsonarray.length();i++) {
                                        JSONObject users = jsonarray.getJSONObject(i);

                                        String foodOrderID = users.getString("foodOrderID");
                                        String qty = users.getString("qty");
                                        String calories = users.getString("calories");
                                        String name = users.getString("name");
                                        String price = users.getString("price");


                                        OrderFoodDetailModel dataItem = new OrderFoodDetailModel();
                                        dataItem.setFoodOrderID(foodOrderID);
                                        dataItem.setQty(qty);
                                        dataItem.setCalories(calories);
                                        dataItem.setName(name);
                                        dataItem.setPrice(price);
                                        listOrder.add(dataItem);
                                    }
                                    adapteOrderDetail.notifyDataSetChanged();
                                }else{

                                    MyConfig.showToast(context, message);
                                }

                            } catch (JSONException e) {

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
                params.put("foodOrderID", foodOrderID);
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }

    private void processConfirmOrder() {
        ProgressLoadingJIGB.setupLoading = (setup) ->  {
            setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
            setup.message = "Konirmasi Penerimaan";//  Center Message
            setup.timer = 0;   // Time of live for progress.
            setup.width = 200; // Optional
            setup.hight = 200; // Optional
        };
        ProgressLoadingJIGB.startLoading(context);
        String url = NetworkState.foodApiUrl+"updateConfirmDelivery.php" ;
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ProgressLoadingJIGB.finishLoadingJIGB(context);
                        Log.d("category", response);
                        if(!response.isEmpty()){
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                String message = jsonObject.getString("message");
                                if (success == true){
                                    MyConfig.showToast(context, message);
                                    onBackPressed();
                                }else{
                                    MyConfig.showToast(context, message);
                                }

                            } catch (JSONException e) {

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
                        ProgressLoadingJIGB.finishLoadingJIGB(context);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("foodOrderID", foodOrderID);
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}