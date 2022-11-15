package com.rigadev.nutricapps.pages.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.databinding.ActivityMyDiaryAddPiringkuBinding;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;
import com.rigadev.nutricapps.util.SessionManager;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyDiaryAddPiringkuActivity extends AppCompatActivity {

    Context context = this;
    ActivityMyDiaryAddPiringkuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyDiaryAddPiringkuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ProgressLoadingJIGB.setupLoading = (setup) ->  {
            setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
            setup.message = "Memproses Data";//  Center Message
            setup.timer = 0;   // Time of live for progress.
            setup.width = 200; // Optional
            setup.hight = 200; // Optional
        };

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForm();
            }
        });
    }

    private void checkForm() {
        if (binding.etNamaMakanan.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi nama makanan");
        }else if(binding.etJumlahPorsi.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi jumlah porsi");
        }else if (binding.etBesarKalori.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi besar kalori");
        }else {
            processData();
        }
    }

    private void processData() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        ProgressLoadingJIGB.startLoading(context);
        String url = NetworkState.diaryApiUrl +"addPlate.php";
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
                        MyConfig.showToast(context, message);
                        onBackPressed();
                    }else {
                        new AestheticDialog.Builder(
                                MyDiaryAddPiringkuActivity.this,
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
                        MyDiaryAddPiringkuActivity.this,
                        DialogStyle.EMOTION,
                        DialogType.ERROR)
                        .setTitle("Error")
                        .setMessage(error.getMessage())
                        .show();

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUser", new SessionManager(context).getIdUser());
                params.put("portion", binding.etJumlahPorsi.getText().toString());
                params.put("calories", binding.etBesarKalori.getText().toString());
                params.put("name", binding.etNamaMakanan.getText().toString());
                params.put("foodOrderID", "");
                params.put("type", "manual");
                return params;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }

}