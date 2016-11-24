package com.youtube.sorcjc.fullday2016.io;

import com.youtube.sorcjc.fullday2016.io.response.AnswersResponse;
import com.youtube.sorcjc.fullday2016.io.response.LoginResponse;
import com.youtube.sorcjc.fullday2016.io.response.SurveyResponse;
import com.youtube.sorcjc.fullday2016.model.Speaker;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FullDayApiService {

    @GET("information")
    Call<ArrayList<Speaker>> getSpeakers();

    @GET("authentication") // the last param represents a FCM token
    Call<LoginResponse> getLogin(@Query("email") String email, @Query("password") String password, @Query("key") String key);

    @GET("refresh")
    Call<LoginResponse> getNewToken(@Query("token") String oldToken);

    @GET("question")
    Call<SurveyResponse> getSurvey(@Query("token") String token);

    @GET("question/registrar")
    Call<AnswersResponse> getAnswers(@Query("token") String token,@Query("answers[]") ArrayList<String> answers);

    /*@FormUrlEncoded
    @POST("registrar-foto.php")
    Call<SimpleResponse> postPhoto(
            @Field("image") String base64, @Field("extension") String extension,
            @Field("hoja_id") String hoja_id, @Field("QR_code") String QR_code);*/
}