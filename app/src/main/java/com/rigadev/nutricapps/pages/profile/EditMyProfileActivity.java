package com.rigadev.nutricapps.pages.profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;
import com.rigadev.nutricapps.HomeActivity;
import com.rigadev.nutricapps.R;
import com.rigadev.nutricapps.database.RetrofitInterface;
import com.rigadev.nutricapps.databinding.ActivityEditMyProfileBinding;
import com.rigadev.nutricapps.pages.food.FoodPaymentDetailActivity;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class EditMyProfileActivity extends AppCompatActivity implements PickiTCallbacks  {

    Context context = this;
    ActivityEditMyProfileBinding binding;

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


    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    String tglLahir="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ProgressLoadingJIGB.setupLoading = (setup) ->  {
            setup.srcLottieJson = com.forms.sti.progresslitieigb.R.raw.loader; // Tour Source JSON Lottie
            setup.message = "Proses update profil";//  Center Message
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

        getMyProfile();

        pickiT = new PickiT(this, this, this);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        binding.etBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        binding.linearUbahProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPdf();
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
        if (!pathPDF.isEmpty() || !pathPDF.equals("")){
            processUpload();
        }else {
            processUploadNonFile();
        }
    }

    private void processUploadNonFile() {
        ProgressLoadingJIGB.startLoading(context);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.urlProfile)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RetrofitInterface getResponse = retrofit.create(RetrofitInterface.class);
        Call<String> call = getResponse.uploadMyProfileNonPhoto(
                binding.etFullname.getText().toString(),
                binding.etAlamat.getText().toString(),
                binding.etNoHp.getText().toString(),
                binding.etBirth.getText().toString(),
                binding.etBirthDate.getText().toString(),
                new SessionManager(context).getIdUser());
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
                        //MyConfig.showToast(context, message);
                        new AestheticDialog.Builder(
                                EditMyProfileActivity.this,
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


                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        };
                        handler.postDelayed(runnable, 2000);

                    }else {
                        new AestheticDialog.Builder(
                                EditMyProfileActivity.this,
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
                new AestheticDialog.Builder(EditMyProfileActivity.this, DialogStyle.EMOTION, DialogType.ERROR)
                        .setTitle("Error")
                        .setMessage(call.toString())
                        .show();
            }
        });
    }

    private void processUpload() {
        ProgressLoadingJIGB.startLoading(context);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.urlProfile)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        File file = new File(pathPDF);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData(
                "fileupload",
                file.getName(),
                requestBody);

        RetrofitInterface getResponse = retrofit.create(RetrofitInterface.class);
        Call<String> call = getResponse.updateMyProfile(
                fileToUpload,
                binding.etFullname.getText().toString(),
                binding.etAlamat.getText().toString(),
                binding.etNoHp.getText().toString(),
                binding.etBirth.getText().toString(),
                binding.etBirthDate.getText().toString(),
                new SessionManager(context).getIdUser());
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
                        //MyConfig.showToast(context, message);
                        new AestheticDialog.Builder(
                                EditMyProfileActivity.this,
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


                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                onBackPressed();
                            }
                        };
                        handler.postDelayed(runnable, 2000);

                    }else {
                        new AestheticDialog.Builder(
                                EditMyProfileActivity.this,
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
                new AestheticDialog.Builder(EditMyProfileActivity.this, DialogStyle.EMOTION, DialogType.ERROR)
                        .setTitle("Error")
                        .setMessage(call.toString())
                        .show();
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

                binding.etBirthDate.setText(dateFormatter.format(newDate.getTime()));
                tglLahir = dateFormatter.format(newDate.getTime());
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    private void getMyProfile() {
        String url = NetworkState.profileApiUrl+"getMyProfile.php" ;
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
                                        String address = users.getString("address");
                                        String noHandphone = users.getString("noHandphone");
                                        String birth = users.getString("birth");
                                        String dateBirth = users.getString("dateBirth");
                                        String username = users.getString("username");
                                        String email = users.getString("email");


                                        String photoUser = NetworkState.locatedStorage+"/profile/"+ users.getString("photoUser");
                                        binding.etFullname.setText(fullname);

                                        Glide.with(context)
                                                .load(photoUser)
                                                .apply(new RequestOptions().placeholder(R.drawable.placeholder_profile)
                                                        .error(R.drawable.placeholder_profile))
                                                .into(binding.imgUsers);

                                        binding.etAlamat.setText(address);
                                        binding.etNoHp.setText(noHandphone);
                                        binding.etBirthDate.setText(dateBirth);
                                        binding.etUsername.setText(username);
                                        binding.etEmail.setText(email);
                                        binding.etBirth.setText(birth);
                                        binding.etNoHp.setText(noHandphone);
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
        binding.imgUsers.setVisibility(View.VISIBLE);
        binding.imgUsers.setImageBitmap(decoded);
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}