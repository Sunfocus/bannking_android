package com.bannking.app.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.bannking.app.BuildConfig
import com.bannking.app.R
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.UiExtension.drawable
import com.bannking.app.UiExtension.getCurrentLanguage
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.databinding.ActivitySigninBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.ErrorResponse
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.model.viewModel.SignInViewModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SessionManager
import com.bannking.app.utils.SharedPref
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.zeugmasolutions.localehelper.Locales
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity :
    BaseActivity<SignInViewModel, ActivitySigninBinding>(SignInViewModel::class.java) {
    lateinit var viewModel: SignInViewModel
    private var isShowPassword = false
    private lateinit var pref:SharedPref
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

    private fun faceDetection() {
        setupBiometricPrompt()/*val packageManager = packageManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API level 30
            val hasFaceFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_FACE)
            if (hasFaceFeature) {
                // The device supports face authentication
            } else {
                Toast.makeText(applicationContext, "Face authentication not supported on this device.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "Face authentication requires Android 11 or higher.", Toast.LENGTH_SHORT).show()
        }*/
    }

    private fun setupBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // The device supports biometric authentication (strong or weak, e.g., fingerprint, face, or iris)
                // Proceed with biometric authentication setup
                val executor = ContextCompat.getMainExecutor(this)
                val biometricPrompt = BiometricPrompt(
                    this,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {

                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            val userId =
                                savedSessionManager.getString(SessionManager.UserId).toString()
                            val userPassword =
                                savedSessionManager.getString(SessionManager.Password).toString()
                            loginUser(
                                userId, userPassword
                            )
//                            Toast.makeText(
//                                applicationContext,
//                                "Authentication succeeded!",
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }

                    })

                val promptInfo =
                    BiometricPrompt.PromptInfo.Builder().setTitle("Biometric login for my app")
                        .setSubtitle("Log in using your biometric credential")
                        .setNegativeButtonText("Use account password").build()
                biometricPrompt.authenticate(promptInfo)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
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
        savedSessionManager =
            SessionManager(this@SignInActivity, SessionManager.savedSharedPreferences)

        val userId = savedSessionManager.getString(SessionManager.UserId).toString()
        val userPassword = savedSessionManager.getString(SessionManager.Password).toString()
        val logout = sessionManager.getBoolean(SessionManager.isLogin)
        val faceLogin =sessionManager.getBoolean("FaceLogin")
        if (userId.isNotEmpty() && userPassword.isNotEmpty() && !logout && faceLogin) {
            sessionManager.setBoolean("FaceLogin", true)
            faceDetection()
        }

        if (savedSessionManager.getBoolean(SessionManager.isRemember)) {
            binding!!.edtUsername.setText(
                savedSessionManager.getString(SessionManager.UserId).toString()
            )
            binding!!.edtPassword.setText(
                savedSessionManager.getString(SessionManager.Password).toString()
            )
        }else if (faceLogin){
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
                        binding!!.edtUsername.text.toString().trim(), binding!!.edtPassword.text.toString()
                    )
                    if (binding!!.remember.isChecked) {
                        savedSessionManager.setString(
                            SessionManager.UserId, binding!!.edtUsername.text.toString().trim()
                        )
                        savedSessionManager.setString(
                            SessionManager.Password, binding!!.edtPassword.text.toString()
                        )
                        savedSessionManager.setBoolean(SessionManager.isRemember, true)
                    } else {
                        savedSessionManager.setString(SessionManager.UserId, "")
                        savedSessionManager.setString(SessionManager.Password, "")
                        savedSessionManager.setBoolean(SessionManager.isRemember, false)
                    }
                } else binding!!.edtPassword.error =
                    resources.getString(R.string.str_please_enter_password)
            } else binding!!.edtUsername.error = resources.getString(R.string.enter_your_username)

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
                    this@SignInActivity, FindAccountActivity::class.java
                )
            )
        }


    }

    fun loginUser(strUserName: String, strPassword: String) {
        sessionManager.setString(SessionManager.USERTOKEN,"")
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
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    dialogClass.hideLoadingDialog()
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            val commonResponseModel =
                                CommonResponseModel(response.body(), response.code())
                            if (commonResponseModel != null) {
                                val mainModel = gson.fromJson(
                                    commonResponseModel.apiResponse, UserModel::class.java
                                )
                                if (commonResponseModel.code in 199..299) {
                                    if (mainModel.status == 200) {
                                        pref = SharedPref(this@SignInActivity)
                                        pref.saveBoolean("HideTransactions", true)

                                        sessionManager.setUserDetails(
                                            SessionManager.userData, mainModel.data!!
                                        )
                                        savedSessionManager.setString(
                                            SessionManager.UserId, mainModel.data!!.email
                                        )
                                        savedSessionManager.setString(
                                            SessionManager.Password,
                                            binding!!.edtPassword.text.toString()
                                        )
                                        sessionManager.setBoolean(SessionManager.isLogin, true)
                                        sessionManager.setString(
                                            SessionManager.USERTOKEN,
                                            "bearer ${mainModel.extraData}"
                                        )

                                        if (mainModel.data!!.subscriptionStatus == 1) {
                                            inAppPurchaseSM.setBoolean(
                                                SessionManager.isPremium, true
                                            )
                                        } else {
                                            inAppPurchaseSM.setBoolean(
                                                SessionManager.isPremium, false
                                            )
                                        }

                                        if (mainModel.data?.notification_status != true){
                                            FirebaseMessaging.getInstance()
                                                .unsubscribeFromTopic("user_" + mainModel.data!!.id)
                                                .addOnCompleteListener { task ->
                                                }

                                            FirebaseMessaging.getInstance()
                                                .unsubscribeFromTopic("topic_bnk_usrs_broadcast")
                                                .addOnCompleteListener { task ->

                                                }
                                        }else{
                                            FirebaseMessaging.getInstance()
                                                .subscribeToTopic("user_" + mainModel.data!!.id)
                                                .addOnCompleteListener { task ->
                                                    Log.d(
                                                        "token====",
                                                        "user_${mainModel.data!!.id.toString()}"
                                                    )
                                                }

                                            FirebaseMessaging.getInstance()
                                                .subscribeToTopic("topic_bnk_usrs_broadcast")
                                                .addOnCompleteListener { task ->

                                                }
                                        }

                                        when (mainModel.data!!.language!!.name) {
                                            "English" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.English) updateLocale(
                                                    Locales.English
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }

                                            }

                                            "Spanish" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Spanish) updateLocale(
                                                    Locales.Spanish
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }

                                            }

                                            "French" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.French) updateLocale(
                                                    Locales.French
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            "Arabic" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Arabic) updateLocale(
                                                    Locales.Arabic
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            "Russia" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Russian) updateLocale(
                                                    Locales.Russian
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            "Portuguese" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Portuguese) updateLocale(
                                                    Locales.Portuguese
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            "Dutch" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Dutch) updateLocale(
                                                    Locales.Dutch
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            "Hindi" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Hindi) updateLocale(
                                                    Locales.Hindi
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            "Japanese" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Japanese) updateLocale(
                                                    Locales.Japanese
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            "German" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.German) updateLocale(
                                                    Locales.German
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            "Italian" -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.Italian) updateLocale(
                                                    Locales.Italian
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                            else -> {
                                                if (this@SignInActivity.getCurrentLanguage() != Locales.English) updateLocale(
                                                    Locales.English
                                                )
                                                else {
                                                    sessionManager.setBoolean(
                                                        SessionManager.isDeleteORLogOut, false
                                                    )
                                                    val intent = Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            }

                                        }

                                    } else {
                                        dialogClass.showError(mainModel.message.toString())
                                    }
                                }
                            }

                        } else if (response.code() in 400..500) {
                            dialogClass.showServerErrorDialog()
                        }
                    } else {
                        val errorBodyString = response.errorBody()?.string()
                        val mainModel = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                        dialogClass.hideLoadingDialog()
                        if (mainModel.message.contains("Your email address has not been verified yet. Please verify your email to continue.")) {
                            showVerificationDialog(mainModel.message)
                        } else {
                            dialogClass.showErrorMessageDialog(mainModel.message)
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

    private fun showVerificationDialog(strMessage: String, callbacks: (() -> Unit)? = null) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.verify_mail)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnResend: TextView = dialog.findViewById(R.id.btnResend)
        val tvMessage: TextView = dialog.findViewById(R.id.tvMessage)
        val btnCancel: TextView = dialog.findViewById(R.id.btnCancel)
        val emailResend: AppCompatEditText = dialog.findViewById(R.id.emailResend)
        tvMessage.text = strMessage
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnResend.setOnClickListener {
            if (emailResend.text.toString().isNotEmpty()) {
                resendMail(emailResend.text.toString())
                dialog.dismiss()
            } else {
                emailResend.error = resources.getString(R.string.str_enter_email_address)
            }

        }
        dialog.show()
    }

    private fun resendMail(strEmail: String) {
        dialogClass.showLoadingDialog()
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("email", strEmail)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.postEmailResend(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                dialogClass.hideLoadingDialog()
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        val commonResponseModel =
                            CommonResponseModel(response.body(), response.code())

                        if (commonResponseModel != null) {
                            val mainModel = gson.fromJson(
                                commonResponseModel.apiResponse, CommonResponseApi::class.java
                            )
                            if (commonResponseModel.code in 199..299) {
                                dialogClass.showSuccessfullyDialog(mainModel.message!!)
                            }


                        }
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        dialogClass.showServerErrorDialog()
                    }
                } else dialogClass.showServerErrorDialog()
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialogClass.hideLoadingDialog()
                dialogClass.showServerErrorDialog()
            }
        })
    }

    /*override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
            finishAffinity();
        } else if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        }
    }*/


}