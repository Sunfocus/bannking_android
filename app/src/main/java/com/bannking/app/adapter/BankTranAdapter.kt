package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.model.GetTransactionData

class BankTranAdapter(
    private var mContext: Context, private var newTransactions: ArrayList<GetTransactionData>
) : RecyclerView.Adapter<BankTranViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankTranViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.item_bank_transaction, parent, false)
        return BankTranViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newTransactions.size
    }

    fun getAddList(): ArrayList<GetTransactionData> {
        return newTransactions
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BankTranViewHolder, position: Int) {

        holder.tvDateOfTrans.text = newTransactions[position].date
        holder.tvTransactionDetails.text =
            "${newTransactions[position].name} CCD ID: ${newTransactions[position].category_id}"

        holder.tvTransactionAmount.text = newTransactions[position].current_balance.toString()
        holder.tvTransactionId.text = "Transaction ID: ${newTransactions[position].transaction_id}"

        val amount = newTransactions[position].amount
        val formattedAmount = if (amount >= 0) {
            "$$amount"
        } else {
            "-$${Math.abs(amount)}"
        }
        holder.tvTransactionDedCred.text = formattedAmount

        if (amount >= 0) {
            holder.tvTransactionDedCred.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context, R.color.clr_text_blu
                )
            )
        } else {
            holder.tvTransactionDedCred.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context, R.color.clr_red
                )
            )
        }

    }

}

class BankTranViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvDateOfTrans: AppCompatTextView = itemView.findViewById(R.id.tvDateOfTrans)
    val tvTransactionDetails: AppCompatTextView = itemView.findViewById(R.id.tvTransactionDetails)
    val tvTransactionId: AppCompatTextView = itemView.findViewById(R.id.tvTransactionId)
    val tvTransactionAmount: AppCompatTextView = itemView.findViewById(R.id.tvTransactionAmount)
    val tvTransactionDedCred: AppCompatTextView = itemView.findViewById(R.id.tvTransactionDedCred)
}