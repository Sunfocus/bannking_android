package com.bannking.app.model.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.BaseViewModel
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.model.retrofitResponseModel.headerModel.Data
import com.bannking.app.network.RetrofitClient
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

    fun setDataInDeleteBankAccount(
        strAccountId: String,
        headerTitleID: String,
        type: String,
        userToken: String?
    ) {
        progressObservable.value = true
        App.FCM_TOKEN.let {
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("accountId", strAccountId)
                apiBody.addProperty("header_title_id", headerTitleID)
                apiBody.addProperty("type", type)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance?.myApi?.deleteBankAccount(strAccountId,type,userToken!!)

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

    fun setIdInFilterData(id: ArrayList<Data>?) {
        accountListData.value
        filterTabDataList.value = id
    }

    fun setIdInFilterDatanull(id: ArrayList<Data>?) {
//        accountListData.value
        filterTabDataList.value = id
    }

}