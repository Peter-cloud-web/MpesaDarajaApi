package com.example.mpesadarajaapi.model


import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class STKPushRequest(
    @SerializedName("BusinessShortCode") val businessShortCode: Int,
    @SerializedName("Password") val password: String,
    @SerializedName("Timestamp") val timestamp: String,
    @SerializedName("TransactionType") val transactionType: String,
    @SerializedName("Amount") val amount: Int,
    @SerializedName("PartyA") val partyA: Long,
    @SerializedName("PartyB") val partyB: Int,
    @SerializedName("PhoneNumber") val phoneNumber: Long,
    @SerializedName("CallBackURL") val callBackURL: String,
    @SerializedName("AccountReference") val accountReference: String,
    @SerializedName("TransactionDesc") val transactionDesc: String
)