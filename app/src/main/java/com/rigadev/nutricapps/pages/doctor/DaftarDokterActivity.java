package com.rigadev.nutricapps.pages.doctor;

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
import com.rigadev.nutricapps.adapter.AdapterDoctor;
import com.rigadev.nutricapps.adapter.AdapterDoctorV2;
import com.rigadev.nutricapps.databinding.ActivityDaftarDokterBinding;
import com.rigadev.nutricapps.listener.ItemDoctorClickListener;
import com.rigadev.nutricapps.model.DoctorModel;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaftarDokterActivity extends AppCompatActivity implements ItemDoctorClickListener {

    Context context = this;
    ActivityDaftarDokterBinding binding;

    LinearLayoutManager linearLayoutDoctor;
    List<DoctorModel> listDoctor = new ArrayList<DoctorModel>();
    AdapterDoctorV2 adapterDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDaftarDokterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listDoctor = new ArrayList<DoctorModel>();
        binding.rcDokter.setHasFixedSize(true);
        linearLayoutDoctor = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.rcDokter.setLayoutManager(linearLayoutDoctor);
        adapterDoctor = new AdapterDoctorV2(context, listDoctor);
        binding.rcDokter.setAdapter(adapterDoctor);
        adapterDoctor.setDoctorClickListener(this);
        callDoctor();

        binding.imgHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, HistoryDoctorConsultationActivity.class));

            }
        });
    }

    public void callDoctor(){
        listDoctor.clear();
        voidOnLoadDoctor();
        String url = NetworkState.doctorApiUrl+"getDoctor.php" ;
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
                                    onFoundDoctor();
                                    String data = jsonObject.getString("data");
                                    JSONArray jsonarray=new JSONArray(data);
                                    for(int i=0;i<jsonarray.length();i++) {
                                        JSONObject users = jsonarray.getJSONObject(i);

                                        String idDoctor = users.getString("idDoctor");
                                        String fullname = users.getString("fullname");
                                        String phoneNumber = users.getString("phoneNumber");
                                        String specialist = users.getString("specialist");
                                        String linkInstagram = users.getString("linkInstagram");
                                        String linkLinkedIn = users.getString("linkLinkedIn");
                                        String shortBiography = users.getString("shortBiography");
                                        String fee = users.getString("fee");
                                        String doctorPhoto = NetworkState.locatedStorage+"/doctor/"+ users.getString("doctorPhoto");
                                        String status = users.getString("status");

                                        String practicePlace = users.getString("practicePlace");
                                        String dayPractice = users.getString("dayPractice");
                                        String timePractice = users.getString("timePractice");

                                        DoctorModel dataItem = new DoctorModel();
                                        dataItem.setIdDoctor(idDoctor);
                                        dataItem.setFullname(fullname);
                                        dataItem.setPhoneNumber(phoneNumber);
                                        dataItem.setSpecialist(specialist);
                                        dataItem.setLinkInstagram(linkInstagram);
                                        dataItem.setLinkLinkedIn(linkLinkedIn);
                                        dataItem.setShortBiography(shortBiography);
                                        dataItem.setFee(fee);
                                        dataItem.setDoctorPhoto(doctorPhoto);
                                        dataItem.setPracticePlace(practicePlace);
                                        dataItem.setDayPractice(dayPractice);
                                        dataItem.setTimePractice(timePractice);
                                        listDoctor.add(dataItem);
                                    }
                                    adapterDoctor.notifyDataSetChanged();
                                }else{
                                    onNotFoundDoctor();
                                    MyConfig.showToast(context, message);
                                }

                            } catch (JSONException e) {
                                onNotFoundDoctor();
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

    void voidOnLoadDoctor(){
        binding.shimmerDokter.setVisibility(View.VISIBLE);
        binding.rcDokter.setVisibility(View.GONE);
        binding.linearDokterEmpty.setVisibility(View.GONE);
    }

    void onFoundDoctor(){
        binding.shimmerDokter.setVisibility(View.GONE);
        binding.rcDokter.setVisibility(View.VISIBLE);
        binding.linearDokterEmpty.setVisibility(View.GONE);
    }

    void onNotFoundDoctor(){
        binding.shimmerDokter.setVisibility(View.GONE);
        binding.rcDokter.setVisibility(View.GONE);
        binding.linearDokterEmpty.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDoctorClick(View view, int position) {
        DoctorModel dm = listDoctor.get(position);

        Intent intent = new Intent(context, DetailDoctorActivity.class);
        intent.putExtra("idDoctor", dm.getIdDoctor());
        intent.putExtra("doctorName", dm.getFullname());
        intent.putExtra("shortBiography", dm.getShortBiography());
        intent.putExtra("specialist", dm.getSpecialist());
        intent.putExtra("doctorPhoto", dm.getDoctorPhoto());
        intent.putExtra("phoneNumber", dm.getPhoneNumber());
        intent.putExtra("linkInstagram", dm.getLinkInstagram());
        intent.putExtra("linkLinkedIn", dm.getLinkLinkedIn());
        intent.putExtra("fee", dm.getFee());
        intent.putExtra("dayPractice", dm.getDayPractice());
        intent.putExtra("timePractice", dm.getTimePractice());
        intent.putExtra("practicePlace", dm.getPracticePlace());
        startActivity(intent);
    }
}