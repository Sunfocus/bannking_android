package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.bannking.app.R
import com.bannking.app.UiExtension.getDateMMMDDYYYY
import com.bannking.app.adapter.RecentTranSectionListAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityTransectionDetailBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.retrofitResponseModel.tranSectionListModel.TranSectionListModel
import com.bannking.app.model.retrofitResponseModel.typeTransactionModel.Data
import com.bannking.app.model.retrofitResponseModel.typeTransactionModel.TypeTransactionModel
import com.bannking.app.model.retrofitResponseModel.updateAccountModel.UpdateAccountModel
import com.bannking.app.model.viewModel.RecentTransactionViewModel
import com.bannking.app.uiUtil.CustomSpinnerAdapter
import com.bannking.app.utils.AdController
import com.bannking.app.utils.Constants
import com.bannking.app.utils.EasyMoneyEditText
import com.bannking.app.utils.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class TranSectionDetailActivity :
    BaseActivity<RecentTransactionViewModel, ActivityTransectionDetailBinding>(
        RecentTransactionViewModel::class.java
    ) {

    private var account: String? = null
    private var budget_id: String? = null
    private var strAmount: String? = null
    private var strAccountMenuId: String? = null
    private var Id: String? = null
    private var strAccountCode: String? = null
    private var adapter: RecentTranSectionListAdapter? = null
    private lateinit var savedAdTime: SessionManager
    //    private var bottomSheetDialog : BottomSheetDialog? = null
    lateinit var viewModel: RecentTransactionViewModel
    lateinit var list: ArrayList<com.bannking.app.model.retrofitResponseModel.tranSectionListModel.Data>
    private lateinit var spinnerList: ArrayList<Data>
    private lateinit var customSpinnerAdapter: CustomSpinnerAdapter
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0

    override fun getBinding(): ActivityTransectionDetailBinding {
        return ActivityTransectionDetailBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: RecentTransactionViewModel) {
        this.viewModel = viewModel
        viewModel.setDataInTypeTransactionListData()
    }

    @SuppressLint("SetTextI18n")
    override fun initialize() {
        list = arrayListOf()
        spinnerList = arrayListOf()
        customSpinnerAdapter = CustomSpinnerAdapter()
//        onCreatePaymentBottomShit()
        savedAdTime = SessionManager(this, SessionManager.ADTIME)
        val calendar: Calendar = Calendar.getInstance()
        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)


        /*Get Intent Here*/
        if (intent != null) {
            account = intent.extras!!.getString("account")  // Transection Name
            budget_id = intent.extras!!.getString("budget_id")  // Transection Name
            strAmount = intent.extras!!.getString("amount")  //amount
            strAccountMenuId = intent.extras!!.getString("accMenuId")  //acc_menu_id
            strAccountCode = intent.extras!!.getString("account_code")  // Account Code for print
            Id = intent.extras!!.getString("Id")  //Account Id

            binding!!.txtTransactionName.text =
                account.toString() + " ..." + strAccountCode.toString()
            binding!!.txtTransactionNameSmall.text =
                account.toString() + " ..." + strAccountCode.toString()
            binding!!.txtTransactionAmount.text = strAmount.toString()

            if (getAmount(strAmount.toString()).startsWith( "-")) {
                binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
            } else {
                binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
            }

            viewModel.setDataInRecentTransactionListData(Id!!)
        }

    }

    override fun setMethod() {
        setOnClickListener()
        setAdapter()
    }

    @SuppressLint("SetTextI18n")
    override fun observe() {
        with(viewModel) {
            recentTransactionListData.observe(this@TranSectionDetailActivity) { transectionList ->
                if (transectionList != null) {
                    if (transectionList.code in 199..299) {
                        if (transectionList.apiResponse != null) {
                            val mainModel = gson.fromJson(
                                transectionList.apiResponse,
                                TranSectionListModel::class.java
                            )
                            if (mainModel.data.size != 0) {
//                                val reverseList = utils.reverse(mainModel.data)
                                list.clear()
                                list = mainModel.data
                                setAdapter()
//                                adapter?.updateList(reverseList)
                            }
                        }
                    }
                }
            }

            typeTransactionListData.observe(this@TranSectionDetailActivity) { typeList ->
                if (typeList != null) {
                    if (typeList.code in 199..299) {
                        if (typeList.apiResponse != null) {
                            val mainModel = gson.fromJson(
                                typeList.apiResponse,
                                TypeTransactionModel::class.java
                            )
                            if (mainModel.data.size != 0) {
                                spinnerList = mainModel.data
                                customSpinnerAdapter.updateList(mainModel.data)
                            }
                        }
                    }
                }
            }

            createTransactionListData.observe(this@TranSectionDetailActivity) { transectionCreate ->
                if (transectionCreate != null) {
                    if (transectionCreate.code in 199..299) {
                        if (transectionCreate.apiResponse != null) {
                            val model = gson.fromJson(
                                transectionCreate.apiResponse,
                                CommonResponseApi::class.java
                            )
                            if (model.status.equals(Constants.STATUSSUCCESS)) {
                                viewModel.setDataInRecentTransactionListData(Id!!)
                                binding!!.txtTransactionAmount.text = model.amount

                                if (getAmount(model.amount.toString()).startsWith( "-")) {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                                } else {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                                }

                                strAmount = model.amount
                            } else if (model.status?.equals(Constants.STATUSFALSE) == true) {
                                dialogClass.showError(model.message.toString())
                            }
//                            Toast.makeText(
//                                this@TranSectionDetailActivity,
//                                "" + model.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }
                    }
                }
            }

            updateAccountListData.observe(this@TranSectionDetailActivity) { updateAccountListData ->
                if (updateAccountListData != null) {
                    if (updateAccountListData.code in 199..299) {
                        if (updateAccountListData.apiResponse != null) {
                            val model = gson.fromJson(
                                updateAccountListData.apiResponse,
                                UpdateAccountModel::class.java
                            )
                            if (model.status.equals(Constants.STATUSSUCCESS)) {
                                binding!!.txtTransactionName.text =
                                    "${model.account} ...${model.accountCode}"
                                binding!!.txtTransactionNameSmall.text =
                                    "${model.account} ...${model.accountCode}"
                                binding!!.txtTransactionAmount.text = "${model.amount}"

                                if (getAmount(model.amount.toString()).startsWith( "-")) {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                                } else {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                                }

                                strAccountCode = model.accountCode
                                account = model.account
                                strAmount = model.amount
//                                viewModel.setDataInRecentTransactionListData(Id!!)
                            }
//                            Toast.makeText(
//                                this@TranSectionDetailActivity,
//                                "" + model.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }
                    }
                }
            }

            progressObservable.observe(this@TranSectionDetailActivity) { isProgress ->
                if (isProgress != null) {
                    if (isProgress)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }
        }
    }

    private fun setAdapter() {
        adapter = RecentTranSectionListAdapter(this@TranSectionDetailActivity, list)
        binding!!.rvTransaction.adapter = adapter
        binding!!.rvTransaction.setHasFixedSize(true)
    }

    private fun setOnClickListener() {
        with(binding!!) {
            imgBack.setOnClickListener { finish() }
            txtPay.setOnClickListener {

                val intent = Intent(this@TranSectionDetailActivity, PaymentActivity::class.java)
                intent.putExtra("Id", Id.toString())
                intent.putExtra("TranSectionName", account.toString())
                intent.putExtra("TranSectionNumber", strAccountCode.toString())
                intent.putExtra("ComeFrom", "TranSectionDetailActivity")

                payrRsultLauncher.launch(intent)
            }

            txtTransfer.setOnClickListener {
                val intent =
                    Intent(this@TranSectionDetailActivity, ScheduleTransferActivity::class.java)
                intent.putExtra("Id", Id.toString())
                intent.putExtra("TranSectionName", account.toString())
                intent.putExtra("TranSectionNumber", strAccountCode.toString())
                intent.putExtra("ComeFrom", "TranSectionDetailActivity")
//                startActivity(intent)
                resultLauncher.launch(intent)

            }

            txtCreatePayment.setOnClickListener {
                onCreatePaymentBottomShit()
            }
            imgMore.setOnClickListener {
                editAccountDetailBottomShit()
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == 1112) {
                if (data?.hasExtra("CurrentAmount") == true) {
                    strAmount = data.extras!!.getString("CurrentAmount")
                    binding!!.txtTransactionAmount.text = strAmount

                    if (getAmount(strAmount.toString()).startsWith( "-")) {
                        binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                    } else {
                        binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                    }
                }

                viewModel.setDataInRecentTransactionListData(Id!!)
            }
        }

    private var payrRsultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == 1113) {
                if (data?.hasExtra("amount") == true) {
                    if (data.extras!!.getString("amount")?.isNotEmpty() == true) {
                        strAmount = data.extras!!.getString("amount")
                        binding!!.txtTransactionAmount.text = strAmount

                        if (getAmount(strAmount.toString()).startsWith( "-")) {
                            binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                        } else {
                            binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                        }
                    }
                }
                viewModel.setDataInRecentTransactionListData(Id!!)
            }
        }

    private fun editAccountDetailBottomShit() {
        val bottomSheetDialog =
            BottomSheetDialog(this@TranSectionDetailActivity, R.style.NoBackgroundDialogTheme)
        val view = LayoutInflater.from(this@TranSectionDetailActivity)
            .inflate(R.layout.bottomshit_budget_planner, findViewById(R.id.linearLayout))
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()

        val btnDone: Button = view.findViewById(R.id.btn_done)
        val chkAccount: CheckBox = view.findViewById(R.id.chk_account)
        val chkAccountCode: CheckBox = view.findViewById(R.id.chk_account_code)
        val chkAmount: CheckBox = view.findViewById(R.id.chk_amount)
        val edtAccount: EditText = view.findViewById(R.id.edt_account)
        val edtAccountCode: EditText = view.findViewById(R.id.edt_account_code)
        val edtAmount: EasyMoneyEditText = view.findViewById(R.id.edt_amount)
        val strdone: String = getString(R.string.str_done)

        btnDone.setText(strdone)
        edtAccount.setText(account.toString())
        edtAccountCode.setText(strAccountCode)
        edtAmount.setText(utils.removeCurrencySymbol(strAmount.toString()))

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
            if (timeDifference > fiveMinutesInMillis) {
                AdController.showInterAd(this@TranSectionDetailActivity, null, 0)
            }
            if (edtAccount.text.isNotEmpty()) {
                if (edtAccountCode.text.isNotEmpty()) {
                    if (edtAmount.valueString.isNotEmpty()) {
                        viewModel.setDataInUpdateAccountListData(
                            Id!!,
                            budget_id.toString(),
                            edtAccount.text.toString(),
                            edtAccountCode.text.toString(),
                            edtAmount.valueString,
                            strAccountMenuId.toString()
                        )
                        bottomSheetDialog.dismiss()
                    } else
                        edtAmount.error = resources.getString(R.string.str_enter_amount)
                } else
                    edtAccountCode.error =
                        resources.getString(R.string.str_please_enter_account_code)
            } else
                edtAccount.error = resources.getString(R.string.str_please_enter_account_name)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun onCreatePaymentBottomShit() {
        var selectedTransactionType = ""
        var selectedTransactionTypeId = ""
        val bottomSheetDialog =
            BottomSheetDialog(this@TranSectionDetailActivity, R.style.NoBackgroundDialogTheme)
        val view = LayoutInflater.from(this@TranSectionDetailActivity).inflate(
            R.layout.bottomshit_create_transaction_details,
            findViewById(R.id.linearLayout)
        )
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
        val btnDone: Button = view.findViewById(R.id.btn_done)
        val txtDatePicker: TextView = view.findViewById(R.id.txt_date_picker)
        val transitionTitle: EditText = view.findViewById(R.id.transition_title)
        val txtTransactionAmount: EasyMoneyEditText = view.findViewById(R.id.txt_transaction_amount)
        val spinnerTransactionType: Spinner = view.findViewById(R.id.spinner_transaction_type)

        txtDatePicker.text = utils.getCurrentDateForTransfer()
//        transitionTitle.setText(intent.extras!!.getString("account").toString())
        customSpinnerAdapter = CustomSpinnerAdapter(this@TranSectionDetailActivity, spinnerList)
        spinnerTransactionType.adapter = customSpinnerAdapter

        spinnerTransactionType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val transactionData: Data = adapterView?.selectedItem as Data
                    selectedTransactionType = transactionData.name.toString()
                    selectedTransactionTypeId = transactionData.id.toString()
                }

                override fun onNothingSelected(adapter: AdapterView<*>?) {}
            }

        txtDatePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth ->
                    val strNewDayOfMonth =
                        if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    val strNewMonthOfYear =
                        if ((monthOfYear + 1) < 10) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()
                    txtDatePicker.text= "$year-$strNewMonthOfYear-$strNewDayOfMonth".getDateMMMDDYYYY()
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }
        val adTime = savedAdTime.getLong(SessionManager.ADTIME)
        val fiveMinutesInMillis = 5 * 60 * 1000 // 5 minutes in milliseconds
        val currentTime = System.currentTimeMillis()
        // Calculate the time difference
        val timeDifference = currentTime - adTime

        btnDone.setOnClickListener {
            if (timeDifference > fiveMinutesInMillis) {
                AdController.showInterAd(this@TranSectionDetailActivity, null, 0)
            }
            if (selectedTransactionType
                    .isNotEmpty() && selectedTransactionTypeId.isNotEmpty()
            ) {
                if (txtTransactionAmount.valueString.isNotEmpty()) {
                    if (txtTransactionAmount.valueString.toDouble() > 0.0) {
                        viewModel.setDataInCreateTransactionListData(
                            selectedTransactionTypeId,
                            Id.toString(),
                            transitionTitle.text.toString(),
                            txtDatePicker.text.toString(),
                            txtTransactionAmount.valueString
                        )
                        bottomSheetDialog.dismiss()
                    } else
                        txtTransactionAmount.error =
                            resources.getString(R.string.str_please_enter_amount_grater_then_zero)
                } else
                    txtTransactionAmount.error =
                        resources.getString(R.string.str_transaction_amount_empty)
//                if (transitionTitle.text.toString().isNotEmpty()) {
//
//                } else
//                    transitionTitle.error = resources.getString(R.string.str_transaction_text_empty)
            } else
                dialogClass.showError(resources.getString(R.string.str_please_select_any_type))
//                Toast.makeText(this@TranSectionDetailActivity, , Toast.LENGTH_SHORT).show()

        }
    }

    override fun onResume() {
        super.onResume()

        Glide.with(this@TranSectionDetailActivity)
            .asBitmap()
            .load(userModel!!.image)
            .placeholder(R.drawable.glide_dot) //<== will simply not work:
            .error(R.drawable.glide_warning) // <== is also useless
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    binding!!.imgProfile.setImageBitmap(resource)
                }
            })
    }

    fun getAmount(amount : String) : String {
        return amount.replace("[^0-9.-]".toRegex(), "")
    }
}