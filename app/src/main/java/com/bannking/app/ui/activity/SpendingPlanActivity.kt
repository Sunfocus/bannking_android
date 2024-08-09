package com.bannking.app.ui.activity

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.adapter.SpendingAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.BaseViewModel
import com.bannking.app.databinding.ActivitySpendingPlanBinding
import com.bannking.app.model.SpendingModel
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SpendAdapterClick

class SpendingPlanActivity :
    BaseActivity<BaseViewModel, ActivitySpendingPlanBinding>(BaseViewModel::class.java),
    SpendAdapterClick {
    var list: MutableList<SpendingModel>? = null
    var adapter: SpendingAdapter? = null
    var viewModel: BaseViewModel? = null


    override fun getBinding(): ActivitySpendingPlanBinding {
        return ActivitySpendingPlanBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: BaseViewModel) {
        this.viewModel = viewModel
    }

    override fun initialize() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.rlSpending.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding!!.tvBankingHere.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvSpending.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding!!.rlSpending.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.clr_card_background
                )
            )
            binding!!.tvBankingHere.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvSpending.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.black))
        }

        list = ArrayList()
        adapter = SpendingAdapter()
    }

    override fun setMethod() {
        setOnClickListener()
        setAdapter()
    }

    override fun observe() {
    }

    private fun setOnClickListener() {

        binding!!.txtUpgrade.setOnClickListener {
            finish()
            startActivity(Intent(this@SpendingPlanActivity, UpgradeActivity::class.java))
        }


        binding!!.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun setAdapter() {

        list!!.add(SpendingModel(R.drawable.ic_clr_header, resources.getString(R.string.str_header), true))

        list!!.add(SpendingModel(R.drawable.ic_budget, resources.getString(R.string.str_expenses), false))

        list!!.add(SpendingModel(R.drawable.ic_transfer, resources.getString(R.string.str_transfer), false))
        list!!.add(SpendingModel(R.drawable.ic_debit, resources.getString(R.string.str_payment), false))
        list!!.add(SpendingModel(R.drawable.ic_pie_chart, resources.getString(R.string.str_statistics), false))
        list!!.add(SpendingModel(R.drawable.ic_languages, resources.getString(R.string.str_languages), false))
        list!!.add(SpendingModel(R.drawable.ic_currency, resources.getString(R.string.str_currency), false))
        adapter = SpendingAdapter(this@SpendingPlanActivity, list, this)
        binding!!.rvSpending.adapter = adapter
    }

    override fun clickOnSpentTitle(strString: String) {
        when (strString) {
            resources.getString(R.string.str_header) -> {
                val getIntent = intent
                val intent = Intent(this@SpendingPlanActivity, AccountMenuNewActivity::class.java)
                intent.putExtra("ComeFrom", "Navigation")
                intent.putExtra("Headermodel", getIntent.getSerializableExtra("Headermodel"))
                intent.putExtra("accountList", getIntent.getSerializableExtra("accountList"))
                startActivityForResult(intent, Constants.MAIN_TO_MENU)
            }
            resources.getString(R.string.str_expenses) -> {
                startActivity(
                    Intent(
                        this@SpendingPlanActivity,
                        ExploreExpensesActivity::class.java
                    )
                )
            }
            resources.getString(R.string.str_transfer) -> {
                startActivity(
                    Intent(
                        this@SpendingPlanActivity,
                        ScheduleTransferActivity::class.java
                    )
                )
            }
            resources.getString(R.string.str_payment) -> {
                startActivity(Intent(this@SpendingPlanActivity, PaymentActivity::class.java))
            }
            resources.getString(R.string.str_statistics) -> {
                startActivity(
                    Intent(
                        this@SpendingPlanActivity,
                        ExploreExpensesActivity::class.java
                    )
                )
            }
            resources.getString(R.string.str_languages) -> {
                startActivity(Intent(this@SpendingPlanActivity, ProfileActivity::class.java))
            }
            resources.getString(R.string.str_currency) -> {
                startActivity(Intent(this@SpendingPlanActivity, ProfileActivity::class.java))
            }

        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.txtUpgrade.isVisible = !isPremium
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constants.MAIN_TO_MENU) {
            if (data!!.hasExtra("MainToMenu")) {
                intent.putExtra("MainToMenu", "MainToMenu")
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}