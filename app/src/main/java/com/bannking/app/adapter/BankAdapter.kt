package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.model.BankListData
import com.bannking.app.model.ItemClicked
import com.bannking.app.model.OnClickedItems

class BankAdapter(
    private var mContext: Context,
    private var model: ArrayList<BankListData>,
    private var onClickListener: OnClickedItems
) : RecyclerView.Adapter<BankTypeViewHolder>() {
    private var expandedIndex: Int = -1 // Track which index is currently expanded

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankTypeViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_bank_name, parent, false)
        return BankTypeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return model.size
    }

    override fun onBindViewHolder(
        holder: BankTypeViewHolder, @SuppressLint("RecyclerView") position: Int
    ) {
        val isExpanded = position == expandedIndex
        holder.rvChildSubBankType.visibility = if (isExpanded) View.VISIBLE else View.GONE

        if (isExpanded) {
            holder.imgBankArrow.rotation = 180f
        } else {
            holder.imgBankArrow.rotation = 0f
        }
        holder.tvCashOuts.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.clBankName.setOnClickListener {
            // Update the expanded index
            expandedIndex = if (expandedIndex == position) {
                -1
            } else {
                position
            }
            notifyDataSetChanged()
        }

        val childAdapter = SubBankAdapter(mContext,
            model[position].accountsData,
            model[position].institutionId,
            object : ItemClicked {
                override fun onClickedBankItem(
                    position: Int,
                    institutionId: String,
                    accountNumber: String,
                    balance: Int,
                    accountName: String,
                    accountId: String
                ) {
                    onClickListener.clickedPassChildBankData(
                        position, institutionId, accountNumber, balance, accountName, accountId
                    )
                }

            })
        holder.rvChildSubBankType.adapter = childAdapter

        holder.tvBankName.text = model[position].institutionName
        holder.tvBankNameUpdatedOn.text = "Last updated Oct 8,2024"

    }

}


class BankTypeViewHolder(dataItem: View) : RecyclerView.ViewHolder(dataItem) {
    val tvBankName: TextView = dataItem.findViewById(R.id.tvBankName)
    val clBankName: ConstraintLayout = dataItem.findViewById(R.id.clBankName)
    val rvChildSubBankType: RecyclerView = dataItem.findViewById(R.id.rvChildSubBankType)
    val imgBankArrow: ImageView = dataItem.findViewById(R.id.imgBankArrow)
    val tvBankNameUpdatedOn: TextView = dataItem.findViewById(R.id.tvBankNameUpdatedOn)
    val tvCashOuts: TextView = dataItem.findViewById(R.id.tvCashOuts)
}