package com.bannking.app.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bannking.app.adapter.BankAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityBankBinding
import com.bannking.app.model.BankListData
import com.bannking.app.model.ExchangeTokenResponse
import com.bannking.app.model.GetBankLinkTokenResponse
import com.bannking.app.model.GetBankListResponse
import com.bannking.app.model.OnClickedItems
import com.bannking.app.model.viewModel.BankViewModel
import com.bannking.app.utils.SessionManager
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
        premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)

        if (!premiumFeatures) {
            binding!!.clBankDetails.visibility = View.GONE
            binding!!.rvBankName.visibility = View.VISIBLE
        } else {
            binding!!.clBankDetails.visibility = View.VISIBLE
            binding!!.rvBankName.visibility = View.GONE
        }
    }

    override fun setMethod() {
        binding!!.apply {
            imgBack.setOnClickListener {
                finish()
            }

            clBankDetails.setOnClickListener {
                val intent = Intent(this@BankActivity, UpgradeActivity::class.java)
                startActivity(intent)
            }

            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)
            imgAddBank.setOnClickListener {
                if (!premiumFeatures) {
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
                } else {
                    val intent = Intent(this@BankActivity, UpgradeActivity::class.java)
                    startActivity(intent)
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
            binding!!.tvItemNotFound.visibility = View.VISIBLE
            binding!!.rvBankName.visibility = View.GONE
        } else {
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
                                if (data.data.size > 0) {
                                    binding!!.tvItemNotFound.visibility = View.GONE
                                    binding!!.rvBankName.visibility = View.VISIBLE
                                    setAdapter(data.data)
                                } else {
                                    binding!!.tvItemNotFound.visibility = View.VISIBLE
                                    binding!!.rvBankName.visibility = View.GONE
                                }
                            }
                    }

                }
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
        if (!premiumFeatures) {
            viewModel.setDataInBankList(userToken).observe(this) { response ->
                val data = response as GetBankListResponse
                if (data.data.size > 0) {
                    binding!!.tvItemNotFound.visibility = View.GONE
                    binding!!.rvBankName.visibility = View.VISIBLE
                    setAdapter(data.data)
                } else {
                    binding!!.tvItemNotFound.visibility = View.VISIBLE
                    binding!!.rvBankName.visibility = View.GONE
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