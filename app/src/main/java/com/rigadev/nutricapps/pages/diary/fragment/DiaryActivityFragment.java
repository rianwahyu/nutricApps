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
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.adapter.AdapterMyActivity;
import com.rigadev.nutricapps.adapter.AdapterMyPlate;
import com.rigadev.nutricapps.databinding.FragmentDiaryActivityBinding;
import com.rigadev.nutricapps.databinding.FragmentDiaryPlateBinding;
import com.rigadev.nutricapps.model.MyActivityModel;
import com.rigadev.nutricapps.model.MyPlateModel;
import com.rigadev.nutricapps.pages.diary.MyDiaryActivity;
import com.rigadev.nutricapps.pages.diary.MyDiaryAddAktifitasActivity;
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

public class DiaryActivityFragment extends Fragment {

    FragmentDiaryActivityBinding binding;
    LinearLayoutManager linearLayoutManager;
    AdapterMyActivity adapterMyActivity;
    List<MyActivityModel> listMyActivity = new ArrayList<MyActivityModel>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDiaryActivityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listMyActivity = new ArrayList<MyActivityModel>();
        binding.rcActivity.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcActivity.setLayoutManager(linearLayoutManager);
        adapterMyActivity = new AdapterMyActivity(getContext(), listMyActivity);
        binding.rcActivity.setAdapter(adapterMyActivity);
        //callMyActivity();

        binding.btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyDiaryAddAktifitasActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        callMyActivity();
    }

    public void callMyActivity(){
        listMyActivity.clear();
        voidOnLoadFood();
        String url = NetworkState.diaryApiUrl+"getActivity.php" ;
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

                                        String idActivity = users.getString("idActivity");
                                        String name = users.getString("name");
                                        String dateActivity = users.getString("dateActivity");
                                        String calories = users.getString("calories");
                                        String idUser = users.getString("idUser");


                                        MyActivityModel dataItem = new MyActivityModel();
                                        dataItem.setIdActivity(idActivity);
                                        dataItem.setDateActivity(dateActivity);
                                        dataItem.setIdUser(idUser);
                                        dataItem.setName(name);
                                        dataItem.setCalories(calories);
                                        listMyActivity.add(dataItem);
                                    }
                                    adapterMyActivity.notifyDataSetChanged();
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
        binding.shimmerActivity.setVisibility(View.VISIBLE);
        binding.rcActivity.setVisibility(View.GONE);
        binding.linearActivityEmpty.setVisibility(View.GONE);
        binding.btnTambah.setVisibility(View.GONE);
    }

    void onFoundFood(){
        binding.shimmerActivity.setVisibility(View.GONE);
        binding.rcActivity.setVisibility(View.VISIBLE);
        binding.linearActivityEmpty.setVisibility(View.GONE);
        binding.btnTambah.setVisibility(View.VISIBLE);
    }

    void onNotFoundFood(){
        binding.shimmerActivity.setVisibility(View.GONE);
        binding.rcActivity.setVisibility(View.GONE);
        binding.linearActivityEmpty.setVisibility(View.VISIBLE);
        binding.btnTambah.setVisibility(View.VISIBLE);
    }
}