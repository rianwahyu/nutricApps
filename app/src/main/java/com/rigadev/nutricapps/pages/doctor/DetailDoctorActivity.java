package com.rigadev.nutricapps.pages.doctor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.databinding.ActivityDetailDoctorBinding;
import com.rigadev.nutricapps.pages.food.CartFoodActivity;
import com.rigadev.nutricapps.pages.food.FoodPaymentDetailActivity;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailDoctorActivity extends AppCompatActivity {

    Context context = this;
    ActivityDetailDoctorBinding binding;

    String idDoctor = "", doctorName="", shortBiography="", specialist="", doctorPhoto="",
            phoneNumber="", linkInstagram="", linkLinkedIn="", fee="";

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    String tglJanji;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ProgressLoadingJIGB.setupLoading = (setup) ->  {
            setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
            setup.message = "Membuat Janji dengan Dokter";//  Center Message
            setup.timer = 0;   // Time of live for progress.
            setup.width = 200; // Optional
            setup.hight = 200; // Optional
        };


        idDoctor = getIntent().getStringExtra("idDoctor");
        doctorName = getIntent().getStringExtra("doctorName");
        shortBiography = getIntent().getStringExtra("shortBiography");
        specialist = getIntent().getStringExtra("specialist");
        doctorPhoto = getIntent().getStringExtra("doctorPhoto");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        linkInstagram = getIntent().getStringExtra("linkInstagram");
        linkLinkedIn = getIntent().getStringExtra("linkLinkedIn");
        fee = getIntent().getStringExtra("fee");


        binding.toolbar.setTitle("Detail Dokter");
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_white);
        binding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(binding.toolbar);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Glide.with(context)
                .load(doctorPhoto)
                .apply(new RequestOptions().placeholder(R.drawable.doctor_placeholder).error(R.drawable.food_placeholder))
                .into(binding.imgDoctor);

        binding.textNamaDokter.setText(doctorName);
        binding.textKetDokter.setText(shortBiography);
        binding.textBiaya.setText("Rp." + MyConfig.formatNumberComma(fee) );

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        binding.etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        binding.btnBuatJanji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForm();
            }
        });
    }

    private void checkForm() {
        if (binding.etDate.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi tanggal");
        }else if(binding.etNamaLengkap.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi nama");
        }else if (binding.etNoWhatsapp.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi nomor whatsapp");
        }else{
            processOrderConsultation();
        }
    }

    private void processOrderConsultation() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        ProgressLoadingJIGB.startLoading(context);
        String url = NetworkState.doctorApiUrl +"orderConsultation.php";
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
                        String idConsultation = jsonObject.getString("idConsultation");
                        Intent intent = new Intent(context, DoctorPaymentDetailActivity.class);
                        intent.putExtra("idConsultation", idConsultation);
                        intent.putExtra("fee", String.valueOf(fee));
                        startActivity(intent);
                        finish();

                        /*Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                            }
                        };
                        handler.postDelayed(runnable, 2000);*/

                    }else {
                        new AestheticDialog.Builder(
                                DetailDoctorActivity.this,
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
                        DetailDoctorActivity.this,
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
                params.put("idDoctor", idDoctor);
                params.put("patientName", binding.etNamaLengkap.getText().toString());
                params.put("whatsappNumber", binding.etNoWhatsapp.getText().toString());
                params.put("fee", fee);
                params.put("dateConsultation", tglJanji);

                return params;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }

    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                binding.etDate.setText(dateFormatter.format(newDate.getTime()));
                tglJanji = dateFormatter.format(newDate.getTime());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.US);
                String dayName= MyConfig.convertDayNameToIndo(simpleDateFormat.format(newDate.getTime())) ;
                Log.d("Doctor", "Date Name :" + dayName);
                binding.etDayName.setText(dayName);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}