package com.rigadev.nutricapps.pages.diary.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rigadev.nutricapps.adapter.AdapterMyPlate;
import com.rigadev.nutricapps.databinding.FragmentDiaryPlateBinding;
import com.rigadev.nutricapps.model.MyPlateModel;
import com.rigadev.nutricapps.pages.diary.MyDiaryAddAktifitasActivity;
import com.rigadev.nutricapps.pages.diary.MyDiaryAddPiringkuActivity;
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

public class DiaryPlateFragment extends Fragment {

    FragmentDiaryPlateBinding binding;

    LinearLayoutManager linearLayoutManager;
    AdapterMyPlate adapterMyPlate;
    List<MyPlateModel> listPlate = new ArrayList<MyPlateModel>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDiaryPlateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listPlate = new ArrayList<MyPlateModel>();
        binding.rcPlate.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcPlate.setLayoutManager(linearLayoutManager);
        adapterMyPlate = new AdapterMyPlate(getContext(), listPlate);
        binding.rcPlate.setAdapter(adapterMyPlate);
        //callPLate();

        binding.btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyDiaryAddPiringkuActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        callPLate();
    }

    public void callPLate(){
        listPlate.clear();
        voidOnLoadFood();
        String url = NetworkState.diaryApiUrl+"getPlate.php" ;
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
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

                                        String idPlate = users.getString("idPlate");
                                        String type = users.getString("type");
                                        String foodOrderID = users.getString("foodOrderID");
                                        String idUser = users.getString("idUser");
                                        String name = users.getString("name");
                                        String portion = users.getString("portion");
                                        String calories = users.getString("calories");
                                        String datePlate = users.getString("datePlate");

                                        MyPlateModel dataItem = new MyPlateModel();
                                        dataItem.setIdPlate(idPlate);
                                        dataItem.setType(type);
                                        dataItem.setFoodOrderID(foodOrderID);
                                        dataItem.setIdUser(idUser);
                                        dataItem.setName(name);
                                        dataItem.setCalories(calories);
                                        dataItem.setDatePlate(datePlate);
                                        dataItem.setPortion(portion);
                                        listPlate.add(dataItem);
                                    }
                                    adapterMyPlate.notifyDataSetChanged();
                                }else{
                                    onNotFoundFood();
                                    MyConfig.showToast(getContext(), message);
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
                params.put("idUser",new SessionManager(getContext()).getIdUser());
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
        binding.shimmerPlate.setVisibility(View.VISIBLE);
        binding.rcPlate.setVisibility(View.GONE);
        binding.linearPlateEmpty.setVisibility(View.GONE);
        binding.btnTambah.setVisibility(View.GONE);
    }

    void onFoundFood(){
        binding.shimmerPlate.setVisibility(View.GONE);
        binding.rcPlate.setVisibility(View.VISIBLE);
        binding.linearPlateEmpty.setVisibility(View.GONE);
        binding.btnTambah.setVisibility(View.VISIBLE);
    }

    void onNotFoundFood(){
        binding.shimmerPlate.setVisibility(View.GONE);
        binding.rcPlate.setVisibility(View.GONE);
        binding.linearPlateEmpty.setVisibility(View.VISIBLE);
        binding.btnTambah.setVisibility(View.VISIBLE);
    }

}