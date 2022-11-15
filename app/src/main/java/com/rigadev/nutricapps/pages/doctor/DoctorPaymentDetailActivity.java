package com.rigadev.nutricapps.pages.doctor;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.forms.sti.progresslitieigb.ProgressLoadingJIGB;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.rigadev.nutricapps.HomeActivity;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.database.RetrofitInterface;
import com.rigadev.nutricapps.databinding.ActivityDoctorPaymentDetailBinding;
import com.rigadev.nutricapps.model.PaymentMethodModel;
import com.rigadev.nutricapps.pages.food.FoodPaymentDetailActivity;
import com.rigadev.nutricapps.util.MyConfig;
import com.rigadev.nutricapps.util.NetworkState;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DoctorPaymentDetailActivity extends AppCompatActivity implements PickiTCallbacks {

    Context context = this;
    ActivityDoctorPaymentDetailBinding binding;

    List<PaymentMethodModel> listPayment = new ArrayList<PaymentMethodModel>();

    String idPaymentMethod="", name="", type="", account="";

    PickiT pickiT;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = PERMISSION_REQ_ID_RECORD_AUDIO + 1;
    File myDocFile=null;
    String pathPDF="";
    File myPhotoFile=null;
    Bitmap bitmap, decoded;
    private int GALLERY = 2;

    String pathImage="";
    int bitmap_size = 100;
    int max_resolution_image = 1024;


    String fee, idConsultation ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorPaymentDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        idConsultation = getIntent().getStringExtra("idConsultation");
        fee = getIntent().getStringExtra("fee");


        ProgressLoadingJIGB.setupLoading = (setup) ->  {
            setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
            setup.message = "Upload Bukti Pembayaran";//  Center Message
            setup.timer = 0;   // Time of live for progress.
            setup.width = 200; // Optional
            setup.hight = 200; // Optional
        };


        binding.toolbar.setTitle("Detail Pembayaran");
        binding.toolbar.setNavigationIcon(R.drawable.ic_back_white);
        binding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(binding.toolbar);

        listPayment = new ArrayList<>();

        binding.textOrderId.setText(idConsultation);
        binding.textTotalPayment.setText("Rp. "+ MyConfig.formatNumberComma(fee));

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getPaymentMethod();

        binding.buttonBuktiPembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPdf();
            }
        });

        pickiT = new PickiT(this, this, this);

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForm();
            }
        });

    }

    private void getPaymentMethod() {
        listPayment.clear();
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        String url = NetworkState.otherApiUrl +"getPaymentMethod.php";
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");
                    if (success == true){
                        String data = jsonObject.getString("data");
                        JSONArray jsonArray = new JSONArray(data.toString());
                        PaymentMethodModel pm = new PaymentMethodModel();
                        pm.setIdPaymentMethod("");
                        pm.setName("Pilih Kategori Pembayaran");
                        listPayment.add(pm);

                        for (int i=0; i< jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            PaymentMethodModel pmm = new PaymentMethodModel();
                            pmm.setIdPaymentMethod(object.getString("idPaymentMethod"));
                            pmm.setName(object.getString("name"));
                            pmm.setType(object.getString("type"));
                            pmm.setAccount(object.getString("account"));
                            listPayment.add(pmm);
                        }
                        onLoadPayment();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
                Map<String, String> params = new HashMap<String, String>();
                /*params.put("username", binding.etUsername.getText().toString().trim());
                params.put("password", binding.etPassword.getText().toString().trim());*/
                return params;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }

    void onLoadPayment(){
        ArrayList spinCategory = (ArrayList) listPayment;

        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(context,
                R.layout.spinner_text, spinCategory ){

            @Override
            public boolean isEnabled(int position) {
                if (position ==0 ) {
                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        binding.spinnerMetodePembayaran.setAdapter(langAdapter);
        binding.spinnerMetodePembayaran.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String selectedItemText = (String) parent.getItemAtPosition(position);

                PaymentMethodModel data = (PaymentMethodModel) parent.getItemAtPosition(position);

                if (position > 0){
                    idPaymentMethod = data.getIdPaymentMethod();
                    name = data.getName();
                    type = data.getType();
                    account = data.getAccount();
                    binding.textNoAkun.setText(data.getAccount());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }


    ProgressBar mProgressBar;
    TextView percentText;
    private AlertDialog mdialog;
    ProgressDialog progressBar;

    @Override
    public void PickiTonUriReturned() {
        progressBar = new ProgressDialog(this);
        progressBar.setMessage("Waiting to receive file...");
        progressBar.setCancelable(false);
        progressBar.show();
    }

    @Override
    public void PickiTonStartListener() {
        if (progressBar.isShowing()) {
            progressBar.cancel();
        }
        final AlertDialog.Builder mPro = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        @SuppressLint("InflateParams") final View mPView = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        percentText = mPView.findViewById(R.id.percentText);

        percentText.setOnClickListener(view -> {
            pickiT.cancelTask();
            if (mdialog != null && mdialog.isShowing()) {
                mdialog.cancel();
            }
        });

        mProgressBar = mPView.findViewById(R.id.mProgressBar);
        mProgressBar.setMax(100);
        mPro.setView(mPView);
        mdialog = mPro.create();
        mdialog.show();
    }

    @Override
    public void PickiTonProgressUpdate(int progress) {
        String progressPlusPercent = progress + "%";
        percentText.setText(progressPlusPercent);
        mProgressBar.setProgress(progress);
    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {

        if (mdialog != null && mdialog.isShowing()) {
            mdialog.cancel();
        }

        if (wasSuccessful) {
            //  Set returned path to TextView
            if (path.contains("/proc/")) {
                //binding.textFileName.setText("Sub-directory inside Downloads was selected." + "\n" + " We will be making use of the /proc/ protocol." + "\n" + " You can use this path as you would normally." + "\n\n" + "PickiT path:" + "\n" + path);
            } else {
                //binding.textFileName.setText(path);
            }

            //  Make TextView's visible


            pathPDF = path;

        } else {
            //showLongToast("Error, please see the log..");
            //binding.textFileName.setText(reason);
        }
    }

    @Override
    public void PickiTonMultipleCompleteListener(ArrayList<String> paths, boolean wasSuccessful, String Reason) {
        if (mdialog != null && mdialog.isShowing()) {
            mdialog.cancel();
        }
        StringBuilder allPaths = new StringBuilder();
        for (int i = 0; i < paths.size(); i++) {
            allPaths.append("\n").append(paths.get(i)).append("\n");
        }

        //  Set returned path to TextView
        //binding.textFileName.setText(allPaths.toString())
    }

    private void selectPdf() {
        if (checkSelfPermission()) {

            Intent intent;
            intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra("return-data", true);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activityResultLauncher.launch(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPdf();
            }
            //  Permissions was not granted
            else {
                MyConfig.showToast(context,"No permission for " + Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        //  Get path from PickiT (The path will be returned in PickiTonCompleteListener)
                        //
                        //  If the selected file is from Dropbox/Google Drive or OnDrive:
                        //  Then it will be "copied" to your app directory (see path example below) and when done the path will be returned in PickiTonCompleteListener
                        //  /storage/emulated/0/Android/data/your.package.name/files/Temp/tempDriveFile.mp4
                        //
                        //  else the path will directly be returned in PickiTonCompleteListener

                        //Uri uris = data.getData();
                        //myDocFile = FileUtils.getFile(context, data.getData());
                        Uri contentURI = data.getData();

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                            setToImageView(getResizedBitmap(bitmap, max_resolution_image));
                            Log.d("ioooo",pathImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                        }


                        ClipData clipData = Objects.requireNonNull(data).getClipData();
                        if (clipData != null) {
                            int numberOfFilesSelected = clipData.getItemCount();
                            if (numberOfFilesSelected > 1) {
                                pickiT.getMultiplePaths(clipData);
                                StringBuilder allPaths = new StringBuilder("Multiple Files Selected:" + "\n");
                                for(int i = 0; i < clipData.getItemCount(); i++) {
                                    allPaths.append("\n\n").append(clipData.getItemAt(i).getUri());
                                }
                                //binding.textFileName.setText(allPaths.toString());
                            }else {
                                pickiT.getPath(clipData.getItemAt(0).getUri(), Build.VERSION.SDK_INT);
                                //binding.textFileName.setText(String.valueOf(clipData.getItemAt(0).getUri()));
                            }
                        } else {
                            pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
                            //binding.textFileName.setText(String.valueOf(data.getData()));
                        }

                    }
                }

            });


    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        binding.imgMenu.setVisibility(View.VISIBLE);
        binding.imgMenu.setImageBitmap(decoded);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void checkForm() {
        if (idPaymentMethod.isEmpty()|| idPaymentMethod.equals("")){
            MyConfig.showToast(context, "Mohon memilih metode pembayaran");
        }else if (pathPDF.equals("")|| pathPDF.isEmpty()){
            MyConfig.showToast(context, "Mohon melampirkan bukti pembayaran");
        }else {
            processUpload();
        }
    }


    private void processUpload() {
        ProgressLoadingJIGB.startLoading(context);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.urlDoctor)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        File file = new File(pathPDF);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData(
                "fileupload",
                file.getName(),
                requestBody);

        RetrofitInterface getResponse = retrofit.create(RetrofitInterface.class);
        Call<String> call = getResponse.uploadProofPaymentDoctor(
                fileToUpload,
                idConsultation,
                idPaymentMethod);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call,
                                   retrofit2.Response<String> response) {
                Log.d("Response", "Res :" +response.body());
                ProgressLoadingJIGB.finishLoadingJIGB(context);
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    boolean success = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");
                    if (success == true){
                        MyConfig.showToast(context, message);

                        startActivity(new Intent(context, HomeActivity.class));
                        finish();

                    }else {
                        new AestheticDialog.Builder(
                                DoctorPaymentDetailActivity.this,
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
            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("gttt", call.toString());
                ProgressLoadingJIGB.finishLoadingJIGB(context);
                new AestheticDialog.Builder(DoctorPaymentDetailActivity.this, DialogStyle.EMOTION, DialogType.ERROR)
                        .setTitle("Error")
                        .setMessage(call.toString())
                        .show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(context, HomeActivity.class));
    }
}