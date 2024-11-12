package com.bannking.app.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.adapter.BankAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityBankBinding
import com.bannking.app.model.BankListData
import com.bannking.app.model.ExchangeTokenResponse
import com.bannking.app.model.GetBankLinkTokenResponse
import com.bannking.app.model.GetBankListResponse
import com.bannking.app.model.OnClickedItems
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountListModel
import com.bannking.app.model.retrofitResponseModel.headerModel.HeaderModel
import com.bannking.app.model.viewModel.BankViewModel
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

class BankActivity : BaseActivity<BankViewModel, ActivityBankBinding?>(BankViewModel::class.java),
    OnClickedItems {

    private var premiumFeatures = false
    private var plaidHandler: PlaidHandler? = null
    private lateinit var viewModel: BankViewModel
    private lateinit var bankAdapter: BankAdapter
    override fun getBinding(): ActivityBankBinding {
        return ActivityBankBinding.inflate(layoutInflater)
    }

    override fun initialize() {
        uiColor()
        premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)
        Log.e("premiumFeatures", premiumFeatures.toString())
        if (premiumFeatures) {
            binding!!.clBankDetails.visibility = View.GONE
            binding!!.rvBankName.visibility = View.VISIBLE
        } else {
            binding!!.clBankDetails.visibility = View.VISIBLE
            binding!!.rvBankName.visibility = View.GONE
        }
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.bottomNavBank.setBackgroundResource(R.drawable.nav_shape_night) // Dark mode background color
        } else {
            binding!!.bottomNavBank.setBackgroundResource(R.drawable.nav_shape) // Light mode background color
        }
    }

    private fun uiColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.clTopBank.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            binding!!.tvUnRecognizedDetails.setTextColor(
                ContextCompat.getColor(
                    this, R.color.white
                )
            )
            binding!!.tvAudioIfo.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvTips.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.imgAddBank.setColorFilter(this.resources.getColor(R.color.white))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding!!.ivHints.setColorFilter(this.resources.getColor(R.color.white))

        } else {
            binding!!.clTopBank.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvUnRecognizedDetails.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvAudioIfo.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
            binding!!.tvTips.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.clr_blue))
            binding!!.imgAddBank.setColorFilter(this.resources.getColor(R.color.clr_blue))
            binding!!.ivHints.setColorFilter(this.resources.getColor(R.color.clr_blue))

        }
    }

    override fun setMethod() {
        binding!!.apply {
            imgBack.setOnClickListener {
                finish()
            }
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)

            clBankDetails.setOnClickListener {
                if (premiumFeatures) {
                    getBankLinkToken(userToken)
                } else {
                    val intent = Intent(this@BankActivity, UpgradeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }



            imgAddBank.setOnClickListener {
                if (premiumFeatures) {
                    getBankLinkToken(userToken)
                } else {
                    val intent = Intent(this@BankActivity, UpgradeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }
    }

    private fun getBankLinkToken(userToken: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.getTokenForPlaidApi(userToken).observe(this@BankActivity) {
                val response = it as GetBankLinkTokenResponse
                if (response.status == 200) {
                    onLinkTokenSuccess(response.data.link_token)
                } else {
                    Toast.makeText(
                        this@BankActivity, response.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun observe() {
        viewModel.apply {
            progressObservable.observe(this@BankActivity) {
                if (it != null) {
                    if (it) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
                }
            }
        }
    }

    private fun setAdapter(model: ArrayList<BankListData>) {
        if (model.isEmpty()) {
            binding!!.clBankDetails.visibility = View.VISIBLE
            binding!!.rvBankName.visibility = View.GONE
        } else {
            binding!!.clBankDetails.visibility = View.GONE
            binding!!.rvBankName.visibility = View.VISIBLE
            bankAdapter = BankAdapter(this, model, this)
            binding!!.rvBankName.adapter = bankAdapter
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

    private fun showSuccess(success: LinkSuccess) {
        Log.e("linkSuccess", success.publicToken)
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.exchangeTokenForPlaidApi(userToken, success.publicToken)
                .observe(this@BankActivity) {
                    val response = it as ExchangeTokenResponse
                    if (response.status == 200) {
                        viewModel.setDataInBankList(userToken)
                            .observe(this@BankActivity) { responses ->
                                val data = responses as GetBankListResponse
                                if (data.status == 200) {
                                    if (data.data.size > 0) {
                                        binding!!.clBankDetails.visibility = View.GONE
                                        binding!!.rvBankName.visibility = View.VISIBLE
                                        setAdapter(data.data)
                                    } else {
                                        binding!!.clBankDetails.visibility = View.VISIBLE
                                        binding!!.rvBankName.visibility = View.GONE
                                    }
                                } else {
                                    binding!!.clBankDetails.visibility = View.VISIBLE
                                    binding!!.rvBankName.visibility = View.GONE
                                }

                            }
                        viewModel.errorResponse.observe(this@BankActivity) { its ->
                            if (its != null){
                                Log.e("errorMessage", its)
                                if (!its.isNullOrEmpty()) {
                                    binding!!.clBankDetails.visibility = View.VISIBLE
                                    binding!!.rvBankName.visibility = View.GONE
                                }
                            }
                        }
                    }

                }
        }
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

        binding!!.bottomNavBank.selectedItemId = R.id.nav_spending_plan

        binding!!.bottomNavBank.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_account -> {
                    val intent = Intent(this@BankActivity, MainActivity::class.java)
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

                        val intent = Intent(this@BankActivity, AccountMenuNewActivity::class.java)
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
                    val intent = Intent(this@BankActivity,ExploreExpensesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }

                R.id.nav_menu -> {
                    val intent = Intent(this@BankActivity, ProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }

            }
            true
        }

        val menu = binding!!.bottomNavBank.menu
        binding!!.bottomNavBank.itemIconTintList = null
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
                val intent = Intent(this@BankActivity, BudgetPlannerActivity::class.java)
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


    private fun showFailure(exit: LinkExit) {
        if (exit.error != null) {
            Toast.makeText(
                this, "${exit.error?.displayMessage}, ${exit.error?.errorCode}", Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this, "${exit.metadata.status?.jsonValue}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun initViewModel(viewModel: BankViewModel) {
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)

        if (userToken != null) {
            viewModel.getUserProfileData(userToken).observe(this) { mainModel ->
                if (mainModel.status == 200) {
                    if (mainModel.data!!.subscriptionStatus == 1) {
                        inAppPurchaseSM.setBoolean(SessionManager.isPremium, true)
                    } else {
                        inAppPurchaseSM.setBoolean(SessionManager.isPremium, false)
                    }
                }
            }
        }

        if (premiumFeatures) {
            viewModel.setDataInBankList(userToken).observe(this) { response ->
                val data = response as GetBankListResponse
                if (response.status == 200) {
                    if (data.data.size > 0) {
                        binding!!.clBankDetails.visibility = View.GONE
                        binding!!.rvBankName.visibility = View.VISIBLE
                        setAdapter(data.data)
                    } else {
                        binding!!.clBankDetails.visibility = View.VISIBLE
                        binding!!.rvBankName.visibility = View.GONE
                    }

                } else {
                    binding!!.clBankDetails.visibility = View.VISIBLE
                    binding!!.rvBankName.visibility = View.GONE
                }

            }

            viewModel.errorResponse.observe(this@BankActivity) { its ->
                its?.let { message ->  // Check if 'its' is not null
                    Log.e("errorMessage", message)
                    if (message.isNotEmpty()) {
                        binding!!.clBankDetails.visibility = View.VISIBLE
                        binding!!.rvBankName.visibility = View.GONE
                    }
                } ?: run {
                    Log.e("errorMessage", "Error message is null")
                }
            }
        } else {
            binding!!.clBankDetails.visibility = View.VISIBLE
            binding!!.rvBankName.visibility = View.GONE
        }

        this.viewModel = viewModel
    }

    override fun clickedPassChildBankData(
        position: Int,
        institutionId: String,
        accountNumber: String,
        balance: Int,
        accountName: String,
        accountId: String
    ) {
        val intent = Intent(this, AccountBalanceActivity::class.java)
        intent.putExtra("institutionId", institutionId)
        intent.putExtra("accountNumber", accountNumber)
        intent.putExtra("balance", balance)
        intent.putExtra("accountName", accountName)
        intent.putExtra("accountId", accountId)
        startActivity(intent)
    }
}