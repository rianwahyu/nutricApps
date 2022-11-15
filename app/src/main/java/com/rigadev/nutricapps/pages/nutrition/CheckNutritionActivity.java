package com.rigadev.nutricapps.pages.nutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.rigadev.nutricapps.databinding.ActivityCheckNutritionBinding;
import com.rigadev.nutricapps.pages.doctor.DaftarDokterActivity;
import com.rigadev.nutricapps.pages.food.DaftarMakananActivity;
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

public class CheckNutritionActivity extends AppCompatActivity {

    Context context = this;
    ActivityCheckNutritionBinding binding;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    String tglCheckNutrisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckNutritionBinding.inflate(getLayoutInflater());
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

        binding.etTanggalPengecekan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        binding.btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForm();
            }
        });
    }

    private void checkForm() {
        if (binding.etTanggalPengecekan.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi tanggal pengecekan");
        }else if (binding.etNamaLengkap.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi nama lengkap");
        }else if (binding.etUmu.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi umur");
        }else if (binding.etBeratBadan.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi berat badan");
        }else if (binding.etTinggiBadan.getText().toString().isEmpty()){
            MyConfig.showToast(context, "Mohon mengisi tinggi badan");
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Cek Nutrisi");
            builder.setMessage("Apakah anda yakin isian data sudah benar ?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    processData();
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

    }

    private void processData() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        ProgressLoadingJIGB.startLoading(context);
        String url = NetworkState.nutririonApiUrl +"checkNutrition.php";
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
                        //onBackPressed();
                        clearForm();
                        String nama = jsonObject.getString("nama");
                        String tanggalPengecekan = jsonObject.getString("tanggalPengecekan");
                        String umur = jsonObject.getString("umur");
                        String berat = jsonObject.getString("berat");
                        String tinggi = jsonObject.getString("tinggi");
                        String imt = jsonObject.getString("imt");
                        String ketentuanImt = jsonObject.getString("ketentuanImt");
                        String keteranganKetentuanImt = jsonObject.getString("keteranganKetentuanImt");

                        onLoadLinearResult(tanggalPengecekan, nama, umur, berat, tinggi, imt, ketentuanImt, keteranganKetentuanImt);
                    }else {
                        new AestheticDialog.Builder(
                                CheckNutritionActivity.this,
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
                        CheckNutritionActivity.this,
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
                params.put("dateCheck", tglCheckNutrisi);
                params.put("fullname", binding.etNamaLengkap.getText().toString());
                params.put("age", binding.etUmu.getText().toString());
                params.put("weight", binding.etBeratBadan.getText().toString());
                params.put("height", binding.etTinggiBadan.getText().toString());
                return params;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }

    private void onLoadLinearResult(String tanggalPengecekan, String nama, String umur, String berat, String tinggi, String imt, String ketentuanImt, String keteranganKetentuanImt) {
        binding.linearFormNutrition.setVisibility(View.GONE);
        binding.linearFormResultNutrition.setVisibility(View.VISIBLE);
        binding.textTanggalPengecekanResult.setText(tanggalPengecekan);
        binding.textNamaLengkapResult.setText(nama);
        binding.textImtResult.setText(imt);
        binding.textBeratBadanResult.setText(berat +" kg");
        binding.textTinggiBadanResult.setText(tinggi+" cm");
        binding.textUmurResult.setText(umur +" tahun");
        binding.textImtResult2.setText(imt);
        binding.textKetentuanImt.setText(ketentuanImt);
        binding.textKeteranganKetentuanImt.setText(keteranganKetentuanImt);

        binding.btnCariDokter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, DaftarDokterActivity.class));
            }
        });


        binding.btnCariMakananSehat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, DaftarMakananActivity.class));
            }
        });
    }

    private void clearForm() {
        binding.etTinggiBadan.setText("");
        binding.etNamaLengkap.setText("");
        binding.etBeratBadan.setText("");
        binding.etUmu.setText("");
        binding.etTanggalPengecekan.setText("");
    }

    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                binding.etTanggalPengecekan.setText(dateFormatter.format(newDate.getTime()));
                tglCheckNutrisi = dateFormatter.format(newDate.getTime());
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


}