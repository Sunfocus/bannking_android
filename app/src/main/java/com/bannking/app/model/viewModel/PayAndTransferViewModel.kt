package com.bannking.app.model.viewModel

import android.app.Application
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

class PayAndTransferViewModel(val App: Application) : BaseViewModel(App) {

    val accountListData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    val scheduleTransferData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    val PayDataList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)


    fun setDataInAccountList() {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance?.myApi?.accountList(apiBody.toString())

            call?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            accountListData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            accountListData.value = CommonResponseModel(jsonObject, response.code())
                        }
                    } else accountListData.value = CommonResponseModel(null, 500)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    accountListData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setDataInScheduleTransfer(
        accountFromId: String,
        accountToId: String,
        transactionTitle: String,
        amount: String,
        transferDate: String
    ) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("account_from_id", accountFromId)
                apiBody.addProperty("account_to_id", accountToId)
                apiBody.addProperty("transaction_title", transactionTitle)
                apiBody.addProperty("amount", amount)
                apiBody.addProperty("transfer_date", transferDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance?.myApi?.scheduleTransfer(apiBody.toString())

            call?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            scheduleTransferData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            scheduleTransferData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    } else scheduleTransferData.value = CommonResponseModel(null, 500)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    progressObservable.value = false
                    scheduleTransferData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setDataInPayDataList(
        accountFromId: String,
        accountToId: String,
        transactionTitle: String,
        amount: String,
        paymentDate: String
    ) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("account_from_id", accountFromId)
                apiBody.addProperty("account_to_id", accountToId)
                apiBody.addProperty("transaction_title", transactionTitle)
                apiBody.addProperty("amount", amount)
                apiBody.addProperty("payment_date", paymentDate)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance?.myApi?.pay(apiBody.toString())

            call?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            PayDataList.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            PayDataList.value = CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                    progressObservable.value = false
                    PayDataList.value = CommonResponseModel(null, 500)
                }
            })
        }
    }
}