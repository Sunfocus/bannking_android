package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.UiExtension.drawable
import com.bannking.app.databinding.ItemSpendingBinding
import com.bannking.app.model.SpendingModel
import com.bannking.app.utils.SpendAdapterClick

class SpendingAdapter : RecyclerView.Adapter<SpendingAdapter.ViewHolder> {
    var mBinding: ItemSpendingBinding? = null
    private var context: Context? = null
    private var list: List<SpendingModel>? = null
    private var listner: SpendAdapterClick? = null

    //    int selectedPosition = -1;
    constructor()
    constructor(context: Context?, list: List<SpendingModel>?, listner: SpendAdapterClick) {
        this.context = context
        this.list = list
        this.listner = listner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSpendingBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        if (UiExtension.isDarkModeEnabled()) {
            mBinding!!.cardClick.background = context!!.drawable(
                R.drawable.drawable_unselected_night
            )

        } else {
            mBinding!!.cardClick.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))

        }

        mBinding!!.imgDrawable.setImageResource(list!![position].image)
        mBinding!!.txtTitle.text = list!![position].title
        holder.itemView.setOnClickListener {
            listner?.clickOnSpentTitle(list!![position].title)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: ItemSpendingBinding) : RecyclerView.ViewHolder(itemView.root) {
        init {
            mBinding = itemView
        }
    }
}