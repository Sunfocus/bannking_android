package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.model.ItemClicked
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountsData

class SubBankAdapter(
    private var mContext: Context,
    private var accountsList: ArrayList<AccountsData>,
    private var extraDataHideList: ArrayList<AccountsData>,
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SubBankViewHolder, position: Int) {

        Log.d("sdfsdfdsf",accountsList.size.toString())

        holder.txtAmountBankChild.text = "$"+accountsList[position].balance.toString()
        holder.txtSubBnkNameChild.text =
            "${accountsList[position].accountName}...${accountsList[position].accountNumber}"


        holder.img_announceChild.setOnClickListener {
            onItemClicked.onClickedBankItemVoice(accountsList[position])
        }
        holder.img_moreChild.setOnClickListener {
            onItemClicked.onClickedBankItemMore(accountsList[position].accountId,institutionId,accountsList,extraDataHideList)

        }

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

    val txtSubBnkNameChild: TextView = itemView.findViewById(R.id.txtSubBnkNameChild)
    val txtAmountBankChild: TextView = itemView.findViewById(R.id.txtAmountBankChild)
    val img_moreChild: ImageView = itemView.findViewById(R.id.img_moreChild)
    val img_announceChild: ImageView = itemView.findViewById(R.id.img_announceChild)
}