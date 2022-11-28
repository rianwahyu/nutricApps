package com.rigadev.nutricapps.pages.food;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.rigadev.nutricapps.adapter.AdapterAddress;
import com.rigadev.nutricapps.adapter.AdapterNotifikasi;
import com.rigadev.nutricapps.databinding.ActivityDeliveryChooseAddressBinding;
import com.rigadev.nutricapps.databinding.DialogAddressBinding;
import com.rigadev.nutricapps.databinding.DialogFeedbackBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.listener.ItemUbahClickListener;
import com.rigadev.nutricapps.model.AddressModel;
import com.rigadev.nutricapps.model.HistoryFoodModel;
import com.rigadev.nutricapps.model.NotifikasiModel;
import com.rigadev.nutricapps.pages.profile.MyProfileActivity;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;
import com.rigadev.nutricapps.util.SessionManager;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryChooseAddressActivity extends AppCompatActivity implements ItemClickListener, ItemUbahClickListener {

    Context context = this;

    ActivityDeliveryChooseAddressBinding binding;

    LinearLayoutManager linearLayoutManager;
    AdapterAddress adapterAddress;
    List<AddressModel> listAddress = new ArrayList<AddressModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeliveryChooseAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.rcAddress.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rcAddress.setLayoutManager(linearLayoutManager);
        adapterAddress = new AdapterAddress(context, listAddress);
        binding.rcAddress.setAdapter(adapterAddress);
        adapterAddress.setClickListener(this);
        adapterAddress.setUbahClickListener(this);

        binding.btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTambah();
            }
        });

        //callAddress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callAddress();
    }

    public void callAddress(){
        listAddress.clear();
        voidOnLoadFood();
        String url = NetworkState.otherApiUrl+"getAddress.php" ;
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

                                        String idAddress = users.getString("idAddress");
                                        String address = users.getString("address");


                                        AddressModel dataItem = new AddressModel();
                                        dataItem.setAddress(address);
                                        dataItem.setIdAddress(idAddress);
                                        listAddress.add(dataItem);
                                    }
                                    adapterAddress.notifyDataSetChanged();
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
                params.put("idUser", new SessionManager(context).getIdUser());
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
        binding.shimmerAddress.setVisibility(View.VISIBLE);
        binding.rcAddress.setVisibility(View.GONE);
        binding.linearAddressEmpty.setVisibility(View.GONE);
        binding.btnTambah.setVisibility(View.GONE);
    }

    void onFoundFood(){
        binding.shimmerAddress.setVisibility(View.GONE);
        binding.rcAddress.setVisibility(View.VISIBLE);
        binding.linearAddressEmpty.setVisibility(View.GONE);
        binding.btnTambah.setVisibility(View.VISIBLE);
    }

    void onNotFoundFood(){
        binding.shimmerAddress.setVisibility(View.GONE);
        binding.rcAddress.setVisibility(View.GONE);
        binding.linearAddressEmpty.setVisibility(View.VISIBLE);
        binding.btnTambah.setVisibility(View.VISIBLE);
    }


    DialogAddressBinding dialogAddressBinding;
    private void dialogTambah() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        dialogAddressBinding = DialogAddressBinding.inflate(
                LayoutInflater.from(context));
        alert.setView(dialogAddressBinding.getRoot());
        alert.create();
        alert.setTitle("Tambah Alamat Baru");

        final AlertDialog dialog = alert.create();
        dialogAddressBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogAddressBinding.etFeedBack.getText().toString().isEmpty()){
                    MyConfig.showToast(context, "Mohon mengisi alamat");
                }else{
                    ProgressLoadingJIGB.setupLoading = (setup) ->  {
                        setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
                        setup.message = "Menambahkan alamat";//  Center Message
                        setup.timer = 0;   // Time of live for progress.
                        setup.width = 200; // Optional
                        setup.hight = 200; // Optional
                    };
                    ProgressLoadingJIGB.startLoading(context);
                    String url = NetworkState.otherApiUrl+"addAddress.php" ;
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
                                                ProgressLoadingJIGB.finishLoadingJIGB(context);
                                                dialog.dismiss();
                                                new AestheticDialog.Builder(
                                                        DeliveryChooseAddressActivity.this,
                                                        DialogStyle.EMOTION,
                                                        DialogType.SUCCESS)
                                                        .setTitle("Sukses")
                                                        .setMessage(message)
                                                        .setOnClickListener(new OnDialogClickListener() {
                                                            @Override
                                                            public void onClick(@NonNull AestheticDialog.Builder builder) {
                                                                builder.dismiss();
                                                            }
                                                        })
                                                        .setCancelable(true)
                                                        .show();

                                                onResume();
                                            }else{
                                                ProgressLoadingJIGB.finishLoadingJIGB(context);
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
                            params.put("idUser", new SessionManager(context).getIdUser());
                            params.put("address", dialogAddressBinding.etFeedBack.getText().toString());
                            return params;
                        }
                    };
                    int socketTimeout = 30000;
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    stringRequest.setRetryPolicy(policy);
                    mRequestQueue.add(stringRequest);
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        AddressModel am = listAddress.get(position);

        Bundle mBundle = new Bundle();
        mBundle.putString("address", am.getAddress());

        Intent mBackIntent = new Intent();
        mBackIntent.putExtras(mBundle);
        setResult(Activity.RESULT_OK, mBackIntent);
        finish();
    }

    DialogAddressBinding dialogUbahAddressBinding;
    @Override
    public void onUbahClick(View view, int position) {
        AddressModel am = listAddress.get(position);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        dialogUbahAddressBinding = DialogAddressBinding.inflate(
                LayoutInflater.from(context));
        alert.setView(dialogUbahAddressBinding.getRoot());
        alert.create();
        alert.setTitle("Ubah Alamat Pengiriman");

        dialogUbahAddressBinding.etFeedBack.setText(am.getAddress());

        final AlertDialog dialog = alert.create();
        dialogUbahAddressBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogUbahAddressBinding.etFeedBack.getText().toString().isEmpty()){
                    MyConfig.showToast(context, "Mohon mengisi alamat");
                }else{
                    ProgressLoadingJIGB.setupLoading = (setup) ->  {
                        setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
                        setup.message = "Mengubah alamat";//  Center Message
                        setup.timer = 0;   // Time of live for progress.
                        setup.width = 200; // Optional
                        setup.hight = 200; // Optional
                    };
                    ProgressLoadingJIGB.startLoading(context);
                    String url = NetworkState.otherApiUrl+"updateAddress.php" ;
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
                                                ProgressLoadingJIGB.finishLoadingJIGB(context);
                                                dialog.dismiss();
                                                new AestheticDialog.Builder(
                                                        DeliveryChooseAddressActivity.this,
                                                        DialogStyle.EMOTION,
                                                        DialogType.SUCCESS)
                                                        .setTitle("Sukses")
                                                        .setMessage(message)
                                                        .setOnClickListener(new OnDialogClickListener() {
                                                            @Override
                                                            public void onClick(@NonNull AestheticDialog.Builder builder) {
                                                                builder.dismiss();
                                                            }
                                                        })
                                                        .setCancelable(true)
                                                        .show();

                                                onResume();
                                            }else{
                                                ProgressLoadingJIGB.finishLoadingJIGB(context);
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
                            params.put("idAddress", am.getIdAddress());
                            params.put("address", dialogUbahAddressBinding.etFeedBack.getText().toString());
                            return params;
                        }
                    };
                    int socketTimeout = 30000;
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    stringRequest.setRetryPolicy(policy);
                    mRequestQueue.add(stringRequest);
                }
            }
        });
        dialog.show();
    }
}