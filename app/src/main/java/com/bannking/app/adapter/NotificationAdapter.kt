package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.databinding.ItemNotificationBinding
import com.bannking.app.model.retrofitResponseModel.notificationModel.Data

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private var context: Context? = null
    private var list: List<Data>? = null
    var mBinding: ItemNotificationBinding? = null

    constructor()

    constructor(context: Context?, list: List<Data>?) {
        this.context = context
        this.list = list
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Data>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position % 3 == 0) {
            mBinding!!.imgNotification.background.setTint(
                ContextCompat.getColor(
                    context!!,
                    R.color.notification_1
                )
            )
        } else if (position % 3 == 1) {
            mBinding!!.imgNotification.background.setTint(
                ContextCompat.getColor(
                    context!!,
                    R.color.notification_2
                )
            )
        } else if (position % 3 == 2) {
            mBinding!!.imgNotification.background.setTint(
                ContextCompat.getColor(
                    context!!,
                    R.color.notification_3
                )
            )
        }

        if (position == list!!.size - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 300
            holder.itemView.layoutParams = params
        } else {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 0
            holder.itemView.layoutParams = params
        }

        mBinding!!.txtNotificationMessage.text = list!![position].message
        mBinding!!.txtNotificationTitle.text = list!![position].title
        mBinding!!.txtNotificationDate.text = list!![position].datetime
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    inner class ViewHolder(itemView: ItemNotificationBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        init {
            mBinding = itemView
        }
    }
}