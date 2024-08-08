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

class ProfileUpdateViewModel(val App: Application) : BaseViewModel(App) {

    var profileUpdateData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)
    var profileData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var deleteAccountData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    fun setDataUpdateDataList(
        strUserName: String,
        strEmail: String,
        strProfileImage: String,
        firstName: String,
        userToken: String?,
        checked: Boolean
    ) {
        App.FCM_TOKEN.let {

            val imgFile: MultipartBody.Part = if (strProfileImage.isNotEmpty()) {
                val file = File(strProfileImage)
                val requestFile: RequestBody =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile", file.name, requestFile)
            } else {
                MultipartBody.Part.createFormData("profile", "")
            }
            val mdToken: RequestBody =
                it.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val mdID: RequestBody = BaseActivity.userModel!!.id.toString()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val mdEmail: RequestBody =
                strEmail.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val mdName: RequestBody =
                firstName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val mdUserName: RequestBody =
                strUserName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val mdSecurity: RequestBody =
                Constants.SECURITY_0.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val switchFaceVerification: RequestBody =
                checked.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

            progressObservable.value = true
            val call = RetrofitClient.instance!!.myApi.updateProfile(
                mdSecurity,
                mdUserName,
                mdEmail,
                mdID,
                mdToken,mdName,switchFaceVerification,
                imgFile,userToken!!
            )
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            profileUpdateData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            profileUpdateData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    } else {
                        profileUpdateData.value = CommonResponseModel(null, 500)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    profileUpdateData.value = CommonResponseModel(null, 500)
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
    fun setDataProfileDataList(userToken: String?) {
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
                            profileData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            profileData.value = CommonResponseModel(jsonObject, response.code())
                        }
                    } else {
                        profileData.value = CommonResponseModel(null, 500)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    profileData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }
}