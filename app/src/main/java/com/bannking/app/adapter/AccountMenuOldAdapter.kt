package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension.drawable
import com.bannking.app.databinding.ItemAccountMenuBinding
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.Data
import com.bannking.app.utils.Constants
import com.bannking.app.utils.CreateOwnMenuTitle
import com.bannking.app.utils.DialogClass


class AccountMenuOldAdapter : RecyclerView.Adapter<AccountMenuOldAdapter.ViewHolder> {
    private var context: Context? = null
    private var list: ArrayList<Data>? = null
    var mBinding: ItemAccountMenuBinding? = null
    private var clickListener: CreateOwnMenuTitle? = null
    var dialogClass: DialogClass? = null

    constructor()

    constructor(
        context: Context?,
        list: ArrayList<Data>?,
        clickListener: CreateOwnMenuTitle,
        dialogClass: DialogClass
    ) {
        this.context = context
        this.dialogClass = dialogClass
        this.list = list
        this.clickListener = clickListener

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Data>) {
        this.list?.clear()
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAccountMenuBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list!![position]
        mBinding!!.txtTitle.text = list!![position].name

        if (model.isSelected) {
            holder.itemView.background = context!!.drawable(R.drawable.drawable_selected)
        } else {
            holder.itemView.background = context!!.drawable(
                R.drawable.drawable_unselected
            )
        }

        holder.itemView.background =
            if (model.isSelected) context!!.drawable(R.drawable.drawable_selected) else context!!.drawable(
                R.drawable.drawable_unselected
            )

//        if (position == list!!.size - 1) {
//            mBinding!!.txtTitle.text = context!!.resources.getString(R.string.str_create_own_title)
//        }

        holder.itemView.setOnClickListener {
            if (list!![position].accountType != Constants.ACCOUNT_CUSTOME) {
                val length = selectedItem
                if (length.size < 2) {
                    model.isSelected = !model.isSelected
                    holder.itemView.background =
                        if (model.isSelected) context!!.drawable(R.drawable.drawable_selected) else context!!.drawable(
                            R.drawable.drawable_unselected
                        )
                } else {
                    if (model.isSelected) {
                        model.isSelected = false
                    } else {
                        dialogClass?.showError(context!!.resources.getString(R.string.str_please_unselect_the_previous_selected_value_then_try))
                    }
                }
                notifyDataSetChanged()
            } else {
                clickListener?.clickOnOwnMenuTitle()
            }
        }

    }

    val selectedItem: List<Data>
        get() {
            val returnList: MutableList<Data> = ArrayList()
            for (data in list!!) {
                if (data.isSelected) {
                    returnList.add(data)
                }
            }
            return returnList
        }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(itemView: ItemAccountMenuBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        init {
            mBinding = itemView
        }
    }

//    @SuppressLint("NotifyDataSetChanged")
//    private fun addItem() {
//        list!!.add(Data("-1", "Create Own", "-1", false))
//        notifyDataSetChanged()
//    }

}