package com.rigadev.nutricapps.pages.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.databinding.ActivityMyDiaryAddAktifitasBinding;
import com.rigadev.nutricapps.pages.doctor.DetailDoctorActivity;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;
import com.rigadev.nutricapps.util.SessionManager;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyDiaryAddAktifitasActivity extends AppCompatActivity {

    Context context = this;
    ActivityMyDiaryAddAktifitasBinding binding;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    String tglAktifitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyDiaryAddAktifitasBinding.inflate(getLayoutInflater());
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

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        binding.etTanggalAktifitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });


        binding.btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForm();
            }
        });
    }

    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                binding.etTanggalAktifitas.setText(dateFormatter.format(newDate.getTime()));
                tglAktifitas = dateFormatter.format(newDate.getTime());
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void checkForm() {
        if (binding.etNamaAktifitas.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi nama aktifitas");
        }else if(binding.etTanggalAktifitas.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi tanggal aktifitas");
        }else if (binding.etBesarKalori.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi besar kalori");
        }else {
            processData();
        }
    }

    private void processData() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        ProgressLoadingJIGB.startLoading(context);
        String url = NetworkState.diaryApiUrl +"addActivity.php";
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
                                MyDiaryAddAktifitasActivity.this,
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
                        MyDiaryAddAktifitasActivity.this,
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
                params.put("dateActivity", tglAktifitas);
                params.put("calories", binding.etBesarKalori.getText().toString());
                params.put("name", binding.etNamaAktifitas.getText().toString());
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