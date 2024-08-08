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
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreExpensesViewModel(val App: Application) : BaseViewModel(App) {

    var exploreExpensesDataList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)


    fun setDataInExploreExpensesDataList(userToken: String?) {
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
            val call = RetrofitClient.instance!!.myApi.exploreExpenses(userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            exploreExpensesDataList.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            exploreExpensesDataList.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    } else exploreExpensesDataList.value = CommonResponseModel(null, 500)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    exploreExpensesDataList.value = CommonResponseModel(null, 500)
                }
            })
        }
    }

}