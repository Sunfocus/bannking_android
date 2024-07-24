package com.bannking.app.model.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.core.BaseViewModel
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SessionManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashViewModel(val App: Application) : BaseViewModel(App) {

    var appVersionDataList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var getPremiumStatusDataList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)


    fun setDataInAppVersionDataList() {
        progressObservable.value = true
        val apiBody = JsonObject()

        try {
            apiBody.addProperty("security", Constants.SECURITY_1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val call = RetrofitClient.instance!!.myApi.appDetails(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        appVersionDataList.value =
                            CommonResponseModel(response.body(), response.code())
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        appVersionDataList.value = CommonResponseModel(jsonObject, response.code())
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
                appVersionDataList.value = CommonResponseModel(null, 500)
            }
        })
    }

    fun setPremiumStatusDataList() {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            val sessionManager = SessionManager(App, SessionManager.mySharedPref)
            val data = sessionManager.getUserDetails(SessionManager.userData)

            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("token", it)
                apiBody.addProperty("id", data?.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val call = RetrofitClient.instance!!.myApi.getPremiumStatus(apiBody.toString())
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            getPremiumStatusDataList.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            getPremiumStatusDataList.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    getPremiumStatusDataList.value = CommonResponseModel(null, 500)
                }
            })
        }
    }


}