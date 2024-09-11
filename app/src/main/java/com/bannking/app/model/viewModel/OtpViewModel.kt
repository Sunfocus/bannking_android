package com.bannking.app.model.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.core.BaseViewModel
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.model.ErrorResponse
import com.bannking.app.model.ErrorResponseOtp
import com.bannking.app.network.RetrofitClient
import com.bannking.app.network.okhttploginterceptor.LoggingInterceptor.Companion.gson
import com.bannking.app.utils.Constants
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpViewModel(val App: Application) : BaseViewModel(app = App) {


    var otpList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var resendEmail: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)
    var registerDataList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var accountForgetData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var forgotPassword: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var errorResponse: MutableLiveData<String> = MutableLiveData(null)


    fun setDataInOtpList(strEmail: String, strTypeNumber: String,name: String,userName:String) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("security", Constants.SECURITY_1)
            apiBody.addProperty("email", strEmail)
            apiBody.addProperty("type", strTypeNumber)
            apiBody.addProperty("name", name)
            apiBody.addProperty("username", userName)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.sendOtp(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        otpList.value = CommonResponseModel(response.body(), response.code())
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        otpList.value = CommonResponseModel(jsonObject, response.code())
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val mainModel = gson.fromJson(errorBodyString, ErrorResponseOtp::class.java)
                    errorResponse.value = mainModel.message
//                    otpList.value = CommonResponseModel(null, 500)

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
                otpList.value = CommonResponseModel(null, 500)
            }
        })
    }
    fun resendMail(strEmail: String) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("email", strEmail)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.postEmailResend(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        resendEmail.value = CommonResponseModel(response.body(), response.code())
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        resendEmail.value = CommonResponseModel(jsonObject, response.code())
                    }
                } else resendEmail.value = CommonResponseModel(null, 500)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
                resendEmail.value = CommonResponseModel(null, 500)
            }
        })
    }
    fun setDataInOtpListForget(strEmail: String, strTypeNumber: String,name: String) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("security", Constants.SECURITY_1)
            apiBody.addProperty("username", strEmail)
            apiBody.addProperty("type", strTypeNumber)
            apiBody.addProperty("name", name)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.sendOtpForget(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        otpList.value = CommonResponseModel(response.body(), response.code())
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        otpList.value = CommonResponseModel(jsonObject, response.code())
                    }
                } else {
                        val errorBodyString = response.errorBody()?.string()
                        val mainModel = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                        errorResponse.value = mainModel.message
                        otpList.value = CommonResponseModel(null, 500)

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
                otpList.value = CommonResponseModel(null, 500)
            }
        })
    }

    fun setDataInRegisterDataList(
        strEmail: String,
        strUsername: String,
        strPassword: String,
        country: String,
        name: String
    ) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("security", Constants.SECURITY_1)
            apiBody.addProperty("username", strUsername)
            apiBody.addProperty("email", strEmail)
            apiBody.addProperty("password", strPassword)
            apiBody.addProperty("name", name)
            apiBody.addProperty("country", country)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.registerUser(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        registerDataList.value =
                            CommonResponseModel(response.body(), response.code())
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        registerDataList.value = CommonResponseModel(jsonObject, response.code())
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val mainModel = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                    errorResponse.value = mainModel.message
//                    registerDataList.value = CommonResponseModel(null, 500)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
                registerDataList.value = CommonResponseModel(null, 500)
            }
        })
    }


    fun setDataInFindAccountList(strEmail: String) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("security", Constants.SECURITY_1)
            apiBody.addProperty("email", strEmail)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.forgetUserNameAndPassword(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        accountForgetData.value =
                            CommonResponseModel(response.body(), response.code())
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        accountForgetData.value = CommonResponseModel(jsonObject, response.code())
                    }
                } else accountForgetData.value = CommonResponseModel(null, 500)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
                accountForgetData.value = CommonResponseModel(null, 500)
            }
        })
    }


    fun forgotPassword(new_pass: String,id: String) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("security", Constants.SECURITY_1)
            apiBody.addProperty("id", id)
            apiBody.addProperty("new_pass", new_pass)
            apiBody.addProperty("token", App.FCM_TOKEN)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.forgotPassword(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        forgotPassword.value =
                            CommonResponseModel(response.body(), response.code())
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        forgotPassword.value = CommonResponseModel(jsonObject, response.code())
                    }
                } else forgotPassword.value = CommonResponseModel(null, 500)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
                forgotPassword.value = CommonResponseModel(null, 500)
            }
        })
    }


}