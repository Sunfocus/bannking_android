package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.text.method.PasswordTransformationMethod
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityChangePasswordBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.viewModel.ChangePasswordViewModel
import com.bannking.app.utils.SessionManager

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
    private fun uiColor(){
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.tvChangePass.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding!!.edtOldPassword.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.edtNewPassword.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.edtConfirmPassword.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.edtOldPassword.setHintTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.edtNewPassword.setHintTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.edtConfirmPassword.setHintTextColor(ContextCompat.getColor(this, R.color.white))
        } else {

            binding!!.tvChangePass.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.black))
            binding!!.edtOldPassword.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.edtNewPassword.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.edtConfirmPassword.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.edtOldPassword.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.edtNewPassword.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.edtConfirmPassword.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
        }
    }
    override fun observe() {
        with(viewModel) {
            changePasswordData.observe(this@ChangePasswordActivity) {
                if (it != null) {
                    if (it.code in 199..299) {
                        val model = gson.fromJson(it.apiResponse, CommonResponseApi::class.java)
                        if (model.status == 200) {
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
        uiColor()
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
            imgConfirmPasswordToggle.setOnClickListener {
                if (isShowPassword) {
                    edtConfirmPassword.transformationMethod = PasswordTransformationMethod()
                    imgPasswordToggle.setImageDrawable(getDrawable(R.drawable.ic_eye))
                    isShowPassword = false
                } else {
                    edtConfirmPassword.transformationMethod = null
                    imgPasswordToggle.setImageDrawable(getDrawable(R.drawable.ic_eye_close))
                    isShowPassword = true
                }
                edtConfirmPassword.setSelection(binding!!.edtConfirmPassword.text.length)
            }

            btnChangePassword.setOnClickListener {
                changePasswordClick()
            }
        }
    }

    private fun changePasswordClick() {
        with(binding!!) {
            if (utils.isValidPassword(edtNewPassword.text.toString())) {
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                if (edtNewPassword.text.toString() != edtOldPassword.text.toString()) {
                    if (edtNewPassword.text.toString() == edtConfirmPassword.text.toString()){
                        viewModel.setDataInChangePasswordList(
                            edtOldPassword.text.toString(),
                            edtNewPassword.text.toString(),userToken
                        )
                    }else edtConfirmPassword.error = "New password and Confirm password is not same."

                } else

                    edtNewPassword.error =
                        resources.getString(R.string.str_old_and_new_password_is_same)
            } else
                edtNewPassword.error = resources.getString(R.string.str_password_privacy)

        }

    }


}