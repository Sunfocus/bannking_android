package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.UiExtension.getDateMMMDDYYYY
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityPaymentBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.PayResponseApi
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountListModel
import com.bannking.app.model.retrofitResponseModel.accountListModel.Data
import com.bannking.app.model.viewModel.PayAndTransferViewModel
import com.bannking.app.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity :
    BaseActivity<PayAndTransferViewModel, ActivityPaymentBinding>(PayAndTransferViewModel::class.java) {
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0

    private var datePickerDialog: DatePickerDialog? = null

    lateinit var viewModel: PayAndTransferViewModel
    lateinit var accountFromId: String
    private lateinit var strTranSectionName: String
    private lateinit var strTranSectionNumber: String
    private lateinit var spinnerList: ArrayList<String>
    lateinit var accountDataList: ArrayList<Data>
    private lateinit var savedSessionManagerCurrency: SessionManager

    var spinner1AccountNamr = ""
    var accountToName = ""
    var accountToCode = ""
    var accountToId = ""


    private var checkComingFromAct = ""

//    val tranSaferFromArray : ArrayList<String> = arrayListOf()

    override fun getBinding(): ActivityPaymentBinding {
        return ActivityPaymentBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: PayAndTransferViewModel) {
        this.viewModel = viewModel
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        if (userToken != null) {
            viewModel.setDataInAccountList(userToken)
        }

        savedSessionManagerCurrency =
            SessionManager(this@PaymentActivity, SessionManager.CURRENCY)
    }
    private fun setUIColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.rlPay.backgroundTintList =
                ContextCompat.getColorStateList(
                    this,
                    R.color.dark_mode
                )
            binding!!.tvTF.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.txtPaymentDate.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.txtTransferFrom.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding!!.tvTT.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvTN.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding!!.rlPay.backgroundTintList =
                ContextCompat.getColorStateList(
                    this,
                    R.color.white
                )
            binding!!.tvTF.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.grey
                )
            )
            binding!!.txtTransferFrom.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            binding!!.tvTT.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.txtPaymentDate.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvTN.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.grey
                )
            )

        }
    }
    override fun initialize() {
        setUIColor()
        if (intent != null) {
            accountFromId = intent.getStringExtra("Id").toString()
            strTranSectionName = intent.getStringExtra("TranSectionName").toString()
            strTranSectionNumber = intent.getStringExtra("TranSectionNumber").toString()
        }
        checkComingFromAct = if (intent.getStringExtra("ComeFrom") != null) {
            intent.getStringExtra("ComeFrom").toString()
        } else {
            "Other"
        }
        if (checkComingFromAct == "TranSectionDetailActivity") {
            binding!!.txtTransferFrom.isVisible = true
            binding!!.spinTransferFrom.isVisible = false
            binding!!.txtTransferFrom.text = "$strTranSectionName ...$strTranSectionNumber"
            spinner1AccountNamr = strTranSectionName
        } else if (checkComingFromAct == "Other") {
            binding!!.txtTransferFrom.isVisible = false
            binding!!.spinTransferFrom.isVisible = true
        }
        spinnerList = arrayListOf()
        accountDataList = arrayListOf()

        val c = Calendar.getInstance()
        mYear = c[Calendar.YEAR] // current year
        mMonth = c[Calendar.MONTH] // current month
        mDay = c[Calendar.DAY_OF_MONTH] // current day

        val currencyCode = savedSessionManagerCurrency.getCurrency()

        when (currencyCode) {
            "1" -> {
                binding!!.tvIconAmount.text = "$"
            }

            "2" -> {
                binding!!.tvIconAmount.text = "SAR"
            }

            "3" -> {
                binding!!.tvIconAmount.text = "AED"
            }

            "4" -> {
                binding!!.tvIconAmount.text = "QAR"
            }

            "5" -> {
                binding!!.tvIconAmount.text = "€"
            }

            "6" -> {
                binding!!.tvIconAmount.text = "£"
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setMethod() {
        setOnClickListener()
//        tranSaferFromArray.add(strTranSectionName)
//        binding!!.spinTransferFrom.adapter = ArrayAdapter(this@PaymentActivity, R.layout.simple_spinner_item, tranSaferFromArray)
    }

    override fun observe() {
        with(viewModel) {
            accountListData.observe(this@PaymentActivity) { accountList ->
                if (accountList != null) {
                    if (accountList.code in 199..299) {
                        if (accountList.apiResponse != null) {
                            val mainModel =
                                gson.fromJson(accountList.apiResponse, AccountListModel::class.java)
                            accountDataList = mainModel.data
                            for (data in mainModel.data) {
                                spinnerList.add("${data.account.toString()} ...${data.account_code.toString()} ")
//                                spinnerList.add(data.account.toString())
                            }
                            binding!!.spinTransferTo.apply {
                                adapter = ArrayAdapter(
                                    this@PaymentActivity,
                                    android.R.layout.simple_spinner_item,
                                    spinnerList
                                ).apply {
                                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                }
                                setSelection(0)
                            }
                            binding!!.spinTransferFrom.apply {
                                adapter = ArrayAdapter(
                                    this@PaymentActivity,
                                    android.R.layout.simple_spinner_item,
                                    spinnerList
                                ).apply {
                                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                }
                                setSelection(0)
                            }
//                            val spinnerAdapter = ArrayAdapter(this@PaymentActivity, R.layout.simple_spinner_item, spinnerList)
//                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                            binding!!.spinTransferTo.adapter = spinnerAdapter
                        }
                    } else if (accountList.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }

            PayDataList.observe(this@PaymentActivity) { payDataList ->
                if (payDataList != null) {
                    if (payDataList.code in 199..299) {
                        if (payDataList.apiResponse != null) {
                            val model = gson.fromJson(
                                payDataList.apiResponse,
                                PayResponseApi::class.java
                            )
                            if (model.status== 200) {
                                showPaymentSuccessfully(model.data!!.amount!!, model)
                                Toast.makeText(this@PaymentActivity, "" + model.message, Toast.LENGTH_SHORT).show()
                            } else
                                model.message?.let { dialogClass.showError(it) }

//                            Toast.makeText(
//                                    this@PaymentActivity,
//                                    "" + model.message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
                        }
                    } else if (payDataList.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }

            }

            progressObservable.observe(this@PaymentActivity) { isProgress ->
                if (isProgress != null) {
                    if (isProgress)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun setOnClickListener() {
        with(binding!!) {
            imgBack.setOnClickListener { finish() }

            btnPay.setOnClickListener {

                if (txtPayAmount.valueString.isNotEmpty()) {
                    if (txtPayAmount.valueString.toDouble() > 0.0) {
                        if (spinner1AccountNamr.isNotEmpty() && accountToName.isNotEmpty()) {
                            if (accountFromId != accountToId) {
                                val userToken = sessionManager.getString(SessionManager.USERTOKEN)


                                val format1 = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
                                val format2 = SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH)
                                // Parse the input date string
                                val date = format1.parse(txtPaymentDate.text.toString())
                                // Format the parsed date to the desired format
                                val formattedDate = date?.let { format2.format(it) }
                                viewModel.setDataInPayDataList(
                                    accountFromId,
                                    accountToId,
                                    "",
                                    txtPayAmount.valueString,
                                    formattedDate!!,userToken
                                )
                            } else
                                dialogClass.showError(resources.getString(R.string.str_sender_and_depositor_account_cannot_be_same))

//                            Toast.makeText(
//                                    this@PaymentActivity,
//
//                                    resources.getString(R.string.str_sender_and_depositor_account_cannot_be_same),
//                                    Toast.LENGTH_SHORT
//                                ).show()
                        } else
                            dialogClass.showError(resources.getString(R.string.str_please_select_account))

//                        Toast.makeText(
//                                this@PaymentActivity,
//
//                                resources.getString(R.string.str_please_select_account),
//                                Toast.LENGTH_SHORT
//                            ).show()
                    } else
                        txtPayAmount.error =
                            resources.getString(R.string.str_please_enter_amount_grater_then_zero)

                } else {
                    txtPayAmount.error = resources.getString(R.string.str_please_enter_amount)
                    txtPayAmount.isFocusable = true
                }


            }
            val currentDate = utils.getCurrentDateForTransfer()

            txtPaymentDate.text = currentDate

            llPayDate.setOnClickListener {
                datePickerDialog = DatePickerDialog(
                    this@PaymentActivity,
                    { _, year, monthOfYear, dayOfMonth ->
                        val strNewDayOfMonth =
                            if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                        val strNewMonthOfYear =
                            if ((monthOfYear + 1) < 10) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()


                        txtPaymentDate.text =  "$year-$strNewMonthOfYear-$strNewDayOfMonth".getDateMMMDDYYYY()
                    }, mYear, mMonth, mDay
                )
//                datePickerDialog!!.datePicker.minDate = System.currentTimeMillis() - 1000;
                datePickerDialog!!.show()
            }

            spinTransferFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
//                    spinner1AccountNamr = tranSaferFromArray[position].toString()
                    spinner1AccountNamr = accountDataList[position].account.toString()
                    accountFromId = accountDataList[position].id.toString()

                    strTranSectionName = accountDataList[position].account.toString()
                    strTranSectionNumber = accountDataList[position].account_code.toString()
                }

                override fun onNothingSelected(adapter: AdapterView<*>?) {}
            }
            spinTransferTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    accountToName = accountDataList[position].account.toString()
                    accountToId = accountDataList[position].id.toString()
                    accountToCode = accountDataList[position].account_code.toString()
//                    Toast.makeText(this@PaymentActivity, "" + accountToName, Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(adapter: AdapterView<*>?) {}
            }

        }
    }

    private fun showPaymentSuccessfully(amount: String = "", model: PayResponseApi) {
        val dialog = Dialog(this@PaymentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_payment_successfully)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnReTransfer: Button = dialog.findViewById(R.id.btn_re_transfer)
        val txtDone: TextView = dialog.findViewById(R.id.txt_done)

        val txtAmount: TextView = dialog.findViewById(R.id.txt_amount)
        val txtTransactionNumber: TextView =
            dialog.findViewById(R.id.txt_transaction_number)
        val txtTransferFrom: TextView = dialog.findViewById(R.id.txt_transfer_from)
        val txtTransferTo: TextView = dialog.findViewById(R.id.txt_transfer_to)
        val txtTransferOn: TextView = dialog.findViewById(R.id.txt_transfer_on)
        val txtTransferStatus: TextView =
            dialog.findViewById(R.id.txt_transfer_status)

        txtAmount.text = binding!!.txtPayAmount.formattedString.toString()
        txtTransactionNumber.text = model.data!!.transactionId.toString()
//        txtTransferFrom.text = strTranSectionName
//        txtTransferTo.text = accountToName
        txtTransferFrom.text = "$strTranSectionName...$strTranSectionNumber"
        txtTransferTo.text = "$accountToName...$accountToCode"
        txtTransferOn.text = binding!!.txtPaymentDate.text.toString()
        if (model.data!!.transactionStatus!=null){
            txtTransferStatus.text = model.data!!.transactionStatus.toString()
        }


        btnReTransfer.setOnClickListener { dialog.dismiss() }
        txtDone.setOnClickListener {
            dialog.dismiss()
            if (checkComingFromAct == "TranSectionDetailActivity") {
                val intent = Intent()
                if (amount.isNotEmpty()) {
                    intent.putExtra("amount", amount)
                }
                setResult(1113, intent)
            }

            dialog.dismiss()
            finish()
        }
        dialog.show()
    }
}