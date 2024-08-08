package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.UiExtension.getDateMMMDDYYYY
import com.bannking.app.adapter.RecentTranSectionListAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityTransectionDetailBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.retrofitResponseModel.tranSectionListModel.TranSectionListModel
import com.bannking.app.model.retrofitResponseModel.typeTransactionModel.Data
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
import java.text.SimpleDateFormat
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
    private var icon: String? = null
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
    private lateinit var savedSessionManagerCurrency: SessionManager
    override fun getBinding(): ActivityTransectionDetailBinding {
        return ActivityTransectionDetailBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: RecentTransactionViewModel) {
        this.viewModel = viewModel
//        viewModel.setDataInTypeTransactionListData()
        savedSessionManagerCurrency =
            SessionManager(this@TranSectionDetailActivity, SessionManager.CURRENCY)
    }

    @SuppressLint("SetTextI18n")
    override fun initialize() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.rlMainTD.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding!!.txtTransactionName.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.txtTransactionNameSmall.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding!!.tvAvailTD.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.txtRecentTransaction.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding!!.rlMainTD.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.clr_tab_frag
                )
            )
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.black))
            binding!!.txtTransactionName.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.clr_text
                )
            )
            binding!!.txtTransactionNameSmall.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.clr_text
                )
            )
            binding!!.tvAvailTD.setTextColor(ContextCompat.getColor(this, R.color.clr_text))
            binding!!.txtRecentTransaction.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.clr_text
                )
            )

        }


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
            icon = intent.extras!!.getString("icon")  // currency icon

            binding!!.txtTransactionName.text =
                account.toString() + " ..." + strAccountCode.toString()
            binding!!.txtTransactionNameSmall.text =
                account.toString() + " ..." + strAccountCode.toString()
            binding!!.txtTransactionAmount.text = icon.toString() + strAmount.toString()

            if (getAmount(strAmount.toString()).startsWith("-")) {
                binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
            } else {
                binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
            }
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            viewModel.setDataInRecentTransactionListData(Id!!, userToken)
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
                                Log.d("sdfsdfdsfdsf", list.toString())
                                binding!!.txtTransactionAmount.text =
                                    icon.toString() + mainModel.extraData

                                if (getAmount(mainModel.extraData.toString()).startsWith("-")) {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                                } else {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                                }

                                strAmount = mainModel.data[0].totalAmount
//                                adapter?.updateList(reverseList)
                            }
                        }
                    }
                }
            }

            val spinnerLists = ArrayList<Data>()
            spinnerLists.add(Data("1", "Credit"))
            spinnerLists.add(Data("2", "Debit"))
            spinnerList = spinnerLists
            customSpinnerAdapter.updateList(spinnerLists)

            /*typeTransactionListData.observe(this@TranSectionDetailActivity) { typeList ->
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
            }*/

            createTransactionListData.observe(this@TranSectionDetailActivity) { transectionCreate ->
                if (transectionCreate != null) {
                    if (transectionCreate.code in 199..299) {
                        if (transectionCreate.apiResponse != null) {
                            val model = gson.fromJson(
                                transectionCreate.apiResponse,
                                CommonResponseApi::class.java
                            )
                            if (model.status == 200) {
                                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                                viewModel.setDataInRecentTransactionListData(Id!!, userToken)
                                Log.d("Asdkjsafhjkds", transectionCreate.toString())
                                binding!!.txtTransactionAmount.text = icon.toString() + model.amount

                                if (getAmount(model.amount.toString()).startsWith("-")) {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                                } else {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                                }

                                strAmount = model.amount
                            } else if (model.status == 200) {
                                dialogClass.showError(model.message.toString())
                            }
//                            Toast.makeText(
//                                this@TranSectionDetailActivity,
//                                "" + model.message,
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }
                    } else {
                        val model = gson.fromJson(
                            transectionCreate.apiResponse,
                            CommonResponseApi::class.java
                        )
                        dialogClass.showError(model.message.toString())
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
                            if (model.status == 200) {
                                binding!!.txtTransactionName.text =
                                    "${model.data!!.account} ...${model.data!!.account_code}"
                                binding!!.txtTransactionNameSmall.text =
                                    "${model.data!!.account} ...${model.data!!.account_code}"
                                binding!!.txtTransactionAmount.text = "$icon${model.data!!}"

                                if (getAmount(model.data!!.toString()).startsWith("-")) {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                                } else {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                                }

                                strAccountCode = model.data!!.account_code
                                account = model.data!!.account
                                strAmount = model.extraData!!
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
                    binding!!.txtTransactionAmount.text = icon.toString() + strAmount

                    if (getAmount(strAmount.toString()).startsWith("-")) {
                        binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                    } else {
                        binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                    }
                }
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                viewModel.setDataInRecentTransactionListData(Id!!, userToken)
            }
        }

    private var payrRsultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == 1113) {
                if (data?.hasExtra("amount") == true) {
                    if (data.extras!!.getString("amount")?.isNotEmpty() == true) {
                        strAmount = data.extras!!.getString("amount")
                        binding!!.txtTransactionAmount.text = icon.toString() + strAmount

                        if (getAmount(strAmount.toString()).startsWith("-")) {
                            binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                        } else {
                            binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                        }
                    }
                }
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                viewModel.setDataInRecentTransactionListData(Id!!, userToken)
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

        btnDone.text = strdone
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
                        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                        viewModel.setDataInUpdateAccountListData(
                            Id!!,
                            budget_id.toString(),
                            edtAccount.text.toString(),
                            edtAccountCode.text.toString(),
                            edtAmount.valueString,
                            strAccountMenuId.toString(), userToken
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
            findViewById(R.id.linearLayoutCT)
        )
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
        val btnDone: Button = view.findViewById(R.id.btn_done)
        val txtDatePicker: TextView = view.findViewById(R.id.txt_date_picker)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvTP: TextView = view.findViewById(R.id.tvTP)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tableCT: TableLayout = view.findViewById(R.id.tableCT)
        val transitionTitle: EditText = view.findViewById(R.id.transition_title)
        val txtTransactionAmount: EasyMoneyEditText = view.findViewById(R.id.txt_transaction_amount)
        val iconAmount: TextView = view.findViewById(R.id.iconAmount)
        val spinnerTransactionType: Spinner = view.findViewById(R.id.spinner_transaction_type)
        val currencyCode = savedSessionManagerCurrency.getCurrency()

        setCreateTranUiColor(
            view,
            txtDatePicker,
            tvDate,
            tvAmount,
            tvTP,
            tvTitle,
            tableCT,
            transitionTitle,
            txtTransactionAmount,
            iconAmount
        )


        Log.d("sdfshdjsdkfs", currencyCode.toString())
        when (currencyCode) {
            "1" -> {
                iconAmount.text = "$"
            }

            "2" -> {
            }

            "3" -> {
            }

            "4" -> {
            }

            "5" -> {
            }

            "6" -> {
            }
        }

        val currentDate = utils.getCurrentDateForTransfer()

        txtDatePicker.text = currentDate
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
                        val userToken = sessionManager.getString(SessionManager.USERTOKEN)

                        val format1 = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
                        val format2 = SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH)
                        // Parse the input date string
                        val date = format1.parse(txtDatePicker.text.toString())
                        // Format the parsed date to the desired format
                        val formattedDate = date?.let { format2.format(it) }

                        viewModel.setDataInCreateTransactionListData(
                            selectedTransactionTypeId,
                            Id.toString(),
                            transitionTitle.text.toString(),
                            formattedDate!!,
                            txtTransactionAmount.valueString, userToken
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

    private fun setCreateTranUiColor(
        view: View,
        txtDatePicker: TextView,
        tvDate: TextView,
        tvAmount: TextView,
        tvTP: TextView,
        tvTitle: TextView,
        tableCT: TableLayout,
        transitionTitle: EditText,
        txtTransactionAmount: EasyMoneyEditText,
        iconAmount: TextView
    ) {
        if (UiExtension.isDarkModeEnabled()) {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            tableCT.setBackgroundColor(Color.BLACK)
            tvTP.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
            transitionTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
            txtDatePicker.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.white))
            iconAmount.setTextColor(ContextCompat.getColor(this, R.color.white))
            txtTransactionAmount.setTextColor(ContextCompat.getColor(this, R.color.white))
            transitionTitle.setHintTextColor(ContextCompat.getColor(this, R.color.white))
            txtTransactionAmount.setHintTextColor(ContextCompat.getColor(this, R.color.white))
            txtDatePicker.setHintTextColor(ContextCompat.getColor(this, R.color.white))
            tvDate.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvTP.setBackgroundColor(Color.BLACK)
            tvDate.setBackgroundColor(Color.BLACK)
            tvTitle.setBackgroundColor(Color.BLACK)
            transitionTitle.setBackgroundColor(Color.BLACK)
            txtTransactionAmount.setBackgroundColor(Color.BLACK)
            tvAmount.setBackgroundColor(Color.BLACK)
            txtDatePicker.setBackgroundColor(Color.BLACK)
        } else {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            tableCT.setBackgroundColor(Color.WHITE)
            tvTP.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
            transitionTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
            txtDatePicker.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvAmount.setTextColor(ContextCompat.getColor(this, R.color.black))
            iconAmount.setTextColor(ContextCompat.getColor(this, R.color.black))
            txtTransactionAmount.setTextColor(ContextCompat.getColor(this, R.color.black))
            transitionTitle.setHintTextColor(ContextCompat.getColor(this, R.color.clr_dark_gray))
            txtTransactionAmount.setHintTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.clr_dark_gray
                )
            )
            txtDatePicker.setHintTextColor(ContextCompat.getColor(this, R.color.clr_dark_gray))
            tvDate.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvDate.setBackgroundColor(Color.WHITE)
            tvTP.setBackgroundColor(Color.WHITE)
            transitionTitle.setBackgroundColor(Color.WHITE)
            tvTitle.setBackgroundColor(Color.WHITE)
            tvAmount.setBackgroundColor(Color.WHITE)
            txtTransactionAmount.setBackgroundColor(Color.WHITE)
            txtDatePicker.setBackgroundColor(Color.WHITE)
        }
    }

    override fun onResume() {
        super.onResume()

        Glide.with(this@TranSectionDetailActivity)
            .asBitmap()
            .load(Constants.IMG_BASE_URL + userModel!!.image)
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

    fun getAmount(amount: String): String {
        return amount.replace("[^0-9.-]".toRegex(), "")
    }
}