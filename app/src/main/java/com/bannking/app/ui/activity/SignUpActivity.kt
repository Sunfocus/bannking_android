package com.bannking.app.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.telephony.TelephonyManager
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bannking.app.BuildConfig
import com.bannking.app.R
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.databinding.ActivitySignUpBinding
import com.bannking.app.model.ErrorResponse
import com.bannking.app.model.retrofitResponseModel.otpModel.OtpModel
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.model.viewModel.OtpViewModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SessionManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale


class SignUpActivity : BaseActivity<OtpViewModel, ActivitySignUpBinding>(OtpViewModel::class.java), GoogleApiClient.OnConnectionFailedListener {

    lateinit var viewModel: OtpViewModel
    private var isShowPassword = false
    private var isShowCnfPassword = false
    private var mGoogleApiClient: GoogleApiClient? = null
    private companion object {
        private const val RC_SIGN_IN = 1
    }
    override fun getBinding(): ActivitySignUpBinding {
        return ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: OtpViewModel) {
        this.viewModel = viewModel
    }

    override fun initialize() {
        checkAndRequestPermissions()
        if (mGoogleApiClient == null) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        }
        mGoogleApiClient!!.connect()
    }


    override fun observe() {
        with(viewModel) {

            registerDataList.observe(this@SignUpActivity) { register ->
                if (register != null) {
                    if (register.apiResponse != null) {
                        val mainModel = gson.fromJson(register.apiResponse, UserModel::class.java)
                        if (mainModel.status == 200) {
                            Log.d("sdfbvsdfshdfsd",mainModel.data!!.toString())
/*
                            FirebaseMessaging.getInstance()
                                .subscribeToTopic("user_" + mainModel.data!!.id)
                                .addOnCompleteListener { task ->
                                    Log.d("token====", mainModel.data!!.id.toString())
                                }

                            sessionManager.setUserDetails(SessionManager.userData, mainModel.data!!)
                            sessionManager.setBoolean(SessionManager.isLogin, true)
                            sessionManager.setString(SessionManager.USERTOKEN,"bearer ${mainModel.extraData}")*/

                            showVerificationDialog(mainModel.message!!)
                        } else {
                            mainModel.message?.let { dialogClass.showError(it) }

                        }
                    }
                }
            }
            errorResponse.observe(this@SignUpActivity){message->
                if (message!=null){
                    dialogClass.showErrorMessageDialog(message)
                }
            }
            otpList.observe(this@SignUpActivity) { apiResponse ->
                if (apiResponse != null) {
                    if (apiResponse.code in 199..299) {
                        val model = gson.fromJson(apiResponse.apiResponse, OtpModel::class.java)
                        if (model.status == 200) {
//                            Toast.makeText(this@SignUpActivity, model.message + " " + model.note, Toast.LENGTH_SHORT)
//                                .show()
                            val intent =
                                Intent(this@SignUpActivity, VerifyMailActivity::class.java)
                            intent.putExtra("Email", binding!!.edtEmail.text.toString())
                            intent.putExtra("UserName", binding!!.edtUsername.text.toString())
                            intent.putExtra("Password", binding!!.edtPassword.text.toString())
                            intent.putExtra("name", binding!!.etFirstName.text.toString())
                            intent.putExtra("otp", model.data!!.otp.toString())
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
            startActivity(
                Intent(
                    this@SignUpActivity,
                    SignInActivity::class.java
                )
            )
            finishAffinity()
            dialog.dismiss()
        }


        btnResend.setOnClickListener {
            if (emailResend.text.toString().isNotEmpty()) {
                viewModel.resendMail(emailResend.text.toString())
                startActivity(
                    Intent(
                        this@SignUpActivity,
                        SignInActivity::class.java
                    )
                )
                finishAffinity()
                dialog.dismiss()
            }else{
                emailResend.error = resources.getString(R.string.str_enter_email_address)
            }

        }
        dialog.show()
    }

    override fun setMethod() {
        setOnClickListener()

    }

    private fun signOut() {
        if (mGoogleApiClient != null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient!!).setResultCallback {
                if (it.isSuccess)
                    signIn()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            val statusCode = result!!.status.statusCode
            handleSignInResult(result)
        }
    }
    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val acct = result.signInAccount
            //   Log.e("GoogleSuccess", "display name: " + acct!!.displayName)
            val personName = acct!!.displayName
            val givenName = acct.givenName
            val email = acct.email
            val google_id = acct.id
            Log.d("google======", google_id!!)
            hitApiGoogle(google_id,email,personName,givenName)
        } else {
            // Signed out, show unauthenticated UI.
            Log.e("GoogleFail", "handleSignInResult:$result")
        }
    }
    private fun getCountryNameFromNetwork(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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
    private fun hitApiGoogle(
        google_id: String,
        email: String?,
        personName: String?,
        givenName: String?
    ) {
        val country = getCountryNameFromNetwork(this@SignUpActivity)
      loginWithSocialMedia(google_id,email,personName,givenName,country)

    }

    private fun loginWithSocialMedia(
        google_id: String,
        email: String?,
        personName: String?,
        givenName: String?,
        country: String
    ) {
        dialogClass.showLoadingDialog()
        val apiBody = JsonObject()
        try {
            apiBody.addProperty("google_id", google_id)
            apiBody.addProperty("email", email)
            apiBody.addProperty("name", personName?:"")
            apiBody.addProperty("login_type", "google")
            apiBody.addProperty("country", country)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val call = RetrofitClient.instance!!.myApi.loginWithSocial(apiBody.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                dialogClass.hideLoadingDialog()
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        val commonResponseModel =
                            CommonResponseModel(response.body(), response.code())
                        if (commonResponseModel != null) {
                            val gsons: Gson = Gson()
                            val mainModel = gsons.fromJson(
                                commonResponseModel.apiResponse,
                                UserModel::class.java
                            )
                            Log.d("sdfsdfsdfs",mainModel.toString())
                            if (mainModel.status == 200) {
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
                                            Log.d("token====", mainModel.data!!.id.toString())
                                        }
                                    FirebaseMessaging.getInstance()
                                        .subscribeToTopic("topic_bnk_usrs_broadcast")
                                        .addOnCompleteListener { task ->

                                        }
                                }


                                sessionManager.setUserDetails(SessionManager.userData, mainModel.data!!)
                                sessionManager.setBoolean(SessionManager.isLogin, true)
                                sessionManager.setString(SessionManager.USERTOKEN,"bearer ${mainModel.extraData}")
                                startActivity(
                                    Intent(
                                        this@SignUpActivity,
                                        CompletionActivity::class.java
                                    )
                                )
                                finishAffinity()
                            }else {
                                mainModel.message?.let { dialogClass.showError(it) }
                            }
                        }
                    } else if (response.code() in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }else{
                    val errorBodyString = response.errorBody()?.string()
                    val mainModel = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                    dialogClass.hideLoadingDialog()
                    dialogClass.showErrorMessageDialog(mainModel.message)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                dialogClass.hideLoadingDialog()
                dialogClass.showServerErrorDialog()
            }
        })
    }
    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient!!)
        startActivityForResult(signInIntent, RC_SIGN_IN)
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
            LLGoogleSign.setOnClickListener {
                signOut()
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
                                        val country = getCountryNameFromNetwork(this@SignUpActivity)
                                        /*viewModel.setDataInRegisterDataList(edtEmail.text.toString(),
                                            edtUsername.text.toString(),
                                            edtPassword.text.toString(),
                                            country,etFirstName.text.toString())*/
                                        viewModel.setDataInOtpList(
                                            edtEmail.text.toString(),
                                            Constants.SECURITY_1,etFirstName.text.toString(),edtUsername.text.toString()
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

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
}
