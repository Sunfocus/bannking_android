package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.CountDownTimer
import android.widget.EditText
import com.bannking.app.R
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityForgetProfilrOtpBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.retrofitResponseModel.otpModel.OtpModel
import com.bannking.app.model.viewModel.OtpViewModel
import com.bannking.app.utils.Constants
import com.bannking.app.utils.GenericTextWatcher


class ForgetProfileVerifyOtpActivity :
    BaseActivity<OtpViewModel, ActivityForgetProfilrOtpBinding>(OtpViewModel::class.java) {

    private lateinit var edit: Array<EditText>
    private lateinit var viewModel: OtpViewModel
    var email = ""
    private var otp = ""


    override fun getBinding(): ActivityForgetProfilrOtpBinding {
        return ActivityForgetProfilrOtpBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: OtpViewModel) {
        this.viewModel = viewModel

    }

    override fun initialize() {
        if (intent != null) {
            email = intent.getStringExtra("email").toString()
            otp = intent.getStringExtra("otp").toString()
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
            otpList.observe(this@ForgetProfileVerifyOtpActivity) { apiResponse ->
                if (apiResponse != null) {
                    if (apiResponse.code in 199..299) {
                        val model = gson.fromJson(apiResponse.apiResponse, OtpModel::class.java)
                        if (model.status == 200 ) {
                            dialogClass.showSuccessfullyDialog(model.message.toString())
//                            Toast.makeText(this@ForgetProfileVerifyOtpActivity, model.message + " " + model.note, Toast.LENGTH_SHORT)
//                                .show()
                            otp = model.data!!.otp.toString()
                        } else {
                            dialogClass.showError(model.message.toString())
//                            Toast.makeText(this@ForgetProfileVerifyOtpActivity, "" + model.message, Toast.LENGTH_SHORT).show()
                        }
                    } else if (apiResponse.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }

            accountForgetData.observe(
                this@ForgetProfileVerifyOtpActivity
            ) {
                if (it != null) {
                    if (it.code in 199..299) {
                        val model = gson.fromJson(it.apiResponse, CommonResponseApi::class.java)
                        if (model.status == 200) {
//                            Toast.makeText(
//                                this@ForgetProfileVerifyOtpActivity,
//                                model.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
 /*                           startActivity(
                                Intent(
                                    this@ForgetProfileVerifyOtpActivity,
                                    SignInActivity::class.java
                                )
                            )
                            finishAffinity()*/
                        } else
                            dialogClass.showError(model.message.toString())
//                            Toast.makeText(this@ForgetProfileVerifyOtpActivity, model.message, Toast.LENGTH_SHORT).show()
                    } else if (it.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }

            progressObservable.observe(this@ForgetProfileVerifyOtpActivity) {
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


    private fun setOnClickListener() {
        with(binding!!) {
            btnSubmit.setOnClickListener {
                if (otp.isNotEmpty()) {
                    if (getTextFromOtp() == otp) {
                        startActivity(Intent(this@ForgetProfileVerifyOtpActivity,ResetPasswordActivity::class.java).putExtra("Userid",intent.getStringExtra("user_id").toString()))
//                        viewModel.setDataInFindAccountList(email)
                    } else {
                        dialogClass.showError(resources.getString(R.string.str_otp_mismatch))
                    }
                } else {
                    dialogClass.showServerErrorDialog()
                }
            }
            imgBack.setOnClickListener { finish() }

            txtResendOtp.setOnClickListener {
                if (txtResendOtp.text.toString() == resources.getString(R.string.str_re_send_code)) {
                    reSendOtpTimer()
                    viewModel.setDataInOtpListForget(email, Constants.SECURITY_2, "")
                } else {
//                    Toast.makeText(this@ForgetProfileVerifyOtpActivity, resources.getString(R.string.str_please_wait),
//                        Toast.LENGTH_SHORT).show()
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