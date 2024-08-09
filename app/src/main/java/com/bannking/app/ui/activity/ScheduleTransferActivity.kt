package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.UiExtension.getDateMMMDDYYYY
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityScheduleTransferBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.PayResponseApi
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountListModel
import com.bannking.app.model.retrofitResponseModel.accountListModel.Data
import com.bannking.app.model.viewModel.PayAndTransferViewModel
import com.bannking.app.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*


class ScheduleTransferActivity :
    BaseActivity<PayAndTransferViewModel, ActivityScheduleTransferBinding>(PayAndTransferViewModel::class.java) {

    private var datePickerDialog: DatePickerDialog? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private lateinit var savedSessionManagerCurrency: SessionManager

    lateinit var viewModel: PayAndTransferViewModel
    lateinit var accountFromId: String
    private lateinit var strTranSectionName: String
    private lateinit var strTranSectionNumber: String
    private lateinit var spinnerList: ArrayList<String>
    lateinit var accountDataList: ArrayList<Data>

    var spinner1AccountNamr = ""
    var accountToName = ""
    var accountToCode = ""
    var accountToId = ""

    private var checkComingFromAct = ""

//    val tranSaferFromArray : ArrayList<String> = arrayListOf()

    override fun getBinding(): ActivityScheduleTransferBinding {
        return ActivityScheduleTransferBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: PayAndTransferViewModel) {
        this.viewModel = viewModel
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        if (userToken != null) {
            viewModel.setDataInAccountList(userToken)
        }
        savedSessionManagerCurrency =
            SessionManager(this@ScheduleTransferActivity, SessionManager.CURRENCY)
    }

    private fun setUIColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.rlTransfer.backgroundTintList =
                ContextCompat.getColorStateList(
                    this,
                    R.color.dark_mode
                )
            binding!!.tvTF.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.txtTransferDate.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.txtTransferFrom.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding!!.tvTT.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvTN.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding!!.rlTransfer.backgroundTintList =
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
            binding!!.txtTransferDate.setTextColor(ContextCompat.getColor(this, R.color.black))
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

    override fun setMethod() {
        setOnClickListener()
//        tranSaferFromArray.add(strTranSectionName)
//        binding!!.spinTransferFrom.adapter = ArrayAdapter(this@ScheduleTransferActivity, android.R.layout.simple_spinner_item, tranSaferFromArray)
    }

    override fun observe() {
        with(viewModel) {
            accountListData.observe(this@ScheduleTransferActivity) { accountList ->
                if (accountList != null) {
                    if (accountList.code in 199..299) {
                        if (accountList.apiResponse != null) {
                            val mainModel =
                                gson.fromJson(accountList.apiResponse, AccountListModel::class.java)
                            accountDataList = mainModel.data
                            for (data in mainModel.data) {
                                spinnerList.add("${data.account.toString()} ...${data.account_code.toString()} ")
                            }

                            binding!!.spinTransferTo.apply {
                                adapter = ArrayAdapter(
                                    this@ScheduleTransferActivity,
                                    android.R.layout.simple_spinner_item,
                                    spinnerList
                                ).apply {
                                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                }
                                setSelection(0)
                            }
                            binding!!.spinTransferFrom.apply {
                                adapter = ArrayAdapter(
                                    this@ScheduleTransferActivity,
                                    android.R.layout.simple_spinner_item,
                                    spinnerList
                                ).apply {
                                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                }
                                setSelection(0)
                            }
                        }
                    }
                }
            }

            scheduleTransferData.observe(this@ScheduleTransferActivity) { transfer ->
                if (transfer != null) {
                    if (transfer.code in 199..299) {
                        if (transfer.apiResponse != null) {
                            val model =
                                gson.fromJson(transfer.apiResponse, PayResponseApi::class.java)
                            if (model.status == 200) {
                                showPaymentSuccessfully(model.data!!.amount.toString(), model)
                            } else
                                dialogClass.showError(model.message.toString())
                        }
                    } else if (transfer.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }

            }

            progressObservable.observe(this@ScheduleTransferActivity) { isProgress ->
                if (isProgress != null) {
                    if (isProgress)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setOnClickListener() {
        with(binding!!) {
            imgBack.setOnClickListener { finish() }

            btnTransfer.setOnClickListener {

                if (edtTransferAmount.valueString.toString().isNotEmpty()) {
                    if (edtTransferAmount.valueString.toString().toDouble() > 0.0) {
                        if (spinner1AccountNamr.isNotEmpty() && accountToName.isNotEmpty()) {
                            if (accountFromId != accountToId) {
                                val userToken = sessionManager.getString(SessionManager.USERTOKEN)

                                val format1 = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
                                val format2 = SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH)
                                // Parse the input date string
                                val date = format1.parse(txtTransferDate.text.toString())
                                // Format the parsed date to the desired format
                                val formattedDate = date?.let { format2.format(it) }
                                viewModel.setDataInScheduleTransfer(
                                    accountFromId,
                                    accountToId,
                                    "",
                                    edtTransferAmount.valueString,
                                    formattedDate!!, userToken
                                )
                            } else
                                dialogClass.showError(resources.getString(R.string.str_sender_and_depositor_account_cannot_be_same))
                        } else
                            dialogClass.showError(resources.getString(R.string.str_please_select_account))
                    } else
                        edtTransferAmount.error =
                            resources.getString(R.string.str_please_enter_amount_grater_then_zero)
                } else
                    edtTransferAmount.error = resources.getString(R.string.str_enter_amount)


            }
            val currentDate = utils.getCurrentDateForTransfer()
            txtTransferDate.text = currentDate

            llTransferDate.setOnClickListener {
                datePickerDialog = DatePickerDialog(
                    this@ScheduleTransferActivity,
                    { _, year, monthOfYear, dayOfMonth ->
                        val strNewDayOfMonth =
                            if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                        val strNewMonthOfYear =
                            if ((monthOfYear + 1) < 10) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()

                        txtTransferDate.text = "$year-$strNewMonthOfYear-$strNewDayOfMonth".getDateMMMDDYYYY()
                    }, mYear, mMonth, mDay
                )
//                datePickerDialog!!.datePicker.minDate = System.currentTimeMillis() - 1000;
                datePickerDialog!!.show()
            }

            spinTransferFrom.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {

//                    spinner1Value = tranSaferFromArray[position].toString()
                        spinner1AccountNamr = accountDataList[position].account.toString()
                        accountFromId = accountDataList[position].id.toString()

                        strTranSectionName = accountDataList[position].account.toString()
                        strTranSectionNumber = accountDataList[position].account_code.toString()
                    }

                    override fun onNothingSelected(adapter: AdapterView<*>?) {}
                }
            spinTransferTo.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        accountToName = accountDataList[position].account.toString()
                        accountToCode = accountDataList[position].account_code.toString()
                        accountToId = accountDataList[position].id.toString()
//                    Toast.makeText(this@ScheduleTransferActivity, "" + accountToName, Toast.LENGTH_SHORT).show()
                    }

                    override fun onNothingSelected(adapter: AdapterView<*>?) {}
                }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun showPaymentSuccessfully(
        currentAmount: String? = null,
        model: PayResponseApi
    ) {
        val dialog = Dialog(this@ScheduleTransferActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_payment_successfully)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnReTransfer: Button = dialog.findViewById(R.id.btn_re_transfer)
        val txtDone: TextView = dialog.findViewById(R.id.txt_done)
        val rlSuccess: RelativeLayout = dialog.findViewById(R.id.rlSuccess)
        val llSuccess: LinearLayout = dialog.findViewById(R.id.llSuccess)

        val txtAmount: TextView = dialog.findViewById(R.id.txt_amount)
        val tvTranStatus: TextView = dialog.findViewById(R.id.tvTranStatus)
        val tvTranOn: TextView = dialog.findViewById(R.id.tvTranOn)
        val tvTranTo: TextView = dialog.findViewById(R.id.tvTranTo)
        val tvTranFrom: TextView = dialog.findViewById(R.id.tvTranFrom)
        val txtTransactionNumber: TextView = dialog.findViewById(R.id.txt_transaction_number)
        val tvTranNo: TextView = dialog.findViewById(R.id.tvTranNo)
        val txtTransferFrom: TextView = dialog.findViewById(R.id.txt_transfer_from)
        val txtTransferTo: TextView = dialog.findViewById(R.id.txt_transfer_to)
        val txtTransferOn: TextView = dialog.findViewById(R.id.txt_transfer_on)
        val txtTransferStatus: TextView = dialog.findViewById(R.id.txt_transfer_status)

        if (UiExtension.isDarkModeEnabled()) {
           rlSuccess.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
           llSuccess.setBackgroundResource(R.drawable.corner_radius_stroke)
            tvTranNo.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvTranTo.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvTranFrom.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvTranOn.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvTranStatus.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
           rlSuccess.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            llSuccess.setBackgroundResource(R.drawable.round_layout_otp)
            tvTranNo.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tvTranTo.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tvTranFrom.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tvTranOn.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tvTranStatus.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        txtAmount.text = binding!!.edtTransferAmount.formattedString.toString()
        txtTransactionNumber.text = model.data!!.transactionId.toString()
        txtTransferFrom.text = "$strTranSectionName...$strTranSectionNumber"
        txtTransferTo.text = "$accountToName...$accountToCode"
        txtTransferOn.text = binding!!.txtTransferDate.text.toString()
        txtTransferStatus.text = model.data!!.transactionStatus.toString()


        btnReTransfer.setOnClickListener { dialog.dismiss() }
        txtDone.setOnClickListener {

            if (checkComingFromAct == "TranSectionDetailActivity") {
                val intent = Intent()
                if (currentAmount != null) {
                    intent.putExtra("CurrentAmount", currentAmount)
                }

                setResult(1112, intent)
            }

            dialog.dismiss()
            finish()
        }
        dialog.show()
    }
}