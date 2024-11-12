package com.bannking.app.ui.fragment.tabFragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.adapter.HideListener
import com.bannking.app.adapter.ShowHideAdapter
import com.bannking.app.adapter.TabsAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.BaseFragment
import com.bannking.app.databinding.FragmentTabTwoBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.PostVoiceResponse
import com.bannking.app.model.retrofitResponseModel.accountListModel.*
import com.bannking.app.model.retrofitResponseModel.headerModel.HeaderModel
import com.bannking.app.model.viewModel.MainViewModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.ui.activity.ScheduleTransferActivity
import com.bannking.app.ui.activity.SoundActivity
import com.bannking.app.utils.*
import com.bannking.app.utils.Constants._DELETE_ACCOUNT
import com.bannking.app.utils.Constants._DELETE_ACCOUNT_TITLE
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class TabTwoFragment :
    BaseFragment<MainViewModel, FragmentTabTwoBinding>(MainViewModel::class.java) {

    var adapter: TabsAdapter? = null
    lateinit var list: ArrayList<Data>
    lateinit var extraData: ArrayList<ExtraData>
    lateinit var extraDataHideList: ArrayList<ExtraData>
    lateinit var hiddenData: ArrayList<HiddenData>
    var tabTwoID: String = ""
    var mTextToSpeech: TextToSpeech? = null
    var util: Utils = Utils()
    private lateinit var savedSessionManagerVoice: SessionManager
    private lateinit var savedSessionManager: SessionManager
    private lateinit var savedSessionManagerTab2: SessionManager
    private lateinit var savedSessionManagerCurrency: SessionManager
    var accountName: String = ""
    var returnz: String = ""
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var pref: SharedPref
    private var showHideDialog: Dialog? = null


    override fun getBinding(inflater: LayoutInflater, container: ViewGroup): FragmentTabTwoBinding {
        return FragmentTabTwoBinding.inflate(inflater, container, false)
    }

    override fun getViewModelClass(): MainViewModel {
        return ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun viewCreated() {
//        setOnClickListener()
        pref = SharedPref(requireActivity())
        savedSessionManager = SessionManager(requireActivity(), SessionManager.LANGUAGE)
        savedSessionManagerVoice = SessionManager(requireActivity(), SessionManager.VOICE)
        savedSessionManagerTab2 = SessionManager(requireActivity(), SessionManager.TAB2)
        savedSessionManagerCurrency = SessionManager(requireActivity(), SessionManager.CURRENCY)

        accountName = savedSessionManagerTab2.getTab2().toString()
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
                    } else if (savedSessionManager.getLanguage() == "Hindi") {
                        locale = Locale.forLanguageTag("hi")
                    } else if (savedSessionManager.getLanguage() == "Japanese") {
                        locale = Locale.forLanguageTag("ja")
                    } else if (savedSessionManager.getLanguage() == "German") {
                        locale = Locale.forLanguageTag("de")
                    } else if (savedSessionManager.getLanguage() == "Italian") {
                        locale = Locale.forLanguageTag("it")
                    }
                    mTextToSpeech!!.language = locale


                }
                val defaultEngine: String = mTextToSpeech!!.defaultEngine

                if (defaultEngine.equals("com.samsung.SMT")) {
                    val engineInfo: List<TextToSpeech.EngineInfo> = mTextToSpeech!!.engines
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
                        requireContext().getString(R.string.language_not_supported),
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
        val premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)
        list = arrayListOf()
        extraData = arrayListOf()
        hiddenData = arrayListOf()
        extraDataHideList = arrayListOf()
        adapter = TabsAdapter(
            requireActivity(),
            list,
            extraData,
            hiddenData,
            extraDataHideList,
            savedSessionManagerVoice,
            object : MoreDotClick {
                override fun openDialogBox(list: Data, list1: ArrayList<Data>) {
                    val greaterValue = hasAnotherAccountWithSameBudgetTitle(list1, list)
                    showDotClickDialog(list, greaterValue)
                }

                override fun openDialogBoxExtraData(
                    currentExtraItem: ExtraData,
                    extraData: ArrayList<AccountsData>,
                    hiddenData: ArrayList<HiddenData>?
                ) {
//                    showBottomSheet(
//                        "",
//                        "",
//                        "delete",
//                        currentExtraItem.institutionName,
//                        currentExtraItem.institutionId
//                    )

                    dialogForShowHide("", currentExtraItem.institutionId, extraData,hiddenData)

                }

                override fun dotForChildren(
                    accountId: String,
                    instituteId: String,
                    accountsList: ArrayList<AccountsData>,
                    hiddenData: ArrayList<HiddenData>?,
                    extraDataHideList: ArrayList<AccountsData>
                ) {
//                    showBottomSheet(
//                        accountId,
//                        instituteId,
//                        "hide",
//                        "",
//                        ""
//                    )

                    dialogForShowHide(accountId, instituteId, extraDataHideList,hiddenData)

                }

            },
            object : OnClickAnnouncementDialog {
                override fun clickOnAnnouncementDialog(list: Data) {
                    if (premiumFeatures) {
                        voiceForAccountRead(accountName, list)
                    } else {
                        val intent = Intent(requireActivity(), SoundActivity::class.java)
                        startActivity(intent)
//                        showAnnouncementDialog(list)
                    }
                }

                override fun clickOnAnnouncementDialogExtra(currentExtraItem: AccountsData) {
                    if (premiumFeatures) {
                        voiceForAccountReadForExtra(currentExtraItem.accountName, currentExtraItem)
                    } else {
                        val intent = Intent(requireActivity(), SoundActivity::class.java)
                        startActivity(intent)
//                        showAnnouncementDialogExtra(currentExtraItem)
                    }
                }

            },
            object : OnClickAnnouncement {
                override fun clickOnAnnouncement(list: Data) {
                    if (premiumFeatures) {
                        voiceForAccountRead(accountName, list)
                    } else {
                        speechToText(accountName, list)
                    }
                }

                override fun clickOnAnnouncementExtra(currentExtraItem: AccountsData) {
                    if (premiumFeatures) {
                        voiceForAccountReadForExtra(currentExtraItem.accountName, currentExtraItem)
                    } else {
                        speechToTextExtra(currentExtraItem.accountName, currentExtraItem)
                    }
                }

            })
        mBinding.rvIncome.adapter = adapter
    }

    private fun dialogForShowHide(
        accountIds: String,
        instituteId: String,
        accountsList: ArrayList<AccountsData>,
        hiddenData: ArrayList<HiddenData>?
    ) {
        showHideDialog?.dismiss()
        showHideDialog = Dialog(requireActivity())
        showHideDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        showHideDialog!!.setCancelable(false)
        showHideDialog!!.setContentView(R.layout.dialog_for_show_hide)
        showHideDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        showHideDialog!!.window!!.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val btnCancel = showHideDialog!!.findViewById<AppCompatButton>(R.id.btnCancelUnlinkBank)
        val btnUnlinkBank = showHideDialog!!.findViewById<AppCompatButton>(R.id.btnUnlinkBank)
        val rvSubBankName = showHideDialog!!.findViewById<RecyclerView>(R.id.rvSubBankName)

        btnUnlinkBank.setOnClickListener {
            showRemoveBankDialog(requireActivity(), instituteId)
            showHideDialog!!.dismiss()
        }
        btnCancel.setOnClickListener {
            showHideDialog!!.cancel()
        }

        val adapter = ShowHideAdapter(requireActivity(), accountsList, hiddenData,object : HideListener{
            override fun hideShowBank(accountId: String, isChecked: Boolean) {
                if (isChecked){
                    hideBankDetails(accountId, instituteId)
                }else{
                    showHideBankApi(instituteId,accountId)
                }
            }

        })
        rvSubBankName.adapter = adapter

        showHideDialog!!.show()

    }


    private fun showBottomSheet(
        accountId: String,
        instituteId: String,
        whichType: String,
        institutionName: String,
        institutionId: String
    ) {
        val bottomSheetDialog =
            BottomSheetDialog(requireActivity(), R.style.NoBackgroundDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_hide_show, null)
        bottomSheetDialog.setContentView(view)
        val showHiddenAccounts = view.findViewById<TextView>(R.id.showHiddenAccounts)
        val deleteAccount = view.findViewById<TextView>(R.id.deleteAccount)

        if (whichType == "hide") {
            deleteAccount.text = "Hide Account"
            showHiddenAccounts.visibility = View.GONE
        } else {
            deleteAccount.text = "Delete Account"
            showHiddenAccounts.visibility = View.VISIBLE

        }
        showHiddenAccounts.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        deleteAccount.setOnClickListener {
            if (whichType == "hide") {
                hideBankDetails(accountId, instituteId)
            } else {
//                showRemoveBankDialog(requireActivity(), institutionName, institutionId)
            }

            bottomSheetDialog.dismiss()
        }

        view.findViewById<TextView>(R.id.cancelButton).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun showHideBankApi(institutionId: String, accountId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            viewModel.showAllBankFromList(institutionId, userToken,accountId)
                .observe(this@TabTwoFragment) {
                    if (userToken != null) {
                        viewModel.setDataInAccountList(userToken)
                    }
                }
        }
    }

    private fun hideBankDetails(accountId: String, instituteId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            viewModel.hideBankFromList(instituteId, userToken, accountId)
                .observe(this@TabTwoFragment) {
                    if (userToken != null) {
                        viewModel.setDataInAccountList(userToken)
                    }
                }
        }
    }

    private fun removeBankFromDatabase(institutionId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            viewModel.removeBankFromList(institutionId, userToken).observe(this@TabTwoFragment) {
                if (userToken != null) {
                    viewModel.setDataInAccountList(userToken)
                    viewModel.setDataInHeaderTitleList(userToken)
                }
            }
        }

    }

    private fun showRemoveBankDialog(
        context: Context,
        institutionId: String,

        ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Remove Bank")
        builder.setMessage("Are you sure you want to remove your Bank details?")

        // Set up the positive button
        builder.setPositiveButton("Remove") { dialog: DialogInterface, _: Int ->
            removeBankFromDatabase(institutionId)
            dialog.dismiss()
        }

        // Set up the negative button
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss() // Dismiss the dialog
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }


    private fun voiceForAccountRead(accountName: String, list: Data) {
        returnz = Constant.convertNumericToSpokenWords(
            list.amount, savedSessionManagerCurrency.getCurrency()
        )

        val completeText =
            "Your $accountName account balance ending in ${list.account_code} is ${list.amount}"

        val engine = pref.getString(SessionManager.ENGINEFORAPI)
        val voiceId = pref.getString(SessionManager.VOICEFORAPI)
        val langCode = pref.getString(SessionManager.LANGUAGECODEFORAPI)

        if (engine.isNotEmpty() && voiceId.isNotEmpty() && langCode.isNotEmpty()) {
            viewModel.postVoiceInLanguageList(engine, voiceId, langCode, completeText)
                .observe(this@TabTwoFragment) { its ->
                    val apiResponseData = its as PostVoiceResponse
                    if (apiResponseData.success) {
                        playAudio(apiResponseData.path)
                    } else dialogClass.showError("Something went wrong")
                }
        } else {
            val intent = Intent(activity, SoundActivity::class.java)
            startActivity(intent)
        }
    }

    private fun hasAnotherAccountWithSameBudgetTitle(list: ArrayList<Data>, list2: Data): Boolean {
        val count = list.count { it.budgetPlanner?.name == list2.budgetPlanner?.name }
        return count > 1
    }

    private fun getDeviceName(): String {
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
            accountListData.observe(requireActivity()) { accountlist ->
                if (isVisible) {
                    if (accountlist != null) {
                        if (accountlist.code in 199..299) {
                            if (accountlist.apiResponse != null) {
                                val mainModel = gson.fromJson(
                                    accountlist.apiResponse, AccountListModel::class.java
                                )
                                if (mainModel.data.size != 0) {
                                    var filterdata: ArrayList<Data> = ArrayList()
                                    if (filterTabDataList.value.isNullOrEmpty()) {
                                        val model = gson.fromJson(
                                            headerTitleList.value!!.apiResponse,
                                            HeaderModel::class.java
                                        )
                                        try {
                                            tabTwoID = model.data[1].id.toString()
                                        } catch (_: Exception) {
                                        }

                                        filterdata = utils.filterAccountListData(
                                            mainModel.data, model.data[1].name.toString()
                                        )
                                    } else {
                                        try {
                                            filterdata = utils.filterAccountListData(
                                                mainModel.data,
                                                filterTabDataList.value!![1].name.toString()
                                            )
                                        } catch (e: Exception) {
                                            Log.e("TAG_tab_crash", "observer: " + e.message)
                                        }
                                    }

                                    if (mainModel.extraData.size != 0) {
                                        val filterableData =
                                            if (filterTabDataList.value.isNullOrEmpty()) {
                                                val model = gson.fromJson(
                                                    headerTitleList.value!!.apiResponse,
                                                    HeaderModel::class.java
                                                )
                                                try {
                                                    tabTwoID = model.data[1].id.toString()
                                                } catch (_: Exception) {
                                                }

                                                utils.filterAccountListExtraData(
                                                    mainModel.extraData,
                                                    model.data[1].id!!.toInt()
                                                )

                                            } else {
                                                utils.filterAccountListExtraData(
                                                    mainModel.extraData,
                                                    filterTabDataList.value!![1].id!!.toInt()
                                                )
                                            }

                                        Log.e("filterableData", filterableData.toString())
                                        val filteredExtraData = if (mainModel.hiddenData.size > 0) {
                                            filterableData.map { extraData ->
                                                val filteredAccountsData =
                                                    extraData.accountsData.filter { accountData ->
                                                        mainModel.hiddenData.none { hiddenData ->
                                                            hiddenData.accountId == accountData.accountId
                                                        }
                                                    }.toCollection(ArrayList())

                                                extraData.copy(accountsData = filteredAccountsData)
                                            }.toCollection(ArrayList())
                                        } else {
                                            filterableData
                                        }


                                        adapter?.updateList(filterdata, filteredExtraData,mainModel.hiddenData,filterableData)
                                    } else {
                                        val filterableData =
                                            if (filterTabDataList.value.isNullOrEmpty()) {
                                                val model = gson.fromJson(
                                                    headerTitleList.value!!.apiResponse,
                                                    HeaderModel::class.java
                                                )
                                                try {
                                                    tabTwoID = model.data[1].id.toString()
                                                } catch (_: Exception) {
                                                }

                                                utils.filterAccountListExtraData(
                                                    mainModel.extraData,
                                                    model.data[1].id!!.toInt()
                                                )

                                            } else {
                                                utils.filterAccountListExtraData(
                                                    mainModel.extraData,
                                                    filterTabDataList.value!![1].id!!.toInt()
                                                )
                                            }
                                        Log.e("filterableData", filterableData.toString())


                                        val filteredExtraData = if (mainModel.hiddenData.size > 0) {
                                            filterableData.map { extraData ->
                                                val filteredAccountsData =
                                                    extraData.accountsData.filter { accountData ->
                                                        mainModel.hiddenData.none { hiddenData ->
                                                            hiddenData.accountId == accountData.accountId
                                                        }
                                                    }.toCollection(ArrayList())

                                                extraData.copy(accountsData = filteredAccountsData)
                                            }.toCollection(ArrayList())
                                        } else {
                                            filterableData
                                        }

                                        adapter?.updateList(
                                            filterdata,
                                            filteredExtraData,
                                            mainModel.hiddenData,
                                            filterableData
                                        )
                                    }

                                } else {
                                    if (mainModel.extraData.size != 0) {
                                        val filterableData =
                                            if (filterTabDataList.value.isNullOrEmpty()) {
                                                val model = gson.fromJson(
                                                    headerTitleList.value!!.apiResponse,
                                                    HeaderModel::class.java
                                                )
                                                try {
                                                    tabTwoID = model.data[1].id.toString()
                                                } catch (_: Exception) {
                                                }

                                                utils.filterAccountListExtraData(
                                                    mainModel.extraData,
                                                    model.data[1].id!!.toInt()
                                                )

                                            } else {
                                                utils.filterAccountListExtraData(
                                                    mainModel.extraData,
                                                    filterTabDataList.value!![1].id!!.toInt()
                                                )
                                            }
                                        Log.e("filterableData", filterableData.toString())

                                        val filteredExtraData = if (mainModel.hiddenData.size > 0) {
                                            filterableData.map { extraData ->
                                                val filteredAccountsData =
                                                    extraData.accountsData.filter { accountData ->
                                                        mainModel.hiddenData.none { hiddenData ->
                                                            hiddenData.accountId == accountData.accountId
                                                        }
                                                    }.toCollection(ArrayList())

                                                extraData.copy(accountsData = filteredAccountsData)
                                            }.toCollection(ArrayList())
                                        } else {
                                            filterableData
                                        }

                                        adapter?.updateList(
                                            list,
                                            filteredExtraData,
                                            mainModel.hiddenData,
                                            filterableData
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            errorResponse.observe(this@TabTwoFragment) { message ->
                if (isVisible) {
                    if (message != null) {
                        dialogClass.showErrorMessageDialog(message)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun playAudio(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer?.apply {
                setDataSource(url)
                setOnPreparedListener {
                    start()
                }
                setOnCompletionListener {
                    release()
                }
                setOnErrorListener { _, _, _ ->
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("catchError", "Error initializing media player: ${e.message}")
        }
    }

    private fun showDotClickDialog(list: Data, greaterValue: Boolean) {
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

        if (greaterValue) {
            btnTitleDelete.visibility = View.VISIBLE
        } else {
            btnTitleDelete.visibility = View.GONE
        }

        btnScheduleTransfer.setOnClickListener {
            val intent = Intent(activity, ScheduleTransferActivity::class.java)
            intent.putExtra("Id", list.id.toString())
            intent.putExtra("TranSectionName", list.account.toString())
            intent.putExtra("TranSectionNumber", list.account_code.toString())
            intent.putExtra("ComeFrom", "TranSectionDetailActivity")
            startActivity(intent)
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
        btnDeleteBoth.setOnClickListener {
            dialog.dismiss()
            deleteConfirmationDialog(
                list,
                getString(R.string.str_delete_account_title),
                Constants._DELETE_ACCOUNT_AND_TITLE
            )
        }
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

    private fun deleteHeaderConfirmationDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

        builder.setMessage(resources.getString(R.string.str_remove_header))
        builder.setTitle(resources.getString(R.string.str_alert))
        builder.setIcon(R.drawable.ic_warning)
        builder.setCancelable(false)
        builder.setPositiveButton(
            resources.getString(R.string.str_confirm)
        ) { _: DialogInterface?, _: Int ->
            setDataInDeleteBankAccount(tabTwoID)
        }
        builder.setNegativeButton(
            resources.getString(R.string.str_cancel)
        ) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun setDataInDeleteBankAccount(strHeaderId: String) {
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
                    call: Call<CommonResponseApi>, response: Response<CommonResponseApi>
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
            budgetId = if (deleteType == "1") {
                ""
            } else {
                list.budget_id!!
            }
            viewModel.setDataInDeleteBankAccount(
                list.id.toString(), tabTwoID, deleteType, userToken, budgetId
            )
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

        if (savedSessionManagerVoice.getAnnouncementVoice()!!.isNotEmpty()) {
            dialog.dismiss()
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
//                    savedSessionManagerVoice.setAnnouncementVoice(util.getGenderDescription(Gender.MALE))
                    speechToTextForDialog(accountName, list, util.getGenderDescription(Gender.MALE))
                }

                R.id.btn_femaleVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_femaleVoice)
//                    savedSessionManagerVoice.setAnnouncementVoice(util.getGenderDescription(Gender.FEMALE))
                    speechToTextForDialog(
                        accountName, list, util.getGenderDescription(Gender.FEMALE)
                    )
                }

                R.id.btn_otherVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_otherVoice)
//                    savedSessionManagerVoice.setAnnouncementVoice(util.getGenderDescription(Gender.OTHER))
                    speechToTextForDialog(
                        accountName, list, util.getGenderDescription(Gender.OTHER)
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
        mediaPlayer?.release()
        mediaPlayer = null

        mTextToSpeech!!.shutdown()
    }


    private fun speakTextDialog(text: String, type: String) {
        if (mTextToSpeech!!.isSpeaking) {
            mTextToSpeech!!.stop()
        }

        mTextToSpeech!!.setPitch(1.toFloat())
        mTextToSpeech!!.setSpeechRate(0.9.toFloat())

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
        try {
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
                mTextToSpeech!!.speak(word, TextToSpeech.QUEUE_ADD, null, null)
                // Add a short pause between words (adjust the length as needed)
                mTextToSpeech!!.playSilence(70, TextToSpeech.QUEUE_ADD, null)
            }

        } catch (e: Exception) {
            Log.e("catchError", e.message.toString())
        }

    }

    private fun speechToText(accountName: String, list: Data) {

        try {

            val amount = utils.removeCurrencySymbol(list.amount!!)

            returnz = Constant.convertNumericToSpokenWords(
                amount, savedSessionManagerCurrency.getCurrency()
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
                amount, savedSessionManagerCurrency.getCurrency()
            )
//            val numericValue = BigDecimal(amount)
//            returnz = utils.convertNumericToWords(list.amount!!).toString();
//            returnz = Words.convert(numericValue.toLong())
//            val number: Long = finalAmount.toLong()
//            returnz = Words.convert(number)
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

    private fun speechToTextExtra(accountName: String, list: AccountsData) {
        try {

            val amount = utils.removeCurrencySymbol(list.balance.toString())

            returnz = Constant.convertNumericToSpokenWords(
                amount, savedSessionManagerCurrency.getCurrency()
            )
        } catch (e: NumberFormatException) {
            //Toast.makeToast("illegal number or empty number" , toast.long)
        }

        if (savedSessionManagerVoice.getAnnouncementVoice() == "Male") {
            if (savedSessionManager.getLanguage() == "English") {
                speakText(
                    "Your " + accountName + " Account Balance ending in*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*is*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakText(
                    "Su cuenta " + accountName + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*cuesta*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakText(
                    "Votre compte " + accountName + " Solde du compte se terminant par*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*est de*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakText(
                    "حسابك  " + accountName + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*يكون*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakText(
                    "Твой" + accountName + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*является*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakText(
                    "Sua conta" + accountName + "Saldo da conta terminando em*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*é*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakText(
                    "Uw" + accountName + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*bedraagt*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Hindi") {
                speakText(
                    "आपके" + accountName + "का बैलेंस*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*और*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Chinese") {
                speakText(
                    "你的" + accountName + "账户余额 以...结尾*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*是*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Japanese") {
                speakText(
                    "あなたの" + accountName + "口座残高 で終わる*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "* です *" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "German") {
                speakText(
                    "Dein" + accountName + "Kontostand endet mit*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "* ist *" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Italian") {
                speakText(
                    "Il tuo" + accountName + "Saldo del conto che termina con*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "* è *" + returnz
                )
            }
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "Female") {
            if (savedSessionManager.getLanguage() == "English") {
                speakText(
                    "Your " + accountName + " Account Balance ending in*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*is*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakText(
                    "Su cuenta " + accountName + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*cuesta*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakText(
                    "Votre compte " + accountName + " Solde du compte se terminant par*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*est de*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakText(
                    "حسابك  " + accountName + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*يكون*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakText(
                    "Твой" + accountName + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*является*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakText(
                    "Sua conta" + accountName + "Saldo da conta terminando em*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*é*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakText(
                    "Uw" + accountName + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*bedraagt*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Hindi") {
                speakText(
                    "आपके" + accountName + "का बैलेंस*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*और*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Chinese") {
                speakText(
                    "你的" + accountName + "账户余额 以...结尾*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*是*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Japanese") {
                speakText(
                    "あなたの" + accountName + "口座残高 で終わる*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "* です *" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "German") {
                speakText(
                    "Dein" + accountName + "Kontostand endet mit*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "* ist *" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Italian") {
                speakText(
                    "Il tuo" + accountName + "Saldo del conto che termina con*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "* è *" + returnz
                )
            }
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "") {
            if (savedSessionManager.getLanguage() == "English") {
                speakText(
                    "Your " + accountName + " Account Balance ending in*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*is*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakText(
                    "Su cuenta " + accountName + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*cuesta*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakText(
                    "Votre compte " + accountName + " Solde du compte se terminant par*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*est de*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakText(
                    "حسابك  " + accountName + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*يكون*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakText(
                    "Твой" + accountName + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*является*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakText(
                    "Sua conta" + accountName + "Saldo da conta terminando em*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*é*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakText(
                    "Uw" + accountName + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*bedraagt*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Hindi") {
                speakText(
                    "आपके" + accountName + "का बैलेंस*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*और*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Chinese") {
                speakText(
                    "你的" + accountName + "账户余额 以...结尾*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*是*" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Japanese") {
                speakText(
                    "あなたの" + accountName + "口座残高 で終わる*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "* です *" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "German") {
                speakText(
                    "Dein" + accountName + "Kontostand endet mit*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "* ist *" + returnz
                )
            } else if (savedSessionManager.getLanguage() == "Italian") {
                speakText(
                    "Il tuo" + accountName + "Saldo del conto che termina con*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "* è *" + returnz
                )
            }
        }
    }

    fun showAnnouncementDialogExtra(list: AccountsData) {
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
                    speechToTextForDialogExtra(
                        list.accountName,
                        list,
                        util.getGenderDescription(Gender.MALE)
                    )
                }

                R.id.btn_femaleVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_femaleVoice)
                    speechToTextForDialogExtra(
                        list.accountName, list, util.getGenderDescription(Gender.FEMALE)
                    )
                }

                R.id.btn_otherVoice -> {
                    selection(btn_maleVoice, btn_femaleVoice, btn_otherVoice, btn_otherVoice)
                    speechToTextForDialogExtra(
                        list.accountName, list, util.getGenderDescription(Gender.OTHER)
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

    private fun voiceForAccountReadForExtra(accountName: String, list: AccountsData) {
        val spacedAccountCode = list.accountId.map { it.toString() }.joinToString(" ")
        var completeText = ""

        if (savedSessionManager.getLanguage() == "English") {
            completeText =
                "Your $accountName account balance ending in $spacedAccountCode is $${list.balance}"
        } else if (savedSessionManager.getLanguage() == "Spanish") {
            completeText =
                "Su cuenta  $accountName Saldo de cuenta que termina en $spacedAccountCode cuesta $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "French") {
            completeText =
                "Votre compte $accountName Solde du compte se terminant par $spacedAccountCode est de $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "Arabic") {
            completeText =
                " حسابك $accountName رصيد الحساب ينتهي بـ $spacedAccountCode يكون $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "Russia") {
            completeText =
                "Твой $accountName Баланс счета заканчивается на $spacedAccountCode является $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "Portuguese") {
            completeText =
                "Sua conta $accountName Saldo da conta terminando em $spacedAccountCode é $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "Dutch") {
            completeText =
                "Uw $accountName Accountsaldo eindigend op $spacedAccountCode bedraagt $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "Hindi") {
            completeText =
                "आपके $accountName खाते का शेष समाप्त हो रहा है $spacedAccountCode है $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "Chinese") {
            completeText =
                "你的 $accountName 账户余额 以...结尾 $spacedAccountCode 是 $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "Japanese") {
            completeText =
                "あなたの $accountName 口座残高 で終わる $spacedAccountCode です $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "German") {
            completeText =
                "Dein $accountName Kontostand endet mit $spacedAccountCode ist $${list.balance}"

        } else if (savedSessionManager.getLanguage() == "Italian") {
            completeText =
                "Il tuo $accountName Saldo del conto che termina con $spacedAccountCode è $${list.balance}"
        }


        val engine = pref.getString(SessionManager.ENGINEFORAPI)
        val voiceId = pref.getString(SessionManager.VOICEFORAPI)
        val langCode = pref.getString(SessionManager.LANGUAGECODEFORAPI)

        if (engine.isNotEmpty() && voiceId.isNotEmpty() && langCode.isNotEmpty()) {
            viewModel.postVoiceInLanguageList(engine, voiceId, langCode, completeText)
                .observe(this@TabTwoFragment) { its ->
                    val apiResponseData = its as PostVoiceResponse
                    if (apiResponseData.success) {
                        playAudio(apiResponseData.path)
                    } else dialogClass.showError("Something went wrong")
                }
        } else {
            val intent = Intent(activity, SoundActivity::class.java)
            startActivity(intent)
        }
    }

    private fun speechToTextForDialogExtra(accountName: String, list: AccountsData, type: String) {

        try {
            val amount = utils.removeCurrencySymbol(list.balance.toString())
            returnz = Constant.convertNumericToSpokenWords(
                amount, savedSessionManagerCurrency.getCurrency()
            )
        } catch (e: NumberFormatException) {
            //Toast.makeToast("illegal number or empty number" , toast.long)
        }

        if (type == "Male") {
            if (savedSessionManager.getLanguage() == "English") {
                speakTextDialog(
                    "Your " + accountName + " Account Balance ending in*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*is*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakTextDialog(
                    "Su cuenta " + accountName + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*cuesta*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakTextDialog(
                    "Votre compte " + accountName + " Solde du compte se terminant par*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*est de*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakTextDialog(
                    "حسابك  " + accountName + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*يكون*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakTextDialog(
                    "Твой" + accountName + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*является*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakTextDialog(
                    "Sua conta" + accountName + "Saldo da conta terminando em*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*é*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakTextDialog(
                    "Uw" + accountName + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*bedraagt*" + returnz, type
                )
            }
        } else if (type == "Female") {
            if (savedSessionManager.getLanguage() == "English") {
                speakTextDialog(
                    "Your " + accountName + " Account Balance ending in*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*is*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Spanish") {
                speakTextDialog(
                    "Su cuenta " + accountName + " Saldo de cuenta que termina en*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*cuesta*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "French") {
                speakTextDialog(
                    "Votre compte " + accountName + " Solde du compte se terminant par*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*est de*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Arabic") {
                speakTextDialog(
                    "حسابك  " + accountName + "رصيد الحساب ينتهي بـ* " + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*يكون*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Russia") {
                speakTextDialog(
                    "Твой" + accountName + "Баланс счета заканчивается на*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*является*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Portuguese") {
                speakTextDialog(
                    "Sua conta" + accountName + "Saldo da conta terminando em*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*é*" + returnz, type
                )
            } else if (savedSessionManager.getLanguage() == "Dutch") {
                speakTextDialog(
                    "Uw" + accountName + "Accountsaldo eindigend op*" + utils.numberToText(
                        list.accountNumber.toString()
                    ) + "*bedraagt*" + returnz, type
                )
            }
        }
    }

}