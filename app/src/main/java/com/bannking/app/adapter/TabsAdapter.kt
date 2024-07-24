package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.databinding.ItemExpensesBinding
import com.bannking.app.model.retrofitResponseModel.accountListModel.Data
import com.bannking.app.ui.activity.TranSectionDetailActivity
import com.bannking.app.utils.MoreDotClick
import com.bannking.app.utils.OnClickAnnouncement
import com.bannking.app.utils.OnClickAnnouncementDialog
import com.bannking.app.utils.SessionManager

class TabsAdapter(
    private val context: Context,
    private var list: ArrayList<Data>?,
    private var sessionManager: SessionManager,
    private var listner: MoreDotClick,
    private var listnerAnnouncementDialog: OnClickAnnouncementDialog,
    private var listnerAnnouncement: OnClickAnnouncement
) : RecyclerView.Adapter<TabsAdapter.ViewHolder>() {
    var mBinding: ItemExpensesBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemExpensesBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Data>) {
        this.list?.clear()
        this.list = list
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {


        /*
        * add recyclerView Last Item Margin is
        * name: stackOverFlow
        * Referenced by: https://stackoverflow.com/questions/33754445/margin-padding-in-last-child-in-recyclerview
        * */

        if (position == list!!.size - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 400
            holder.itemView.layoutParams = params
        } else {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 0
            holder.itemView.layoutParams = params
        }


        if (getAmount(list!![position].amount.toString()).startsWith( "-")) {
            mBinding!!.txtAmount.setTextColor(context.resources.getColor(R.color.clr_red))
            mBinding!!.txtAmount.text = list!![position].amount
        } else {
            mBinding!!.txtAmount.setTextColor(context.resources.getColor(R.color.clr_blue))
            mBinding!!.txtAmount.text = list!![position].amount
        }


        mBinding!!.txtTransaction.text = list!![position].account
        mBinding!!.txtAccountCode.text = list!![position].accountCode

        mBinding!!.imgMore.setOnClickListener {
            listner.openDialogBox(list!![position])
        }

        mBinding!!.imgAnnounce.setOnClickListener {
            val type = sessionManager.getAnnouncementVoice()
            if (type.equals("")) {
                listnerAnnouncementDialog.clickOnAnnouncementDialog(list!![position])
            } else {
                listnerAnnouncement.clickOnAnnouncement(list!![position])
            }
        }

        holder.itemView.setOnClickListener {
            val data = list!![position]
            val intent = Intent(context, TranSectionDetailActivity::class.java)
            intent.putExtra("account", data.account)
            intent.putExtra("budget_id", data.budgetId)
            intent.putExtra("amount", data.amount)
            intent.putExtra("accMenuId", data.accMenuId)
            intent.putExtra("account_code", data.accountCode)
            intent.putExtra("Id", data.id)
            context.startActivity(intent)
        }


    }

    fun getAmount(amount : String) : String {
        return amount.replace("[^0-9.-]".toRegex(), "")
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: ItemExpensesBinding) : RecyclerView.ViewHolder(itemView.root) {
        init {
            mBinding = itemView
        }
    }
}