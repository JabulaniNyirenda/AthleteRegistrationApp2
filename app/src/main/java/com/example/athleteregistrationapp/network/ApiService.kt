package com.example.athleteregistrationapp.network

import com.example.athleteregistrationapp.model.Athlete
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register_athlete.php")
    fun registerAthlete(
        @Field("fullname") fullname: String,
        @Field("dob") dob: String,
        @Field("weight_class") weight: String,
        @Field("club") club: String,
        @Field("medical_info") medical: String
    ): Call<String>

    @GET("get_athlete_details.php")
    fun getAthleteDetails(@Query("athlete_id") id: String): Call<Athlete>

    @Multipart
    @POST("upload_document.php")
    fun uploadDocument(
        @Part("athlete_id") athleteId: String,
        @Part("doc_type") docType: String,
        @Part file: MultipartBody.Part
    ): Call<String>
}
