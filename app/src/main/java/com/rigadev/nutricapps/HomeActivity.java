package com.rigadev.nutricapps;

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
import com.rigadev.nutricapps.adapter.AdapterBlog;
import com.rigadev.nutricapps.adapter.AdapterDoctor;
import com.rigadev.nutricapps.adapter.AdapterFood;
import com.rigadev.nutricapps.databinding.ActivityHomeBinding;
import com.rigadev.nutricapps.listener.ItemBlogClickListener;
import com.rigadev.nutricapps.listener.ItemClickListener;
import com.rigadev.nutricapps.listener.ItemDoctorClickListener;
import com.rigadev.nutricapps.model.BlogModel;
import com.rigadev.nutricapps.model.DoctorModel;
import com.rigadev.nutricapps.model.FoodModel;
import com.rigadev.nutricapps.pages.blog.BlogDetailActivity;
import com.rigadev.nutricapps.pages.diary.MyDiaryActivity;
import com.rigadev.nutricapps.pages.doctor.DaftarDokterActivity;
import com.rigadev.nutricapps.pages.doctor.DetailDoctorActivity;
import com.rigadev.nutricapps.pages.food.DaftarMakananActivity;
import com.rigadev.nutricapps.pages.food.FoodDetailActivity;
import com.rigadev.nutricapps.pages.nutrition.CheckNutritionActivity;
import com.rigadev.nutricapps.pages.profile.MyProfileActivity;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements ItemClickListener, ItemDoctorClickListener, ItemBlogClickListener {

    Context context = this;

    ActivityHomeBinding binding;

    LinearLayoutManager linearLayutFood, linearLayutDoctor, linearLayoutBlog;
    List<FoodModel> listFood = new ArrayList<>();
    AdapterFood adapterFood;
    List<DoctorModel> listDoctor = new ArrayList<DoctorModel>();
    AdapterDoctor adapterDoctor;
    List<BlogModel> listBlog = new ArrayList<BlogModel>();
    AdapterBlog adapterBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initRC();

        binding.textLihatSemuaDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, DaftarDokterActivity.class));
            }
        });

        binding.linearDokterBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, DaftarDokterActivity.class));
            }
        });

        binding.textLihatSemuaMakanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, DaftarMakananActivity.class));
            }
        });

        binding.linearMakananBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, DaftarMakananActivity.class));
            }
        });

        binding.linearMyDiaryBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MyDiaryActivity.class));
            }
        });

        binding.linearNutritionBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, CheckNutritionActivity.class));
            }
        });

        binding.linearMyProfileBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MyProfileActivity.class));
            }
        });


    }

    private void initRC() {
        listFood = new ArrayList<FoodModel>();
        binding.rcMakanan.setHasFixedSize(true);
        linearLayutFood = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.rcMakanan.setLayoutManager(linearLayutFood);
        adapterFood = new AdapterFood(context, listFood);
        binding.rcMakanan.setAdapter(adapterFood);
        adapterFood.setClickListener(this);
        callFood();

        listDoctor = new ArrayList<DoctorModel>();
        binding.rcDokter.setHasFixedSize(true);
        linearLayutDoctor = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.rcDokter.setLayoutManager(linearLayutDoctor);
        adapterDoctor = new AdapterDoctor(context, listDoctor);
        binding.rcDokter.setAdapter(adapterDoctor);
        adapterDoctor.setDoctorClickListener(this);
        callDoctor();


        listBlog = new ArrayList<BlogModel>();
        binding.rcBlog.setHasFixedSize(true);
        linearLayoutBlog = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.rcBlog.setLayoutManager(linearLayoutBlog);
        adapterBlog = new AdapterBlog(context, listBlog);
        binding.rcBlog.setAdapter(adapterBlog);
        adapterBlog.setBlogClickListener(this);
        callBlog();
    }


    public void callFood(){
        listFood.clear();
        voidOnLoadFood();
        String url = NetworkState.foodApiUrl+"getFoodHome.php" ;
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
                                        String foodPhoto = NetworkState.locatedStorage+"/food/"+ users.getString("foodPhoto");
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

    public void callDoctor(){
        listDoctor.clear();
        voidOnLoadDoctor();
        String url = NetworkState.doctorApiUrl+"getDoctorHome.php" ;
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

    public void callBlog(){
        listBlog.clear();
        voidOnLoadBlog();
        String url = NetworkState.blogApiUrl+"getBlog.php" ;
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
                                    onFoundBlog();
                                    String data = jsonObject.getString("data");
                                    JSONArray jsonarray=new JSONArray(data);
                                    for(int i=0;i<jsonarray.length();i++) {
                                        JSONObject users = jsonarray.getJSONObject(i);

                                        String blogID = users.getString("blogID");
                                        String titleBlog = users.getString("titleBlog");
                                        String shortDesciprion = users.getString("shortDesciprion");
                                        String urlBlog = users.getString("urlBlog");
                                        String imageBlog = NetworkState.locatedStorage+"/blog/"+ users.getString("imageBlog");

                                        BlogModel dataItem = new BlogModel();
                                        dataItem.setBlogID(blogID);
                                        dataItem.setTitleBlog(titleBlog);
                                        dataItem.setShortDescription(shortDesciprion);
                                        dataItem.setUrlBlog(urlBlog);
                                        dataItem.setImageBlog(imageBlog);
                                        listBlog.add(dataItem);
                                    }
                                    adapterBlog.notifyDataSetChanged();
                                }else{
                                    onNotFoundBlog();
                                    MyConfig.showToast(context, message);
                                }

                            } catch (JSONException e) {
                                onNotFoundBlog();
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

    void voidOnLoadBlog(){
        binding.shimmerBlog.setVisibility(View.VISIBLE);
        binding.rcBlog.setVisibility(View.GONE);
        binding.linearBlogEmpty.setVisibility(View.GONE);
    }

    void onFoundBlog(){
        binding.shimmerBlog.setVisibility(View.GONE);
        binding.rcBlog.setVisibility(View.VISIBLE);
        binding.linearBlogEmpty.setVisibility(View.GONE);
    }

    void onNotFoundBlog(){
        binding.shimmerBlog.setVisibility(View.GONE);
        binding.rcBlog.setVisibility(View.GONE);
        binding.linearBlogEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view, int position) {
        FoodModel fm = listFood.get(position);

        Intent intent = new Intent(context, FoodDetailActivity.class);
        intent.putExtra("idFood", fm.getIdFood());
        intent.putExtra("foodName", fm.getFoodName());
        intent.putExtra("ingredient", fm.getIngredient());
        intent.putExtra("nutrition", fm.getNutrition());
        intent.putExtra("calories", fm.getCalories());
        intent.putExtra("flavoring", fm.getFlavoring());
        intent.putExtra("expired", fm.getExpired());
        intent.putExtra("price", fm.getPrice());
        intent.putExtra("foodPhoto", fm.getFoodPhoto());
        startActivity(intent);

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
        startActivity(intent);

    }

    @Override
    public void onBlogClick(View view, int position) {
        BlogModel bm = listBlog.get(position);

        Intent intent = new Intent(context, BlogDetailActivity.class);
        intent.putExtra("blogID", bm.getBlogID());
        intent.putExtra("titleBlog", bm.getTitleBlog());
        intent.putExtra("shortDesciprion", bm.getShortDescription());
        intent.putExtra("urlBlog", bm.getUrlBlog());
        startActivity(intent);
    }
}