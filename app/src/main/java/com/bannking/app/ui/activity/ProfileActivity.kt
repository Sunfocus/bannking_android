package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.BuildConfig
import com.bannking.app.R
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.adapter.CurrencyAdapter
import com.bannking.app.adapter.LanguageAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityProfileBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.retrofitResponseModel.languageModel.Data
import com.bannking.app.model.retrofitResponseModel.languageModel.LanguageModel
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.model.viewModel.ProfileViewModel
import com.bannking.app.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.zeugmasolutions.localehelper.Locales
import darren.googlecloudtts.GoogleCloudTTS
import darren.googlecloudtts.parameter.VoiceSelectionParams
import java.util.*


class ProfileActivity :
    BaseActivity<ProfileViewModel, ActivityProfileBinding?>(ProfileViewModel::class.java),
    CheckBoxListener/*, TextToSpeech.OnInitListener*/ {
    private lateinit var rvLanguage: RecyclerView
    private lateinit var rvCurrency: RecyclerView
    private var languageAdapter: LanguageAdapter = LanguageAdapter()
    private var currencyAdapter: CurrencyAdapter = CurrencyAdapter()
    private var languageDataList: ArrayList<Data> = arrayListOf()
    private var currencyDataList: ArrayList<Data> = arrayListOf()
    lateinit var viewModel: ProfileViewModel
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var newLanguagId: String? = null
    private var newCurrencyId: String? = null
    private var newnotificationOnOFF: Int? = null
    private lateinit var savedSessionManager: SessionManager
    private lateinit var savedAdTime: SessionManager
    var util: Utils = Utils()
    private lateinit var savedSessionManagerVoice: SessionManager
    private lateinit var savedSessionManagerCurrency: SessionManager
  //  private lateinit var textToSpeech: TextToSpeech
  var  mTextToSpeech:TextToSpeech? = null

    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: ReviewInfo? = null

    override fun getBinding(): ActivityProfileBinding {
        return ActivityProfileBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: ProfileViewModel) {
        // Initialize ReviewManager
        reviewManager = ReviewManagerFactory.create(this)
        savedAdTime = SessionManager(this@ProfileActivity, SessionManager.ADTIME)



        savedSessionManager = SessionManager(this@ProfileActivity, SessionManager.LANGUAGE)
        savedSessionManagerVoice = SessionManager(this@ProfileActivity, SessionManager.VOICE)
        savedSessionManagerCurrency = SessionManager(this@ProfileActivity, SessionManager.CURRENCY)

        this.viewModel = viewModel
        viewModel.setDataInLanguageList()
        viewModel.setDataInCurrencyList()
     //   textToSpeech = TextToSpeech(this@ProfileActivity, this)
        mTextToSpeech = TextToSpeech(this.applicationContext
        ) { status ->
            var locale = Locale.US
            if (status == TextToSpeech.SUCCESS) {

                if (status == TextToSpeech.SUCCESS) {

                    if(savedSessionManager.getLanguage() == "English") {
                        locale = Locale.US
                    } else if (savedSessionManager.getLanguage() == "Spanish") {
                        locale = Locale.forLanguageTag("es")
                    } else if (savedSessionManager.getLanguage() == "French") {
                        locale = Locale.FRENCH
                    } else if (savedSessionManager.getLanguage() == "Arabic") {
                        locale = Locale.forLanguageTag("ar")
                    } else if (savedSessionManager.getLanguage() == "Russia") {
                        locale = Locale.forLanguageTag("ru")
                    } else if (savedSessionManager.getLanguage() == "Portuguese") {
                        locale = Locale.forLanguageTag("pt")
                    } else if (savedSessionManager.getLanguage() == "Dutch") {
                        locale = Locale.forLanguageTag("nl")
                    }
                    val result = mTextToSpeech!!.setLanguage(locale)

                    /* if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                         // Handle language not supported error
                     } else {
                         val voices = textToSpeech.voices
                         for (voice in voices) {
                             if (textToSpeech.voice == voice){
                                 break
                             }
                         }
                     }*/
                }

              /*  val a = HashSet<String>()
                a.add(savedSessionManagerVoice.getAnnouncementVoice().toString()) //here you can give male if you want to select male voice.
                a.add("male") //here you can give male if you want to select male voice.

                val v = Voice("en-us-x-sfg#male_2-local",locale, 400, 200, true, a)
                mTextToSpeech!!.setVoice(v)
                mTextToSpeech!!.setSpeechRate(0.8f);*/


                val engineInfo: List<TextToSpeech.EngineInfo> =
                    mTextToSpeech!!.getEngines()
                for (info in engineInfo) {
                    Log.e("speach", "info: $info")
                    if (info.equals("EngineInfo{name=com.samsung.SMT}")) {
                        getDeviceName()
                    }
                }
                val result: Int =
                    mTextToSpeech!!.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA) {
                    Toast.makeText(this@ProfileActivity,
                        getString(R.string.language_pack_missing),
                        Toast.LENGTH_SHORT).show()
                } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    getDeviceName()
                    Toast.makeText(this@ProfileActivity,
                        getString(R.string.language_not_supported),
                        Toast.LENGTH_SHORT).show()
                }
                // mImageSpeak.setEnabled(true);
                mTextToSpeech!!.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String) {
                            Log.e("Inside", "OnStart")
                            // process_tts.hide();
                        }

                        override fun onDone(utteranceId: String) {}
                        override fun onError(utteranceId: String) {}
                    })
            } else {
                Log.e("LOG_TAG", "TTS Initilization Failed")
            }
        }

//        viewModel.setDataProfileDataList()
    }

    override fun setMethod() {
        setOnClickListener()
    }

    override fun initialize() {
        updateUi(userModel)
    }
    private fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }
    private fun capitalize(s: String?): String? {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            Log.e("device name", s)
            s
        } else {
            Log.e("device name", first.uppercaseChar().toString() + s.substring(1))
            val name = first.uppercaseChar().toString() + s.substring(1)
            if (name == "Samsung") {
                /*ComponentName componentToLaunch = new ComponentName(
                             "com.android.settings",
                             "com.android.settings.TextToSpeechSettings");
                     Intent intent = new Intent();
                     intent.addCategory(Intent.CATEGORY_LAUNCHER);
                     intent.setComponent(componentToLaunch);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     intent.setAction(Intent.ACTION_VOICE_COMMAND);
                     startActivity(intent);*/
                // setup the alert builder
                val alertDialog = android.app.AlertDialog.Builder(this).create()
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Alert message to be shown")
                alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which ->
                    val intent = Intent()
                    intent.action = "com.android.settings.TTS_SETTINGS"
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                alertDialog.show()
            }
            first.uppercaseChar().toString() + s.substring(1)
        }
    }
    override fun observe() {
        with(viewModel) {
//            Use For Language BottomShit List
            languageList.observe(this@ProfileActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    updateLanguageAdapter(
                        gson.fromJson(
                            apiResponseData.apiResponse,
                            LanguageModel::class.java
                        ), apiResponseData.code
                    )
                }
            }
//            Use For Currency BottomShit List
            currencyList.observe(this@ProfileActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    updateCurrencyAdapter(
                        gson.fromJson(
                            apiResponseData.apiResponse,
                            LanguageModel::class.java
                        ), apiResponseData.code
                    )
                }
            }

            //Change Currency Response
            changeCurrencyData.observe(this@ProfileActivity) { apiResponseData ->

                if (apiResponseData != null) {
                    if (apiResponseData.apiResponse != null) {
                        val model = gson.fromJson(
                            apiResponseData.apiResponse,
                            CommonResponseApi::class.java
                        )
                        if (model.status.toString()
                                .equals(Constants.STATUSSUCCESS, ignoreCase = true)
                        ) {
                            userModel!!.currencyId = newCurrencyId?.toInt()
                            sessionManager.setUserDetails(
                                SessionManager.userData,
                                userModel!!
                            )
                            dialogClass.showSuccessfullyDialog(model.message.toString())
//                            Toast.makeText(this@ProfileActivity,""+  model.message.toString(), Toast.LENGTH_SHORT).show()
                        } else if (model.message.toString().contains("unlock premium")) {
                            dialogClass.showAccountNotSubscriptionDialog(model.message.toString())
                        } else {
                            model.message.toString()
                        }

                    } else
                        dialogClass.showError(resources.getString(R.string.str_failed_currency_change_request))
//                        Toast.makeText(this@ProfileActivity, "Failed Currency Change Request", Toast.LENGTH_SHORT).show()
                }
            }

            //Change Language Response
            changeLanguageData.observe(this@ProfileActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.apiResponse != null) {
                        val model = gson.fromJson(
                            apiResponseData.apiResponse.toString(),
                            CommonResponseApi::class.java
                        )
                        if (model.status.equals(Constants.STATUSSUCCESS)) {
                            userModel!!.languageId = newLanguagId?.toInt()
                            sessionManager.setUserDetails(
                                SessionManager.userData,
                                userModel!!
                            )
//                            dialogClass.showSuccessfullyDialog(model.message.toString())
                            val dialog = Dialog(this@ProfileActivity)
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.setCancelable(false)
                            dialog.setContentView(R.layout.dialog_account_create_successfully)
                            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            val btnOk: TextView = dialog.findViewById(R.id.txt_done)
                            val txtSucessfulyMessage: TextView =
                                dialog.findViewById(R.id.txt_sucessfuly_message)
                            txtSucessfulyMessage.text = model.message.toString()
                            btnOk.setOnClickListener {
//                                AdController.showInterAd(this@ProfileActivity, null, 0) {
                                    dialog.dismiss()
                                    when (model.name) {
                                        "English" -> {
                                            updateLocale(Locales.English)
                                            savedSessionManager.setLanguage("English")
                                        }
                                        "Spanish" -> {
                                            updateLocale(Locales.Spanish)
                                            savedSessionManager.setLanguage("Spanish")
                                        }
                                        "French" -> {
                                            updateLocale(Locales.French)
                                            savedSessionManager.setLanguage("French")
                                        }
                                        "Arabic" -> {
                                            updateLocale(Locales.Arabic)
                                            savedSessionManager.setLanguage("Arabic")
                                        }
                                        "Russia" -> {
                                            updateLocale(Locales.Russian)
                                            savedSessionManager.setLanguage("Russia")
                                        }
                                        "Portuguese" -> {
                                            updateLocale(Locales.Portuguese)
                                            savedSessionManager.setLanguage("Portuguese")
                                        }
                                        "Dutch" -> {
                                            updateLocale(Locales.Dutch)
                                            savedSessionManager.setLanguage("Dutch")
                                        }
                                        else -> {
                                            updateLocale(Locales.English)
                                            savedSessionManager.setLanguage("English")
                                        }

                                    }
//                                }
                                changeLanguageData.value = null
                            }
                            dialog.show()

                        } else {
                            dialogClass.showError(model.message.toString())
                        }
                    } else
                        dialogClass.showError(resources.getString(R.string.str_failed_language_change_request))
                }
            }

            //Change Language Response
            changeNotificationData.observe(this@ProfileActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.apiResponse != null) {
                        val model = gson.fromJson(
                            apiResponseData.apiResponse.toString(),
                            CommonResponseApi::class.java
                        )
                        if (model.status.equals(Constants.STATUSSUCCESS)) {
                            userModel!!.notification = newnotificationOnOFF.toString()
                            dialogClass.showSuccessfullyDialog(model.message.toString())
                            sessionManager.setUserDetails(
                                SessionManager.userData,
                                userModel!!
                            )
                        } else {
                            dialogClass.showError(model.message.toString())
                        }

                    } else
                        dialogClass.showError(resources.getString(R.string.str_failed_notification_change_request))
                }
            }

            //Delete Account Response
            deleteAccountData.observe(this@ProfileActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.apiResponse != null) {
                        val mainModel =
                            gson.fromJson(apiResponseData.apiResponse, UserModel::class.java)
                        if (mainModel.status.equals(Constants.STATUSSUCCESS)) {
                            inAppPurchaseSM.logOut()
                            sessionManager.logOut()
                            sessionManager.setBoolean(SessionManager.isLogin, false)
//                            startActivity(Intent(this@ProfileActivity, SplashActivity::class.java).setFlags(
//                                FLAG_ACTIVITY_CLEAR_TASK))
//                            finishAffinity()
                            val intent = Intent(this@ProfileActivity, SplashActivity::class.java)
                            val cn = intent.component
                            val mainIntent = Intent.makeRestartActivityTask(cn)
                            startActivity(mainIntent)
                        }
                    } else
                        dialogClass.showServerErrorDialog()
                }
            }
            progressObservable.observe(this@ProfileActivity) {
                if (it != null) {
                    if (it)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }
        }
    }

    private fun updateUi(data: com.bannking.app.model.retrofitResponseModel.userModel.Data?) {
        val setdata: com.bannking.app.model.retrofitResponseModel.userModel.Data =
            data ?: userModel!!
        Log.d("hjdsffsjsdfds",setdata.image.toString())
        Glide.with(this@ProfileActivity)
            .load(setdata.image)
            .placeholder(R.drawable.glide_dot) //<== will simply not work:
            .into(binding!!.profileImage)
            /*.into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?,
                ) {
                    binding!!.profileImage.setImageBitmap(resource)
                }

            })*/

        binding!!.txtUsername.text = setdata.username.toString()
        binding!!.txtEmailId.text = setdata.email
        binding!!.txtAppVersion.text =
            " ${getString(R.string.str_version)} ${BuildConfig.VERSION_NAME}"
//            " ${getString(R.string.str_version)} ${sessionManager.getString(SessionManager.AppVersion)}"

    }


    private fun setOnClickListener() {
        with(binding!!) {


            imgNotification.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, NotificationActivity::class.java))
            }


//            switchNotification.isChecked = sessionManager.getBoolean(SessionManager.isNotification)
            switchNotification.isChecked = userModel?.notification!!.toInt() == 1


            llChangePassword.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, ChangePasswordActivity::class.java))
            }

            switchNotification.setOnCheckedChangeListener { _, isChecked ->
//                sessionManager.setBoolean(SessionManager.isNotification, isChecked)
                newnotificationOnOFF = if (isChecked) 1 else 0
                viewModel.setDataNotificationDataList(isChecked)
            }

            imgBack.setOnClickListener { finish() }

            cvRateUp.setOnClickListener {
                launchMarket()
            }

            cardUpgrade.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, UpgradeActivity::class.java))
            }
            cardRemoveAds.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, UpgradeActivity::class.java))
            }

            llLanguage.setOnClickListener {
                val bottomSheetDialog =
                    BottomSheetDialog(this@ProfileActivity, R.style.NoBackgroundDialogTheme)
                this@ProfileActivity.bottomSheetDialog = bottomSheetDialog
                val view = LayoutInflater.from(this@ProfileActivity)
                    .inflate(R.layout.bottomshit_language, findViewById(R.id.linearLayout))
                bottomSheetDialog.setContentView(view)
                bottomSheetDialog.show()
                openLanguageBottomedDialog(view)
            }

            llVoice.setOnClickListener {
                val bottomSheetDialog =
                    BottomSheetDialog(this@ProfileActivity, R.style.NoBackgroundDialogTheme)
                this@ProfileActivity.bottomSheetDialog = bottomSheetDialog
                val view = LayoutInflater.from(this@ProfileActivity)
                    .inflate(R.layout.bottomshit_sounds, findViewById(R.id.linearLayout))
                bottomSheetDialog.setContentView(view)
                bottomSheetDialog.show()
                openSoundsBottomedDialog(bottomSheetDialog,view)
            }

            llCurrency.setOnClickListener {
                val bottomSheetDialog =
                    BottomSheetDialog(this@ProfileActivity, R.style.NoBackgroundDialogTheme)
                this@ProfileActivity.bottomSheetDialog = bottomSheetDialog
                val view = LayoutInflater.from(this@ProfileActivity)
                    .inflate(R.layout.bottomshit_currency, findViewById(R.id.linearLayout))
                bottomSheetDialog.setContentView(view)
                bottomSheetDialog.show()
                openCurrencyBottomedDialog(view)
            }

            cardLogout.setOnClickListener {
                logout()
            }

            llPrivacyPolicy.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bannking.com/privacy"))
                startActivity(browserIntent)
//                startActivity(Intent(this@ProfileActivity, PrivacyPolicyActivity::class.java))
            }

            cvAccountDelete.setOnClickListener {
                deleteUserAccount()
            }

            llEditProfile.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, ProfileUpdateActivity::class.java))
            }
        }

    }

    private fun openSoundsBottomedDialog(bottomSheetDialog: BottomSheetDialog, view: View?) {
        val radiogrp: RadioGroup = view!!.findViewById(R.id.radiogrp)
        val btn_maleVoice: RadioButton = view.findViewById(R.id.btn_maleVoice)
        val btn_femaleVoice: RadioButton = view.findViewById(R.id.btn_femaleVoice)
        val btn_otherVoice: RadioButton = view.findViewById(R.id.btn_otherVoice)
        val btnScheduleTransfer: Button = view.findViewById(R.id.btn_schedule_Voice)


        if (savedSessionManagerVoice.getAnnouncementVoice() == "Male") {
            btn_maleVoice.isChecked = true
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "Female") {
            btn_femaleVoice.isChecked = true
        }

        btn_maleVoice.setOnClickListener {
            speechToText()
        }

        btn_femaleVoice.setOnClickListener {
            speechToText()
        }

        btn_otherVoice.setOnClickListener {
            speechToText()
        }
        var voice = ""
        radiogrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId -> // find which radio button is selected
            when (checkedId) {
                R.id.btn_maleVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_maleVoice)
                    voice = util.getGenderDescription(Gender.MALE)
                    savedSessionManagerVoice.setAnnouncementVoice(voice)
                }
                R.id.btn_femaleVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_femaleVoice)
                    voice = util.getGenderDescription(Gender.FEMALE)
                    savedSessionManagerVoice.setAnnouncementVoice(voice)
                }
                R.id.btn_otherVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_otherVoice)
                    voice = util.getGenderDescription(Gender.OTHER)
                    savedSessionManagerVoice.setAnnouncementVoice(voice)
                }
            }
        })


        btnScheduleTransfer.setOnClickListener {
            if (btn_maleVoice.isChecked) {
                voice = util.getGenderDescription(Gender.MALE)
            } else if (btn_femaleVoice.isChecked) {
                voice = util.getGenderDescription(Gender.FEMALE)
            } else if (btn_otherVoice.isChecked) {
                voice = util.getGenderDescription(Gender.OTHER)
            }
            savedSessionManagerVoice.setAnnouncementVoice(voice)
            bottomSheetDialog.dismiss()
        }
    }

    private fun selection(btn_maleVoice: RadioButton, btn_femaleVoice: RadioButton, btn_otherVoice: RadioButton, select: RadioButton) {
        btn_maleVoice.isChecked = false
        btn_femaleVoice.isChecked = false
        btn_otherVoice.isChecked = false

        select.isChecked = true


    }

    private fun deleteUserAccount() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ProfileActivity)

        builder.setMessage(resources.getString(R.string.str_are_you_sure_you_want_to_delete_this_user_account))

        builder.setTitle(resources.getString(R.string.str_alert))
        builder.setIcon(R.drawable.ic_warning)
        builder.setCancelable(false)
        builder.setPositiveButton(
            resources.getString(R.string.str_confirm)
        ) { dialog: DialogInterface?, _: Int ->
            AdController.showInterAd(this@ProfileActivity, null, 0)
            viewModel.setDataDeleteAccountDataList()
            dialog?.dismiss()
        }
        builder.setNegativeButton(
            resources.getString(R.string.str_cancel)
        ) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun requestReviewFlow() {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                reviewInfo = task.result
                startReviewFlow()
            } else {
                Log.d("sdfsdfdsfsdf",task.toString())
                // There was some problem, log or handle the error code.
                // task.exception
            }
        }
    }
    private fun startReviewFlow() {
        reviewInfo?.let {
            val flow = reviewManager.launchReviewFlow(this, it)
            flow.addOnCompleteListener { _ ->
                // The flow has finished, update the last prompt time
//                saveReviewManager.setBoolean(SessionManager.REVIEWMANAGER,true)
                Log.d("sdfsdfdsfsdf","yes")
            }
        }
    }

    private fun launchMarket() {
        /*   if (utils.checkisFirstDayOfWeek()){
               RateDialog(this, false).show()
           }*/
//        RateDialog(this, false).show()
        requestReviewFlow()

/*        val uri: Uri = Uri.parse("market://details?id=$packageName")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            val uri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            val goMarket = Intent(Intent.ACTION_VIEW, uri)
            startActivity(goMarket)
//            dialogClass.showError(getString())
//            Toast.makeText(this@ProfileActivity,  "unable to find market app", Toast.LENGTH_LONG).show()
        }*/
    }

    private fun logout() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ProfileActivity)

        builder.setMessage(resources.getString(R.string.str_are_you_sure_you_want_to_logout_this_account))
        builder.setTitle(resources.getString(R.string.str_alert))
        builder.setIcon(R.drawable.ic_warning)
        builder.setCancelable(false)
        builder.setPositiveButton(
            resources.getString(R.string.str_confirm)
        ) { dialog: DialogInterface?, _: Int ->
            inAppPurchaseSM.logOut()
            sessionManager.logOut()
            sessionManager.setBoolean(SessionManager.isLogin, false)
            sessionManager.setBoolean(SessionManager.isDeleteORLogOut, true)
//            startActivity(Intent(this@ProfileActivity, SplashActivity::class.java).setFlags(
//                FLAG_ACTIVITY_CLEAR_TASK
//            ))
            val intent = Intent(this@ProfileActivity, SplashActivity::class.java)
            val cn = intent.component
            val mainIntent = Intent.makeRestartActivityTask(cn)
            startActivity(mainIntent)
            finishAffinity()
            dialog?.dismiss()
        }
        builder.setNegativeButton(
            resources.getString(R.string.str_cancel)
        ) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun updateLanguageAdapter(model: LanguageModel, code: Int) {
        if (code in 199..299) {
            if (model.status.equals("success", ignoreCase = true)) {
                if (model.data != null) {
                    languageDataList = model.data!!
                    model.data?.let { languageAdapter.updateList(it) }
                }
            }
        }
    }

    private fun updateCurrencyAdapter(model: LanguageModel, code: Int) {
        if (code in 199..299) {
            if (model.status.equals("success", ignoreCase = true)) {
                if (model.data != null) {
                    currencyDataList = model.data!!
                    model.data?.let { currencyAdapter.updateList(it) }
                }
            }
        }
    }

    private fun openCurrencyBottomedDialog(view: View) {
        rvCurrency = view.findViewById(R.id.rv_currency)
        rvCurrency.layoutManager = LinearLayoutManager(this)
        currencyAdapter = CurrencyAdapter(this@ProfileActivity, currencyDataList, this)
        rvCurrency.adapter = currencyAdapter
    }

    private fun    openLanguageBottomedDialog(view: View) {
        rvLanguage = view.findViewById(R.id.rv_language)
        rvLanguage.layoutManager = LinearLayoutManager(this)
        languageAdapter = LanguageAdapter(this@ProfileActivity, languageDataList, this)
        rvLanguage.adapter = languageAdapter
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onClickCheckBox(checkBoxID: String?, type: String, name: String?) {
        if (bottomSheetDialog!!.isShowing) {
            bottomSheetDialog!!.dismiss()
        }
        val adTime = savedAdTime.getLong(SessionManager.ADTIME)
         val fiveMinutesInMillis = 5 * 60 * 1000 // 5 minutes in milliseconds
        val currentTime = System.currentTimeMillis()
        // Calculate the time difference
        val timeDifference = currentTime - adTime

        if (type == Constants.CLICKLANGUAGE) {
            if (timeDifference > fiveMinutesInMillis) {
                AdController.showInterAd(this@ProfileActivity, null, 0)
            }
            savedSessionManager.setLanguage(name)
            rvLanguage.post { languageAdapter.notifyDataSetChanged() }
            viewModel.setChangeLanguageDataList(checkBoxID.toString())
            newLanguagId = checkBoxID

            //Change Language Response
        }
        if (type == Constants.CLICKCURRENCY) {
            if (timeDifference > fiveMinutesInMillis) {
                AdController.showInterAd(this@ProfileActivity, null, 0)
            }
            newCurrencyId = checkBoxID
            savedSessionManagerCurrency.setCurrency(newCurrencyId)
            rvCurrency.post { currencyAdapter.notifyDataSetChanged() }
            viewModel.setDataCurrencyChangeDataList(checkBoxID.toString())
        }
    }

    fun setLocale(lang: String?) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        Locale.setDefault(myLocale)
        onConfigurationChanged(conf)
    }

    override fun onResume() {
        super.onResume()
        updateUi(userModel)
        binding!!.cardUpgrade.isVisible = !isPremium
        binding!!.cardRemoveAds.isVisible = !isPremium
//        binding!!.llUpgrade.isVisible = !isPremium
    }

    private fun speechToText() {
        if (savedSessionManagerVoice.getAnnouncementVoice() == "Male") {
            if(savedSessionManager.getLanguage() == "English") {
                speakText("Hi I am a Male Gender")
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakText("Hola soy un género masculino")
            } else if (savedSessionManager.getLanguage() == "French") {
                speakText("Salut, je suis un sexe masculin")
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakText("مرحبا، أنا ذكر")
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakText("Привет, я мужской пол")
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakText("Olá, sou do sexo masculino")
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakText("Hallo, ik ben een mannelijk geslacht")
            }
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "Female") {

            if(savedSessionManager.getLanguage() == "English") {
                speakText("Hi I am a Female Gender")
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakText("Hola soy un género femenino")
            } else if (savedSessionManager.getLanguage() == "French") {
                speakText("Salut, je suis un genre féminin")
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakText("مرحباً، أنا أنثى")
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakText("Привет, я женский пол")
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakText("Olá, sou do sexo feminino")
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakText("Hallo, ik ben een vrouwelijk geslacht")
            }
        }
    }

    private fun speakText(text: String) {
        if (mTextToSpeech!!.isSpeaking) {
            mTextToSpeech!!.stop()
        }
        mTextToSpeech!!.setPitch(1.toFloat())
        mTextToSpeech!!.setSpeechRate(0.9.toFloat())

        if (savedSessionManagerVoice.getAnnouncementVoice() == "Male") {
            val availableVoices: Set<Voice> = mTextToSpeech!!.getVoices()
            var selectedVoice: Voice? = null
            for (voice in availableVoices) {
                if (savedSessionManager.getLanguage() == "Arabic"){
                    if (voice.name == "ar-xa-x-ard-local") {
                        selectedVoice = voice
                        mTextToSpeech!!.setVoice(selectedVoice)
                        break
                    }
                }else {
                    if (voice.name == "en-us-x-iom-local") {
                        selectedVoice = voice
                        mTextToSpeech!!.setVoice(selectedVoice)
                        break
                    }
                }
            }
        } else {
            val availableVoices: Set<Voice> = mTextToSpeech!!.getVoices()
            var selectedVoice: Voice? = null
            for (voice in availableVoices) {
                if (savedSessionManager.getLanguage() == "Arabic"){
                    if (voice.name == "ar-xa-x-arc-network") {
                        selectedVoice = voice
                        mTextToSpeech!!.setVoice(selectedVoice)
                        break
                    }
                }else {
                    if (voice.name == "en-us-x-iog-network") {
                        selectedVoice = voice
                        mTextToSpeech!!.setVoice(selectedVoice)
                        break
                    }
                }
            }
        }

        val words: List<String> = text.split(",")

        for (word in words) {
            mTextToSpeech!!.speak(word, TextToSpeech.QUEUE_ADD, null, null)
            // Add a short pause between words (adjust the length as needed)
            mTextToSpeech!!.playSilence(200, TextToSpeech.QUEUE_ADD, null)
        }

//        mTextToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

}