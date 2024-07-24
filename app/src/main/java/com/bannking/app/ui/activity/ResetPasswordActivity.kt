package com.bannking.app.ui.activity

import android.content.Intent
import com.bannking.app.R
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityResetPasswordBinding
import com.bannking.app.model.retrofitResponseModel.otpModel.OtpModel
import com.bannking.app.model.viewModel.OtpViewModel
import com.bannking.app.utils.Constants

class ResetPasswordActivity :
    BaseActivity<OtpViewModel, ActivityResetPasswordBinding>(OtpViewModel::class.java) {

    lateinit var viewModel: OtpViewModel
    override fun getBinding(): ActivityResetPasswordBinding {
        return ActivityResetPasswordBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: OtpViewModel) {
        this.viewModel = viewModel
    }

    override fun initialize() {

    }

    override fun setMethod() {

        with(binding!!) {
            btnChangePassword.setOnClickListener {
                changePasswordClick()

            }
        }


    }

    override fun observe() {
        with(viewModel) {
            forgotPassword.observe(this@ResetPasswordActivity) { apiResponse ->
                if (apiResponse != null) {
                    if (apiResponse.code in 199..299) {
                        val model = gson.fromJson(apiResponse.apiResponse, OtpModel::class.java)
                        if (model.status.equals(Constants.STATUSSUCCESS)) {
                            dialogClass.showSuccessfullyDialog(model.message.toString()) {
                                startActivity(Intent(this@ResetPasswordActivity, SignInActivity::class.java))
                                finishAffinity()
                            }
                        } else {
                            dialogClass.showError(model.message.toString())
                        }
                    } else if (apiResponse.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }
        }
    }

    private fun changePasswordClick() {
        with(binding!!) {
            if (utils.isValidPassword(edtNewPassword.text.toString())) {
                if (utils.isValidPassword(edtNewConfPassword.text.toString())) {
                    if (edtNewPassword.text.toString() == edtNewConfPassword.text.toString()) {
                        viewModel.forgotPassword(
                            edtNewPassword.text.toString(), intent.getStringExtra("Userid")
                                .toString()
                        )
                    } else


                        edtNewPassword.error =
                            resources.getString(R.string.str_old_and_new_password_is_same)
                } else
                    edtNewConfPassword.error = resources.getString(R.string.str_password_privacy)
            } else
                edtNewPassword.error = resources.getString(R.string.str_password_privacy)

        }
    }


}