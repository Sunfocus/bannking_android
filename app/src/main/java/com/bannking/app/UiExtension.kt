package com.bannking.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.LocaleList
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bannking.app.utils.GetTokenFromOverride
import com.bannking.app.utils.SessionManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.zeugmasolutions.localehelper.Locales
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object UiExtension {
    fun isDarkModeEnabled(): Boolean {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }
    fun AppCompatActivity.generateFCM(getTokenFromOverride: GetTokenFromOverride) {
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG_FCM", "Fetching FCM registration token failed", task.exception)
                getTokenFromOverride.sendToString("null")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            getTokenFromOverride.sendToString(token)
            Log.d("TAG_FCM_MY", token)
        })

    }

    val AppCompatActivity.FCM_TOKEN: String?
        get() {
            return SessionManager(this, SessionManager.mySharedPref).getString("SetFcm")
        }

    val Application.FCM_TOKEN: String?
        get() {
            return SessionManager(this, SessionManager.mySharedPref).getString("SetFcm")
        }

    fun Context.drawable(id: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resources.getDrawable(id, theme)
        } else {
            resources.getDrawable(id)
        }
    }

    fun Activity.getCurrentLanguage(): Locale? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
    }

//    fun Application.FCM_TOKEN(): String? {
//        val sessionManager = SessionManager(this, SessionManager.mySharedPref)
//        return sessionManager.getString("SetFcm")
//    }

    fun View.clicks(): Flow<Unit> = callbackFlow {
        setOnClickListener {
            trySend(Unit).isSuccess
        }
        awaitClose { setOnClickListener(null) }
    }

    internal fun BottomNavigationView.checkItem(actionId: Int) {
        if (selectedItemId != actionId) {
            selectedItemId = actionId
        }
    }

    fun String.replaceNonstandardDigits(): String {
        val input: String = this
        if (input == null || input.isEmpty()) {
            return input
        }
        val builder = StringBuilder()
        for (element in input) {
            if (isNonstandardDigit(element)) {
                val numericValue = Character.getNumericValue(element)
                if (numericValue >= 0) {
                    builder.append(numericValue)
                }
            } else {
                builder.append(element)
            }
        }
        return builder.toString()
    }

    private fun isNonstandardDigit(ch: Char): Boolean {
        return Character.isDigit(ch) && ch !in '0'..'9'
    }

    fun String.getDateMMMDDYYYY(): String {
        val format1 = SimpleDateFormat("yyyy-MM-dd")
        val format2 = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
        val date = format1.parse(this)
        return date?.let { format2.format(it) }.toString()
    }


    private fun String.getDecoratedStringFromNumber(): String {
        val numberPattern = "#,###,###,###"
        var decoStr = ""
        val formatter = DecimalFormat.getInstance(Locales.English) as DecimalFormat
        formatter.applyPattern(numberPattern)
        decoStr = formatter.format(this.toLong())
        return decoStr
    }
}