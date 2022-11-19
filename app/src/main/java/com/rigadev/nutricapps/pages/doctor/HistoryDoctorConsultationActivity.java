package com.rigadev.nutricapps.pages.doctor;

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
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.adapter.AdapterHistoryConsultation;
import com.rigadev.nutricapps.adapter.AdapterHistoryFood;
import com.rigadev.nutricapps.databinding.ActivityHistoryDoctorConsultationBinding;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.model.HistoryDoctorModel;
import com.rigadev.nutricapps.model.HistoryFoodModel;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;
import com.rigadev.nutricapps.util.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryDoctorConsultationActivity extends AppCompatActivity implements ItemClickListener {

    Context context = this;
    ActivityHistoryDoctorConsultationBinding binding;

    LinearLayoutManager linearLayoutManager;
    AdapterHistoryConsultation adapterHistoryConsultation;
    List<HistoryDoctorModel> listConsultation = new ArrayList<HistoryDoctorModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryDoctorConsultationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listConsultation = new ArrayList<HistoryDoctorModel>();
        binding.rcDokter.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.rcDokter.setLayoutManager(linearLayoutManager);
        adapterHistoryConsultation = new AdapterHistoryConsultation(context, listConsultation);
        binding.rcDokter.setAdapter(adapterHistoryConsultation);
        adapterHistoryConsultation.setClickListener(this);
        callOrderFood();
    }

    public void callOrderFood(){
        listConsultation.clear();
        voidOnLoadFood();
        String url = NetworkState.doctorApiUrl+"getHistoryDoctorConsultation.php" ;
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

                                        String idConsultation = users.getString("idConsultation");
                                        String idDoctor = users.getString("idDoctor");
                                        String doctorName = users.getString("doctorName");
                                        String idUser = users.getString("idUser");
                                        String patientName = users.getString("patientName");
                                        String whatsappNumber = users.getString("whatsappNumber");
                                        String fee = users.getString("fee");
                                        String statusConsultation = users.getString("statusConsultation");
                                        String dateConsultation = users.getString("dateConsultation");
                                        String ketStatus = users.getString("ketStatus");


                                        HistoryDoctorModel dataItem = new HistoryDoctorModel();
                                        dataItem.setIdConsultation(idConsultation);
                                        dataItem.setIdDoctor(idDoctor);
                                        dataItem.setDoctorName(doctorName);
                                        dataItem.setIdUser(idUser);
                                        dataItem.setPatientName(patientName);
                                        dataItem.setWhatsappNumber(whatsappNumber);
                                        dataItem.setFee(fee);
                                        dataItem.setStatusConsultation(statusConsultation);
                                        dataItem.setDateConsultation(dateConsultation);
                                        dataItem.setKetStatus(ketStatus);
                                        listConsultation.add(dataItem);
                                    }
                                    adapterHistoryConsultation.notifyDataSetChanged();
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
        binding.shimmerDokter.setVisibility(View.VISIBLE);
        binding.rcDokter.setVisibility(View.GONE);
        binding.linearDokterEmpty.setVisibility(View.GONE);
    }

    void onFoundFood(){
        binding.shimmerDokter.setVisibility(View.GONE);
        binding.rcDokter.setVisibility(View.VISIBLE);
        binding.linearDokterEmpty.setVisibility(View.GONE);
    }

    void onNotFoundFood(){
        binding.shimmerDokter.setVisibility(View.GONE);
        binding.rcDokter.setVisibility(View.GONE);
        binding.linearDokterEmpty.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view, int position) {
        HistoryDoctorModel hdm = listConsultation.get(position);

    }
}