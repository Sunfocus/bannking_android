package com.bannking.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.Data

class BankHeaderAdapter(
    private var mContext: Context,
    private var list: ArrayList<Data>,
    private val onClickItem: ClickedItemHeader
) : RecyclerView.Adapter<BankHeaderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankHeaderViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_header_bank, parent, false)
        return BankHeaderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BankHeaderViewHolder, position: Int) {

        holder.tvHeaderBankOn.text = list[position].name

        holder.rbBankHeader.isChecked = list[position].isChecked


        holder.itemView.setOnClickListener {
            list.map { it.isChecked = false }
            list[position].isChecked = true
            notifyDataSetChanged()

            onClickItem.onClickedValue(position, list[position].id,list[position].isAccountCreated,list,list[position].type)
        }


    }

}

class BankHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvHeaderBankOn: TextView = itemView.findViewById(R.id.tvHeaderBankOn)
    val rbBankHeader: RadioButton = itemView.findViewById(R.id.rbBankHeader)

}

interface ClickedItemHeader {
    fun onClickedValue(
        position: Int,
        id: String?,
        accountCreated: Int,
        list: ArrayList<Data>,
        type: String?
    )
}
