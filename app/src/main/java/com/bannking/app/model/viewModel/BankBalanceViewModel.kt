package com.bannking.app.model.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bannking.app.core.BaseViewModel
import com.bannking.app.model.GetBankBalanceSpendResponse
import com.bannking.app.model.GetTransactionResponse
import com.bannking.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankBalanceViewModel(val App: Application) : BaseViewModel(App) {
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)

    fun getBankBalanceSpentApi(
        userToken: String?,
        accountId: String,
        institutionId: String
    ): LiveData<GetBankBalanceSpendResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<GetBankBalanceSpendResponse>()

        val call = RetrofitClient.instance!!.myApi.getBankBalanceAndSpend(
            accountId,
            institutionId,
            userToken!!
        )
        call.enqueue(object : Callback<GetBankBalanceSpendResponse> {
            override fun onResponse(
                call: Call<GetBankBalanceSpendResponse>,
                response: Response<GetBankBalanceSpendResponse>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<GetBankBalanceSpendResponse>, t: Throwable) {
                progressObservable.value = false
            }
        })
        return mutableLiveData
    }

    fun getBankTransactionFilterApi(
        userToken: String?,
        accountId: String,
        institutionId: String,
        filter: String,
        sortDate: String,
        page: String,
        limit: String
    ): LiveData<GetTransactionResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<GetTransactionResponse>()

        val call = RetrofitClient.instance!!.myApi.getTransactionFilter(
            filter,
            sortDate,
            page,
            limit,
            accountId,
            institutionId,
            userToken!!
        )
        call.enqueue(object : Callback<GetTransactionResponse> {
            override fun onResponse(
                call: Call<GetTransactionResponse>, response: Response<GetTransactionResponse>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<GetTransactionResponse>, t: Throwable) {
                progressObservable.value = false
            }
        })
        return mutableLiveData
    }


}