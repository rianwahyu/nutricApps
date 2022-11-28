package com.rigadev.nutricapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.rigadev.nutricapps.databinding.ActivityLoginBinding;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;
import com.rigadev.nutricapps.util.SessionManager;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Context context = this;

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textBelumPunyaAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, RegistrasiActivity.class));
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, HomeActivity.class));
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ProgressLoadingJIGB.setupLoading = (setup) ->  {
            setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
            setup.message = "Prosess Login";//  Center Message
            setup.timer = 0;   // Time of live for progress.
            setup.width = 200; // Optional
            setup.hight = 200; // Optional
        };

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForm(view);
            }
        });
    }


    private void checkForm(View view) {
        if (binding.etEmail.getText().toString().isEmpty()){
            MyConfig.setSnackBar(view, "Mohon mengisi email");
        }else if (binding.etPassword.getText().toString().isEmpty()){
            MyConfig.setSnackBar(view, "Mohon mengisi password");
        }else {
            processLogin();
        }
    }


    private void processLogin() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        ProgressLoadingJIGB.startLoading(context);
        String url = NetworkState.authApiUrl +"login.php";
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                ProgressLoadingJIGB.finishLoadingJIGB(context);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");
                    if (success == true){

                        String data = jsonObject.getString("data");

                        JSONObject objectUser = new JSONObject(data);

                        String idUser = objectUser.getString("idUser");
                        String username = objectUser.getString("username");
                        String email = objectUser.getString("email");

                        new SessionManager(context).login(
                                idUser,
                                username,
                                email
                        );

                        startActivity(new Intent(context, HomeActivity.class));
                        finish();

                        /*new AestheticDialog.Builder(
                                LoginActivity.this,
                                DialogStyle.EMOTION,
                                DialogType.SUCCESS)
                                .setTitle("Sukses Login")
                                .setMessage(message)
                                .show();

                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                            }
                        };
                        handler.postDelayed(runnable, 2000);*/

                    }else {
                        new AestheticDialog.Builder(
                                LoginActivity.this,
                                DialogStyle.EMOTION,
                                DialogType.ERROR)
                                .setTitle("Error")
                                .setMessage(message)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error_res", error.getLocalizedMessage().toString());
                ProgressLoadingJIGB.finishLoadingJIGB(context);

                new AestheticDialog.Builder(
                        LoginActivity.this,
                        DialogStyle.EMOTION,
                        DialogType.ERROR)
                        .setTitle("Error")
                        .setMessage(error.getMessage())
                        .show();

            }
        }){

            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.headers == null)
                {
                    response = new NetworkResponse(
                            response.statusCode,
                            response.data,
                            Collections.<String, String>emptyMap(), // this is the important line, set an empty but non-null map.
                            response.notModified,
                            response.networkTimeMs);
                }

                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", binding.etEmail.getText().toString().trim());
                params.put("password", binding.etPassword.getText().toString().trim());
                return params;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
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