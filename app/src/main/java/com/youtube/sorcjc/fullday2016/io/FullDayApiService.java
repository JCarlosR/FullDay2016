package com.youtube.sorcjc.fullday2016.io;

import com.youtube.sorcjc.fullday2016.model.Speaker;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FullDayApiService {

    @GET("information")
    Call<ArrayList<Speaker>> getSpeakers();

    /*@FormUrlEncoded
    @POST("registrar-foto.php")
    Call<SimpleResponse> postPhoto(
            @Field("image") String base64, @Field("extension") String extension,
            @Field("hoja_id") String hoja_id, @Field("QR_code") String QR_code);*/
}