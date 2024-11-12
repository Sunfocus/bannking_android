package com.bannking.app.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bannking.app.R
import com.bannking.app.adapter.BankHeaderAdapter
import com.bannking.app.adapter.ClickedItemHeader
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityHeaderForBankActivtyBinding
import com.bannking.app.model.ExchangeTokenResponse
import com.bannking.app.model.GetBankLinkTokenResponse
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountListModel
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.AccountTitleModel
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.Data
import com.bannking.app.model.retrofitResponseModel.headerModel.HeaderModel
import com.bannking.app.model.viewModel.HeaderBankViewModel
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.plaid.link.FastOpenPlaidLink
import com.plaid.link.Plaid
import com.plaid.link.PlaidHandler
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HeaderForBankActivity :
    BaseActivity<HeaderBankViewModel, ActivityHeaderForBankActivtyBinding?>(HeaderBankViewModel::class.java),
    ClickedItemHeader {

    private var headerDialog: Dialog? = null
    private var typeForCreateHeader: String? = ""
    private var result: Boolean = false
    private var headerTitleListmodel: HeaderModel? = null
    private lateinit var list: java.util.ArrayList<Data>
    private var accountIdForBank = ""
    private var premiumFeatures = false
    private var plaidHandler: PlaidHandler? = null
    private lateinit var bankHeaderAdapter: BankHeaderAdapter
    private lateinit var viewModel: HeaderBankViewModel

    override fun getBinding(): ActivityHeaderForBankActivtyBinding {
        return ActivityHeaderForBankActivtyBinding.inflate(layoutInflater)
    }

    override fun initialize() {
        premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)
    }


    override fun onResume() {
        super.onResume()

        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.setDataInHeaderTitleList(userToken)
        Handler(Looper.getMainLooper()).postDelayed({
            if (userToken != null) {
                viewModel.setDataInAccountList(userToken)
            }
        }, 100)

        binding!!.bottomNavBankHeader.selectedItemId = R.id.nav_spending_plan

        binding!!.bottomNavBankHeader.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_account -> {
                    val intent = Intent(this@HeaderForBankActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }

                R.id.nav_header -> {
                    val accountListModel = gson.fromJson(
                        viewModel.accountListData.value?.apiResponse, AccountListModel::class.java
                    )
                    if (accountListModel.data != null && accountListModel.data.isNotEmpty() && accountListModel != null) {

                        val intent =
                            Intent(this@HeaderForBankActivity, AccountMenuNewActivity::class.java)
                        val model = gson.fromJson(
                            viewModel.headerTitleList.value?.apiResponse, HeaderModel::class.java
                        )
                        val accountList = gson.fromJson(
                            viewModel.accountListData.value?.apiResponse,
                            AccountListModel::class.java
                        )
                        intent.putExtra("Headermodel", model)
                        intent.putExtra("accountList", accountList)
                        resultLauncher.launch(intent)
                        overridePendingTransition(0, 0)
                    }
                }

                R.id.nav_spending_plan -> {

                }

                R.id.nav_explore -> {
                    val intent =
                        Intent(this@HeaderForBankActivity, ExploreExpensesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }

                R.id.nav_menu -> {
                    val intent = Intent(this@HeaderForBankActivity, ProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }

            }
            true
        }

        val menu = binding!!.bottomNavBankHeader.menu
        binding!!.bottomNavBankHeader.itemIconTintList = null
        val menuItem = menu.findItem(R.id.nav_menu)
        Glide.with(this).asBitmap().load(Constants.IMG_BASE_URL + userModel!!.image).apply(
            RequestOptions.circleCropTransform().override(100, 100)
                .placeholder(R.drawable.sample_user)
        ).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                menuItem?.icon = BitmapDrawable(resources, resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })

    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == 1011) {
                val intent = Intent(this@HeaderForBankActivity, BudgetPlannerActivity::class.java)
                intent.putExtra("SelectedMenu", data?.getStringExtra("SelectedMenu"))
                resultLauncher2.launch(intent)
                overridePendingTransition(0, 0)
            }
        }

    private var resultLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.setIdInFilterDatanull(null)
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
//                viewModel.setDataInAccountList(userToken!!)
                viewModel.setDataInHeaderTitleList(userToken)
            }
        }


    override fun setMethod() {
        binding!!.apply {
            imgBack.setOnClickListener {
                finish()
            }
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)

            btnSaveHeader.setOnClickListener {
                if (!premiumFeatures) {
                    if (accountIdForBank.isNotEmpty()) {
                        val accountExists =
                            headerTitleListmodel!!.data.any { it.name == accountIdForBank }
                        result = !accountExists
                        getBankLinkToken(userToken)
                    } else {
                        Toast.makeText(
                            this@HeaderForBankActivity,
                            "Please select account header.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val intent = Intent(this@HeaderForBankActivity, UpgradeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }
    }

    private fun getBankLinkToken(userToken: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getTokenForPlaidApi(userToken).observe(this@HeaderForBankActivity) {
                val response = it as GetBankLinkTokenResponse
                if (response.status == 200) {
                    onLinkTokenSuccess(response.data.link_token)
                } else {
                    Toast.makeText(
                        this@HeaderForBankActivity, response.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun onLinkTokenSuccess(token: String) {
        if (token.isEmpty()) {
            Toast.makeText(this, "Please wait!", Toast.LENGTH_SHORT).show()
        } else {
            val tokenConfiguration = LinkTokenConfiguration.Builder().token(token).build()
            plaidHandler = Plaid.create(this.application, tokenConfiguration)
            openLink()
        }
    }

    private fun openLink() {
        plaidHandler?.let { linkAccountToPlaid.launch(it) }
    }

    private val linkAccountToPlaid = registerForActivityResult(FastOpenPlaidLink()) { result ->
        when (result) {
            is LinkSuccess -> showSuccess(result)
            is LinkExit -> showFailure(result)

        }
    }

    private fun showFailure(exit: LinkExit) {
        if (exit.error != null) {
            Toast.makeText(
                this, "${exit.error?.displayMessage}, ${exit.error?.errorCode}", Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this, "${exit.metadata.status?.jsonValue}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSuccess(success: LinkSuccess) {
        Log.e("linkSuccess", success.publicToken)
        Log.e("linkSuccess", result.toString())
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.exchangeTokenForPlaidApi(
                userToken,
                success.publicToken,
                accountIdForBank,
                typeForCreateHeader,
                result
            )
                .observe(this@HeaderForBankActivity) {
                    val response = it as ExchangeTokenResponse
                    if (response.status == 200) {
                        finish()
                    }

                }
            viewModel.errorResponse.observe(this@HeaderForBankActivity) { its ->
                if (its != null) {
                    Log.e("errorMessage", its)
                    if (!its.isNullOrEmpty()) {
                        Toast.makeText(
                            this@HeaderForBankActivity, its, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }

    }

    override fun observe() {
        with(viewModel) {
            accountTitleList.observe(this@HeaderForBankActivity) {
                if (it != null) {
                    if (it.code in 199..299) {
                        val model = gson.fromJson(it.apiResponse, AccountTitleModel::class.java)
                        if (model.status == 200) {
                            list = model.data
                            setAdapter(list)
                        } else {
                            dialogClass.showServerErrorDialog()
                        }

                    } else if (it.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }
            headerTitleList.observe(this@HeaderForBankActivity) {
                if (it != null) {
                    if (it.code in 199..299) {
                        headerTitleListmodel =
                            gson.fromJson(it.apiResponse, HeaderModel::class.java)
                        if (headerTitleListmodel!!.data.size >= 2) {
                            headerManageDialog(headerTitleListmodel!!.data,1)
                        }
                    }
                }
            }

            progressObservable.observe(this@HeaderForBankActivity) {
                if (it != null) {
                    if (it) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
                }
            }
        }
    }

    private fun setAdapter(list: ArrayList<Data>) {
        bankHeaderAdapter = BankHeaderAdapter(this, list, this)
        binding!!.rvHeaderListBank.adapter = bankHeaderAdapter

    }

    override fun initViewModel(viewModel: HeaderBankViewModel) {
        this.viewModel = viewModel
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.setDataInAccountTitleList(userToken)
    }


    override fun onClickedValue(
        position: Int,
        id: String?,
        accountCreated: Int,
        list: ArrayList<Data>,
        type: String?
    ) {
        typeForCreateHeader = type
        if (accountCreated == 1 && headerTitleListmodel!!.data.size < 2) {
            accountIdForBank = id!!
        } else {
            if (id != null) {
                handleAccountSelection(id, list)
            }
        }
    }

    private fun handleAccountSelection(selectedId: String, dataList: ArrayList<Data>) {

//        val createdAccountCount = dataList.count { it.isAccountCreated == 1 }
        val createdAccountCount = headerTitleListmodel!!.data.size

        Log.e("createdAccountCount", createdAccountCount.toString())

//        val selectedItem = dataList.find { it.id == selectedId }
        val selectedItem = headerTitleListmodel!!.data.find { it.id == selectedId }

        Log.e("createdAccountCount", selectedItem.toString())

//        if (createdAccountCount >= 2 && selectedItem!!.isAccountCreated == 0) {
        if (createdAccountCount >= 2) {
            accountIdForBank = ""
            showToast("You have already created 2 accounts.")
            headerManageDialog(headerTitleListmodel!!.data,2)
        } else {
            accountIdForBank = selectedId
        }

    }


    // Function to show a toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun headerManageDialog(data: ArrayList<com.bannking.app.model.retrofitResponseModel.headerModel.Data>,type:Int) {
        headerDialog?.dismiss()
        headerDialog = Dialog(this@HeaderForBankActivity)
        headerDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        headerDialog!!.setCancelable(false)
        headerDialog!!.setContentView(R.layout.dialog_header_manage)
        headerDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        headerDialog!!.window!!.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        val ivCloseHeader = headerDialog!!.findViewById<AppCompatImageView>(R.id.ivCloseHeader)
        val tvWhichType = headerDialog!!.findViewById<AppCompatTextView>(R.id.tvWhichType)
        val tvWhichTypeDesc = headerDialog!!.findViewById<AppCompatTextView>(R.id.tvWhichTypeDesc)
        val clFirstHeader = headerDialog!!.findViewById<ConstraintLayout>(R.id.clFirstHeader)
        val tvFirstHeader = headerDialog!!.findViewById<AppCompatTextView>(R.id.tvFirstHeader)
        val rbFirstHeader = headerDialog!!.findViewById<RadioButton>(R.id.rbFirstHeader)
        val clSecondHeader = headerDialog!!.findViewById<ConstraintLayout>(R.id.clSecondHeader)
        val tvSecondHeader = headerDialog!!.findViewById<AppCompatTextView>(R.id.tvSecondHeader)
        val rbSecondHeader = headerDialog!!.findViewById<RadioButton>(R.id.rbSecondHeader)
        val btnOkayHeader = headerDialog!!.findViewById<AppCompatTextView>(R.id.btnOkayHeader)

        if (type == 1){
            tvWhichType.text = "Which account title would you like your bank to be connected to?"
            tvWhichTypeDesc.visibility = View.GONE
        }else{
            tvWhichType.text = "Account Limit Notification"
            tvWhichTypeDesc.visibility = View.VISIBLE
        }

        Log.e("sndfdsfdfjdfds", data[0].name.toString())
        tvFirstHeader.text = data[0].name
        tvSecondHeader.text = data[1].name

        ivCloseHeader.setOnClickListener {
            headerDialog!!.dismiss()
        }
        btnOkayHeader.setOnClickListener {
            if (accountIdForBank == "") {
                showToast("Please select any one.")
            } else {
                headerDialog!!.dismiss()
            }
        }
        clFirstHeader.setOnClickListener {
            rbSecondHeader.isChecked = false
            rbFirstHeader.isChecked = true
            accountIdForBank = data[0].id.toString()
            val objectForHeader = list.find { it.id == data[0].id }
            typeForCreateHeader = objectForHeader!!.type
        }

        clSecondHeader.setOnClickListener {
            rbFirstHeader.isChecked = false
            rbSecondHeader.isChecked = true
            accountIdForBank = data[1].id.toString()
            val objectForHeader = list.find { it.id == data[1].id }
            typeForCreateHeader = objectForHeader!!.type
        }

        headerDialog!!.show()

    }
}