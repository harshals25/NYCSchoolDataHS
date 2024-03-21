package com.example.nycschooldatahs

import com.example.nycschooldatahs.api.RestClient
import com.example.nycschooldatahs.api.response.NYCSchoolResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit


class NYCRepository(private val restClient: RestClient) {

    // old endpoint -> no pagination fetch all at once
    suspend fun getNYCSchoolInfo(header : String) = restClient.getNYCSchoolInformation(header)

    // new endpoint -> has pagination capabilities.
    suspend fun getNYCSchoolInfoNoResponse(header : String, limit : Int, offset : Int) : Flow<List<NYCSchoolResponse>> = flow{
        try{
            emit(restClient.getNYCSchoolInformationNoResponse(header,limit,offset))
        }catch (e : java.lang.Exception){
            // log e or handle as needed.
        }
    }.flowOn(Dispatchers.IO)

    // fetching school info by DBN
    suspend fun getNYCSchoolSatInfo(header: String, dbn : String) = restClient.getNYCSchoolSATInfo(header, dbn)

}