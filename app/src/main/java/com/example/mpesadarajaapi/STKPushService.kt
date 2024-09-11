package com.example.mpesadarajaapi


import com.example.mpesadarajaapi.model.STKPushRequest
import com.example.mpesadarajaapi.model.STKPushResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface STKPushService {
    @POST("mpesa/stkpush/v1/processrequest")
    fun initiateSTKPush(
        @Header("Authorization") authorization: String,
        @Body stkPush: STKPushRequest
    ): Call<STKPushResponse>
}