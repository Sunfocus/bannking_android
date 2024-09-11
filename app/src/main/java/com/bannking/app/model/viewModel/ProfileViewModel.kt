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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileViewModel(val App: Application) : BaseViewModel(App) {

    var languageList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var currencyList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var changeLanguageData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var changeCurrencyData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var changeNotificationData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var deleteAccountData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var notificationUpdate: MutableLiveData<CommonResponseModel> = MutableLiveData(null)


    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)

    fun setDataInLanguageList(userToken: String?) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("security", Constants.SECURITY_1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.getLanguageList(userToken!!)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        languageList.value = CommonResponseModel(response.body(), response.code())
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
            }
        })
    }

    fun setDataInCurrencyList(userToken: String?) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("security", Constants.SECURITY_1)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.getCurrencyList(userToken!!)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        currencyList.value = CommonResponseModel(response.body(), response.code())
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
            }
        })
    }

    fun setDataUpdateDataList(
        notification: Boolean,
        userToken:String?
    ) {
        App.FCM_TOKEN.let {
            val switchNotification: RequestBody =
                notification.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

            progressObservable.value = true
            val call = RetrofitClient.instance!!.myApi.updateNotification(switchNotification,userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            notificationUpdate.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            notificationUpdate.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    } else {
                        notificationUpdate.value = CommonResponseModel(null, 500)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    notificationUpdate.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setChangeLanguageDataList(strLanguageId: String, userToken: String?) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("language_id", strLanguageId)
                apiBody.addProperty("token", it)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance!!.myApi.changeLanguage(apiBody.toString(),userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            BaseActivity.userModel!!.languageId = strLanguageId.toInt()
                            changeLanguageData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            changeLanguageData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    changeLanguageData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setDataCurrencyChangeDataList(strCurrencyId: String, userToken: String?) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("currency_id", strCurrencyId)
                apiBody.addProperty("token", it)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance!!.myApi.changeCurrency(apiBody.toString(),userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            BaseActivity.userModel!!.currencyId = strCurrencyId.toInt()
                            changeCurrencyData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            changeCurrencyData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    changeCurrencyData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setDataNotificationDataList(isChecked: Boolean, userToken: String?) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val notificationOnOFF: Int = if (isChecked) 1 else 0
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("notification", notificationOnOFF)
                apiBody.addProperty("token", it)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance!!.myApi.changeNotification(apiBody.toString(),userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            changeNotificationData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            changeNotificationData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    changeNotificationData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setDataDeleteAccountDataList(userToken: String?) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance!!.myApi.deleteAccount(userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            deleteAccountData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            deleteAccountData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    deleteAccountData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

}