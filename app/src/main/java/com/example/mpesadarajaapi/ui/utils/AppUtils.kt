package com.example.mpesadarajaapi.ui.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.databinding.ktx.BuildConfig
import java.util.Calendar
import java.util.UUID

object AppUtils {

    fun generateUUID(): String =
        UUID.randomUUID().toString()

    val passKey: String
        get() = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"

    fun saveAccessToken(context: Context, accessToken: String) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR, 1)
        val oneHourAfter = cal.timeInMillis
    }

    fun getAccessToken(context: Context): String? {
        return if (expired(context)) {
            null
        } else {
            val mSettings = context.getSharedPreferences("com.example.mpesadarajaap", MODE_PRIVATE)
            mSettings.getString("accessToken", "")
        }
    }

    private fun expired(context: Context): Boolean {
        val mSettings = context.getSharedPreferences("com.example.mpesadarajaap", MODE_PRIVATE)
        val expiryTime = mSettings.getLong("expiryDate", 0)
        val currentTime = Calendar.getInstance().timeInMillis
        return currentTime > expiryTime
    }
}