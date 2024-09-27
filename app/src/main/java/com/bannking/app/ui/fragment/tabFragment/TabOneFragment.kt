package com.bannking.app.ui.fragment.tabFragment

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.EngineInfo
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bannking.app.R
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.adapter.TabsAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.BaseFragment
import com.bannking.app.databinding.FragmentTabOneBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountListModel
import com.bannking.app.model.retrofitResponseModel.accountListModel.Data
import com.bannking.app.model.retrofitResponseModel.headerModel.HeaderModel
import com.bannking.app.model.viewModel.MainViewModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.ui.activity.ScheduleTransferActivity
import com.bannking.app.utils.Constant
import com.bannking.app.utils.Constants
import com.bannking.app.utils.Constants._DELETE_ACCOUNT
import com.bannking.app.utils.Constants._DELETE_ACCOUNT_TITLE
import com.bannking.app.utils.Gender
import com.bannking.app.utils.MoreDotClick
import com.bannking.app.utils.OnClickAnnouncement
import com.bannking.app.utils.OnClickAnnouncementDialog
import com.bannking.app.utils.SessionManager
import com.bannking.app.utils.Utils
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.UUID


class TabOneFragment :
    BaseFragment<MainViewModel, FragmentTabOneBinding>(MainViewModel::class.java)/*TextToSpeech.OnInitListener*/ {
    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: ReviewInfo? = null
    var mTextToSpeech: TextToSpeech? = null
    var util: Utils = Utils()
    var adapter: TabsAdapter? = null
    lateinit var list: ArrayList<Data>
    var tabOneID: String = ""
    private lateinit var savedSessionManagerVoice: SessionManager
    private lateinit var savedSessionManager: SessionManager
    private lateinit var savedSessionManagerTab1: SessionManager
    private lateinit var savedSessionManagerCurrency: SessionManager
    private lateinit var saveReviewManager: SessionManager
    var accountName: String = ""
    var returnz: String = ""
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup): FragmentTabOneBinding {
        return FragmentTabOneBinding.inflate(inflater, container, false)
    }

    override fun viewCreated() {
        // textToSpeech = TextToSpeech(requireActivity(), this)
        // Initialize ReviewManager
        reviewManager = ReviewManagerFactory.create(requireActivity())

        saveReviewManager = SessionManager(requireActivity(), SessionManager.REVIEWMANAGER)
        savedSessionManager = SessionManager(requireActivity(), SessionManager.LANGUAGE)
        savedSessionManagerVoice = SessionManager(requireActivity(), SessionManager.VOICE)
        savedSessionManagerTab1 = SessionManager(requireActivity(), SessionManager.TAB1)
        savedSessionManagerCurrency = SessionManager(requireActivity(), SessionManager.CURRENCY)


        accountName = savedSessionManagerTab1.getTab1().toString()

        // create an object textToSpeech and adding features into it
        mTextToSpeech = TextToSpeech(
            requireContext().applicationContext
        ) { status ->
            var locale = Locale.US
            if (status == TextToSpeech.SUCCESS) {
                if (status == TextToSpeech.SUCCESS) {

                    if (savedSessionManager.getLanguage() == "English") {
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
                    }else if (savedSessionManager.getLanguage() == "Hindi") {
                        locale = Locale.forLanguageTag("hi")
                    }else if (savedSessionManager.getLanguage() == "Japanese") {
                        locale = Locale.forLanguageTag("ja")
                    }else if (savedSessionManager.getLanguage() == "German") {
                        locale = Locale.forLanguageTag("de")
                    }else if (savedSessionManager.getLanguage() == "Italian") {
                        locale = Locale.forLanguageTag("it")
                    }
                    mTextToSpeech!!.language = locale
                }
                val defaultEngine: String = mTextToSpeech!!.defaultEngine

                if (defaultEngine.equals("com.samsung.SMT")) {
                    val engineInfo: List<EngineInfo> = mTextToSpeech!!.engines
                    for (info in engineInfo) {
                        Log.e("speach", "info: $info")
                        //  if (info.equals("EngineInfo{name=com.samsung.SMT}")) {
                        if (info.toString().equals("EngineInfo{name=com.samsung.SMT}")) {
                            getDeviceName()
                        }
                    }
                }
                val result: Int = mTextToSpeech!!.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA) {
                    Toast.makeText(
                        context,
                        requireContext().getString(R.string.language_pack_missing),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                    getDeviceName()
                    Toast.makeText(
                        context,
                        requireActivity().getString(R.string.language_not_supported),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // mImageSpeak.setEnabled(true);
                mTextToSpeech!!.setOnUtteranceProgressListener(object :
                    UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        Log.e("Inside", "OnStart")
                    }

                    override fun onDone(utteranceId: String) {}
                    override fun onError(utteranceId: String) {}
                })
            } else {
                Log.e("LOG_TAG", "TTS Initilization Failed")
            }
        }
        list = arrayListOf()
        adapter =
            TabsAdapter(requireActivity(), list, savedSessionManagerVoice, object : MoreDotClick {
                override fun openDialogBox(list: Data, list1: ArrayList<Data>) {
                    val greaterValue = hasAnotherAccountWithSameBudgetTitle(list1,list)
                    showDotClickDialog(list,greaterValue)
                }

            }, object : OnClickAnnouncementDialog {
                override fun clickOnAnnouncementDialog(list: Data) {
                    showAnnouncementDialog(list)
                    Log.d("sdfsdfsdfds", list.toString())
//                showAnnouncementDialogDemo(list)
                }

            }, object : OnClickAnnouncement {
                override fun clickOnAnnouncement(list: Data) {
                    speechToText(accountName, list)
                }

            })
        mBinding.rvExpenses.adapter = adapter
    }

    private fun hasAnotherAccountWithSameBudgetTitle(list: ArrayList<Data>, list2: Data): Boolean {
        val count = list.count { it.budgetPlanner?.name == list2.budgetPlanner?.name }
        return count > 1
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

    private fun capitalize(s: String?): String {
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
                try {
                    val alertDialog = android.app.AlertDialog.Builder(context).create()
                    alertDialog.setTitle("Change the Preferred engine")
                    alertDialog.setMessage("Go to Settings -> Text-to-speech output -> Preferred engine -> Please select Speech Recognition and synthesis from Google option")
                    alertDialog.setButton(
                        android.app.AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { dialog, which ->
                        alertDialog.dismiss()
                        val intent = Intent()
                        intent.action = "com.android.settings.TTS_SETTINGS"
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        requireActivity().startActivity(intent)
                    }
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                } catch (e: Exception) {

                }
            }
            first.uppercaseChar().toString() + s.substring(1)
        }
    }

    override fun observer() {
        with(viewModel) {
            accountListData.observe(this@TabOneFragment) { accountList ->
                if (isVisible) {
                    if (accountList != null) {
                        if (accountList.code in 199..299) {
                            if (accountList.apiResponse != null) {
                                val mainModel = gson.fromJson(
                                    accountList.apiResponse, AccountListModel::class.java
                                )
                                if (mainModel.data.size != 0) {
                                    val filterdata = if (filterTabDataList.value.isNullOrEmpty()) {
                                        val model = gson.fromJson(
                                            headerTitleList.value!!.apiResponse,
                                            HeaderModel::class.java
                                        )
                                        try {
                                            tabOneID = model.data[0].id.toString()
                                        } catch (_: Exception) {
                                        }

                                        utils.filterAccountListData(
                                            mainModel.data, model.data[0].name.toString()
                                        )
                                    } else {
                                        utils.filterAccountListData(
                                            mainModel.data,
                                            filterTabDataList.value!![0].name.toString()
                                        )
                                    }
                                    Log.e("dsfhghdsfsd",mainModel.data.size.toString())

                                    if (filterdata.size != 0) {
                                        adapter?.updateList(filterdata)
                                        if (filterdata.size == 1) {
                                            val saveReviewManager =
                                                saveReviewManager.getLong(SessionManager.REVIEWMANAGER)

                                            val twentyFourInMillis =
                                                24 * 60 * 60 * 1000 // 24hr in milliseconds
                                            val currentTime = System.currentTimeMillis()
                                            // Calculate the time difference
                                            val timeDifference = currentTime - saveReviewManager
                                            if (timeDifference > twentyFourInMillis) {
                                                requestReviewFlow()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private fun requestReviewFlow() {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                reviewInfo = task.result
                startReviewFlow()
            } else {
                Log.d("sdfsdfdsfsdf", task.toString())
                // There was some problem, log or handle the error code.
                // task.exception
            }
        }
    }

    private fun startReviewFlow() {
        reviewInfo?.let {
            val flow = reviewManager.launchReviewFlow(requireActivity(), it)
            flow.addOnCompleteListener { _ ->
                // The flow has finished, update the last prompt time
                saveReviewManager.saveLong(SessionManager.REVIEWMANAGER, System.currentTimeMillis())
            }
        }
    }


    fun setDataInDeleteBankAccount(strHeaderId: String) {
        viewModel.progressObservable.value = true
        requireActivity().application.FCM_TOKEN.let {
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("header_title_id", strHeaderId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val call = RetrofitClient.instance?.myApi?.removeHeader(apiBody.toString())

            call?.enqueue(object : Callback<CommonResponseApi> {
                override fun onResponse(
                    call: Call<CommonResponseApi>,
                    response: Response<CommonResponseApi>,
                ) {
                    viewModel.progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            if (response.body()?.status == 200) {
                                if (response.body()?.message.equals("Header title remove successfully..")) {
                                    dialogClass.showSuccessfullyDialog(response.body()?.message.toString()) {
                                        requireActivity().recreate()
                                    }
                                } else {
                                    dialogClass.showError(response.body()?.message.toString())
                                }

                            } else {
                                dialogClass.showError(response.body()?.message.toString())
                            }
                        } else if (response.code() in 400..500) {
                            dialogClass.showServerErrorDialog()
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponseApi>, t: Throwable) {
                    viewModel.progressObservable.value = false
                    dialogClass.showServerErrorDialog()
                }
            })
        }
    }


    override fun getViewModelClass(): MainViewModel {
        return ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    fun showDotClickDialog(list: Data, greaterValue: Boolean) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_accountlist_option)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)
        val btnAccountDelete: Button = dialog.findViewById(R.id.btn_account_delete)
        val btnTitleDelete: Button = dialog.findViewById(R.id.btn_title_delete)
        val btnDeleteBoth: Button = dialog.findViewById(R.id.btn_delete_both)
        val btnScheduleTransfer: Button = dialog.findViewById(R.id.btn_schedule_transfer)
        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        if (greaterValue){
            btnTitleDelete.visibility = View.VISIBLE
        }else{
            btnTitleDelete.visibility = View.GONE
        }

        btnScheduleTransfer.setOnClickListener {
            val intent = Intent(activity, ScheduleTransferActivity::class.java)
            intent.putExtra("Id", list.id.toString())
            intent.putExtra("TranSectionName", list.account.toString())
            intent.putExtra("TranSectionNumber", list.account_code.toString())
            intent.putExtra("ComeFrom", "TranSectionDetailActivity")
            startActivity(intent)
//            startActivity(Intent(requireActivity(), ScheduleTransferActivity::class.java))
            dialog.dismiss()
        }

        val parentBudgetName = list.budgetPlanner!!.name
        btnTitleDelete.setOnClickListener {
            dialog.dismiss()
            deleteConfirmationDialog(
                list,
                "Are you sure.\nyou want to delete all budget planners under $parentBudgetName?",
                _DELETE_ACCOUNT_TITLE
            )
        }

        /*btnDeleteBoth.setOnClickListener {
            dialog.dismiss()
            deleteConfirmationDialog(
                list, getString(R.string.str_delete_account_title), _DELETE_ACCOUNT_AND_TITLE
            )
        }*/
        btnAccountDelete.setOnClickListener {
            dialog.dismiss()
            deleteConfirmationDialog(
                list,
                getString(R.string.str_are_you_sure_you_want_to_delete_this_account),
                _DELETE_ACCOUNT
            )
        }
        dialog.show()
    }

    private fun deleteConfirmationDialog(list: Data, alertMsg: String, deleteType: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

        builder.setMessage(alertMsg)
        builder.setTitle(resources.getString(R.string.str_alert))
        builder.setIcon(R.drawable.ic_warning)
        builder.setCancelable(false)
        builder.setPositiveButton(
            resources.getString(R.string.str_confirm)
        ) { _: DialogInterface?, _: Int ->
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            var budgetId = ""
            budgetId = if (deleteType == "1"){
                ""
            }else{
                list.budget_id!!
            }
            viewModel.setDataInDeleteBankAccount(list.id.toString(), tabOneID, deleteType,userToken,budgetId)
        }
        builder.setNegativeButton(
            resources.getString(R.string.str_cancel)
        ) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun deleteHeaderConfirmationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

        builder.setMessage(resources.getString(R.string.str_remove_header))
        builder.setTitle(resources.getString(R.string.str_alert))
        builder.setIcon(R.drawable.ic_warning)
        builder.setCancelable(false)
        builder.setPositiveButton(
            resources.getString(R.string.str_confirm)
        ) { _: DialogInterface?, _: Int ->
            setDataInDeleteBankAccount(tabOneID)
        }
        builder.setNegativeButton(
            resources.getString(R.string.str_cancel)
        ) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }


    fun showAnnouncementDialog(list: Data) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_announcement)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)
        val radiogrp: RadioGroup = dialog.findViewById(R.id.radiogrp)
        val btn_maleVoice: RadioButton = dialog.findViewById(R.id.btn_maleVoice)
        val btn_femaleVoice: RadioButton = dialog.findViewById(R.id.btn_femaleVoice)
        val btn_otherVoice: RadioButton = dialog.findViewById(R.id.btn_otherVoice)
        val btnScheduleTransfer: Button = dialog.findViewById(R.id.btn_schedule_transfer)

        if (savedSessionManagerVoice.getAnnouncementVoice() == "Male") {
            btn_maleVoice.isChecked = true
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "Female") {
            btn_femaleVoice.isChecked = true
        }

        imgClose.setOnClickListener {
            dialog.dismiss()
            if (mTextToSpeech!!.isSpeaking) {
                mTextToSpeech!!.stop()
            }
        }

        radiogrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId -> // find which radio button is selected
            when (checkedId) {
                R.id.btn_maleVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_maleVoice)
                    speechToTextForDialog(accountName, list, util.getGenderDescription(Gender.MALE))
                }

                R.id.btn_femaleVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_femaleVoice)
                    speechToTextForDialog(
                        accountName,
                        list,
                        util.getGenderDescription(Gender.FEMALE)
                    )
                }

                R.id.btn_otherVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_otherVoice)
                    speechToTextForDialog(
                        accountName,
                        list,
                        util.getGenderDescription(Gender.OTHER)
                    )
                }
            }
        })

        var voice = ""
        btnScheduleTransfer.setOnClickListener {
            if (btn_maleVoice.isChecked) {
                voice = util.getGenderDescription(Gender.MALE)
            } else if (btn_femaleVoice.isChecked) {
                voice = util.getGenderDescription(Gender.FEMALE)
            } else if (btn_otherVoice.isChecked) {
                voice = util.getGenderDescription(Gender.OTHER)
            }
            savedSessionManagerVoice.setAnnouncementVoice(voice)
            dialog.dismiss()
            if (mTextToSpeech!!.isSpeaking) {
                mTextToSpeech!!.stop()
            }
        }
        dialog.show()
    }

    private fun selection(
        btn_maleVoice: RadioButton,
        btn_femaleVoice: RadioButton,
        btn_otherVoice: RadioButton,
        select: RadioButton
    ) {
        btn_maleVoice.isChecked = false
        btn_femaleVoice.isChecked = false
        btn_otherVoice.isChecked = false

        select.isChecked = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mTextToSpeech!!.isSpeaking) {
            mTextToSpeech!!.stop()
        }
        mTextToSpeech!!.shutdown()
    }


    private fun speakTextDialog(text: String, type: String) {
        if (mTextToSpeech!!.isSpeaking) {
            mTextToSpeech!!.stop()
        }

        mTextToSpeech!!.setPitch(1.toFloat())
        mTextToSpeech!!.setSpeechRate(0.8.toFloat())

        if (type == "Male") {
            val availableVoices: Set<Voice> = mTextToSpeech!!.voices
            var selectedVoice: Voice? = null
            for (voice in availableVoices) {
                if (savedSessionManager.getLanguage() == "Arabic") {
                    if (voice.name == "ar-xa-x-ard-local") {
                        selectedVoice = voice
                        mTextToSpeech!!.voice = selectedVoice
                        break
                    }
                } else {
                    if (voice.name == "en-us-x-iom-local") {
                        selectedVoice = voice
                        mTextToSpeech!!.voice = selectedVoice
                        break
                    }
                }
            }
        } else if (type == "Female") {
            val availableVoices: Set<Voice> = mTextToSpeech!!.voices
            var selectedVoice: Voice? = null
            for (voice in availableVoices) {
                if (savedSessionManager.getLanguage() == "Arabic") {
                    if (voice.name == "ar-xa-x-arc-network") {
                        selectedVoice = voice
                        mTextToSpeech!!.voice = selectedVoice
                        break
                    }
                } else {
                    if (voice.name == "en-us-x-iog-network") {
                        selectedVoice = voice
                        mTextToSpeech!!.voice = selectedVoice
                        break
                    }
                }
            }
        }

        val words: List<String> = text.split("*")

        for (word in words) {
            mTextToSpeech!!.speak(word, TextToSpeech.QUEUE_ADD, null, null)
            // Add a short pause between words (adjust the length as needed)
            mTextToSpeech!!.playSilence(70, TextToSpeech.QUEUE_ADD, null)
        }
    }

    private fun speakText(text: String) {
        if (mTextToSpeech!!.isSpeaking) {
            mTextToSpeech!!.stop()
        }

        mTextToSpeech!!.setPitch(1.toFloat())
        mTextToSpeech!!.setSpeechRate(0.9.toFloat())

        if (savedSessionManagerVoice.getAnnouncementVoice() == "Male") {
            val availableVoices: Set<Voice> = mTextToSpeech!!.voices
            var selectedVoice: Voice? = null
            for (voice in availableVoices) {
                if (savedSessionManager.getLanguage() == "Arabic") {
                    if (voice.name == "ar-xa-x-ard-local") {
                        selectedVoice = voice
                        mTextToSpeech!!.voice = selectedVoice
                        break
                    }
                } else {
                    if (voice.name == "en-us-x-iom-local") {
                        selectedVoice = voice
                        mTextToSpeech!!.voice = selectedVoice
                        break
                    }
                }
            }
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "Female") {
            val availableVoices: Set<Voice> = mTextToSpeech!!.voices
            var selectedVoice: Voice? = null
            for (voice in availableVoices) {
                if (savedSessionManager.getLanguage() == "Arabic") {
                    if (voice.name == "ar-xa-x-arc-network") {
                        selectedVoice = voice
                        mTextToSpeech!!.voice = selectedVoice
                        break
                    }
                } else {
                    if (voice.name == "en-us-x-iog-network") {
                        selectedVoice = voice
                        mTextToSpeech!!.voice = selectedVoice
                        break
                    }
                }
            }
        }


        val words: List<String> = text.split("*")

        for (word in words) {
            Log.e("AMittt",word)
            mTextToSpeech!!.speak(word, TextToSpeech.QUEUE_ADD, null, null)
            mTextToSpeech!!.playSilentUtterance(
                70,
                TextToSpeech.QUEUE_ADD,
                UUID.randomUUID().toString()
            )
        }

    }

    private fun speechToText(accountName: String, list: Data) {
        try {

            val amount = utils.removeCurrencySymbol(list.amount!!)

            returnz = Constant.convertNumericToSpokenWords(
                amount,
                savedSessionManagerCurrency.getCurrency()
            )
        } catch (e: NumberFormatException) {
            //Toast.makeToast("illegal number or empty number" , toast.long)
        }

        if (savedSessionManagerVoice.getAnnouncementVoice() == "Male") {
            if (savedSessionManager.getLanguage() == "English") {
                speakText(
                    "Your " + list.account + " Account Balance ending in*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*is*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakText(
                    "Su cuenta " + list.account + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*cuesta*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakText(
                    "Votre compte " + list.account + " Solde du compte se terminant par*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*est de*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakText(
                    "حسابك  " + list.account + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.account_code.toString()
                    ) + "*يكون*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakText(
                    "Твой" + list.account + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*является*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakText(
                    "Sua conta" + list.account + "Saldo da conta terminando em*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*é*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakText(
                    "Uw" + list.account + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*bedraagt*" + returnz
                )
            }
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "Female") {
            if (savedSessionManager.getLanguage() == "English") {
                speakText(
                    "Your " + list.account + " Account Balance ending in*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*is*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakText(
                    "Su cuenta " + list.account + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*cuesta*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakText(
                    "Votre compte " + list.account + " Solde du compte se terminant par*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*est de*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakText(
                    "حسابك  " + list.account + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.account_code.toString()
                    ) + "*يكون*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakText(
                    "Твой" + list.account + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*является*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakText(
                    "Sua conta" + list.account + "Saldo da conta terminando em*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*é*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakText(
                    "Uw" + list.account + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*bedraagt*" + returnz
                )
            }
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "") {
            if (savedSessionManager.getLanguage() == "English") {
                speakText(
                    "Your " + list.account + " Account Balance ending in*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*is*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakText(
                    "Su cuenta " + list.account + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*cuesta*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakText(
                    "Votre compte " + list.account + " Solde du compte se terminant par*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*est de*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakText(
                    "حسابك  " + list.account + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.account_code.toString()
                    ) + "*يكون*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakText(
                    "Твой" + list.account + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*является*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakText(
                    "Sua conta" + list.account + "Saldo da conta terminando em*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*é*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakText(
                    "Uw" + list.account + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*bedraagt*" + returnz
                )
            }
        }
    }


    private fun speechToTextForDialog(accountName: String, list: Data, type: String) {

        try {
            val amount = utils.removeCurrencySymbol(list.amount!!)
            returnz = Constant.convertNumericToSpokenWords(
                amount,
                savedSessionManagerCurrency.getCurrency()
            )
        } catch (e: NumberFormatException) {
            //Toast.makeToast("illegal number or empty number" , toast.long)
        }

        if (type == "Male") {
            if (savedSessionManager.getLanguage() == "English") {
                speakTextDialog(
                    "Your " + list.account + " Account Balance ending in*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*is*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakTextDialog(
                    "Su cuenta " + list.account + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*cuesta*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakTextDialog(
                    "Votre compte " + list.account + " Solde du compte se terminant par*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*est de*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakTextDialog(
                    "حسابك  " + list.account + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.account_code.toString()
                    ) + "*يكون*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakTextDialog(
                    "Твой" + list.account + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*является*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakTextDialog(
                    "Sua conta" + list.account + "Saldo da conta terminando em*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*é*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakTextDialog(
                    "Uw" + list.account + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*bedraagt*" + returnz, type
                )
            }
        } else if (type == "Female") {
            if (savedSessionManager.getLanguage() == "English") {
                speakTextDialog(
                    "Your " + list.account + " Account Balance ending in*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*is*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakTextDialog(
                    "Su cuenta " + list.account + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*cuesta*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakTextDialog(
                    "Votre compte " + list.account + " Solde du compte se terminant par*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*est de*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakTextDialog(
                    "حسابك  " + list.account + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.account_code.toString()
                    ) + "*يكون*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakTextDialog(
                    "Твой" + list.account + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*является*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakTextDialog(
                    "Sua conta" + list.account + "Saldo da conta terminando em*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*é*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakTextDialog(
                    "Uw" + list.account + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.account_code.toString()
                    ) + "*bedraagt*" + returnz, type
                )
            }
        }
    }

    private fun speechToTextForDialogDemo1(accountName: String, list: Data, type: String) {
        if (type == "Male") {
            if (savedSessionManager.getLanguage() == "English") {
                speakTextDialog(
                    "Your " + accountName + " Account ending in" + utils.numberToText(
                        list.account_code.toString()
                    ) + "is," + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakTextDialog(
                    "Su cuenta " + accountName + " que termina en" + utils.numberToText(
                        list.account_code.toString()
                    ) + "cuesta" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakTextDialog(
                    "Votre compte " + accountName + " se terminant par" + utils.numberToText(
                        list.account_code.toString()
                    ) + "est de" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakTextDialog(
                    "حسابك  " + accountName + " المنتهي بالرقم" + utils.numberToText(
                        list.account_code.toString()
                    ) + "هو" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakTextDialog(
                    "Стоимость вашего" + accountName + "счета, оканчивающегося на" + utils.numberToText(
                        list.account_code.toString()
                    ) + "составляет" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakTextDialog(
                    "Sua conta" + accountName + "terminando em" + utils.numberToText(
                        list.account_code.toString()
                    ) + "é" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakTextDialog(
                    "Uw" + accountName + "account eindigend op" + utils.numberToText(
                        list.account_code.toString()
                    ) + "bedraagt" + list.amount, type
                )
            }
        } else if (type == "Female") {
            if (savedSessionManager.getLanguage() == "English") {
                speakTextDialog(
                    "Your " + accountName + " Account ending in" + utils.numberToText(
                        list.account_code.toString()
                    ) + "is" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakTextDialog(
                    "Su cuenta " + accountName + " que termina en" + utils.numberToText(
                        list.account_code.toString()
                    ) + "cuesta" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakTextDialog(
                    "Votre compte " + accountName + " se terminant par" + utils.numberToText(
                        list.account_code.toString()
                    ) + "est de" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakTextDialog(
                    "حسابك  " + accountName + " المنتهي بالرقم" + utils.numberToText(
                        list.account_code.toString()
                    ) + "هو" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakTextDialog(
                    "Стоимость вашего" + accountName + "счета, оканчивающегося на" + utils.numberToText(
                        list.account_code.toString()
                    ) + "составляет" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakTextDialog(
                    "Sua conta" + accountName + "terminando em" + utils.numberToText(
                        list.account_code.toString()
                    ) + "é" + list.amount, type
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakTextDialog(
                    "Uw" + accountName + "account eindigend op" + utils.numberToText(
                        list.account_code.toString()
                    ) + "bedraagt" + list.amount, type
                )
            }
        }
    }
}