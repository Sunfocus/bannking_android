package com.bannking.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.databinding.ItemBudgetChildBinding
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.SubBudgetPlanner
import com.bannking.app.utils.OnClickListener

class ChildAdapter(
    private var context: Context?,
    private var list: ArrayList<SubBudgetPlanner>?,
    private var onClickListener: OnClickListener?
) : RecyclerView.Adapter<ChildAdapter.ViewHolder>() {
    var mBinding: ItemBudgetChildBinding? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBudgetChildBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (UiExtension.isDarkModeEnabled()) {
            mBinding!!.clChildBudget.setBackgroundResource(R.drawable.corner_radius_stroke) // Dark mode background color
            mBinding!!.tvBudgetChildItem.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        } else {
            mBinding!!.clChildBudget.setBackgroundColor(ContextCompat.getColor(
                context!!, R.color.white
            ))
            mBinding!!.tvBudgetChildItem.setTextColor(ContextCompat.getColor(context!!, R.color.clr_text))
        }

        if (position == list!!.size - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 20
            holder.itemView.layoutParams = params
        } else {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 0
            holder.itemView.layoutParams = params
        }
        if (list!![position].isSubBudgetSelected == 1) {
            mBinding!!.LLBudgetChild.background = ContextCompat.getDrawable(
                context!!,
                R.drawable.sub_budget_item_selected
            )
        } else {
            mBinding!!.LLBudgetChild.background = ContextCompat.getDrawable(
                context!!,
                R.drawable.sub_budget_item
            )
        }
        mBinding!!.tvBudgetChildItem.text = list!![position].name
        mBinding!!.LLBudgetChild.setOnClickListener { onClickListener!!.clickLister(list!![position]) }
        if (position == list!!.size - 1) {
            mBinding!!.imgFloatingChildClick.visibility = View.VISIBLE
        } else {
            mBinding!!.imgFloatingChildClick.visibility = View.GONE
        }
        mBinding!!.imgFloatingChildClick.setOnClickListener {
            Log.d("floatingIconCLick","Yesss")
            onClickListener!!.createNewItem(position)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    inner class ViewHolder(itemView: ItemBudgetChildBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        init {
            mBinding = itemView
        }
    }
}