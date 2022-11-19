package com.rigadev.nutricapps.pages.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.rigadev.nutricapps.MainActivity;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.databinding.ActivityLoginBinding;
import com.rigadev.nutricapps.databinding.ActivityMyProfileBinding;
import com.rigadev.nutricapps.databinding.DialogDetailNotifikasiBinding;
import com.rigadev.nutricapps.databinding.DialogFeedbackBinding;
import com.rigadev.nutricapps.model.DoctorModel;
import com.rigadev.nutricapps.pages.food.HistoryOrderActivity;
import com.rigadev.nutricapps.pages.notification.NotifikasiActivity;
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

import java.util.HashMap;
import java.util.Map;

public class MyProfileActivity extends AppCompatActivity {

    Context context = this;
    ActivityMyProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.linearPesananSaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,HistoryOrderActivity.class));
            }
        });

        binding.linearEditProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, EditMyProfileActivity.class));
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Logout");
                builder.setMessage("Apakah anda ingin keluar dari akun anda");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        new SessionManager(context).logOut();
                        startActivity(new Intent(context, MainActivity.class));
                        finish();
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

        getMyName();

        binding.linearFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFeedBack();
            }
        });

        binding.linearNotifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, NotifikasiActivity.class));
            }
        });
    }

    DialogFeedbackBinding dialogFeedbackBinding;
    private void dialogFeedBack() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        dialogFeedbackBinding = DialogFeedbackBinding.inflate(
                LayoutInflater.from(context));
        alert.setView(dialogFeedbackBinding.getRoot());
        alert.create();
        alert.setTitle("Detail Notifikasi");

        final AlertDialog dialog = alert.create();
        dialogFeedbackBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogFeedbackBinding.etFeedBack.getText().toString().isEmpty()){
                    MyConfig.showToast(context, "Mohon mengisi masukan");
                }else{
                    ProgressLoadingJIGB.setupLoading = (setup) ->  {
                        setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
                        setup.message = "Mengirimkan masukan";//  Center Message
                        setup.timer = 0;   // Time of live for progress.
                        setup.width = 200; // Optional
                        setup.hight = 200; // Optional
                    };
                    ProgressLoadingJIGB.startLoading(context);
                    String url = NetworkState.otherApiUrl+"addFeedBack.php" ;
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
                                                        MyProfileActivity.this,
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
                            params.put("contentFeedBack", dialogFeedbackBinding.etFeedBack.getText().toString());
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

    private void getMyName() {
        String url = NetworkState.profileApiUrl+"getMyName.php" ;
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
                                        String fullname = users.getString("fullname");
                                        String photoUser = NetworkState.locatedStorage+"/profile/"+ users.getString("photoUser");
                                        binding.textFullname.setText(fullname);

                                        Glide.with(context)
                                                .load(photoUser)
                                                .apply(new RequestOptions().placeholder(R.drawable.placeholder_profile)
                                                        .error(R.drawable.placeholder_profile))
                                                .into(binding.imgUsers);
                                    }
                                }else{
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}