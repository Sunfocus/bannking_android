package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.databinding.ItemExpensesBinding
import com.bannking.app.model.ItemClicked
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountsData
import com.bannking.app.model.retrofitResponseModel.accountListModel.Data
import com.bannking.app.model.retrofitResponseModel.accountListModel.ExtraData
import com.bannking.app.model.retrofitResponseModel.accountListModel.HiddenData
import com.bannking.app.ui.activity.AccountBalanceActivity
import com.bannking.app.ui.activity.TranSectionDetailActivity
import com.bannking.app.utils.MoreDotClick
import com.bannking.app.utils.OnClickAnnouncement
import com.bannking.app.utils.OnClickAnnouncementDialog
import com.bannking.app.utils.SessionManager
import java.text.DecimalFormat

class TabsAdapter(
    private val context: Context,
    private var list: ArrayList<Data>?,
    private var extraData: ArrayList<ExtraData>?,
    private var hiddenData: ArrayList<HiddenData>?,
    private var extraDataHideList: ArrayList<ExtraData>?,
    private var sessionManager: SessionManager,
    private var listener: MoreDotClick,
    private var listenerAnnouncementDialog: OnClickAnnouncementDialog,
    private var listenerAnnouncement: OnClickAnnouncement
) : RecyclerView.Adapter<TabsAdapter.ViewHolder>() {

    private var expandedIndex: Int = -1 // Track which index is currently expanded

    init {
        setHasStableIds(true) // Enable stable IDs for improved performance
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpensesBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(
        list: ArrayList<Data>,
        extraData: ArrayList<ExtraData>,
        hiddenData: ArrayList<HiddenData>,
        extraDataHideList: ArrayList<ExtraData>
    ) {
        this.list?.clear()
        this.list = list
        this.extraData?.clear()
        this.hiddenData?.clear()
        this.extraDataHideList?.clear()
        this.extraData = extraData
        this.hiddenData = hiddenData
        this.extraDataHideList = extraDataHideList
        Log.d("sdfdsfsdfsfsdfs", "yesssss")
        Log.d("sdfdsfsdfsfsdfs", extraData.size.toString())
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binding = holder.binding

        // Set bottom margin for the last item
        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        params.bottomMargin = if (position == itemCount - 1) 400 else 0
        holder.itemView.layoutParams = params

        // Determine if in dark mode
        val isDarkMode = UiExtension.isDarkModeEnabled()
        if (isDarkMode) {
            setupDarkMode(binding)
        } else {
            setupLightMode(binding)
        }

        // Handle both extra data and normal data
        if (position < extraData!!.size) {
            handleExtraData(binding, position)
        } else {
            handleNormalData(binding, position - extraData!!.size)
        }
    }

    private fun setupDarkMode(binding: ItemExpensesBinding) {
        binding.clAccountList.setBackgroundResource(R.drawable.corner_radius_stroke)
        binding.cvAccountList.setBackgroundResource(R.drawable.corner_radius_stroke)
        binding.txtTransaction.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.txtAstric.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.txtAccountCode.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.tvAvail.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    private fun setupLightMode(binding: ItemExpensesBinding) {
        binding.clAccountList.backgroundTintList =
            ContextCompat.getColorStateList(context, R.color.white)
        binding.cvAccountList.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        binding.txtTransaction.setTextColor(ContextCompat.getColor(context, R.color.black))
        binding.txtAstric.setTextColor(ContextCompat.getColor(context, R.color.black))
        binding.txtAccountCode.setTextColor(ContextCompat.getColor(context, R.color.black))
        binding.tvAvail.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    @SuppressLint("SetTextI18n")
    private fun handleExtraData(binding: ItemExpensesBinding, position: Int) {
        binding.LLInsideBank.visibility = View.GONE
        binding.LLOutSideBank.visibility = View.VISIBLE
        binding.imgBankArrowTab.visibility = View.VISIBLE

        val currentExtraItem = extraData!![position]
        val currentExtraHideShow = extraDataHideList!![position]
        binding.tvBankNameTab.text = currentExtraItem.institutionName
        binding.txtAmountBank.text = "$" + currentExtraItem.accountsData[0].balance.toString()
        binding.txtSubBnkName.text =
            "${currentExtraItem.accountsData[0].accountName}...${currentExtraItem.accountsData[0].accountNumber}"

        // Manage the expanded/collapsed state
        val isExpanded = position == expandedIndex
        binding.rvChildSubBankTypeTab.visibility = if (isExpanded) View.VISIBLE else View.GONE
        binding.imgBankArrowTab.rotation = if (isExpanded) 180f else 0f

        binding.imgBankArrowTab.setOnClickListener {
            val previousExpandedIndex = expandedIndex
            expandedIndex = if (expandedIndex == position) -1 else position
            notifyItemChanged(previousExpandedIndex) // Refresh previously expanded item
            notifyItemChanged(position) // Expand/collapse clicked item
        }

        if (processAccountsData(currentExtraItem.accountsData).size > 0) {
            binding.imgBankArrowTab.visibility = View.VISIBLE
        } else {
            binding.imgBankArrowTab.visibility = View.GONE
        }

        binding.imgMore.setOnClickListener {
            listener.openDialogBoxExtraData(currentExtraItem, currentExtraHideShow.accountsData,hiddenData)
        }

        binding.imgAnnounce.setOnClickListener {
            handleAnnouncementClickExtra(currentExtraItem.accountsData[0])
        }

        binding.LLOutSideBank.setOnClickListener {
            val intent = Intent(context, AccountBalanceActivity::class.java).apply {
                putExtra("institutionId", currentExtraItem.institutionId)
                putExtra("accountNumber", currentExtraItem.accountsData[0].accountNumber)
                putExtra("balance", currentExtraItem.accountsData[0].balance)
                putExtra("accountName", currentExtraItem.accountsData[0].accountName)
                putExtra("accountId", currentExtraItem.accountsData[0].accountId)
            }
            context.startActivity(intent)
        }

        // Set up child RecyclerView
        val childAdapter = SubBankAdapter(context,
            processAccountsData(currentExtraItem.accountsData),
            currentExtraHideShow.accountsData,
            currentExtraItem.institutionId,
            object : ItemClicked {
                override fun onClickedBankItem(
                    position: Int,
                    institutionId: String,
                    accountNumber: String,
                    balance: Int,
                    accountName: String,
                    accountId: String
                ) {
                    val intent = Intent(context, AccountBalanceActivity::class.java).apply {
                        putExtra("institutionId", institutionId)
                        putExtra("accountNumber", accountNumber)
                        putExtra("balance", balance)
                        putExtra("accountName", accountName)
                        putExtra("accountId", accountId)
                    }
                    context.startActivity(intent)
                }

                override fun onClickedBankItemVoice(accountsData: AccountsData) {
                    handleAnnouncementClickExtra(accountsData)
                }

                override fun onClickedBankItemMore(
                    accountsId: String,
                    institutionId: String,
                    accountsList: ArrayList<AccountsData>,
                    extraDataHideList: ArrayList<AccountsData>
                ) {
                    listener.dotForChildren(accountsId, institutionId,accountsList,hiddenData,extraDataHideList)
                }
            })

        binding.rvChildSubBankTypeTab.layoutManager = LinearLayoutManager(context)
        binding.rvChildSubBankTypeTab.adapter = childAdapter
    }

    private fun handleNormalData(binding: ItemExpensesBinding, position: Int) {
        binding.LLInsideBank.visibility = View.VISIBLE
        binding.LLOutSideBank.visibility = View.GONE
        binding.rvChildSubBankTypeTab.visibility = View.GONE
        binding.imgBankArrowTab.visibility = View.GONE

        val currentItem = list!![position]
        val amountTextColor = if (getAmount(currentItem.amount.toString()).startsWith("-")) {
            ContextCompat.getColor(context, R.color.clr_red)
        } else {
            ContextCompat.getColor(context, R.color.clr_blue)
        }

        binding.txtAmount.setTextColor(amountTextColor)
        binding.txtAmount.text =
            "${currentItem.currency!!.icon}${formatMoney(currentItem.amount!!.toDouble())}"
        binding.txtTransaction.text = currentItem.account
        binding.txtAccountCode.text = currentItem.account_code

        binding.imgMore.setOnClickListener {
            listener.openDialogBox(currentItem, list!!)
        }

        binding.imgAnnounce.setOnClickListener {
            handleAnnouncementClick(currentItem)
        }

        binding.LLInsideBank.setOnClickListener {
            val intent = Intent(context, TranSectionDetailActivity::class.java).apply {
                putExtra("account", currentItem.account)
                putExtra("budget_id", currentItem.budget_id)
                putExtra("amount", currentItem.amount)
                putExtra("accMenuId", currentItem.acc_title_id)
                putExtra("account_code", currentItem.account_code)
                putExtra("Id", currentItem.id)
                putExtra("icon", currentItem.currency!!.icon)
                putExtra("userAccountTitle", currentItem.userAccountTitle!!.name)
            }
            context.startActivity(intent)
        }
    }

    private fun handleAnnouncementClickExtra(currentItem: AccountsData) {
        val type = sessionManager.getAnnouncementVoice()
        if (type.equals("")) {
            listenerAnnouncementDialog.clickOnAnnouncementDialogExtra(currentItem)
        } else {
            listenerAnnouncement.clickOnAnnouncementExtra(currentItem)
        }
    }

    private fun handleAnnouncementClick(currentItem: Data) {
        val type = sessionManager.getAnnouncementVoice()
        if (type.equals("")) {
            listenerAnnouncementDialog.clickOnAnnouncementDialog(currentItem)
        } else {
            listenerAnnouncement.clickOnAnnouncement(currentItem)
        }
    }

    private fun processAccountsData(accountsData: ArrayList<AccountsData>): ArrayList<AccountsData> {
        return if (accountsData.size > 1) {
            ArrayList(accountsData.subList(1, accountsData.size))
        } else {
            arrayListOf()
        }
    }

    private fun formatMoney(value: Double): String {
        val decimalFormat = DecimalFormat("#,###.00#")
        return decimalFormat.format(value)
    }

    private fun getAmount(amount: String): String {
        return amount.replace("[^0-9.-]".toRegex(), "")
    }

    override fun getItemCount(): Int {
        return (list?.size ?: 0) + (extraData?.size ?: 0)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(val binding: ItemExpensesBinding) : RecyclerView.ViewHolder(binding.root)
}
