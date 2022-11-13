package com.rigadev.nutricapps.database;



import com.rigadev.nutricapps.util.NetworkState;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface RetrofitInterface {

    String urlActivity = NetworkState.foodApiUrl;

    @Multipart
    @POST("uploadProofPayment.php")
    Call<String> uploadProofPayment(
            @Part MultipartBody.Part fileupload,
            @Part("foodOrderID") String foodOrderID,
            @Part("paymentmethod") String paymentmethod
    );

}
