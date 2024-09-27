package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.databinding.ItemBudgetPlannerBinding
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.Data
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.SubBudgetPlanner
import com.bannking.app.utils.OnClickListener
import com.bannking.app.utils.OnClickListenerBudget

class BudgetPlannerNewAdapter(
    private var context: Context?,
    private var list: ArrayList<Data>?,
    private var onClickListener: OnClickListenerBudget?
) : RecyclerView.Adapter<BudgetPlannerNewAdapter.ViewHolder>() {
    private var expandedIndex: Int = -1 // Track which index is currently expanded
    var mBinding: ItemBudgetPlannerBinding? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBudgetPlannerBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.setIsRecyclable(false)
        if (UiExtension.isDarkModeEnabled()) {
            mBinding!!.cvListBudget.setBackgroundResource(R.drawable.corner_radius_stroke) // Dark mode background color
            mBinding!!.txtTitle.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        } else {
            mBinding!!.cvListBudget.setBackgroundColor(ContextCompat.getColor(
                context!!, R.color.white
            ))
            mBinding!!.txtTitle.setTextColor(ContextCompat.getColor(context!!, R.color.clr_text))
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

        var checkedValue = false
        checkedValue = list!![position].isSelected == 1
        mBinding!!.txtSelected.isVisible = checkedValue

        val isExpanded = position == expandedIndex
        mBinding!!.rvChildBudget.visibility = if (isExpanded) View.VISIBLE else View.GONE
//        mBinding!!.imgFloatingChild.visibility = if (isExpanded) View.VISIBLE else View.GONE

        mBinding!!.LLBudget.setOnClickListener {
            // Update the expanded index
            expandedIndex = if (expandedIndex == position) {
                -1
            } else {
                position
            }
            notifyDataSetChanged()
        }
      /*  mBinding!!.imgFloatingChild.setOnClickListener {
            Log.d("floatingIconCLick","${list!![position]}, ${list!![position].subBudgetPlanners[position]}")
            onClickListener!!.clickListerBudget(list!![position], list!![position].subBudgetPlanners[position],"Clicked")
        }*/
//        // Set up child RecyclerView
        val childAdapter =
            ChildAdapter(context, list!![position].subBudgetPlanners, object : OnClickListener {
                override fun clickLister(data: SubBudgetPlanner) {
                    onClickListener!!.clickListerBudget(list!![position], data,"")
                }

                override fun createNewItem(position: Int) {
                    val emptyObject = SubBudgetPlanner()
                    onClickListener!!.clickListerBudget(list!![expandedIndex], emptyObject ,"Clicked")
                }

            })
        mBinding!!.rvChildBudget.adapter = childAdapter


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