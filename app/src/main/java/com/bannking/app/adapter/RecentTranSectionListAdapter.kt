package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.databinding.ItemTransactionBinding
import com.bannking.app.model.retrofitResponseModel.tranSectionListModel.Data
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class RecentTranSectionListAdapter(
    private val context: Context, private var list: ArrayList<Data>
) : RecyclerView.Adapter<RecentTranSectionListAdapter.ViewHolder>() {
    var mBinding: ItemTransactionBinding? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(context), parent, false
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
        if (UiExtension.isDarkModeEnabled()) {
            mBinding!!.cvListTD.setBackgroundResource(R.drawable.corner_radius_stroke)
            mBinding!!.llTDList.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.dark_mode
                )
            )
            mBinding!!.txtTransactionTitle.setTextColor(
                ContextCompat.getColor(
                    context, R.color.white
                )
            )
            mBinding!!.txtTransactionDescription.setTextColor(
                ContextCompat.getColor(
                    context, R.color.white
                )
            )
            mBinding!!.txtOrigId.setTextColor(ContextCompat.getColor(context, R.color.white))
            mBinding!!.txtTotalAmount.setTextColor(ContextCompat.getColor(context, R.color.white))
            mBinding!!.txtTransactionId.setTextColor(ContextCompat.getColor(context, R.color.white))
            mBinding!!.txtTransactionDate.setTextColor(
                ContextCompat.getColor(
                    context, R.color.white
                )
            )
        } else {
            mBinding!!.txtTotalAmount.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.clr_text_blu
                )
            )
            mBinding!!.cvListTD.backgroundTintList = ContextCompat.getColorStateList(
                context, R.color.white
            )
            mBinding!!.llTDList.setBackgroundColor(
                ContextCompat.getColor(
                    context, R.color.clr_tab_frag
                )
            )
            mBinding!!.txtTransactionTitle.setTextColor(
                ContextCompat.getColor(
                    context, R.color.clr_text
                )
            )
            mBinding!!.txtTransactionDescription.setTextColor(
                ContextCompat.getColor(
                    context, R.color.clr_text
                )
            )
            mBinding!!.txtOrigId.setTextColor(ContextCompat.getColor(context, R.color.clr_text))
            mBinding!!.txtTransactionId.setTextColor(
                ContextCompat.getColor(
                    context, R.color.clr_text
                )
            )
            mBinding!!.txtTransactionDate.setTextColor(
                ContextCompat.getColor(
                    context, R.color.white
                )
            )

        }


        if (list[position].transactionTitle != null) {
            mBinding!!.txtTransactionTitle.isVisible = true
            mBinding!!.txtTransactionTitle.text = list[position].transactionTitle.toString()
        } else {
            mBinding!!.txtTransactionTitle.isVisible = false
        }
        if (list[position].transactionPrefix != null) {
            mBinding!!.txtTransactionDescription.visibility = View.VISIBLE
            mBinding!!.txtTransactionDescription.text = list[position].transactionPrefix.toString()
        }
        mBinding!!.txtOrigId.visibility = View.GONE
        mBinding!!.txtTransactionId.isVisible = false

        /* list[position].isOrig?.let {
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

         }*/
        if (list[position].origId != null) {
            mBinding!!.txtOrigId.visibility = View.GONE
            mBinding!!.txtTransactionId.isVisible = true
            mBinding!!.txtTransactionId.text =
                "Transaction ID:" + list[position].transactionId.toString()
        } else {
            mBinding!!.txtOrigId.visibility = View.VISIBLE
            mBinding!!.txtTransactionId.isVisible = false
            mBinding!!.txtOrigId.text = "ORIG ID:" + list[position].origId.toString()
        }

        when (list[position].cron_status) {
            2 -> {
                mBinding!!.txtTransactionStatus.visibility = View.VISIBLE
            }

            3 -> {
                mBinding!!.txtTransactionStatus.visibility = View.VISIBLE
                mBinding!!.txtTransactionStatus.text = "Failed"
                mBinding!!.txtTransactionStatus.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.clr_red
                    )
                )
            }

            else -> {
                mBinding!!.txtTransactionStatus.visibility = View.GONE
            }
        }

        if (list[position].is_pending == true) {
            mBinding!!.divider2.background.setTint(context.getColor(R.color.color_pending_State))
        } else if (list[position].type.equals("2")) {
            mBinding!!.divider2.background.setTint(context.getColor(R.color.clr_red))
        } else {
            mBinding!!.divider2.background.setTint(context.getColor(R.color.clr_text_blu))
        }
        val format1 = SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH)
        val format2 = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
        // Parse the input date string
        val date = format1.parse(list[position].transactionDate.toString())
        // Format the parsed date to the desired format
        val formattedDate = date?.let { format2.format(it) }

        mBinding!!.txtTransactionDate.text = formattedDate
//            list[position].transactionDate.toString().getDateMMMDDYYYY()
        val amount = formatMoney(list[position].totalAmount!!.toDouble())
        mBinding!!.txtTotalAmount.text =
            list[position].account_data!!.currency!!.icon + amount.toString()
//        mBinding!!.txtTransactionDescription.text = list[position].transactionTitle.toString()


        if (list[position].type.equals("2")) {
            mBinding!!.txtTransactionAmount.text =
                "-" + list[position].account_data!!.currency!!.icon + list[position].amount.toString()
            mBinding!!.txtTransactionAmount.setTextColor(context.getColor(R.color.clr_red))
        } else {
            mBinding!!.txtTransactionAmount.text =
                list[position].account_data!!.currency!!.icon + list[position].amount.toString()
            mBinding!!.txtTransactionAmount.setTextColor(context.getColor(R.color.clr_text_blu))
        }

    }

    private fun formatMoney(value: Double): String {
        val decimalFormat = DecimalFormat("#,###.00#")
        return decimalFormat.format(value)
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