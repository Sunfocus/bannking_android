package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension.getDateMMMDDYYYY
import com.bannking.app.databinding.ItemTransactionBinding
import com.bannking.app.model.retrofitResponseModel.tranSectionListModel.Data

class RecentTranSectionListAdapter(
    private val context: Context,
    private var list: ArrayList<Data>
) : RecyclerView.Adapter<RecentTranSectionListAdapter.ViewHolder>() {
    var mBinding: ItemTransactionBinding? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
        viewHolder.setIsRecyclable(false)
        return viewHolder
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Data>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list[position].transactionTitle.toString().isNotEmpty()) {
            mBinding!!.txtTransactionTitle.isVisible = true
            mBinding!!.txtTransactionTitle.text = list[position].transactionTitle.toString()
        } else {
            mBinding!!.txtTransactionTitle.isVisible = false
        }
        mBinding!!.txtTransactionDescription.text = list[position].transactionPrefix.toString()
        mBinding!!.txtOrigId.isVisible = false
        mBinding!!.txtTransactionId.isVisible = false
        list[position].isOrig?.let {
            if (it) {
                mBinding!!.txtOrigId.isVisible = true
                mBinding!!.txtTransactionId.isVisible = false
                mBinding!!.txtOrigId.text = "ORIG ID:" + list[position].origId.toString()
            } else {
                mBinding!!.txtOrigId.isVisible = false
                mBinding!!.txtTransactionId.isVisible = true
                mBinding!!.txtTransactionId.text =
                    "Transaction ID:" + list[position].transactionId.toString()
            }

        }
        mBinding!!.txtTransactionStatus.isVisible = list[position].is_pending!!
        if (list[position].is_pending == true) {
            mBinding!!.divider2.background.setTint(context.getColor(R.color.color_pending_State))
        } else if (list[position].type.equals("2")) {
            mBinding!!.divider2.background.setTint(context.getColor(R.color.clr_red))
        } else {
            mBinding!!.divider2.background.setTint(context.getColor(R.color.clr_green))
        }
//       mBinding!!.txtTransactionDate.isVisible= !list[position].is_pending!!
        mBinding!!.txtTransactionDate.text =
//            list[position].transactionDate.toString().getDateMMMDDYYYY()
            list[position].transactionDate.toString().getDateMMMDDYYYY()
        mBinding!!.txtTotalAmount.text = list[position].totalAmount.toString()
//        mBinding!!.txtTransactionDescription.text = list[position].transactionTitle.toString()


        if (list[position].type.equals("2")) {
            mBinding!!.txtTransactionAmount.text = "-" + list[position].amount.toString()
            mBinding!!.txtTransactionAmount.setTextColor(context.getColor(R.color.clr_red))
        } else {
            mBinding!!.txtTransactionAmount.text = list[position].amount.toString()
            mBinding!!.txtTransactionAmount.setTextColor(context.getColor(R.color.clr_green))
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(itemView: ItemTransactionBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        init {
            mBinding = itemView
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}