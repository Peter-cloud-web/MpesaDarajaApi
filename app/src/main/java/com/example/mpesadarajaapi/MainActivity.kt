package com.example.mpesadarajaapi

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.androidstudy.daraja.Daraja
import com.androidstudy.daraja.callback.DarajaResult
import com.androidstudy.daraja.util.Environment
import com.androidstudy.daraja.util.TransactionType
import com.example.mpesadarajaapi.databinding.ActivityMainBinding
import com.example.mpesadarajaapi.model.STKPushRequest
import com.example.mpesadarajaapi.model.STKPushResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var daraja: Daraja
    private lateinit var binding: ActivityMainBinding
    private lateinit var progressDialog: ProgressDialogFragment
    private var accessToken:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        daraja = getDaraja()
        accessToken()
    }

    private fun toast(text: String) = Toast.makeText(baseContext, text, Toast.LENGTH_LONG).show()

    private fun getDaraja(): Daraja {
        return Daraja.builder(
            "DRxRa5QN3zfR8C2xBySWiOo0kWJAW0yHIkF8ZLZyXC1sWWjP",
            "K30GfHGn6zgZEMKXd8ndid2ANVUpZTWUMmzsWV91pB1keDDfqo4AGoa6IU4LMsZs"
        )
            .setBusinessShortCode("174379")
            .setPassKey("bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919")
            .setTransactionType(TransactionType.CustomerPayBillOnline)
            .setCallbackUrl("https://mydomain.com/path")
            .setEnvironment(Environment.SANDBOX)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pay() {
        val phoneNumber = binding.etPhoneNumber.text.toString()
        val amount = binding.etAmount.text.toString()

        if(phoneNumber.isBlank()|| amount.isBlank()){
            Toast.makeText(this,"You have left some fields blank", Toast.LENGTH_SHORT).show()
            return
        }
        val amountString = amount.toInt()

        initiatePayment(phoneNumber,amountString)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initiatePayment(phoneNumber:String, amount:Int){
        if (accessToken == null) {
            accessToken()
            Toast.makeText(this,"Your access token was refreshed. Retry again.",Toast.LENGTH_SHORT).show()
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sandbox.safaricom.co.ke/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(STKPushService::class.java)
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val password = generatePassword(timestamp)

        val requestBody = STKPushRequest(
            accountReference = "CompanyXLTD" ,
            amount = amount ,
            businessShortCode = 174379,
            callBackURL = "https://mydomain.com/path",
            partyA = phoneNumber.toLong(),
            partyB = 174379,
            password = password,
            phoneNumber = phoneNumber.toLong(),
            timestamp = timestamp,
            transactionDesc = "Payment of Carwash",
            transactionType = "CustomerPayBillOnline"
        )

        Log.d("MpesaRequest", "Request Body: ${Gson().toJson(requestBody)}")
        Log.d("MpesaRequest", "Access Token: $accessToken")

        showProgressDialog("Processing Mpesa Payment")
        service.initiateSTKPush("Bearer $accessToken", requestBody).enqueue(object : Callback<STKPushResponse> {
            override fun onResponse(call: Call<STKPushResponse>, response: Response<STKPushResponse>) {
                dismissProgressDialog()
                Log.d("MpesaResponse", "Response Code: ${response.code()}")
                Log.d("MpesaResponse", "Response Body: ${response.body()}")
                Log.d("MpesaResponse", "Error Body: ${response.errorBody()?.string()}")
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Mpesa payment initiated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@MainActivity, "Mpesa payment failed: $errorBody", Toast.LENGTH_SHORT).show()
                    Log.e("MpesaError", "Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<STKPushResponse>, t: Throwable) {
                dismissProgressDialog()
                Toast.makeText(this@MainActivity, "Mpesa payment failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("MpesaError", "Exception: ${t.message}", t)
            }
        })
    }


    private fun accessToken() {
        showProgressDialog()
        daraja.getAccessToken { darajaResult ->
            dismissProgressDialog()
            when (darajaResult) {
                is DarajaResult.Success -> {
                    accessToken = darajaResult.value.access_token
                    Log.d("AccessToken", "Token: $accessToken") // Log the token for debugging
                    binding.bPay.setOnClickListener { pay() }
                }
                is DarajaResult.Failure -> {
                    val darajaException = darajaResult.darajaException
                    Log.e("AccessToken", "Error: ${darajaException?.message}", darajaException) // Log the error
                    Toast.makeText(this, darajaException?.message ?: "An error occurred!", Toast.LENGTH_SHORT).show()
                    binding.bPay.setOnClickListener { accessToken() }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generatePassword(timestamp: String): String {
        val businessShortCode = "174379"
        val passkey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
        val str = businessShortCode + passkey + timestamp
        return Base64.getEncoder().encodeToString(str.toByteArray())
    }

    private fun showProgressDialog(title: String = "This will only take a sec", message: String = "Loading") {
        progressDialog = ProgressDialogFragment.newInstance(title, message)
        progressDialog.isCancelable = false
        progressDialog.show(supportFragmentManager, "progress")
    }

    private fun dismissProgressDialog() {
        progressDialog.dismiss()
    }
}