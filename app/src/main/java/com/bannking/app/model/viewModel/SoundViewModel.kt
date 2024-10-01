package com.bannking.app.model.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bannking.app.core.BaseViewModel
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.model.PostVoiceErrorResponse
import com.bannking.app.model.retrofitResponseModel.soundModel.SoundResponse
import com.bannking.app.network.ApiInterFace
import com.bannking.app.network.RetrofitClient
import com.bannking.app.network.RetrofitClients
import com.bannking.app.network.okhttploginterceptor.LoggingInterceptor
import com.google.gson.JsonObject
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SoundViewModel(val App: Application) : BaseViewModel(App) {
    var soundMakerList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var updateVoiceMakerData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var postSoundMakerList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)
    var errorResponse: MutableLiveData<String> = MutableLiveData(null)


    fun updateVoiceMakerApi(
        userToken: String?,
        languageCodeForAPI: String,
        languageNameForAPI: String,
        voiceForApi: String,
        engineForApi: String,
        voiceGender: String
    ) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("voice_id", voiceForApi)
            apiBody.addProperty("voice_gender", voiceGender)
            apiBody.addProperty("language_code", languageCodeForAPI)
            apiBody.addProperty("language_region", languageNameForAPI)
            apiBody.addProperty("engine", engineForApi)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.updateVoiceMakerData(apiBody,userToken!!)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        updateVoiceMakerData.value = CommonResponseModel(response.body(), response.code())
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
            }
        })
    }
    fun setDataInLanguageList(typeOfLang: String) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("language", typeOfLang)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClients.voiceMakerApiClient().create(ApiInterFace::class.java)
        call.getVoiceMaker(apiBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        soundMakerList.value = CommonResponseModel(response.body(), response.code())
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
            }
        })
    }

    fun setDataInLanguageListWithLiveData(typeOfLang: String): LiveData<SoundResponse> {
        progressObservable.value = true
        val mutableLiveData = MutableLiveData<SoundResponse>()
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("language", typeOfLang)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClients.voiceMakerApiClient().create(ApiInterFace::class.java)
        call.getVoiceMakerWithLive(apiBody).enqueue(object : Callback<SoundResponse> {
            override fun onResponse(call: Call<SoundResponse>, response: Response<SoundResponse>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        mutableLiveData.postValue(response.body())
                    }
                }
            }

            override fun onFailure(call: Call<SoundResponse>, t: Throwable) {
                progressObservable.value = false
            }
        })

        return mutableLiveData

    }

    fun postVoiceInLanguageList(engine: String, voiceId: String, language: String) {
        progressObservable.value = true
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("Engine", engine)
            apiBody.addProperty("VoiceId", voiceId)
            apiBody.addProperty("LanguageCode", language)
            apiBody.addProperty("Text", "Welcome to Banking! Select me if you like my voice.")
            apiBody.addProperty("OutputFormat", "mp3")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClients.voiceMakerApiClient().create(ApiInterFace::class.java)
        call.postVoiceMaker(apiBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        postSoundMakerList.value =
                            CommonResponseModel(response.body(), response.code())
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

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
            }
        })
    }
}