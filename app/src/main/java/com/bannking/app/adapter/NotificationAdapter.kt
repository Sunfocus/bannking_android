package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.databinding.ItemNotificationBinding
import com.bannking.app.model.retrofitResponseModel.notificationModel.Data
import com.bannking.app.model.retrofitResponseModel.notificationModel.Notification
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private var context: Context? = null
    private var list: List<Notification>? = null
    var mBinding: ItemNotificationBinding? = null

    constructor()

    constructor(context: Context?, list: List<Notification>?) {
        this.context = context
        this.list = list
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Notification>) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (UiExtension.isDarkModeEnabled()) {
            mBinding!!.txtNotificationTitle.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            mBinding!!.txtNotificationMessage.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            mBinding!!.txtNotificationDate.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            mBinding!!.llNotification.setBackgroundColor(ContextCompat.getColor(context!!, R.color.dark_mode_nav))

        } else {
            mBinding!!.txtNotificationTitle.setTextColor(ContextCompat.getColor(context!!, R.color.black))
            mBinding!!.txtNotificationMessage.setTextColor(ContextCompat.getColor(context!!, R.color.black))
            mBinding!!.txtNotificationDate.setTextColor(ContextCompat.getColor(context!!, R.color.black))
            mBinding!!.llNotification.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))

        }

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

        mBinding!!.txtNotificationMessage.text = list!![position].details
        mBinding!!.txtNotificationTitle.text = list!![position].title
        val zonedDateTime = ZonedDateTime.parse(list!![position].createdAt)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm a", Locale.ENGLISH)
        // Format the parsed date-time to the desired format
        val formattedDate = zonedDateTime.format(outputFormatter)
        mBinding!!.txtNotificationDate.text = formattedDate
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