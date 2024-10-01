package com.bannking.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.bannking.app.model.retrofitResponseModel.soundModel.Voices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPref(private var context: Context) {
    private var pref: SharedPreferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE)


    fun saveString(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }


    fun getString(key: String): String {
        return pref.getString(key, "").toString()
    }

    fun saveInt(key: String, value: Int) {
        pref.edit().putInt(key, value).apply()
    }

    fun saveLong(key: String, value: Long) {
        pref.edit().putLong(key, value).apply()
    }

    fun saveFloat(key: String, value: Float) {
        pref.edit().putFloat(key, value).apply()
    }

    fun getFloat(key: String): Float {
        return pref.getFloat(key, 0.00f)
    }


    fun getLong(key: String): Long {
        return pref.getLong(key, 0)
    }

    fun getInt(key: String): Int {
        return pref.getInt(key, 0)
    }

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, false)
    }

    fun saveBoolean(key: String, value: Boolean) {
        pref.edit().putBoolean(key, value).apply()
    }


    fun clearPreference(context: Context) {
        pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val prefsEditor: SharedPreferences.Editor = pref.edit()
        prefsEditor.clear()
        prefsEditor.apply()
    }

    fun saveListOfVoiceMaker(key: String, voicesList: ArrayList<Voices>) {
        val gson = Gson()
        val jsonString = gson.toJson(voicesList)
        pref.edit().putString(key, jsonString).apply()
    }

    fun getListOfVoiceMaker(key: String): ArrayList<Voices> {
        val gson = Gson()
        val jsonString = pref.getString(key, null)
        val type = object : TypeToken<ArrayList<Voices>>() {}.type
        return if (jsonString != null) {
            gson.fromJson(jsonString, type)
        } else {
            arrayListOf()
        }
    }

}