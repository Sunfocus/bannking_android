package com.bannking.app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.Data
import com.bannking.app.ui.activity.AccountMenuNewActivity
import com.bannking.app.ui.activity.BudgetPlannerNewActivity


class AccountMenuWithSavedAdapterNew(private var context: Context?, list: ArrayList<Data>, private var callback: ((Int) -> Unit)? = null) :
    RecyclerView.Adapter<AccountMenuWithSavedAdapterNew.ViewHolder>() {
    private var list: ArrayList<Data>? = list


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_account_menu_new, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list?.get(position)

        if (position == 0) {
            holder.txtTitle.text = list?.get(0)?.name
        } else if (position == 1) {
            holder.txtTitle.text = list?.get(1)?.name
        }

//        holder.btnCreate.isVisible = model!!.isFillUp

/*        if (model!!.isAccountCreated) {
            holder.btnCreate.text = context?.getString(R.string.str_created)
            holder.btnCreate.background.setTint(context!!.getColor(R.color.clr_green))
            holder.cardAccount.setCardBackgroundColor(context!!.getColor(R.color.clr_semi_dark_gray))
            holder.txtSelectedBugPlanner.isClickable = false
            holder.txtSelectedBugPlanner.isEnabled = false
            holder.imgFloating.isVisible = false
            holder.imgDelete.isVisible = false
        } else {
            holder.cardAccount.setCardBackgroundColor(context!!.getColor(R.color.white))
        }*/

/*        holder.btnCreate.setOnClickListener {
            if (!model.isAccountCreated) {
                callback?.invoke(position)
            } else {
                (context as AccountMenuNewActivity).dialogClass.showError((context as AccountMenuNewActivity).getString(R.string.str_account_created))
            }
        }*/


        if (model?.budgetName != null) {
            holder.txtSelectedBugPlanner.text = model.budgetName
            holder.txtSelectedBugPlanner.setTextColor(ContextCompat.getColor(context!!, R.color.clr_text_blu))
        } else {
            holder.txtSelectedBugPlanner.text =
                context?.getString(R.string.str_select_budget_planner)
            holder.txtSelectedBugPlanner.setTextColor(ContextCompat.getColor(context!!, R.color.clr_red))
        }

        holder.txtSelectedBugPlanner.setOnClickListener {
            val intent = Intent(context, BudgetPlannerNewActivity::class.java)
            intent.putExtra("SelectedItemMenu", model?.id)
            intent.putExtra("position", position.toString())
            (context as AccountMenuNewActivity).resultLauncher.launch(intent)
        }

        holder.imgDelete.setOnClickListener {
//            if (!model.isAccountCreated) {
            if (model!!.isTitleMenuHasAccount) {
                callback?.invoke(position)
            } else {
                (context as AccountMenuNewActivity).adapter?.removeMenuTitle(list?.get(position)?.name.toString())
                list?.removeAt(position)
                notifyItemRemoved(position)
                (context as AccountMenuNewActivity).checkSavedHeaderList()
            }
//            } else {
//                (context as AccountMenuNewActivity).dialogClass.showError((context as AccountMenuNewActivity).getString(R.string.str_account_created))
//            }

        }
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

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txt_title)
        val txtSelectedBugPlanner: TextView = itemView.findViewById(R.id.txt_selected_bug_planner)
        val imgDelete: ImageView = itemView.findViewById(R.id.img_delete)
        val imgFloating: ImageView = itemView.findViewById(R.id.img_floating)
        val cardAccount: CardView = itemView.findViewById(R.id.card_account)
//        val btnCreate: AppCompatButton = itemView.findViewById(R.id.btn_create)
    }


}