package com.bannking.app.ui.activity

import android.content.Intent
import com.bannking.app.R
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityFindAccountBinding
import com.bannking.app.model.retrofitResponseModel.otpModel.OtpModel
import com.bannking.app.model.viewModel.OtpViewModel
import com.bannking.app.utils.Constants

class FindAccountActivity :
    BaseActivity<OtpViewModel, ActivityFindAccountBinding>(OtpViewModel::class.java) {

    lateinit var viewModel: OtpViewModel


    override fun getBinding(): ActivityFindAccountBinding {
        return ActivityFindAccountBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: OtpViewModel) {
        this.viewModel = viewModel
    }

    override fun setMethod() {
        setOnClickListener()
    }

    override fun initialize() {
//        Nothing
    }

    override fun observe() {
        with(viewModel) {
            otpList.observe(this@FindAccountActivity) { apiResponse ->
                if (apiResponse != null) {
                    if (apiResponse.code in 199..299) {
                        val model = gson.fromJson(apiResponse.apiResponse, OtpModel::class.java)
                        if (model.status.equals(Constants.STATUSSUCCESS)) {

//                            Toast.makeText(
//                                this@FindAccountActivity,
//                                model.message + " " + model.note,
//                                Toast.LENGTH_SHORT
//                            ).show()
                            val intent = Intent(
                                this@FindAccountActivity,
                                ForgetProfileVerifyOtpActivity::class.java
                            )
                            intent.putExtra("email", model.email.toString())
                            intent.putExtra("otp", model.otp.toString())
                            intent.putExtra("user_id", model.user_id.toString())
                            startActivity(intent)
                        } else {
                            dialogClass.showError(model.message.toString())
                        }
                    } else if (apiResponse.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }

            progressObservable.observe(this@FindAccountActivity) {
                if (it != null) {
                    if (it)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }

        }


    }

    private fun setOnClickListener() {
        with(binding!!) {
            btnNext.setOnClickListener {
                if (!edtFindAccountEmail.text.isNullOrEmpty()) {
                    if (utils.isValidEmailId(edtFindAccountEmail.text.toString().trim())) {
                        viewModel.setDataInOtpList(
                            edtFindAccountEmail.text.toString(),
                            Constants.SECURITY_2,""
                        )
                    } else {
                        edtFindAccountEmail.error =
                            resources.getString(R.string.str_please_enter_valid_email_id)
                    }
                } else
                    edtFindAccountEmail.error =
                        resources.getString(R.string.str_enter_email_address)
            }
            btnSignUp.setOnClickListener {
                val intent = Intent(
                    this@FindAccountActivity,
                    SignUpActivity::class.java
                )
                startActivity(intent)
                finish()
            }
            imgBack.setOnClickListener { finish() }
        }

    }


}