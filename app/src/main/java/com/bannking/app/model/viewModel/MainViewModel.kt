package com.bannking.app.model.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.BaseViewModel
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.model.DeleteBankResponse
import com.bannking.app.model.HideBankResponse
import com.bannking.app.model.PostVoiceErrorResponse
import com.bannking.app.model.PostVoiceResponse
import com.bannking.app.model.retrofitResponseModel.headerModel.Data
import com.bannking.app.network.ApiInterFace
import com.bannking.app.network.RetrofitClient
import com.bannking.app.network.RetrofitClients
import com.bannking.app.network.okhttploginterceptor.LoggingInterceptor
import com.bannking.app.utils.Constants
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(val App: Application) : BaseViewModel(App) {
    //    by lazy { MutableLiveData<CommonResponseModel>() }
    val accountListData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)
    var deleteBankAccount: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var filterTabDataList: MutableLiveData<ArrayList<Data>> = MutableLiveData(null)

    //    var fcmToken : MutableLiveData<String> = MutableLiveData("")
    var getProfileData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)

    var errorResponse: MutableLiveData<String> = MutableLiveData(null)


    fun postVoiceInLanguageList(
        engine: String,
        voiceId: String,
        language: String,
        completeText: String
    ): LiveData<PostVoiceResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<PostVoiceResponse>()
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("Engine", engine)
            apiBody.addProperty("VoiceId", voiceId)
            apiBody.addProperty("LanguageCode", language)
            apiBody.addProperty("Text", completeText)
            apiBody.addProperty("OutputFormat", "mp3")
            apiBody.addProperty("SampleRate", "48000")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClients.voiceMakerApiClient().create(ApiInterFace::class.java)
        call.postVoiceMakerLiveOb(apiBody).enqueue(object : Callback<PostVoiceResponse> {
            override fun onResponse(
                call: Call<PostVoiceResponse>,
                response: Response<PostVoiceResponse>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    } else {
                        val errorBodyString = response.errorBody()?.string()
                        val mainModel = LoggingInterceptor.gson.fromJson(
                            errorBodyString,
                            PostVoiceErrorResponse::class.java
                        )
                        errorResponse.value = mainModel.message
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val mainModel = LoggingInterceptor.gson.fromJson(
                        errorBodyString,
                        PostVoiceErrorResponse::class.java
                    )
                    errorResponse.value = mainModel.message
                }
            }

            override fun onFailure(call: Call<PostVoiceResponse>, t: Throwable) {
                progressObservable.value = false
            }

        })
        return mutableLiveData
    }

    fun hideBankFromList(
        institutionId: String, userToken: String?, accountId: String
    ): LiveData<HideBankResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<HideBankResponse>()
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("accountId", accountId)
            apiBody.addProperty("instituteId", institutionId)
          } catch (e: JSONException) {
            e.printStackTrace()
        }

        val call = RetrofitClient.instance!!.myApi.hideBankAccount(userToken!!,apiBody)

        call.enqueue(object : Callback<HideBankResponse> {
            override fun onResponse(
                call: Call<HideBankResponse>,
                response: Response<HideBankResponse>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<HideBankResponse>, t: Throwable) {
                progressObservable.value = false
            }

        })
        return mutableLiveData
    }


    fun showAllBankFromList(
        institutionId: String, userToken: String?, accountId: String
    ): LiveData<HideBankResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<HideBankResponse>()
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("instituteId", institutionId)
            apiBody.addProperty("accountId", accountId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val call = RetrofitClient.instance!!.myApi.showAllBankAccount(userToken!!,apiBody)

        call.enqueue(object : Callback<HideBankResponse> {
            override fun onResponse(
                call: Call<HideBankResponse>,
                response: Response<HideBankResponse>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<HideBankResponse>, t: Throwable) {
                progressObservable.value = false
            }

        })
        return mutableLiveData
    }



    fun removeBankFromList(
        institutionId: String, userToken: String?

    ): LiveData<DeleteBankResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<DeleteBankResponse>()
        val call = RetrofitClient.instance!!.myApi.removeBankPlaid(institutionId, userToken!!)

        call.enqueue(object : Callback<DeleteBankResponse> {
            override fun onResponse(
                call: Call<DeleteBankResponse>,
                response: Response<DeleteBankResponse>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<DeleteBankResponse>, t: Throwable) {
                progressObservable.value = false
            }

        })
        return mutableLiveData
    }

    fun getUserProfileData(userToken: String?) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel?.id)
                apiBody.addProperty("token", it)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance!!.myApi.getProfile(userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            getProfileData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            getProfileData.value = CommonResponseModel(jsonObject, response.code())
                        }
                    } else {
                        getProfileData.value = CommonResponseModel(null, 500)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    getProfileData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setDataInAccountList(userToken: String) {
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
            val call = RetrofitClient.instance?.myApi?.accountList(userToken)

            Log.d("UserToken", userToken)
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

    fun setDataInDeleteBankAccount(
        strAccountId: String,
        headerTitleID: String,
        type: String,
        userToken: String?,
        budgetId: String?
    ) {
        progressObservable.value = true
        App.FCM_TOKEN.let {
            val apiBody = JsonObject()
            try {
//                apiBody.addProperty("security", Constants.SECURITY_0)
//                apiBody.addProperty("id", BaseActivity.userModel!!.id)
//                apiBody.addProperty("token", it)
//                apiBody.addProperty("accountId", strAccountId)
//                apiBody.addProperty("header_title_id", headerTitleID)
//                apiBody.addProperty("type", type)
//                apiBody.addProperty("budget_id", budgetId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance?.myApi?.deleteBankAccount(
                strAccountId,
                type,
                budgetId!!,"",
                userToken!!
            )

            call?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            deleteBankAccount.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            deleteBankAccount.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    } else deleteBankAccount.value = CommonResponseModel(null, 500)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    deleteBankAccount.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setDataInHeaderTitleList(userToken: String?) {
        progressObservable.value = true
        val apiBody = JsonObject()

        App.FCM_TOKEN.let {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        apiBody.addProperty("security", Constants.SECURITY_0)
                        apiBody.addProperty("id", BaseActivity.userModel!!.id)
                        apiBody.addProperty("token", it)

                        val call =
                            RetrofitClient.instance?.myApi?.headerTitleList(userToken!!)

                        call?.enqueue(object : Callback<JsonObject> {
                            override fun onResponse(
                                call: Call<JsonObject>,
                                response: Response<JsonObject>
                            ) {
                                progressObservable.value = false
                                if (response.isSuccessful) {
                                    if (response.code() in 199..299) {
                                        headerTitleList.value =
                                            CommonResponseModel(response.body(), response.code())
                                    } else if (response.code() in 400..500) {
                                        assert(response.errorBody() != null)
                                        val errorBody = response.errorBody().toString()
                                        val jsonObject: JsonObject =
                                            JsonParser.parseString(errorBody).asJsonObject
                                        headerTitleList.value =
                                            CommonResponseModel(jsonObject, response.code())
                                    }
                                } else headerTitleList.value = CommonResponseModel(null, 500)
                            }

                            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                                progressObservable.value = false
                                headerTitleList.value = CommonResponseModel(null, 500)
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setIdInFilterData(id: ArrayList<Data>?) {
        accountListData.value
        filterTabDataList.value = id
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setIdInFilterDatanull(id: ArrayList<Data>?) {
//        accountListData.value
        filterTabDataList.value = id
    }

}