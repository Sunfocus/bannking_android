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
import com.bannking.app.model.ExchangeTokenResponse
import com.bannking.app.model.GetBankLinkTokenResponse
import com.bannking.app.model.GetBankListResponse
import com.bannking.app.model.retrofitResponseModel.headerModel.Data
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.Constants
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HeaderBankViewModel(val App: Application) : BaseViewModel(App) {

    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)
    var accountTitleList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)

    val accountListData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var filterTabDataList: MutableLiveData<ArrayList<Data>> = MutableLiveData(null)
    var errorResponse: MutableLiveData<String> = MutableLiveData(null)

    fun setDataInAccountTitleList(userToken: String?) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
            } catch (e: Exception) {
                Log.e("TAG_EXCEPTION", "setDataInAccountTitleList: " + e.message.toString())
            }

            val call = RetrofitClient.instance!!.myApi.getAccountMenuTitle(
                userToken!!
            )
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            accountTitleList.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            accountTitleList.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    } else
                        accountTitleList.value = CommonResponseModel(null, 500)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    accountTitleList.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun getUserProfileData(userToken: String?): LiveData<UserModel> {

        progressObservable.value = true
        val mutableLiveData = MutableLiveData<UserModel>()

        val call = RetrofitClient.instance!!.myApi.getProfileLive(userToken!!)
        call.enqueue(object : Callback<UserModel> {
            override fun onResponse(
                call: Call<UserModel>, response: Response<UserModel>
            ) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                progressObservable.value = false
            }
        })
        return mutableLiveData
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

            Log.d("UserToken",userToken)
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
                }else{
                    val errorBody = response.errorBody()?.string() // Convert error body to string
                    errorBody?.let {
                        try {
                            // You can parse the errorBody JSON to get the message if needed
                            val jsonObject = JSONObject(it)
                            val errorMessage = jsonObject.optString("message", "Unknown error")
                            errorResponse.postValue(errorMessage)
                        } catch (e: JSONException) {
                            errorResponse.postValue("Failed to parse error response")
                        }
                    } ?: run {
                        errorResponse.postValue("Unknown error occurred")
                    }
                    Log.e("errorMessage", errorBody ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<GetBankListResponse>, t: Throwable) {
                errorResponse.postValue(t.message) // Handle failure with error message
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
        userToken: String?,
        public_token: String,
        accountIdForBank: String,
        typeForCreateHeader: String?,
        result: Boolean
    ): LiveData<ExchangeTokenResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<ExchangeTokenResponse>()
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("public_token", public_token)
            jsonObject.addProperty("account_title_id", accountIdForBank)
            jsonObject.addProperty("create_account_title_status", result)
            jsonObject.addProperty("type", typeForCreateHeader)
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


    @SuppressLint("NullSafeMutableLiveData")
    fun setIdInFilterDatanull(id: ArrayList<Data>?) {
//        accountListData.value
        filterTabDataList.value = id
    }
}