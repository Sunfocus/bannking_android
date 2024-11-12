package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountsData
import com.bannking.app.model.retrofitResponseModel.accountListModel.HiddenData

class ShowHideAdapter(
    private var mContext: Context,
    private var accountsList: ArrayList<AccountsData>,
    private var hiddenData: ArrayList<HiddenData>?,
    private val hideShowListener: HideListener
) : RecyclerView.Adapter<ShowHideViewHolder>() {

    private val switchStateMap = mutableMapOf<String, Boolean>()

    init {
        // Initialize the switch state map based on hiddenData
        initializeSwitchState()
    }
    private fun initializeSwitchState() {
        // Create a set of hidden account IDs for quick lookup
        val hiddenAccountIds = hiddenData?.map { it.accountId }?.toSet() ?: emptySet()

        // Initialize switch state for each account in accountsList
        for (account in accountsList) {
            // Set true if accountId is in hiddenData, false otherwise
            switchStateMap[account.accountId] = account.accountId in hiddenAccountIds
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowHideViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.list_item_show_hide, parent, false)
        return ShowHideViewHolder(view)

    }

    override fun getItemCount(): Int {
        return accountsList.size
    }

    override fun onBindViewHolder(holder: ShowHideViewHolder, position: Int) {
        val account = accountsList[position]

        holder.tvShowHideBank.text = accountsList[position].accountName
        holder.switchHideSHow.isChecked = switchStateMap[account.accountId] ?: false

        holder.switchHideSHow.setOnCheckedChangeListener { _, isChecked ->
            Log.d("switchHideSHow",isChecked.toString())
            hideShowListener.hideShowBank(account.accountId,isChecked)
            switchStateMap[account.accountId] = isChecked
        }

    }



}


class ShowHideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvShowHideBank = itemView.findViewById<TextView>(R.id.tvShowHideBank)

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    val switchHideSHow = itemView.findViewById<Switch>(R.id.switchHideSHow)

}

interface HideListener {
    fun hideShowBank(accountId: String, isChecked: Boolean)
}
