package com.bannking.app.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bannking.app.BuildConfig
import com.bannking.app.R
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.UiExtension.drawable
import com.bannking.app.UiExtension.getCurrentLanguage
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.databinding.ActivitySigninBinding
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.model.viewModel.SignInViewModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SessionManager
import com.google.gson.JsonObject
import com.zeugmasolutions.localehelper.Locales
import org.json.JSONException
import retrofit2.Call
import retrofit2.Response
import java.util.Locale
import java.util.concurrent.Executor

class SignInActivity :
    BaseActivity<SignInViewModel, ActivitySigninBinding>(SignInViewModel::class.java) {
    lateinit var viewModel: SignInViewModel
    private var isShowPassword = false
    private lateinit var savedSessionManager: SessionManager

    override fun getBinding(): ActivitySigninBinding {
        return ActivitySigninBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: SignInViewModel) {
        this.viewModel = viewModel
    }


    override fun setMethod() {
        setClickListener()
    }

    private fun faceDetection(){
        val packageManager = packageManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API level 30
            val hasFaceFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
            if (hasFaceFeature) {
                // The device supports face authentication
                setupBiometricPrompt()
            } else {
                Toast.makeText(applicationContext, "Face authentication not supported on this device.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "Face authentication requires Android 11 or higher.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // The device supports biometric authentication (strong or weak, e.g., fingerprint, face, or iris)
                // Proceed with biometric authentication setup
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(this, executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    })

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login for my app")
                    .setSubtitle("Log in using your biometric credential")
                    .setNegativeButtonText("Use account password")
                    .build()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(applicationContext, "No biometric features available on this device.", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(applicationContext, "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(applicationContext, "The user hasn't associated any biometric credentials with their account.", Toast.LENGTH_SHORT).show()
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                TODO()
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                TODO()
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                TODO()
            }
        }
    }
    override fun initialize() {
//        faceDetection()

        savedSessionManager =
            SessionManager(this@SignInActivity, SessionManager.savedSharedPreferences)

        if (savedSessionManager.getBoolean(SessionManager.isRemember)) {
            binding!!.edtUsername.setText(
                savedSessionManager.getString(SessionManager.UserId).toString()
            )
            binding!!.edtPassword.setText(
                savedSessionManager.getString(SessionManager.Password).toString()
            )
        }
        if (sessionManager.getBoolean(SessionManager.isLogin)) {
            sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding!!.imgBack.setOnClickListener {
            finish()
        }
    }

    override fun observe() {
//        with(viewModel) {
//            loginData.observe(this@SignInActivity) { loginReponse ->
//                if (loginReponse != null) {
//                    val mainModel = gson.fromJson(loginReponse.apiResponse, UserModel::class.java)
//                    if (loginReponse.code in 199..299) {
//                        if (mainModel.status.equals(Constants.STATUSSUCCESS, true)) {
//                            sessionManager.setUserDetails(SessionManager.userData, mainModel.data!!)
//                            sessionManager.setBoolean(SessionManager.isLogin, true)
//                            if (mainModel.data!!.premium == true) {
//                                inAppPurchaseSM.setBoolean(SessionManager.isPremium, true)
//                            } else if (mainModel.data!!.premium == false) {
//                                inAppPurchaseSM.setBoolean(SessionManager.isPremium, false)
//                            }
//
//                            Handler().postDelayed({
//                                val intent = Intent(this@SignInActivity, MainActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            }, 1000)
//                        } else {
//                            dialogClass.showError(mainModel.message.toString())
//                        }
//                    } else if (loginReponse.code in 400..500)
//                        dialogClass.showServerErrorDialog()
//                }
//            }
//
//
//            progressObservable.observe(this@SignInActivity) {
//                if (it != null) {
//                    if (it)
//                        dialogClass.showLoadingDialog()
//                    else
//                        dialogClass.hideLoadingDialog()
//                }
//            }
//        }
    }
   private fun setClickListener() {

        binding!!.remember.isChecked = savedSessionManager.getBoolean(SessionManager.isRemember)

        binding!!.btnSignIn.setOnClickListener {
            if (!binding!!.edtUsername.text.isNullOrEmpty()) {
                if (!binding!!.edtPassword.text.isNullOrEmpty()) {
                    loginUser(
                        binding!!.edtUsername.text.toString(),
                        binding!!.edtPassword.text.toString()
                    )
                    if (binding!!.remember.isChecked) {
                        savedSessionManager.setString(
                            SessionManager.UserId,
                            binding!!.edtUsername.text.toString()
                        )
                        savedSessionManager.setString(
                            SessionManager.Password,
                            binding!!.edtPassword.text.toString()
                        )
                        savedSessionManager.setBoolean(SessionManager.isRemember, true)
                    } else {
                        savedSessionManager.setString(SessionManager.UserId, "")
                        savedSessionManager.setString(SessionManager.Password, "")
                        savedSessionManager.setBoolean(SessionManager.isRemember, false)
                    }
                } else
                    binding!!.edtPassword.error =
                        resources.getString(R.string.str_please_enter_password)
            } else
                binding!!.edtUsername.error = resources.getString(R.string.enter_your_username)

        }
       val versionName: String = BuildConfig.VERSION_NAME
       binding!!.tvVersion.text = "$versionName"


        binding!!.imgPasswordToggle.setOnClickListener {
            if (isShowPassword) {
                binding!!.edtPassword.transformationMethod = PasswordTransformationMethod()
                binding!!.imgPasswordToggle.setImageDrawable(drawable(R.drawable.ic_eye_close))
                isShowPassword = false
            } else {
                binding!!.edtPassword.transformationMethod = null
                binding!!.imgPasswordToggle.setImageDrawable(drawable(R.drawable.ic_eye))
                isShowPassword = true
            }
            binding!!.edtPassword.setSelection(binding!!.edtPassword.text.length)
        }
        binding!!.txtOpenAccount.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        }
        binding!!.txtForgetUsernamePassword.setOnClickListener {
            startActivity(
                Intent(
                    this@SignInActivity,
                    FindAccountActivity::class.java
                )
            )
        }


    }

    fun loginUser(strUserName: String, strPassword: String) {
        FCM_TOKEN.let {
            dialogClass.showLoadingDialog()
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_1)
                apiBody.addProperty("username", strUserName)
                apiBody.addProperty("password", strPassword)
                apiBody.addProperty("token", it)
            } catch (e: JSONException) {
                e.printStackTrace()

            }
            val call = RetrofitClient.instance!!.myApi.userLogin(apiBody.toString())
            call.enqueue(object : retrofit2.Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    dialogClass.hideLoadingDialog()
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            val commonResponseModel =
                                CommonResponseModel(response.body(), response.code())
                            if (commonResponseModel != null) {
                                val mainModel = gson.fromJson(
                                    commonResponseModel.apiResponse,
                                    UserModel::class.java
                                )
                                if (commonResponseModel.code in 199..299) {
                                    if (mainModel.status.equals(Constants.STATUSSUCCESS, true)) {
                                        sessionManager.setUserDetails(
                                            SessionManager.userData,
                                            mainModel.data!!
                                        )
                                        sessionManager.setBoolean(SessionManager.isLogin, true)
                                        if (mainModel.data!!.premium == true) {
                                            inAppPurchaseSM.setBoolean(
                                                SessionManager.isPremium,
                                                true
                                            )
                                        } else if (mainModel.data!!.premium == false) {
                                            inAppPurchaseSM.setBoolean(
                                                SessionManager.isPremium,
                                                false
                                            )
                                        }
                                        when (mainModel.data!!.languageName) {
                                            "English" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.English)
                                                    updateLocale(Locales.English)
                                                else {
                                                    sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }

                                            }
                                            "Spanish" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Spanish)
                                                    updateLocale(Locales.Spanish)
                                                else {
                                                    sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }

                                            }
                                            "French" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.French)
                                                    updateLocale(Locales.French)
                                                else {
                                                    sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }
                                            "Arabic" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Arabic)
                                                    updateLocale(Locales.Arabic)
                                                else {
                                                    sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }
                                            "Russia" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Russian)
                                                    updateLocale(Locales.Russian)
                                                else {
                                                    sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }
                                            "Portuguese" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Portuguese)
                                                    updateLocale(Locales.Portuguese)
                                                else {
                                                    sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }
                                            "Dutch" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Dutch)
                                                    updateLocale(Locales.Dutch)
                                                else {
                                                    sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }
                                            else -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.English)
                                                    updateLocale(Locales.English)
                                                else {
                                                    sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                        }

//                                        Handler().postDelayed({
//                                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
//                                            startActivity(intent)
//                                            finish()
//                                        }, 1000)
                                    } else {
                                        dialogClass.showError(mainModel.message.toString())
                                    }
                                }
                            }

                        } else if (response.code() in 400..500) {
                            dialogClass.showServerErrorDialog()
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    dialogClass.hideLoadingDialog()
                    dialogClass.showServerErrorDialog()
                }
            })
        }
    }

    /*override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
            finishAffinity();
        } else if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        }
    }*/


}