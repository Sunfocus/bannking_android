package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.text.method.PasswordTransformationMethod
import com.bannking.app.R
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityChangePasswordBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.viewModel.ChangePasswordViewModel
import com.bannking.app.utils.Constants

class ChangePasswordActivity :
    BaseActivity<ChangePasswordViewModel, ActivityChangePasswordBinding>(ChangePasswordViewModel::class.java) {

    lateinit var viewModel: ChangePasswordViewModel
    private var isShowPassword = false

    override fun getBinding(): ActivityChangePasswordBinding {
        return ActivityChangePasswordBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: ChangePasswordViewModel) {
        this.viewModel = viewModel
    }

    override fun observe() {
        with(viewModel) {
            changePasswordData.observe(this@ChangePasswordActivity) {
                if (it != null) {
                    if (it.code in 199..299) {
                        val model = gson.fromJson(it.apiResponse, CommonResponseApi::class.java)
                        if (model.status.equals(Constants.STATUSSUCCESS)) {
                            dialogClass.showSuccessfullyDialog(model.message.toString())
                        } else {
                            dialogClass.showError(model.message.toString())
                        }
                    } else if (it.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }

            progressObservable.observe(this@ChangePasswordActivity) {
                if (it != null) {
                    if (it)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }

        }
    }


    override fun initialize() {

    }

    override fun setMethod() {
        setOnClickListener()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setOnClickListener() {
        with(binding!!) {

            imgBack.setOnClickListener {
                finish()
            }

            imgPasswordToggle.setOnClickListener {
                if (isShowPassword) {
                    edtNewPassword.transformationMethod = PasswordTransformationMethod()
                    imgPasswordToggle.setImageDrawable(getDrawable(R.drawable.ic_eye))
                    isShowPassword = false
                } else {
                    edtNewPassword.transformationMethod = null
                    imgPasswordToggle.setImageDrawable(getDrawable(R.drawable.ic_eye_close))
                    isShowPassword = true
                }
                edtNewPassword.setSelection(binding!!.edtNewPassword.text.length)
            }

            btnChangePassword.setOnClickListener {
                changePasswordClick()
            }
        }
    }

    private fun changePasswordClick() {
        with(binding!!) {
            if (utils.isValidPassword(edtNewPassword.text.toString())) {
                if (edtNewPassword.text.toString() != edtOldPassword.text.toString()) {
                    viewModel.setDataInChangePasswordList(
                        edtOldPassword.text.toString(),
                        edtNewPassword.text.toString()
                    )
                } else

                    edtNewPassword.error =
                        resources.getString(R.string.str_old_and_new_password_is_same)
            } else
                edtNewPassword.error = resources.getString(R.string.str_password_privacy)

        }

    }


}