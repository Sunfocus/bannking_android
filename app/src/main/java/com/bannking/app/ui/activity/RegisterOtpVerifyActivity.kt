package com.bannking.app.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityRegisterOtpVerifyBinding
import com.bannking.app.model.retrofitResponseModel.otpModel.OtpModel
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.model.viewModel.OtpViewModel
import com.bannking.app.utils.Constants
import com.bannking.app.utils.GenericTextWatcher
import com.bannking.app.utils.SessionManager
import java.util.Locale


class RegisterOtpVerifyActivity :
    BaseActivity<OtpViewModel, ActivityRegisterOtpVerifyBinding>(OtpViewModel::class.java) {

    private lateinit var edit: Array<EditText>
    private lateinit var viewModel: OtpViewModel
    var email = ""
    var userName = ""
    private var password = ""
    private var otp = ""
    private var name = ""


    override fun getBinding(): ActivityRegisterOtpVerifyBinding {
        return ActivityRegisterOtpVerifyBinding.inflate(layoutInflater)
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

            Log.d("nnnnnnnnnnn",name)
            Log.d("nnnnnnnnnnn",email)
//            viewModel.setDataInOtpList(intent.getStringExtra("Email").toString(), Constants.SECURITY_1)
        }

        with(binding!!) {
            edit = arrayOf(
                otpEditBox1,
                otpEditBox2,
                otpEditBox3,
                otpEditBox4,
                otpEditBox5,
                otpEditBox6
            )
            otpEditBox1.addTextChangedListener(GenericTextWatcher(otpEditBox1, edit))
            otpEditBox2.addTextChangedListener(GenericTextWatcher(otpEditBox2, edit))
            otpEditBox3.addTextChangedListener(GenericTextWatcher(otpEditBox3, edit))
            otpEditBox4.addTextChangedListener(GenericTextWatcher(otpEditBox4, edit))
            otpEditBox5.addTextChangedListener(GenericTextWatcher(otpEditBox5, edit))
            otpEditBox6.addTextChangedListener(GenericTextWatcher(otpEditBox6, edit))
        }
    }

    override fun observe() {
        with(viewModel) {
            otpList.observe(this@RegisterOtpVerifyActivity) { apiResponse ->
                if (apiResponse != null) {
                    if (apiResponse.code in 199..299) {
                        val model = gson.fromJson(apiResponse.apiResponse, OtpModel::class.java)
                        if (model.status.equals(Constants.STATUSSUCCESS)) {
//                            Toast.makeText(
//                                this@RegisterOtpVerifyActivity,
//                                model.message + " " + model.note,
//                                Toast.LENGTH_SHORT
//                            ).show()
                            otp = model.otp.toString()
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
            registerDataList.observe(this@RegisterOtpVerifyActivity) { register ->
                if (register != null) {
                    if (register.apiResponse != null) {
                        val mainModel = gson.fromJson(register.apiResponse, UserModel::class.java)
                        if (mainModel.status.equals(Constants.STATUSSUCCESS)) {
                            sessionManager.setUserDetails(SessionManager.userData, mainModel.data!!)
                            sessionManager.setBoolean(SessionManager.isLogin, true)
                            startActivity(
                                Intent(
                                    this@RegisterOtpVerifyActivity,
                                    CompletionActivity::class.java
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

            progressObservable.observe(this@RegisterOtpVerifyActivity) {
                if (it != null) {
                    if (it)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }

        }
    }


    override fun setMethod() {
        reSendOtpTimer()
        setOnClickListener()
    }

    private fun getCountryNameFromNetwork(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val networkCountryIso = telephonyManager.networkCountryIso
        val simCountryIso = telephonyManager.simCountryIso

        return when {
            !networkCountryIso.isNullOrEmpty() -> {
                Locale("", networkCountryIso).displayCountry
            }
            !simCountryIso.isNullOrEmpty() -> {
                Locale("", simCountryIso).displayCountry
            }
            else -> Locale.getDefault().displayCountry
        }
    }
    private val PERMISSIONS_REQUEST_CODE = 1
    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSIONS_REQUEST_CODE)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }


    private fun setOnClickListener() {
        with(binding!!) {
            btnSubmit.setOnClickListener {
                if (otp.isNotEmpty()) {
                    if (getTextFromOtp() == otp) {
                        val country = getCountryNameFromNetwork(this@RegisterOtpVerifyActivity)

                        FCM_TOKEN.let { fcm_it ->
                            viewModel.setDataInRegisterDataList(
                                email,
                                userName,
                                password,
                                fcm_it,country,name
                            )
                        }

                    } else
                        dialogClass.showError(resources.getString(R.string.str_otp_mismatch))

//                    Toast.makeText(
//                            this@RegisterOtpVerifyActivity,
//                            resources.getString(R.string.str_otp_mismatch),
//                            Toast.LENGTH_SHORT
//                        ).show()


                } else {
                    dialogClass.showServerErrorDialog()
                }

            }
            imgBack.setOnClickListener { finish() }

            txtResendOtp.setOnClickListener {
                if (txtResendOtp.text.toString() == resources.getString(R.string.str_re_send_code)) {
                    reSendOtpTimer()
                    viewModel.setDataInOtpList(email, Constants.SECURITY_1,name)
                } else {
//                    Toast.makeText(
//                        this@RegisterOtpVerifyActivity,
//                        resources.getString(R.string.str_please_wait),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }
        }
    }


    private fun reSendOtpTimer() {
        object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {

                binding!!.txtResendOtp.text =
                    resources.getString(R.string.str_seconds_remaining) + millisUntilFinished / 1000
            }

            override fun onFinish() {
                binding!!.txtResendOtp.text = getText(R.string.str_re_send_code)
            }
        }.start()
    }

    private fun getTextFromOtp(): String {
        var stringr = ""
        with(binding!!) {
            if (otpEditBox1.text.toString().isNotEmpty()) {
                if (otpEditBox2.text.toString().isNotEmpty()) {
                    if (otpEditBox3.text.toString().isNotEmpty()) {
                        if (otpEditBox4.text.toString().isNotEmpty()) {
                            if (otpEditBox5.text.toString().isNotEmpty()) {
                                if (otpEditBox6.text.toString().isNotEmpty()) {
                                    stringr =
                                        otpEditBox1.text.toString() + otpEditBox2.text.toString() + otpEditBox3.text.toString() +
                                                otpEditBox4.text.toString() + otpEditBox5.text.toString() + otpEditBox6.text.toString()

                                } else
                                    otpEditBox6.error =
                                        resources.getString(R.string.str_please_fill_up)
                            } else
                                otpEditBox5.error = resources.getString(R.string.str_please_fill_up)
                        } else
                            otpEditBox4.error = resources.getString(R.string.str_please_fill_up)
                    } else
                        otpEditBox3.error = resources.getString(R.string.str_please_fill_up)
                } else
                    otpEditBox2.error = resources.getString(R.string.str_please_fill_up)
            } else
                otpEditBox1.error = resources.getString(R.string.str_please_fill_up)
        }
        return stringr
    }

}