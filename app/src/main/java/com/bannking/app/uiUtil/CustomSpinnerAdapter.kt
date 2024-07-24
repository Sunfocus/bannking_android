package com.bannking.app.uiUtil

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bannking.app.R
import com.bannking.app.model.retrofitResponseModel.typeTransactionModel.Data

class CustomSpinnerAdapter() : BaseAdapter() {
    private var context: Context? = null
    var list: ArrayList<Data>? = null

    constructor(context: Context, list: java.util.ArrayList<Data>) : this() {
        this.context = context
        this.list = list
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater: LayoutInflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.label.text = list?.get(position)?.name

        return view
    }

    override fun getItem(position: Int): Any? {
        return list?.get(position)
    }

    override fun getCount(): Int {
        return if (list == null)
            0
        else
            list!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun updateList(list: java.util.ArrayList<Data>) {
        this.list?.clear()
        this.list?.addAll(list)
        notifyDataSetChanged()
    }

    private class ItemHolder(row: View?) {
        val label: TextView

        init {
            label = row?.findViewById(R.id.txt_name) as TextView
        }
    }
}