package com.bannking.app.model.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.BaseViewModel
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.Constants
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecentTransactionViewModel(val App: Application) : BaseViewModel(App) {


    var recentTransactionListData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var typeTransactionListData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var createTransactionListData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var updateAccountListData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)


    fun setDataInRecentTransactionListData(accountId: String, userToken: String?) {
        Log.e("userToken", "setDataInRecentTransactionListData: $userToken", )
        App.FCM_TOKEN.let {
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("account_id", accountId)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            progressObservable.value = true
            val call = RetrofitClient.instance!!.myApi.transactionList(accountId,userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            recentTransactionListData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            recentTransactionListData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    recentTransactionListData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setDataInTypeTransactionListData() {
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("security", Constants.SECURITY_1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        progressObservable.value = true
        val call = RetrofitClient.instance!!.myApi.transactionType(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        typeTransactionListData.value =
                            CommonResponseModel(response.body(), response.code())
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        typeTransactionListData.value =
                            CommonResponseModel(jsonObject, response.code())
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
                typeTransactionListData.value = CommonResponseModel(null, 500)
            }
        })
    }

    fun setDataInCreateTransactionListData(
        strTransactionType: String,
        strAccountId: String,
        strTransactionTitle: String,
        strTransactionDate: String,
        strAmount: String,
        userToken: String?
    ) {
        App.FCM_TOKEN.let {
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("type", strTransactionType)
                apiBody.addProperty("token", it)
                apiBody.addProperty("account_id", strAccountId)
                apiBody.addProperty("transaction_title", strTransactionTitle)
                apiBody.addProperty("transaction_date", strTransactionDate)
                apiBody.addProperty("amount", strAmount)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progressObservable.value = true
            val call = RetrofitClient.instance!!.myApi.createTransactionDetail(apiBody.toString(),userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            createTransactionListData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            createTransactionListData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    createTransactionListData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }


    fun setDataInUpdateAccountListData(
        strAccountId: String,
        budget_id: String,
        account: String,
        account_code: String,
        strAmount: String,
        acc_menu_id: String,
        userToken: String?
    ) {
        App.FCM_TOKEN.let {
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("acc_menu_id", acc_menu_id)
                apiBody.addProperty("budget_id", budget_id)
                apiBody.addProperty("account", account)
                apiBody.addProperty("account_code", account_code)
                apiBody.addProperty("amount", strAmount)
                apiBody.addProperty("accountId", strAccountId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progressObservable.value = true
            val call = RetrofitClient.instance!!.myApi.updateAccount(apiBody.toString(),userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            updateAccountListData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            updateAccountListData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    updateAccountListData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }


}