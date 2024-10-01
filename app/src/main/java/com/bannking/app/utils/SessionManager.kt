package com.bannking.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.bannking.app.model.retrofitResponseModel.soundModel.Voices
import com.bannking.app.model.retrofitResponseModel.userModel.Data
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class SessionManager(context: Context, sharedName: String?) {

    private var sharedPref: SharedPreferences
    private var mEditor: SharedPreferences.Editor
    val gson: Gson = Gson()

    init {
        sharedPref = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE)
        mEditor = sharedPref.edit()
    }

    fun setUserDetails(key: String?, value: Data) {
        mEditor.putString(key, Gson().toJson(value))
        mEditor.commit()
    }

    fun getUserDetails(key: String?): Data? {
        return gson.fromJson(sharedPref.getString(key, ""), Data::class.java)
    }

    fun saveLong(key: String, value: Long) {
        sharedPref.edit().putLong(key, value).apply()
    }


    fun getLong(key: String): Long {
        return sharedPref.getLong(key, 0)
    }

    fun saveInt(key: String, value: Int) {
        sharedPref.edit().putInt(key, value).apply()
    }
    fun getInt(key: String): Int {
        return sharedPref.getInt(key, 0)
    }


    fun getBoolean(key: String?): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun getString(key: String?): String? {
        return sharedPref.getString(key, "")
    }
    fun updateLastActiveTime() {
        sharedPref.edit().putLong(KEY_LAST_ACTIVE_TIME, System.currentTimeMillis()).apply()

    }
    fun getLastActiveTime(): Long {
        return sharedPref.getLong(KEY_LAST_ACTIVE_TIME, 0)
    }
    fun setBoolean(key: String?, value: Boolean?) {
        mEditor.putBoolean(key, value!!)
        mEditor.commit()
    }

    fun setString(key: String?, value: String?) {
        mEditor.putString(key, value)
        mEditor.commit()
    }

    fun logOut() {
        mEditor.putBoolean(isLogin, false)
        mEditor.clear()
        mEditor.commit()
        mEditor.apply()
    }


    fun setAnnouncementVoice(value: String?) {
        mEditor.putString(VOICE, value)
        mEditor.commit()
    }

    fun getAnnouncementVoice(): String? {
        return sharedPref.getString(VOICE, "")
    }


    fun setLanguage(value: String?) {
        mEditor.putString(LANGUAGE, value)
        mEditor.commit()
    }

    fun getLanguage(): String? {
        return sharedPref.getString(LANGUAGE, "")
    }

    fun setTab1(value: String?) {
        mEditor.putString(TAB1, value)
        mEditor.commit()
    }

    fun getTab1(): String? {
        return sharedPref.getString(TAB1, "")
    }

    fun setTab2(value: String?) {
        mEditor.putString(TAB2, value)
        mEditor.commit()
    }

    fun getTab2(): String? {
        return sharedPref.getString(TAB2, "")
    }

    fun setCurrency(value: String?) {
        mEditor.putString(CURRENCY, value)
        mEditor.commit()
    }

    fun getCurrency(): String? {
        return sharedPref.getString(CURRENCY, "")
    }


    companion object {
        //Use For Data Handle

        const val APP_PRIVACY_POLICY = "privacyPolicy"

        var isLogin = "isLogin"
        var USERTOKEN = "userToken"
        var DAYNIGHT = "dayNight"
        var isDeleteORLogOut = "isDeleteORLogOut"
        const val KEY_LAST_ACTIVE_TIME = "last_active_time"
        var userData = "userData"

        //        var TOKEN = "TOKEN"
        var mySharedPref = "BankingShared"
        var AppVersion = "AppVersion"
        var AppIcon = "AppIcon"
        var isRemember = "Remember"


        var UserId = "User_id"

        var Password = "Password"


        var myInAppPurchase = "myInAppPurchase"
        var purchaseId = "purchaseId"
        var isPremium = "isPremium"

        var savedSharedPreferences = "savedSharedPreferences"
        var isRated = "isRated"
        var VOICE = "voice"
        var LANGUAGE = "Language"
        var ADTIME = "adTime"

        var REVIEWMANAGER = "reviewManager"
        var TAB1 = "Tab1"
        var TAB2 = "Tab2"
        var currentTab = "currentTab"
        var clickAccountType = "clickAccountType"
        var CURRENCY = "CURRENCY"
        var VOICEMAKERLIST = "VOICEMAKERLIST"
        var VOICEFORAPI = "VOICEFORAPI"
        var ENGINEFORAPI = "ENGINEFORAPI"
        var VOICEGENDER = "VOICEGENDER"
        var LANGUAGECODEFORAPI = "LANGUAGECODEFORAPI"
        var LANGUAGENAMEFORAPI = "LANGUAGENAMEFORAPI"

    }


}