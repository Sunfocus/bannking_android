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

class UpgradeViewModel(val App: Application) : BaseViewModel(App) {

    var subscription: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var subscriptionNew: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)


    init {
        Log.d("kjfdghdjfg",App.FCM_TOKEN.toString())
        Log.d("kjfdghdjfg",BaseActivity.userModel!!.id.toString())
    }
    fun setDataInAccountTitleList(
        purchaseToken: String,
        productId: String,
        purchaseDate: Long,
        phoneStat: String
    ) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("purchase_token", purchaseToken)
                apiBody.addProperty("product_id", productId)
                apiBody.addProperty("purchase_date", purchaseDate)
                apiBody.addProperty("phone_stat", phoneStat)
            } catch (e: Exception) {
                Log.e("TAG_EXCEPTION", "setDataInAccountTitleList: " + e.message.toString())
            }

            val call = RetrofitClient.instance!!.myApi.purchaseSubscription(apiBody.toString())
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            subscription.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            subscription.value = CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    subscription.value = CommonResponseModel(null, 500)
                }
            })
        }
    }


    fun setDataInAccountTitleListNew(
        purchaseToken: String,
        productId: String,
        purchaseDate: Long,
        phoneStat: String
    ) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("purchase_token", purchaseToken)
                apiBody.addProperty("product_id", productId)
                apiBody.addProperty("purchase_date", purchaseDate)
                apiBody.addProperty("phone_stat", phoneStat)
            } catch (e: Exception) {
                Log.e("TAG_EXCEPTION", "setDataInAccountTitleList: " + e.message.toString())
            }

            val call = RetrofitClient.instance!!.myApi.purchaseSubscriptionNew(apiBody.toString())
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            subscriptionNew.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            subscriptionNew.value = CommonResponseModel(jsonObject, response.code())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    subscriptionNew.value = CommonResponseModel(null, 500)
                }
            })
        }
    }


}