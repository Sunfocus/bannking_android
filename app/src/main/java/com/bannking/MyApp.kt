package com.bannking

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.bannking.app.model.ExceptionListener
import com.bannking.app.utils.AdController
import com.zeugmasolutions.localehelper.LocaleAwareApplication

class MyApp : LocaleAwareApplication(), ExceptionListener {
    override fun onCreate() {
        super.onCreate()
        setupExceptionHandler()
        AdController.initAd(this)

    }
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Log.d("ExampleApp", throwable.message!!)
        Log.d("ExampleApp", thread.name)
    }
    private fun setupExceptionHandler() {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    uncaughtException(Looper.getMainLooper().thread, e)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            uncaughtException(t, e)
        }
    }
}