package com.bannking.app.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.databinding.ItemBudgetPlannerBinding
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.Data
import com.bannking.app.utils.OnClickListener
import com.bannking.app.utils.OnClickListenerBudget

class BudgetPlannerAdapter(
    private var context: Context?,
    private var list: List<Data>?,
    private var onClickListener: OnClickListenerBudget?
) : RecyclerView.Adapter<BudgetPlannerAdapter.ViewHolder>() {

    var mBinding: ItemBudgetPlannerBinding? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBudgetPlannerBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (position == list!!.size - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 300
            holder.itemView.layoutParams = params
        } else {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 0
            holder.itemView.layoutParams = params
        }

        mBinding!!.imgBudgetColor.setBackgroundColor(Color.parseColor(list!![position].color))
        mBinding!!.txtTitle.text = list!![position].name
//        holder.itemView.setOnClickListener { onClickListener!!.clickLister(list!![position]) }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    inner class ViewHolder(itemView: ItemBudgetPlannerBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        init {
            mBinding = itemView
        }
    }
}