package com.bannking.app.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityVerifyMailBinding
import com.bannking.app.model.retrofitResponseModel.otpModel.OtpModel
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.model.viewModel.OtpViewModel
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SessionManager
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Locale

class VerifyMailActivity :
    BaseActivity<OtpViewModel, ActivityVerifyMailBinding>(OtpViewModel::class.java) {


    private lateinit var edit: Array<EditText>
    private lateinit var viewModel: OtpViewModel
    var email = ""
    var userName = ""
    private var password = ""
    private var otp = ""
    private var name = ""

    override fun getBinding(): ActivityVerifyMailBinding {
        return ActivityVerifyMailBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: OtpViewModel) {
        this.viewModel = viewModel

    }

    override fun initialize() {
        checkAndRequestPermissions()
        if (intent != null) {
            email = intent.getStringExtra("Email").toString()
            name = intent.getStringExtra("name").toString()
            userName = intent.getStringExtra("UserName").toString()
            password = intent.getStringExtra("Password").toString()
            otp = intent.getStringExtra("otp").toString()
            Log.d("sadaskjdhas", otp.toString())
            Log.d("nnnnnnnnnnn", name)
            Log.d("nnnnnnnnnnn", email)
//            viewModel.setDataInOtpList(intent.getStringExtra("Email").toString(), Constants.SECURITY_1)
        }

        val text =
            "Please verify your email by clicking the \"Activate your account\" button sent to $email. If you don't see it in your inbox, check your spam folder, and then return to this screen to continue with your registration."

        val spannableString = SpannableString(text)
        val startIndex = text.indexOf(email)
        val endIndex = startIndex + email.length

        val color = ContextCompat.getColor(this, R.color.btn_first_orange)
        spannableString.setSpan(
            ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding!!.tvToVerify.text = spannableString

        binding!!.imgBack.setOnClickListener {
            finish()
        }

    }

    override fun observe() {
        with(viewModel) {

            otpList.observe(this@VerifyMailActivity) { apiResponse ->
                if (apiResponse != null) {
                    if (apiResponse.code in 199..299) {
                        val model = gson.fromJson(apiResponse.apiResponse, OtpModel::class.java)
                        if (model.status == 200) {
//                            Toast.makeText(
//                                this@RegisterOtpVerifyActivity,
//                                model.message + " " + model.note,
//                                Toast.LENGTH_SHORT
//                            ).show()
                            otp = model.data!!.otp.toString()
                        } else {
                            model.message?.let { dialogClass.showError(it) }

//                            Toast.makeText(
//                                this@RegisterOtpVerifyActivity,
//                                model.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }
                    }
                }
            }
            errorResponse.observe(this@VerifyMailActivity) { message ->
                if (message != null) {
                    dialogClass.showErrorMessageDialog(message)
                }
            }
            registerDataList.observe(this@VerifyMailActivity) { register ->
                if (register != null) {
                    if (register.apiResponse != null) {
                        val mainModel = gson.fromJson(register.apiResponse, UserModel::class.java)
                        if (mainModel.status == 200) {
                            Log.d("sdfbvsdfshdfsd", mainModel.data!!.toString())

                            if (mainModel.data?.notification_status != true) {
                                FirebaseMessaging.getInstance()
                                    .unsubscribeFromTopic("user_" + mainModel.data!!.id)
                                    .addOnCompleteListener { task ->
                                    }

                                FirebaseMessaging.getInstance()
                                    .unsubscribeFromTopic("topic_bnk_usrs_broadcast")
                                    .addOnCompleteListener { task ->

                                    }
                            } else {
                                FirebaseMessaging.getInstance()
                                    .subscribeToTopic("user_" + mainModel.data!!.id)
                                    .addOnCompleteListener { task ->
                                        Log.d("token====", mainModel.data!!.id.toString())
                                    }
                                FirebaseMessaging.getInstance()
                                    .subscribeToTopic("topic_bnk_usrs_broadcast")
                                    .addOnCompleteListener { task ->

                                    }
                            }


                            sessionManager.setUserDetails(SessionManager.userData, mainModel.data!!)
                            sessionManager.setBoolean(SessionManager.isLogin, true)
                            sessionManager.setString(
                                SessionManager.USERTOKEN, "bearer ${mainModel.extraData}"
                            )
                            startActivity(
                                Intent(
                                    this@VerifyMailActivity, CompletionActivity::class.java
                                )
                            )
                            finishAffinity()
                        } else {
                            mainModel.message?.let { dialogClass.showError(it) }

//                            Toast.makeText(
//                                this@RegisterOtpVerifyActivity,
//                                mainModel.message.toString(),
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }
                    }
                }
            }

            progressObservable.observe(this@VerifyMailActivity) {
                if (it != null) {
                    if (it) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
                }
            }

        }
    }


    override fun setMethod() {
        setOnClickListener()
    }

    private fun getCountryNameFromNetwork(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val networkCountryIso = telephonyManager.networkCountryIso
        val simCountryIso = telephonyManager.simCountryIso

        return when {
            !networkCountryIso.isNullOrEmpty() -> {
                Locale("", networkCountryIso).isO3Country
            }

            !simCountryIso.isNullOrEmpty() -> {
                Locale("", simCountryIso).isO3Country
            }

            else -> Locale.getDefault().isO3Country
        }
    }

    private val PERMISSIONS_REQUEST_CODE = 1
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }


    private fun setOnClickListener() {
        with(binding!!) {
            btnSubmit.setOnClickListener {
                val country = getCountryNameFromNetwork(this@VerifyMailActivity)
                viewModel.setDataInRegisterDataList(
                    email, userName, password, country, name
                )
            }

            btnResend.setOnClickListener {
                viewModel.setDataInOtpList(
                    email, Constants.SECURITY_1, name, userName
                )
            }
        }

    }


}