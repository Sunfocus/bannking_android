package com.bannking.app.model.viewModel

import android.app.Application
import com.bannking.app.core.BaseViewModel

class SignInViewModel(val App: Application) : BaseViewModel(App) {

//    var loginData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
//    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)

//    fun loginUser(strUserName: String, strPassword: String) {
//        App.FCM_TOKEN.let {
//            progressObservable.value = true
//            val apiBody = JsonObject()
//            try {
//                apiBody.addProperty("security", Constants.SECURITY_1)
//                apiBody.addProperty("username", strUserName)
//                apiBody.addProperty("password", strPassword)
//                apiBody.addProperty("token", it)
//            } catch (e: JSONException) {
//                e.printStackTrace()
//
//            }
//            val call = RetrofitClient.instance!!.myApi.userLogin(apiBody.toString())
//            call.enqueue(object : retrofit2.Callback<JsonObject> {
//                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
//                    progressObservable.value = false
//                    if (response.isSuccessful) {
//                        if (response.code() in 199..299) {
//                            loginData.value = CommonResponseModel(response.body(), response.code())
//                        } else if (response.code() in 400..500) {
//                            assert(response.errorBody() != null)
//                            val errorBody = response.errorBody().toString()
//                            val jsonObject: JsonObject =
//                                JsonParser.parseString(errorBody).asJsonObject
//                            loginData.value = CommonResponseModel(jsonObject, response.code())
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
//                    progressObservable.value = false
//                    loginData.value = CommonResponseModel(null, 500)
//                }
//            })
//        }
//    }

}