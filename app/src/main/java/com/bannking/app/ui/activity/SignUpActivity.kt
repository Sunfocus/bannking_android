package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.text.method.PasswordTransformationMethod
import com.bannking.app.BuildConfig
import com.bannking.app.R
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivitySignUpBinding
import com.bannking.app.model.retrofitResponseModel.otpModel.OtpModel
import com.bannking.app.model.viewModel.OtpViewModel
import com.bannking.app.utils.Constants


class SignUpActivity : BaseActivity<OtpViewModel, ActivitySignUpBinding>(OtpViewModel::class.java) {

    lateinit var viewModel: OtpViewModel
    private var isShowPassword = false
    private var isShowCnfPassword = false

    override fun getBinding(): ActivitySignUpBinding {
        return ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: OtpViewModel) {
        this.viewModel = viewModel
    }

    override fun initialize() {

    }


    override fun observe() {
        with(viewModel) {
            otpList.observe(this@SignUpActivity) { apiResponse ->
                if (apiResponse != null) {
                    if (apiResponse.code in 199..299) {
                        val model = gson.fromJson(apiResponse.apiResponse, OtpModel::class.java)
                        if (model.status.equals(Constants.STATUSSUCCESS)) {
//                            Toast.makeText(this@SignUpActivity, model.message + " " + model.note, Toast.LENGTH_SHORT)
//                                .show()
                            val intent =
                                Intent(this@SignUpActivity, RegisterOtpVerifyActivity::class.java)
                            intent.putExtra("Email", binding!!.edtEmail.text.toString())
                            intent.putExtra("UserName", binding!!.edtUsername.text.toString())
                            intent.putExtra("Password", binding!!.edtPassword.text.toString())
                            intent.putExtra("name", binding!!.etFirstName.text.toString())
                            intent.putExtra("otp", model.otp.toString())
                            startActivity(intent)
                        } else {
                            dialogClass.showError(model.message.toString())
//                            Toast.makeText(this@SignUpActivity, , Toast.LENGTH_SHORT).show()
                        }
                    } else if (apiResponse.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }
            progressObservable.observe(this@SignUpActivity) {
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
        setOnClickListener()

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setOnClickListener() {
        with(binding!!) {
            txtSingIn.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                finish()
            }
            btnSignUp.setOnClickListener {
                clickOnSignUp()
            }
           imgBack.setOnClickListener {
                finish()
            }

            val versionCode: Int = BuildConfig.VERSION_CODE
            val versionName: String = BuildConfig.VERSION_NAME
            tvVersion.text = "$versionName"
//            txtPrivacyPolicy.setOnClickListener {
//                startActivity(Intent(this@SignUpActivity, PrivacyPolicyActivity::class.java))
//            }
            tvTermCondition.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bannking.com/terms"))
                startActivity(browserIntent)
            }
            tvPolicy.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bannking.com/privacy"))
                startActivity(browserIntent)
            }
            imgPasswordToggle.setOnClickListener {
                if (isShowPassword) {
                    edtPassword.transformationMethod = PasswordTransformationMethod()
                    imgPasswordToggle.setImageDrawable(getDrawable(R.drawable.ic_eye_close))
                    isShowPassword = false
                } else {
                    edtPassword.transformationMethod = null
                    imgPasswordToggle.setImageDrawable(getDrawable(R.drawable.ic_eye))
                    isShowPassword = true
                }
                edtPassword.setSelection(edtPassword.text.length)
            }

            imgPasswordConfToggle.setOnClickListener {
                if (isShowCnfPassword) {
                    edtCnfPassword.transformationMethod = PasswordTransformationMethod()
                    imgPasswordConfToggle.setImageDrawable(getDrawable(R.drawable.ic_eye_close))
                    isShowCnfPassword = false
                } else {
                    edtCnfPassword.transformationMethod = null
                    imgPasswordConfToggle.setImageDrawable(getDrawable(R.drawable.ic_eye))
                    isShowCnfPassword = true
                }
                edtCnfPassword.setSelection(edtCnfPassword.text.length)
            }

        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun clickOnSignUp() {
        with(binding!!) {
            if (edtUsername.text.toString().isNotEmpty()) {
                if (edtEmail.text.toString().isNotEmpty()) {
                    if (utils.isValidEmailId(edtEmail.text.toString().trim())) {
                        if (edtPassword.text.toString().isNotEmpty()) {
                            if (edtCnfPassword.text.toString().isNotEmpty()) {
                                if (utils.isValidPassword(edtPassword.text.toString())) {
                                    if (edtPassword.text.toString() == edtCnfPassword.text.toString()) {
                                        viewModel.setDataInOtpList(
                                            edtEmail.text.toString(),
                                            Constants.SECURITY_1,etFirstName.text.toString()
                                        )
                                    } else
                                        dialogClass.showError(resources.getString(R.string.str_confirm_password_not_match_with_password))


//                                    Toast.makeText(
//                                            this@SignUpActivity,
//                                            resources.getString(R.string.str_confirm_password_not_match_with_password),
//                                            Toast.LENGTH_SHORT
//                                        ).show()
                                } else
                                    edtPassword.error =
                                        resources.getString(R.string.str_password_privacy)
                            } else
                                edtCnfPassword.error =
                                    resources.getString(R.string.str_please_enter_confirm_password)
                        } else
                            edtPassword.error =
                                resources.getString(R.string.str_please_enter_password)
                    } else
                        edtEmail.error =
                            resources.getString(R.string.str_please_enter_valid_email_id)
                } else
                    edtEmail.error = resources.getString(R.string.str_enter_email_address)
            } else
                edtUsername.error = resources.getString(R.string.enter_your_username)
        }
    }
}
