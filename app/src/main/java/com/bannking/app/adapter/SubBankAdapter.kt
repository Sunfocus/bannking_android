package com.bannking.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.model.AccountsData
import com.bannking.app.model.ItemClicked

class SubBankAdapter(
    private var mContext: Context,
    private var accountsList: ArrayList<AccountsData>,
    private var institutionId: String,
    var onItemClicked: ItemClicked
) : RecyclerView.Adapter<SubBankViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubBankViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_bank_child, parent, false)
        return SubBankViewHolder(view)
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }

    override fun onBindViewHolder(holder: SubBankViewHolder, position: Int) {

        holder.tvSubBankName.text = accountsList[position].accountName
        holder.tvSubBankAccNo.text = "****${accountsList[position].accountNumber}"
        holder.tvSubBankAmount.text = "$${accountsList[position].balance}"
        holder.itemView.setOnClickListener {
            onItemClicked.onClickedBankItem(
                position,
                institutionId,
                accountsList[position].accountNumber,
                accountsList[position].balance,
                accountsList[position].accountName,accountsList[position].accountId
            )
        }
    }

}

class SubBankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvSubBankName: TextView = itemView.findViewById(R.id.tvSubBankName)
    val tvSubBankAccNo: TextView = itemView.findViewById(R.id.tvSubBankAccNo)
    val tvSubBankAmount: TextView = itemView.findViewById(R.id.tvSubBankAmount)
}