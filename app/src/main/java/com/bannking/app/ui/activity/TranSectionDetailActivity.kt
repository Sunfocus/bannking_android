package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private var userAccountTitle: String? = null
    private var adapter: RecentTranSectionListAdapter? = null
    private lateinit var savedAdTime: SessionManager

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val debounceDelay: Long = 1200 // 1200 milliseconds delay

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
            binding!!.rlMainTD.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding!!.ivSort.setColorFilter(this.resources.getColor(R.color.white))
            binding!!.ivCross.setColorFilter(this.resources.getColor(R.color.white))

            binding!!.tvFilterAndDate.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvFilterDateShow.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.svTransactionManualApp.setBackgroundResource(R.drawable.drawable_selected_night)
            binding!!.txtRecentTransaction.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding!!.svTransactionManualApp.setBackgroundResource(R.drawable.bg_corner)
            binding!!.tvFilterAndDate.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvFilterDateShow.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.rlMainTD.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_tab_frag
                )
            )
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.black))
            binding!!.ivSort.setColorFilter(this.resources.getColor(R.color.black))
            binding!!.ivCross.setColorFilter(this.resources.getColor(R.color.black))

            binding!!.txtRecentTransaction.setTextColor(
                ContextCompat.getColor(
                    this, R.color.clr_text
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
            userAccountTitle = intent.extras!!.getString("userAccountTitle")  // userAccountTitle
            Id = intent.extras!!.getString("Id")  //Account Id
            icon = intent.extras!!.getString("icon")  // currency icon
            val amount = formatMoney(strAmount!!.toDouble())

            binding!!.txtTransactionNameSmall.text = "Total " + userAccountTitle.toString()
            binding!!.tvSccountTypeData.text = account.toString()
            binding!!.tvAccountNumberValue.text = "****$strAccountCode"

            binding!!.txtTransactionAmount.text = icon.toString() + amount.toString()

            if (getAmount(strAmount.toString()).startsWith("-")) {
                binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
            }/* else {
                binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
            }*/
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            viewModel.setDataInRecentTransactionListData(Id!!, userToken, "", "")
        }

    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun setMethod() {
        setOnClickListener()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun observe() {
        with(viewModel) {
            recentTransactionListData.observe(this@TranSectionDetailActivity) { transectionList ->
                if (transectionList != null) {
                    if (transectionList.code in 199..299) {
                        if (transectionList.apiResponse != null) {
                            val mainModel = gson.fromJson(
                                transectionList.apiResponse, TranSectionListModel::class.java
                            )
                            if (mainModel.data.size != 0) {
//                                val reverseList = utils.reverse(mainModel.data)
                                list.clear()
                                list = mainModel.data
                                setAdapter(mainModel.data)

                                val onlySpentData =
                                    mainModel.data.filter { it.type == "2" } as ArrayList
                                val totalSpentAmount =
                                    onlySpentData.sumOf { it.amount!!.toDouble() }

                                binding!!.tvSpent.text =
                                    "Spent(${getMonthNameFromDate(mainModel.data[0].transactionDate!!)})"

                                binding!!.tvSpentBalance.text =
                                    "${list[0].account_data!!.currency!!.icon}$totalSpentAmount"


                                val amount = formatMoney(mainModel.extraData!!.toDouble())
                                binding!!.txtTransactionAmount.text = icon.toString() + amount

                                if (getAmount(mainModel.extraData.toString()).startsWith("-")) {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                                } /*else {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                                }*/

                                strAmount = mainModel.data[0].totalAmount
//                                adapter?.updateList(reverseList)
                            } else {
                                val emptyList =
                                    ArrayList<com.bannking.app.model.retrofitResponseModel.tranSectionListModel.Data>()
                                setAdapter(emptyList)
                            }
                        } else {
                            val emptyList =
                                ArrayList<com.bannking.app.model.retrofitResponseModel.tranSectionListModel.Data>()
                            setAdapter(emptyList)
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
                                transectionCreate.apiResponse, CommonResponseApi::class.java
                            )
                            if (model.status == 200) {
                                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                                viewModel.setDataInRecentTransactionListData(
                                    Id!!, userToken, "", ""
                                )
//                                val amount = formatMoney(model.amount!!.toDouble())
//                                binding!!.txtTransactionAmount.text = icon.toString() + amount

                                if (getAmount(model.amount.toString()).startsWith("-")) {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                                } /*else {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                                }*/

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
                            transectionCreate.apiResponse, CommonResponseApi::class.java
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
                                updateAccountListData.apiResponse, UpdateAccountModel::class.java
                            )
                            if (model.status == 200) {
                                val amount = formatMoney(model.data!!.amount!!.toDouble())



                                binding!!.tvSccountTypeData.text = "${model.data!!.account}"

                                binding!!.tvAccountNumberValue.text =
                                    "****${model.data!!.account_code}"

                                binding!!.txtTransactionAmount.text = "$icon${amount}"

                                if (getAmount(model.data!!.toString()).startsWith("-")) {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                                } /*else {
                                    binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                                }*/

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
                    if (isProgress) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthNameFromDate(dateStr: String): String {
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.getDefault())
        val date = LocalDate.parse(dateStr, formatter)

        // Get the month name
        val monthName =
            date.month.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
        return monthName
    }

    private fun setAdapter(data: ArrayList<com.bannking.app.model.retrofitResponseModel.tranSectionListModel.Data>) {
        hideKeyboard()
        Log.e("hdsgfshdgfsd", data.toString())
        if (data.isNotEmpty()) {
            binding!!.tvTransactionApp.visibility = View.GONE
            binding!!.rvTransaction.visibility = View.VISIBLE
            adapter = RecentTranSectionListAdapter(this@TranSectionDetailActivity, data)
            binding!!.rvTransaction.adapter = adapter
            binding!!.rvTransaction.setHasFixedSize(true)
        } else {
            binding!!.tvTransactionApp.visibility = View.VISIBLE
            binding!!.rvTransaction.visibility = View.GONE
        }
    }


    private fun openDatePicker() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->

                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                setCurrentTime(calendar)

                val formattedDate = formatDateToISO8601(calendar.time)

                Log.e("formattedDate", formattedDate)

                binding!!.tvFilterAndDate.visibility = View.INVISIBLE
                binding!!.tvFilterDateShow.visibility = View.VISIBLE
                binding!!.ivSort.visibility = View.GONE
                binding!!.ivCross.visibility = View.VISIBLE

                binding!!.tvFilterDateShow.text = formattedDate


                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                viewModel.setDataInRecentTransactionListData(Id!!, userToken, "", formattedDate)

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun setCurrentTime(calendar: Calendar) {
        val currentTime = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, currentTime.get(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, currentTime.get(Calendar.MILLISECOND))
    }

    private fun formatDateToISO8601(date: Date): String {
        val iso8601Format = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        iso8601Format.timeZone = TimeZone.getTimeZone("UTC")
        return iso8601Format.format(date)
    }

    private fun setOnClickListener() {

        with(binding!!) {
            imgBack.setOnClickListener { finish() }

            ivSort.setOnClickListener {
                openDatePicker()
            }

            ivCross.setOnClickListener {
                binding!!.tvFilterAndDate.visibility = View.VISIBLE
                binding!!.tvFilterDateShow.visibility = View.GONE
                binding!!.ivSort.visibility = View.VISIBLE
                binding!!.ivCross.visibility = View.GONE
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                viewModel.setDataInRecentTransactionListData(Id!!, userToken, "", "")
            }

            svTransactionManualApp.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onQueryTextChange(newText: String?): Boolean {
                    searchRunnable?.let { handler.removeCallbacks(it) }

                    if (!newText.isNullOrEmpty()) {
                        searchRunnable = Runnable {
                            hideKeyboard()
                            hitApiForSearch(newText)

                        }
                        handler.postDelayed(searchRunnable!!, debounceDelay)
                    } else {
                        hitApiForSearch("")
                    }
                    return false
                }

            })

            LLPay.setOnClickListener {

                val intent = Intent(this@TranSectionDetailActivity, PaymentActivity::class.java)
                intent.putExtra("Id", Id.toString())
                intent.putExtra("TranSectionName", account.toString())
                intent.putExtra("TranSectionNumber", strAccountCode.toString())
                intent.putExtra("ComeFrom", "TranSectionDetailActivity")

                payrRsultLauncher.launch(intent)
            }

            LLTransfer.setOnClickListener {
                val intent =
                    Intent(this@TranSectionDetailActivity, ScheduleTransferActivity::class.java)
                intent.putExtra("Id", Id.toString())
                intent.putExtra("TranSectionName", account.toString())
                intent.putExtra("TranSectionNumber", strAccountCode.toString())
                intent.putExtra("ComeFrom", "TranSectionDetailActivity")
                resultLauncher.launch(intent)

            }

            LLCreate.setOnClickListener {
                onCreatePaymentBottomShit()
            }

            imgMore.setOnClickListener {
                editAccountDetailBottomShit()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun hitApiForSearch(newText: String) {
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.getBankTransactionFilterApi(
            Id!!, userToken, newText, ""
        ).observe(this@TranSectionDetailActivity) {
            val response = it as TranSectionListModel
            if (response.status == 200) {
                if (response.data.size != 0) {
                    list.clear()
                    list = response.data
                    setAdapter(response.data)

                    val onlySpentData = response.data.filter { it.type == "2" } as ArrayList
                    val totalSpentAmount = onlySpentData.sumOf { it.amount!!.toDouble() }

                    binding!!.tvSpent.text =
                        "Spent(${getMonthNameFromDate(response.data[0].transactionDate!!)})"

                    binding!!.tvSpentBalance.text =
                        "${response.data[0].account_data!!.currency!!.icon}$totalSpentAmount"

                    Log.d("sdfsdfdsfdsf", list.toString())
                    val amount = formatMoney(response.extraData!!.toDouble())
                    binding!!.txtTransactionAmount.text = icon.toString() + amount

                    if (getAmount(response.extraData.toString()).startsWith("-")) {
                        binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                    }
                    strAmount = response.data[0].totalAmount
                } else {
                    val emptyList =
                        ArrayList<com.bannking.app.model.retrofitResponseModel.tranSectionListModel.Data>()
                    setAdapter(emptyList)
                }
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == 1112) {
                if (data?.hasExtra("CurrentAmount") == true) {
                    strAmount = data.extras!!.getString("CurrentAmount")
                    val amount = formatMoney(strAmount!!.toDouble())
                    binding!!.txtTransactionAmount.text = icon.toString() + amount

                    if (getAmount(strAmount.toString()).startsWith("-")) {
                        binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                    } /*else {
                        binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                    }*/
                }
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                viewModel.setDataInRecentTransactionListData(Id!!, userToken, "", "")
            }
        }

    private fun formatMoney(value: Double): String {
        val decimalFormat = DecimalFormat("#,###.00#")
        return decimalFormat.format(value)
    }

    private var payrRsultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == 1113) {
                if (data?.hasExtra("amount") == true) {
                    if (data.extras!!.getString("amount")?.isNotEmpty() == true) {
                        strAmount = data.extras!!.getString("amount")
                        val amount = formatMoney(strAmount!!.toDouble())
                        binding!!.txtTransactionAmount.text = icon.toString() + amount

                        if (getAmount(strAmount.toString()).startsWith("-")) {
                            binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_red))
                        } /*else {
                            binding!!.txtTransactionAmount.setTextColor(resources.getColor(R.color.clr_blue))
                        }*/
                    }
                }
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                viewModel.setDataInRecentTransactionListData(Id!!, userToken, "", "")
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
        val edtAccount: EditText = view.findViewById(R.id.edt_account)
        val edtAccountCode: EditText = view.findViewById(R.id.edt_account_code)
        val edtAmount: EasyMoneyEditText = view.findViewById(R.id.edt_amount)
        val iconSubBudget: TextView = view.findViewById(R.id.iconSubBudget)
        val tvAmountSubBudget: TextView = view.findViewById(R.id.tvAmountSubBudget)
        val tvAccountCodeSUbBudeget: TextView = view.findViewById(R.id.tvAccountCodeSUbBudeget)
        val tvAccountSubBudeget: TextView = view.findViewById(R.id.tvAccountSubBudeget)
        val strdone: String = getString(R.string.str_done)

        setEditTranUiColor(
            view,
            iconSubBudget,
            edtAmount,
            tvAmountSubBudget,
            edtAccountCode,
            tvAccountCodeSUbBudeget,
            edtAccount,
            tvAccountSubBudeget
        )
        btnDone.text = strdone
        edtAccount.setText(account.toString())
        edtAccountCode.setText(strAccountCode)
        edtAmount.setText(utils.removeCurrencySymbol(strAmount.toString()))



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
                            strAccountMenuId.toString(),
                            userToken
                        )
                        bottomSheetDialog.dismiss()
                    } else edtAmount.error = resources.getString(R.string.str_enter_amount)
                } else edtAccountCode.error =
                    resources.getString(R.string.str_please_enter_account_code)
            } else edtAccount.error = resources.getString(R.string.str_please_enter_account_name)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun onCreatePaymentBottomShit() {
        var selectedTransactionType = ""
        var selectedTransactionTypeId = ""
        val bottomSheetDialog =
            BottomSheetDialog(this@TranSectionDetailActivity, R.style.NoBackgroundDialogTheme)
        val view = LayoutInflater.from(this@TranSectionDetailActivity).inflate(
            R.layout.bottomshit_create_transaction_details, findViewById(R.id.linearLayoutCT)
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
                iconAmount.text = "SAR"
            }

            "3" -> {
                iconAmount.text = "AED"
            }

            "4" -> {
                iconAmount.text = "QAR"
            }

            "5" -> {
                iconAmount.text = "€"
            }

            "6" -> {
                iconAmount.text = "£"
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
                    adapterView: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val transactionData: Data = adapterView?.selectedItem as Data
                    selectedTransactionType = transactionData.name.toString()
                    selectedTransactionTypeId = transactionData.id.toString()
                }

                override fun onNothingSelected(adapter: AdapterView<*>?) {}
            }

        txtDatePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, { _, year, monthOfYear, dayOfMonth ->
                    val strNewDayOfMonth =
                        if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    val strNewMonthOfYear =
                        if ((monthOfYear + 1) < 10) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()

                    txtDatePicker.text =
                        "$year-$strNewMonthOfYear-$strNewDayOfMonth".getDateMMMDDYYYY()

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
            if (selectedTransactionType.isNotEmpty() && selectedTransactionTypeId.isNotEmpty()) {
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
                            txtTransactionAmount.valueString,
                            userToken
                        )
                        bottomSheetDialog.dismiss()
                    } else txtTransactionAmount.error =
                        resources.getString(R.string.str_please_enter_amount_grater_then_zero)
                } else txtTransactionAmount.error =
                    resources.getString(R.string.str_transaction_amount_empty)
//                if (transitionTitle.text.toString().isNotEmpty()) {
//
//                } else
//                    transitionTitle.error = resources.getString(R.string.str_transaction_text_empty)
            } else dialogClass.showError(resources.getString(R.string.str_please_select_any_type))
//                Toast.makeText(this@TranSectionDetailActivity, , Toast.LENGTH_SHORT).show()

        }
    }

    private fun setEditTranUiColor(
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
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_mode)
            tableCT.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
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
            tvTP.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            tvDate.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            transitionTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            txtTransactionAmount.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            tvAmount.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            txtDatePicker.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
        } else {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            tableCT.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
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
                    this, R.color.clr_dark_gray
                )
            )
            txtDatePicker.setHintTextColor(ContextCompat.getColor(this, R.color.clr_dark_gray))
            tvDate.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvDate.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            tvTP.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            transitionTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            tvAmount.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            txtTransactionAmount.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            txtDatePicker.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    override fun onResume() {
        super.onResume()

        Glide.with(this@TranSectionDetailActivity).asBitmap()
            .load(Constants.IMG_BASE_URL + userModel!!.image)
            .placeholder(R.drawable.glide_dot) //<== will simply not work:
            .error(R.drawable.glide_warning) // <== is also useless
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap, transition: Transition<in Bitmap?>?
                ) {
                    binding!!.imgProfile.setImageBitmap(resource)
                }
            })
    }

    private fun getAmount(amount: String): String {
        return amount.replace("[^0-9.-]".toRegex(), "")
    }
}