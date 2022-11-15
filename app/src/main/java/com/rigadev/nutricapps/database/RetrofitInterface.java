package com.rigadev.nutricapps.database;



import com.rigadev.nutricapps.util.NetworkState;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface RetrofitInterface {

    String urlActivity = NetworkState.foodApiUrl;
    String urlDoctor = NetworkState.doctorApiUrl;
    String urlProfile = NetworkState.profileApiUrl;

    @Multipart
    @POST("uploadProofPayment.php")
    Call<String> uploadProofPayment(
            @Part MultipartBody.Part fileupload,
            @Part("foodOrderID") String foodOrderID,
            @Part("paymentmethod") String paymentmethod
    );

    @Multipart
    @POST("updateMyProfile.php")
    Call<String> updateMyProfile(
            @Part MultipartBody.Part fileupload,
            @Part("fullname") String fullname,
            @Part("address") String address,
            @Part("noHandphone") String noHandphone,
            @Part("birth") String birth,
            @Part("dateBirth") String dateBirth,
            @Part("idUser") String idUser
    );

    @FormUrlEncoded
    @POST("updateMyProfile.php")
    Call<String> uploadMyProfileNonPhoto(
            @Field("fullname") String fullname,
            @Field("address") String address,
            @Field("noHandphone") String noHandphone,
            @Field("birth") String birth,
            @Field("dateBirth") String dateBirth,
            @Field("idUser") String idUser
    );

    @Multipart
    @POST("uploadProofPayment.php")
    Call<String> uploadProofPaymentDoctor(
            @Part MultipartBody.Part fileupload,
            @Part("idConsultation") String idConsultation,
            @Part("paymentmethod") String paymentmethod
    );
}
