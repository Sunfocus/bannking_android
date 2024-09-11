package com.bannking.app.ui.activity

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.adapter.BudgetPlannerNewAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.databinding.ActivityBudgetPlannerBinding
import com.bannking.app.databinding.BottomshitCreateBudgetPlannerBinding
import com.bannking.app.databinding.SubBudgetBottomsheetBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.ErrorResponse
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.BudgetPlannerModel
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.Data
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.SubBudgetPlanner
import com.bannking.app.model.viewModel.BudgetPlannerViewModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.AdController
import com.bannking.app.utils.Constants
import com.bannking.app.utils.EasyMoneyEditText
import com.bannking.app.utils.OnClickListenerBudget
import com.bannking.app.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetPlannerNewActivity :
    BaseActivity<BudgetPlannerViewModel, ActivityBudgetPlannerBinding>(BudgetPlannerViewModel::class.java)/* AppCompatActivity(),*/,
    OnClickListenerBudget {


    private lateinit var intentSendOnDone: Intent
    private var bottomSheetDialog: BottomSheetDialog? = null
    lateinit var viewModel: BudgetPlannerViewModel
    private lateinit var budgetList: ArrayList<Data>
    private var position = ""
    private lateinit var savedSessionManagerCurrency: SessionManager

    override fun getBinding(): ActivityBudgetPlannerBinding {
        return ActivityBudgetPlannerBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: BudgetPlannerViewModel) {
        this.viewModel = viewModel
        savedSessionManagerCurrency =
            SessionManager(this@BudgetPlannerNewActivity, SessionManager.CURRENCY)
    }

    private fun uiChangeColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.rlBuddget.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.white))

        } else {
            binding!!.rlBuddget.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_card_background
                )
            )
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.black))
        }
    }

    override fun initialize() {
        uiChangeColor()
        if (intent != null) {
            position = intent.getStringExtra("position").toString()
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            if (userToken != null) {
                viewModel.setDataInBudgetPlannerList(
                    userModel?.id, intent.getStringExtra("SelectedItemMenu").toString(), userToken
                )
            }
        }
    }

    override fun setMethod() {
        setOnClickListener()
    }

    override fun observe() {
        with(viewModel) {
            budgetPlannerList.observe(this@BudgetPlannerNewActivity) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    if (it != null) {
                        if (it.code in 199..299) {
                            val model =
                                gson.fromJson(it.apiResponse, BudgetPlannerModel::class.java)
                            if (model.status == 200) {
                                binding!!.rvBudgetPlanner.apply {
                                    adapter = BudgetPlannerNewAdapter(
                                        this@BudgetPlannerNewActivity,
                                        model.data,
                                        this@BudgetPlannerNewActivity
                                    )
                                }

                            } else {
                                dialogClass.showServerErrorDialog()
                            }
                        } else if (it.code in 400..500) {
                            dialogClass.showServerErrorDialog()
                        }
                    }
                }
            }

            createAccountListData.observe(this@BudgetPlannerNewActivity) { createAccount ->
                if (createAccount != null) {
                    if (createAccount.code in 199..299) {
                        val mainModel =
                            gson.fromJson(createAccount.apiResponse, CommonResponseApi::class.java)
                        if (mainModel.status == 200) {
                            dialogClass.showAccountCreateSuccessfullyDialog(getString(R.string.str_account_create_successfully)) {
                                headerTitleList.value = CommonResponseModel(
                                    createAccount.apiResponse, createAccount.code
                                )
                                val intent = Intent()
                                intent.putExtra("MainToMenu", "True")
                                setResult(RESULT_OK, intent)
                                finish()
                            }
                        } else if (mainModel.message!!.contains("unlock premium")) {
                            dialogClass.showAccountNotSubscriptionDialog(mainModel.message.toString())
                        } else {
                            dialogClass.showServerErrorDialog()
                        }
                    } else if (createAccount.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }

            progressObservable.observe(this@BudgetPlannerNewActivity) {
                if (it != null) {
                    if (it) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
                }
            }
        }
    }


    private fun setOnClickListener() {
        binding!!.imgFloating.setOnClickListener {
            openCreateBudgetPlannerBottomShit()
        }

        binding!!.txtUpgrade.setOnClickListener {
            if (::intentSendOnDone.isInitialized) {
                setResult(RESULT_OK, intentSendOnDone)
                finish()
            } else {
                Toast.makeText(this, "Create your Budget Planner!", Toast.LENGTH_SHORT).show()
            }
        }

        binding!!.imgBack.setOnClickListener { finish() }
    }

    private fun openCreateBudgetPlannerBottomShit() {
        var colorCode = ""
        val bottomSheetDialog =
            BottomSheetDialog(this@BudgetPlannerNewActivity, R.style.NoBackgroundDialogTheme)
        val bottomBinding =
            BottomshitCreateBudgetPlannerBinding.inflate(LayoutInflater.from(this@BudgetPlannerNewActivity))
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        if (UiExtension.isDarkModeEnabled()) {
            bottomBinding.linearLayout.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.dark_mode)
            bottomBinding.edtBudgetTitle.setHintTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            bottomBinding.edtBudgetTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            bottomBinding.linearLayout.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.white)
            bottomBinding.edtBudgetTitle.setHintTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.grey
                )
            )
            bottomBinding.edtBudgetTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        bottomBinding.linearLayout

        bottomBinding.btnColorChoose.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle(resources.getString(R.string.str_color_picker_dialog))
                .setPreferenceName(resources.getString(R.string.str_bannking_color_picker))
                .setPositiveButton(resources.getString(R.string.str_confirm),
                    ColorEnvelopeListener { envelope, fromUser ->
                        colorCode = "#${envelope.hexCode}"
                        bottomBinding.alphaTileView.setPaintColor(envelope.color)
                    })
                .setNegativeButton(resources.getString(R.string.str_cancel)) { dialogInterface, i -> dialogInterface.dismiss() }
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true) // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show()
        }

        bottomBinding.btnSubmit.setOnClickListener {
            if (bottomBinding.edtBudgetTitle.text.toString().toString().isNotEmpty()) {
                if (colorCode.isNotEmpty()) {
                    dialogClass.showLoadingDialog()
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("security", Constants.SECURITY_0)
                    jsonObject.addProperty("token", FCM_TOKEN)
                    jsonObject.addProperty("id", userModel?.id.toString())
                    jsonObject.addProperty("name", bottomBinding.edtBudgetTitle.text.toString())
                    jsonObject.addProperty("color", colorCode.toString())
                    val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                    val call = RetrofitClient.instance?.myApi?.createOwnBudgetPlanner(
                        jsonObject.toString(), userToken!!
                    )

                    call?.enqueue(object : Callback<JsonObject> {
                        override fun onResponse(
                            call: Call<JsonObject>, response: Response<JsonObject>
                        ) {
                            dialogClass.hideLoadingDialog()
                            if (response.isSuccessful) {
                                if (response.code() in 199..299) {
                                    dialogClass.showSuccessfullyDialog(resources.getString(R.string.str_your_budget_title_created_successfully))/* val model = gson.fromJson(
                                        response.body(),
                                        BudgetPlannerModel::class.java
                                    )
                                    binding?.rvBudgetPlanner?.adapter = BudgetPlannerAdapter(
                                        this@BudgetPlannerNewActivity,
                                        model.data,
                                        this@BudgetPlannerNewActivity
                                    )*/
                                    if (intent != null) {
                                        position = intent.getStringExtra("position").toString()
                                        viewModel.setDataInBudgetPlannerList(
                                            userModel?.id,
                                            intent.getStringExtra("SelectedItemMenu").toString(),
                                            userToken!!
                                        )
                                    }
                                } else

                                    dialogClass.showError(resources.getString(R.string.str_your_budget_title_created_unsuccessfully))
                            }
                            bottomSheetDialog.dismiss()
                        }

                        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                            dialogClass.showServerErrorDialog()
                            dialogClass.hideLoadingDialog()
                            call.cancel()

                        }

                    })

                } else

                    dialogClass.showError(resources.getString(R.string.str_please_choose_any_color))
            } else

                bottomBinding.edtBudgetTitle.error =
                    resources.getString(R.string.str_please_enter_budget_name)

        }

        bottomSheetDialog.show()

    }

    private fun openCreateSubBudgetPlannerBottomShit(id: String) {
        var colorCode = ""
        val bottomSheetDialog =
            BottomSheetDialog(this@BudgetPlannerNewActivity, R.style.NoBackgroundDialogTheme)
        val bottomBinding =
            SubBudgetBottomsheetBinding.inflate(LayoutInflater.from(this@BudgetPlannerNewActivity))
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.btnCancelSubBudget.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        if (UiExtension.isDarkModeEnabled()) {
            bottomBinding.linearLayout.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.dark_mode)
            bottomBinding.etSubBudgetTitle.setHintTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            bottomBinding.etSubBudgetTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            bottomBinding.linearLayout.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.white)
            bottomBinding.etSubBudgetTitle.setHintTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.grey
                )
            )
            bottomBinding.etSubBudgetTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        bottomBinding.btnSubmitSubBudget.setOnClickListener {
            if (bottomBinding.etSubBudgetTitle.text.toString().toString().isNotEmpty()) {
                dialogClass.showLoadingDialog()
                val jsonObject = JsonObject()

                jsonObject.addProperty("name", bottomBinding.etSubBudgetTitle.text.toString())
                jsonObject.addProperty("budget_id", id)
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                val call = RetrofitClient.instance?.myApi?.createSubBudgetAccount(
                    jsonObject.toString(), userToken!!
                )

                call?.enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>, response: Response<JsonObject>
                    ) {
                        dialogClass.hideLoadingDialog()
                        if (response.isSuccessful) {
                            if (response.code() in 199..299) {
                                dialogClass.showSuccessfullyDialog(resources.getString(R.string.str_your_budget_title_created_successfully))
                                if (intent != null) {
                                    position = intent.getStringExtra("position").toString()
                                    viewModel.setDataInBudgetPlannerList(
                                        userModel?.id,
                                        intent.getStringExtra("SelectedItemMenu").toString(),
                                        userToken!!
                                    )
                                }
                            } else dialogClass.showError(resources.getString(R.string.str_your_sub_budget_title_created_unsuccessfully))
                        }
                        bottomSheetDialog.dismiss()
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        dialogClass.showServerErrorDialog()
                        dialogClass.hideLoadingDialog()
                        call.cancel()

                    }

                })

            } else bottomBinding.etSubBudgetTitle.error =
                resources.getString(R.string.str_please_enter_sub_budget_name)

        }

        bottomSheetDialog.show()

    }


    //Adapter Click Listener
    override fun clickListerBudget(data: Data, subData: SubBudgetPlanner, clickedCreate: String) {
        if (clickedCreate != "") {
            openCreateSubBudgetPlannerBottomShit(data.id!!)
        } else {
            if (subData.isSubBudgetSelected == 1) {
                dialogClass.showError(getString(R.string.str_budget_planner_used))
            } else {
                bottomSheetDialog = BottomSheetDialog(
                    this@BudgetPlannerNewActivity, R.style.NoBackgroundDialogTheme
                )
                val view = LayoutInflater.from(this@BudgetPlannerNewActivity)
                    .inflate(R.layout.bottomshit_budget_planner, findViewById(R.id.linearLayout))
                bottomSheetDialog!!.setContentView(view)
                showCreateBottomShitDialog(view, subData, data)
                bottomSheetDialog!!.show()
            }

        }

    }

    private fun setCreateTranUiColor(
        view: View,
        iconAmount: TextView,
        edtAmount: EasyMoneyEditText,
        tvAmountSubBudget: TextView,
        edtAccountCode: EditText,
        tvAccountCodeSUbBudeget: TextView,
        edtAccount: EditText,
        tvAccountSubBudeget: TextView,

        ) {
        if (UiExtension.isDarkModeEnabled()) {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_mode)
            iconAmount.setTextColor(ContextCompat.getColor(this, R.color.white))
            edtAmount.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvAmountSubBudget.setTextColor(ContextCompat.getColor(this, R.color.white))
            edtAccountCode.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvAccountCodeSUbBudeget.setTextColor(ContextCompat.getColor(this, R.color.white))
            edtAccount.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvAccountSubBudeget.setTextColor(ContextCompat.getColor(this, R.color.white))
            edtAmount.setHintTextColor(ContextCompat.getColor(this, R.color.white))
            edtAccountCode.setHintTextColor(ContextCompat.getColor(this, R.color.white))
            edtAccount.setHintTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            iconAmount.setTextColor(ContextCompat.getColor(this, R.color.black))
            edtAmount.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvAmountSubBudget.setTextColor(ContextCompat.getColor(this, R.color.black))
            edtAccountCode.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvAccountCodeSUbBudeget.setTextColor(ContextCompat.getColor(this, R.color.black))
            edtAccount.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvAccountSubBudeget.setTextColor(ContextCompat.getColor(this, R.color.black))
            edtAmount.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
            edtAccountCode.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
            edtAccount.setHintTextColor(ContextCompat.getColor(this, R.color.grey))

        }
    }

    private fun showCreateBottomShitDialog(view: View, BudgetData: SubBudgetPlanner, data: Data) {
        val btnDone: Button = view.findViewById(R.id.btn_done)
        val chkAccount: CheckBox = view.findViewById(R.id.chk_account)
        val chkAccountCode: CheckBox = view.findViewById(R.id.chk_account_code)
        val chkAmount: CheckBox = view.findViewById(R.id.chk_amount)
        val edtAccount: EditText = view.findViewById(R.id.edt_account)
        val edtAccountCode: EditText = view.findViewById(R.id.edt_account_code)
        val edtAmount: EasyMoneyEditText = view.findViewById(R.id.edt_amount)
        val iconSubBudget: TextView = view.findViewById(R.id.iconSubBudget)
        val tvAmountSubBudget: TextView = view.findViewById(R.id.tvAmountSubBudget)
        val tvAccountCodeSUbBudeget: TextView = view.findViewById(R.id.tvAccountCodeSUbBudeget)
        val tvAccountSubBudeget: TextView = view.findViewById(R.id.tvAccountSubBudeget)


        val currencyCode = savedSessionManagerCurrency.getCurrency()
        when (currencyCode) {
            "1" -> {
                iconSubBudget.text = "$"
            }

            "2" -> {
                iconSubBudget.text = "SAR"
            }

            "3" -> {
                iconSubBudget.text = "AED"
            }

            "4" -> {
                iconSubBudget.text = "QAR"
            }

            "5" -> {
                iconSubBudget.text = "€"
            }

            "6" -> {
                iconSubBudget.text = "£"
            }
        }

        setCreateTranUiColor(
            view,
            iconSubBudget,
            edtAmount,
            tvAmountSubBudget,
            edtAccountCode,
            tvAccountCodeSUbBudeget,
            edtAccount,
            tvAccountSubBudeget
        )
        edtAccount.setText(BudgetData.name.toString())
        edtAccountCode.setText("")


        chkAmount.setOnCheckedChangeListener { _, isChecked ->
            edtAmount.isEnabled = isChecked
        }

        chkAccountCode.setOnCheckedChangeListener { _, isChecked ->
            edtAccountCode.isEnabled = isChecked
        }
        chkAccount.setOnCheckedChangeListener { _, isChecked ->
            edtAccount.isEnabled = isChecked
        }


        btnDone.setOnClickListener {


            if (edtAccount.text.toString().isNotEmpty()) {
                if (edtAccountCode.text.toString().isNotEmpty()) {
                    if (edtAmount.valueString.toString().isNotEmpty()) {
                        if (edtAccountCode.text.length == 4) {
                            setDataInCreateAccountListData(
                                budgetData = data,
                                subBudgetID = BudgetData.id!!,
                                budgetId = BudgetData.budget_id.toString(),
                                accountCode = edtAccountCode.text.toString(),
                                amount = edtAmount.text.toString().replace(",",""),
                                accountName = edtAccount.text.toString(),
                                accTitleId = intent.getStringExtra("SelectedItemMenu").toString()
                            )
                            bottomSheetDialog!!.dismiss()
                        } else edtAccountCode.error = getString(R.string.str_accoun_code)
//                            val intentSend = Intent()
//                            intentSend.putExtra("BudgetPlannerData", BudgetData)
//                            intentSend.putExtra(
//                                "position", intent.getStringExtra("position").toString()
//                            )
//                            intentSend.putExtra("edtAccountName", edtAccount.text.toString())
//                            intentSend.putExtra("edtAccountCode", edtAccountCode.text.toString())
//                            intentSend.putExtra("edtAccountAmount", edtAmount.valueString.toString())
//                            setResult(RESULT_OK, intentSend)
//                            finish()


                    } else

                        edtAmount.error = resources.getString(R.string.str_please_enter_amount)
                } else

                    edtAccountCode.error =
                        resources.getString(R.string.str_please_enter_account_code)
            } else

                edtAccount.error = resources.getString(R.string.str_please_enter_account_name)

        }
    }

    private fun setDataInCreateAccountListData(
        subBudgetID: Int,
        budgetId: String,
        accountCode: String,
        amount: String,
        accountName: String,
        accTitleId: String,
        callback: (() -> Unit)? = null,
        budgetData: Data
    ) {
        FCM_TOKEN.let {
            viewModel.progressObservable.value = true
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("budget_id", budgetId)
                apiBody.addProperty("sub_budget_id", subBudgetID)
                apiBody.addProperty("account_code", accountCode)
                apiBody.addProperty("amount", amount)
                apiBody.addProperty("acc_title_id", accTitleId)
                apiBody.addProperty("account", accountName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            val call =
                RetrofitClient.instance!!.myApi.createAccount(apiBody.toString(), userToken!!)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    viewModel.progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            val mainModel =
                                gson.fromJson(response.body(), CommonResponseApi::class.java)
                            if (mainModel.status == 200) {
                                viewModel.headerTitleList.value =
                                    CommonResponseModel(response.body(), response.code())
                                dialogClass.showAccountCreateSuccessfullyDialog(getString(R.string.str_account_create_successfully)) {
                                    AdController.showInterAd(
                                        this@BudgetPlannerNewActivity, null, 0
                                    ) {
                                        viewModel.headerTitleList.value =
                                            CommonResponseModel(response.body(), 200)
                                        intentSendOnDone = Intent()
                                        intentSendOnDone.putExtra("BudgetPlannerData", budgetData)
                                        intentSendOnDone.putExtra(
                                            "position", intent.getStringExtra("position").toString()
                                        )
                                        intentSendOnDone.putExtra("edtAccountName", accountName)
                                        intentSendOnDone.putExtra("edtAccountCode", accountCode)
                                        intentSendOnDone.putExtra("edtAccountAmount", amount)

                                        //Amit
//                                        setResult(RESULT_OK, intentSendOnDone)
//                                        finish()
//

                                        viewModel.setDataInBudgetPlannerList(
                                            userModel?.id,
                                            intent.getStringExtra("SelectedItemMenu").toString(),
                                            userToken
                                        )

                                    }
//                                    savedHeaderlist[postion].isAccountCreated = true
//                                    adapterNew?.notifyItemChanged(postion)
                                }

                            } else if (mainModel.message!!.contains("unlock premium")) {
                                dialogClass.showAccountNotSubscriptionDialog(mainModel.message.toString())
                            } else {
                                dialogClass.showServerErrorDialog()
                            }
                        }
                    } else {
                        val errorBodyString = response.errorBody()?.string()
                        val errorModel = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                        if (errorModel.message.contains("Please upgrade to unlock premium features..")){
                            dialogClass.showAccountNotSubscriptionDialog("Upgrade now to  unlock unlimited access to hidden features and enjoy premium benefits")
                        }else dialogClass.showErrorMessageDialog(errorModel.message)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    viewModel.progressObservable.value = false
                    dialogClass.showServerErrorDialog()
                }
            })
        }
    }

}