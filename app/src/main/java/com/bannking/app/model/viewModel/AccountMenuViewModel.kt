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
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountMenuViewModel(val App: Application) : BaseViewModel(App) {

    var accountTitleList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var createOwnMenuTitleDataList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var saveHeaderDataList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)


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

    fun setDataInCreateOwnMenuTitleList(strTitleName: String, userToken: String?) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("name", strTitleName)
                apiBody.addProperty("token", it)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance!!.myApi.createOwnAccountTitle(apiBody.toString(),userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            createOwnMenuTitleDataList.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            createOwnMenuTitleDataList.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    } else {
                        createOwnMenuTitleDataList.value = CommonResponseModel(null, 500)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    createOwnMenuTitleDataList.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

    fun setDataInSaveHeaderDataList(accMenuId: String) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("acc_menu_id", accMenuId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance!!.myApi.saveHeader(apiBody.toString())
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            saveHeaderDataList.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            saveHeaderDataList.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    } else
                        saveHeaderDataList.value = CommonResponseModel(null, 500)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    saveHeaderDataList.value = CommonResponseModel(null, 500)
                }
            })
        }
    }
}