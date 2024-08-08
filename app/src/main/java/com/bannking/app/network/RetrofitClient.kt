package com.bannking.app.network

import com.bannking.app.network.okhttploginterceptor.LoggingInterceptor
import com.bannking.app.network.okhttploginterceptor.Priority
import com.bannking.app.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitClient private constructor() {
    val myApi: ApiInterFace
    private var client = OkHttpClient.Builder()
    private val logig: HttpLoggingInterceptor = HttpLoggingInterceptor()

    companion object {
        @get:Synchronized
        var instance: RetrofitClient? = null
            get() {
                if (field == null) {
                    field = RetrofitClient()
                }
                return field
            }
            private set
    }

    init {

        val logInterceptor by lazy {
            LoggingInterceptor(
                showLog = true,
                isShowAll = false,
                priority = Priority.E,
                visualFormat = true
            )
        }
        client.addInterceptor(logInterceptor)
        logig.setLevel(HttpLoggingInterceptor.Level.BODY)
        val retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URLS)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
        myApi = retrofit.create(ApiInterFace::class.java)
    }

}