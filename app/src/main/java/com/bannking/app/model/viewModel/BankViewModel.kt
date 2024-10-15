package com.bannking.app.model.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bannking.app.core.BaseViewModel
import com.bannking.app.model.ExchangeTokenResponse
import com.bannking.app.model.GetBankLinkTokenResponse
import com.bannking.app.model.GetBankListResponse
import com.bannking.app.network.RetrofitClient
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankViewModel(val App: Application) : BaseViewModel(App) {
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)
    var errorResponse: MutableLiveData<String> = MutableLiveData(null)


    fun setDataInBankList(userToken: String?): LiveData<GetBankListResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<GetBankListResponse>()

        val call = RetrofitClient.instance!!.myApi.getBankDetails(userToken!!)
        call.enqueue(object : Callback<GetBankListResponse> {
            override fun onResponse(
                call: Call<GetBankListResponse>, response: Response<GetBankListResponse>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<GetBankListResponse>, t: Throwable) {
                progressObservable.value = false
            }
        })
        return mutableLiveData
    }

    fun getTokenForPlaidApi(userToken: String?): LiveData<GetBankLinkTokenResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<GetBankLinkTokenResponse>()

        val call = RetrofitClient.instance!!.myApi.getTokenForAddBank(userToken!!)
        call.enqueue(object : Callback<GetBankLinkTokenResponse> {
            override fun onResponse(
                call: Call<GetBankLinkTokenResponse>, response: Response<GetBankLinkTokenResponse>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<GetBankLinkTokenResponse>, t: Throwable) {
                progressObservable.value = false
            }
        })
        return mutableLiveData
    }

    fun exchangeTokenForPlaidApi(
        userToken: String?, public_token: String
    ): LiveData<ExchangeTokenResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<ExchangeTokenResponse>()
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("public_token", public_token)
        } catch (e: Exception) {
            Log.e("catchError", e.message.toString())
        }

        val call = RetrofitClient.instance!!.myApi.exchangeTokenForAddBank(userToken!!,jsonObject)
        call.enqueue(object : Callback<ExchangeTokenResponse> {
            override fun onResponse(
                call: Call<ExchangeTokenResponse>, response: Response<ExchangeTokenResponse>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<ExchangeTokenResponse>, t: Throwable) {
                progressObservable.value = false
            }
        })
        return mutableLiveData
    }


}