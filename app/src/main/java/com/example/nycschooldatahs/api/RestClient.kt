package com.example.nycschooldatahs.api

import com.example.nycschooldatahs.api.response.NYCSchoolResponse
import com.example.nycschooldatahs.api.response.NYCSchoolSATInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RestClient {

    @GET("s3k6-pzi2.json")
    suspend fun getNYCSchoolInformation(@Query("\$\$app_token") appToken: String):
            Response<List<NYCSchoolResponse>>

    @GET("s3k6-pzi2.json")
    suspend fun getNYCSchoolInformationNoResponse(@Query("\$\$app_token") appToken: String, @Query("\$limit") limit : Int, @Query("\$offset") offset : Int):
            List<NYCSchoolResponse>

    @GET("f9bf-2cp4.json")
    suspend fun getNYCSchoolSATInfo(@Query("\$\$app_token") appToken: String,
                                    @Query("dbn") dbn: String): Response<Set<NYCSchoolSATInfoResponse>>

}
