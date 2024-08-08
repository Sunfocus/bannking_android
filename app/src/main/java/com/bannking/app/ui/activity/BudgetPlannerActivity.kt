package com.bannking.app.ui.activity

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.bannking.app.R
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.adapter.BudgetPlannerAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.databinding.ActivityBudgetPlannerBinding
import com.bannking.app.databinding.BottomshitCreateBudgetPlannerBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.BudgetPlannerModel
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.Data
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.SubBudgetPlanner
import com.bannking.app.model.viewModel.BudgetPlannerViewModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.AdController
import com.bannking.app.utils.Constants
import com.bannking.app.utils.EasyMoneyEditText
import com.bannking.app.utils.OnClickListener
import com.bannking.app.utils.OnClickListenerBudget
import com.bannking.app.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetPlannerActivity :
    BaseActivity<BudgetPlannerViewModel, ActivityBudgetPlannerBinding>(BudgetPlannerViewModel::class.java)/* AppCompatActivity(),*/,
    OnClickListenerBudget {


    private var bottomSheetDialog: BottomSheetDialog? = null
    lateinit var viewModel: BudgetPlannerViewModel
    private lateinit var budgetList: ArrayList<Data>
    private var selectedMenuID = ""
    private lateinit var savedAdTime: SessionManager

    override fun getBinding(): ActivityBudgetPlannerBinding {
        return ActivityBudgetPlannerBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: BudgetPlannerViewModel) {
        this.viewModel = viewModel
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        if (userToken != null) {
            viewModel.setDataInBudgetPlannerList(
                userModel?.id, intent.getStringExtra("SelectedItemMenu").toString(),userToken
            )
        }
    }

    override fun initialize() {

        if (intent != null) {
            selectedMenuID = intent.getStringExtra("SelectedMenu").toString()
//            Toast.makeText(this@BudgetPlannerActivity, "" + selectedMenuID, Toast.LENGTH_SHORT).show()
        }
        savedAdTime = SessionManager(this, SessionManager.ADTIME)
    }

    override fun setMethod() {
        setOnClickListener()
    }

    override fun observe() {
        with(viewModel) {
            budgetPlannerList.observe(this@BudgetPlannerActivity) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    if (it != null) {
                        if (it.code in 199..299) {
                            val model =
                                gson.fromJson(it.apiResponse, BudgetPlannerModel::class.java)
                            if (model.status == 200) {
                                binding!!.rvBudgetPlanner.apply {
                                    adapter = BudgetPlannerAdapter(
                                        this@BudgetPlannerActivity,
                                        model.data,
                                        this@BudgetPlannerActivity
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

            createAccountListData.observe(this@BudgetPlannerActivity) { createAccount ->
                if (createAccount != null) {
                    if (createAccount.code in 199..299) {
                        val mainModel =
                            gson.fromJson(createAccount.apiResponse, CommonResponseApi::class.java)
                        if (mainModel.status == 200) {
                            dialogClass.showAccountCreateSuccessfullyDialog(getString(R.string.str_account_create_successfully)) {

                                headerTitleList.value =
                                    CommonResponseModel(createAccount.apiResponse, createAccount.code)
//                                val data =
//                                    Intent(this@BudgetPlannerActivity, MainActivity::class.java)
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

            progressObservable.observe(this@BudgetPlannerActivity) {
                if (it != null) {
                    if (it)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }
        }
    }


    private fun setOnClickListener() {

        binding!!.imgFloating.setOnClickListener {
            openCreateBudgetPlannerBottomShit()
        }

        binding!!.txtUpgrade.setOnClickListener {
            finish()
            startActivity(Intent(this@BudgetPlannerActivity, UpgradeActivity::class.java))
        }

        binding!!.imgBack.setOnClickListener { finish() }
    }

    private fun openCreateBudgetPlannerBottomShit() {
        var colorCode = ""
        val bottomSheetDialog =
            BottomSheetDialog(this@BudgetPlannerActivity, R.style.NoBackgroundDialogTheme)
        val bottomBinding =
            BottomshitCreateBudgetPlannerBinding.inflate(LayoutInflater.from(this@BudgetPlannerActivity))
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomBinding.btnCancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomBinding.btnColorChoose.setOnClickListener {
            ColorPickerDialog.Builder(this)
                .setTitle(resources.getString(R.string.str_color_picker_dialog))
                .setPreferenceName(resources.getString(R.string.str_bannking_color_picker))
                .setPositiveButton(
                    resources.getString(R.string.str_confirm),
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
                    val call =
                        RetrofitClient.instance?.myApi?.createOwnBudgetPlanner(jsonObject.toString(),userToken!!)

                    call?.enqueue(object : Callback<JsonObject> {
                        override fun onResponse(
                            call: Call<JsonObject>,
                            response: Response<JsonObject>
                        ) {
                            dialogClass.hideLoadingDialog()
                            if (response.isSuccessful) {
                                if (response.code() in 199..299) {
                                    dialogClass.showSuccessfullyDialog(resources.getString(R.string.str_your_budget_title_created_successfully))
                                   /* val model = gson.fromJson(
                                        response.body(),
                                        BudgetPlannerModel::class.java
                                    )
                                    binding?.rvBudgetPlanner?.adapter = BudgetPlannerAdapter(
                                        this@BudgetPlannerActivity,
                                        model.data,
                                        this@BudgetPlannerActivity
                                    )*/

                                    if (userToken != null) {
                                        viewModel.setDataInBudgetPlannerList(
                                            userModel?.id, intent.getStringExtra("SelectedItemMenu").toString(),userToken
                                        )
                                    }

//                                    binding!!.rvBudgetPlanner.apply {
//                                        adapter = BudgetPlannerAdapter(
//                                            this@BudgetPlannerActivity,
//                                            model.data,
//                                            this@BudgetPlannerActivity
//                                        )
//                                    }

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


    //Adapter Click Listener
    override fun clickListerBudget(data: Data,subData: SubBudgetPlanner, clickedCreate: String) {
        bottomSheetDialog =
            BottomSheetDialog(this@BudgetPlannerActivity, R.style.NoBackgroundDialogTheme)
        val view = LayoutInflater.from(this@BudgetPlannerActivity)
            .inflate(R.layout.bottomshit_budget_planner, findViewById(R.id.linearLayout))
        bottomSheetDialog!!.setContentView(view)
        showCreateBottomShitDialog(view, data)
        bottomSheetDialog!!.show()

    }

    private fun showCreateBottomShitDialog(view: View, BudgetData: Data) {
        val btnDone: Button = view.findViewById(R.id.btn_done)
        val chkAccount: CheckBox = view.findViewById(R.id.chk_account)
        val chkAccountCode: CheckBox = view.findViewById(R.id.chk_account_code)
        val chkAmount: CheckBox = view.findViewById(R.id.chk_amount)
        val edtAccount: EditText = view.findViewById(R.id.edt_account)
        val edtAccountCode: EditText = view.findViewById(R.id.edt_account_code)
        val edtAmount: EasyMoneyEditText = view.findViewById(R.id.edt_amount)

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

        val adTime = savedAdTime.getLong(SessionManager.ADTIME)
        val fiveMinutesInMillis = 5 * 60 * 1000 // 5 minutes in milliseconds
        val currentTime = System.currentTimeMillis()
        // Calculate the time difference
        val timeDifference = currentTime - adTime

        btnDone.setOnClickListener {
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            if (timeDifference > fiveMinutesInMillis) {
                AdController.showInterAd(this@BudgetPlannerActivity, null, 0) {
                    if (edtAccount.text.toString().isNotEmpty()) {
                        if (edtAccountCode.text.toString().isNotEmpty()) {
                            if (edtAmount.valueString.toString().isNotEmpty()) {
                                if (userToken != null) {
                                    viewModel.setDataInCreateAccountListData(
                                        selectedMenuID,
                                        BudgetData.id!!,
                                        edtAccount.text.toString(),
                                        edtAccountCode.text.toString(),
                                        edtAmount.valueString.toString(),userToken
                                    )
                                }
                                bottomSheetDialog!!.dismiss()
                            } else

                                edtAmount.error = resources.getString(R.string.str_please_enter_amount)
                        } else

                            edtAccountCode.error =
                                resources.getString(R.string.str_please_enter_account_code)
                    } else

                        edtAccount.error = resources.getString(R.string.str_please_enter_account_name)
                }
            }else{
                if (edtAccount.text.toString().isNotEmpty()) {
                    if (edtAccountCode.text.toString().isNotEmpty()) {
                        if (edtAmount.valueString.toString().isNotEmpty()) {
                            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                            if (userToken != null) {
                                viewModel.setDataInCreateAccountListData(
                                    selectedMenuID,
                                    BudgetData.id!!,
                                    edtAccount.text.toString(),
                                    edtAccountCode.text.toString(),
                                    edtAmount.valueString.toString(),userToken
                                )
                            }
                            bottomSheetDialog!!.dismiss()
                        } else

                            edtAmount.error = resources.getString(R.string.str_please_enter_amount)
                    } else

                        edtAccountCode.error =
                            resources.getString(R.string.str_please_enter_account_code)
                } else

                    edtAccount.error = resources.getString(R.string.str_please_enter_account_name)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.txtUpgrade.isVisible = !isPremium
    }

}