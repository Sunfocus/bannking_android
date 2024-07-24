package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.model.retrofitResponseModel.exploreExpensesModel.Data

class ExploreExpensesAdapter : RecyclerView.Adapter<ExploreExpensesAdapter.ViewHolder> {
    private var context: Context? = null
    private var list: List<Data>? = null
//    var mBinding: ItemExploreExpensesBinding? = null

    constructor()

    constructor(context: Context?, list: ArrayList<Data>) {
        this.context = context
        this.list = list
    }

/*    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Data>) {
        this.list = list
        notifyDataSetChanged()

    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_explore_expenses, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgExpenseColor.setBackgroundColor(Color.parseColor(list!![position].color))
        holder.txtPercentage.text =  list!![position].value+" %"

        holder.txtTitle.text = list!![position].name
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }


    inner class ViewHolder(ItemView: View) :
        RecyclerView.ViewHolder(ItemView) {
        //        val txtTitle=TextView;
        val txtTitle = itemView.findViewById<TextView>(R.id.txt_title)
        val txtPercentage = itemView.findViewById<TextView>(R.id.txt_percentage)
        val imgExpenseColor = itemView.findViewById<ImageView>(R.id.img_expense_color)

        init {

        }
    }
}