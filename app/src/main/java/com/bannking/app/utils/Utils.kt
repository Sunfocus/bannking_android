package com.bannking.app.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bannking.app.model.retrofitResponseModel.accountListModel.Data
import com.bannking.app.model.retrofitResponseModel.accountListModel.ExtraData
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class Utils {

    fun isValidEmailId(email: String): Boolean {

//        if(isValidEmailId(edtEmailId.getText().toString().trim())){
//            Toast.makeText(getApplicationContext(), "Valid Email Address.", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(getApplicationContext(), "InValid Email Address.", Toast.LENGTH_SHORT).show();
//        }

        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    fun isValidPassword(password: String?): Boolean {
        val PASSWORDPATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        val pattern: Pattern = Pattern.compile(PASSWORDPATTERN)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun reMoveCommaString(strString: String): String {
        var returnValue = strString
        returnValue = returnValue.replace("\"", "")
        returnValue = returnValue.replace("\"", "")
        return returnValue
    }

    fun filterAccountListData(data: ArrayList<Data>, query: String): ArrayList<Data> {
        val filterable: ArrayList<Data> = arrayListOf()

        for (item in data) {
//            for (accountDetail in item.accDetail) {
                if (item.userAccountTitle!!.name == query) {
                    filterable.add(item.apply {
                        acc_title_id = item.userAccountTitle!!.id
                        accMenuTitle = item.userAccountTitle!!.name
                    })
                }
//            }
        }
        return filterable
    }

    fun filterAccountListExtraData(data: ArrayList<ExtraData>, query: Int): ArrayList<ExtraData> {
        val filterable: ArrayList<ExtraData> = arrayListOf()

        for (item in data) {
            // Check if accountTitleId is not null and matches the query
            if (item.accountTitleId != null && item.accountTitleId == query) {
                filterable.add(item.apply {
                    accountTitleId = item.accountTitleId
                })
            }
        }
        return filterable
    }


    fun <T> removeDuplicates(list: ArrayList<T>): ArrayList<T>? {

        val newList = ArrayList<T>()
        for (element in list) {
            if (!newList.contains(element)) {
                newList.add(element)
            }
        }
        return newList
    }

    fun <T> reverse(list: ArrayList<T>): ArrayList<T> {
        val length = list.size
        val result = ArrayList<T>(length)
        for (i in length - 1 downTo 0) {
            result.add(list[i])
        }
        return result
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        return sdf.format(Date())
    }

    fun checkisFirstDayOfWeek(): Boolean {
        val getCurrentDay = SimpleDateFormat("EEEE")
        val dayName = getCurrentDay.format(Date())
        Log.e("TAG_name_of_day", "checkisFirstDayOfWeek: " + dayName)
        return dayName == "Monday"
    }
    fun checkIsLastDayOfWeek(): Boolean {
        val getCurrentDay = SimpleDateFormat("EEEE")
        val dayName = getCurrentDay.format(Date())
        Log.e("TAG_name_of_day", "checkisFirstDayOfWeek: " + dayName)
        return dayName == "Sunday"
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateForTransfer(): String {
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
        return sdf.format(Date()).toString()
    }


    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateMain(): String {
        val sdf = SimpleDateFormat(" MMM d, yyyy", Locale.ENGLISH)
        return sdf.format(Date())
    }


    fun checkPermissions(context: Context): Boolean {
        val permissions : Array<String>

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Code for Android 13 (S) and later
            permissions = arrayOf(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.CAMERA
            )

            var result: Int
            val listPermissionsNeeded: MutableList<String> = ArrayList()
            for (p in permissions) {
                result = ContextCompat.checkSelfPermission(context, p)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p)
                }
            }
            if (listPermissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    listPermissionsNeeded.toTypedArray(),
                    10
                )
                return false
            }
            return true
        } else {
            permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )

            var result: Int
            val listPermissionsNeeded: MutableList<String> = ArrayList()
            for (p in permissions) {
                result = ContextCompat.checkSelfPermission(context, p)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p)
                }
            }
            if (listPermissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    listPermissionsNeeded.toTypedArray(),
                    10
                )
                return false
            }
            return true
        }


    }

    fun getImageUri(inImage: Bitmap, context: Context): String {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "title", null)
        Log.e("CheckImageCamera", path.toString())
        val file = FileUtil.from(context, Uri.parse(path))
        return file.toString()
    }


    fun removeAllString(strString: String): String {
        val re = Regex("[^0-9.]")
        return re.replace(strString, "") // works
//        return strString.replace("[^0-9.]", "");
    }

    fun getGenderDescription(gender: Gender): String {
        return when (gender) {
            Gender.MALE -> "Male"
            Gender.FEMALE -> "Female"
            Gender.OTHER -> "Other"
            Gender.UNSPECIFIED -> "Unspecified"
        }
    }


    fun numberToText(number: String): String {
        // Define custom mappings for specific numbers
        val customMappings = arrayOf(
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
        )
        val result = java.lang.StringBuilder()
        var prevIsDigit = false
        for (c in number.toCharArray()) {
            prevIsDigit = if (Character.isDigit(c)) {
                if (prevIsDigit) {
                    result.append(", ")
                }
                val digit = Character.getNumericValue(c)
                if (digit >= 0 && digit < customMappings.size) {
                    // Use the custom mapping
                    result.append(customMappings[digit])
                } else {
                    // Use the default mapping (digits)
                    result.append(digit)
                }
                true
            } else {
                result.append(c)
                false
            }
        }
        return result.toString()
    }

    fun removeCurrencySymbol(currencyAmount: String): String? {
        // Remove the currency symbol (assuming it's a non-digit character)
//        return currencyAmount.replace("[^\\d.]+".toRegex(), "")
        return currencyAmount.replace("[^0-9.-]".toRegex(), "")
    }

    fun convertToDouble(numericString: String): Double {
        return try {
            numericString.toDouble()
        } catch (e: NumberFormatException) {
            // Handle the case where the string is not a valid double
            e.printStackTrace()
            0.0 // or handle it differently based on your use case
        }
    }

    fun convertNumericToWords(monetaryAmount: String): String? {
        return try {
            // Remove currency symbol and commas
            val cleanedString = monetaryAmount.replace("[^\\d.]+".toRegex(), "")

            // Convert the string to BigDecimal to handle precision
            val numericValue = BigDecimal(cleanedString)

            // Convert the numeric part to words
            Words.convert(numericValue.toLong())
        } catch (e: java.lang.NumberFormatException) {
            // Handle the case where the string is not a valid number
            e.printStackTrace()
            "" // or handle it differently based on your use case
        }
    }

}