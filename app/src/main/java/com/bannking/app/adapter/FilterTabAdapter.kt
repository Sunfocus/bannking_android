package com.bannking.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.model.retrofitResponseModel.headerModel.Data
import com.bannking.app.utils.ItemClickListener


class FilterTabAdapter : RecyclerView.Adapter<FilterTabAdapter.ViewHolder> {
    private var selectedPosition = -1

    lateinit var arrayList: ArrayList<Data>
    private var filterRedList: ArrayList<Data>? = null
    private lateinit var itemClickListener: ItemClickListener

    constructor()

    constructor(
        arrayList: ArrayList<Data>,
        itemClickListener: ItemClickListener,
        filterRedList: ArrayList<Data>?
    ) {
        this.arrayList = arrayList
        this.filterRedList = filterRedList
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder { // Initialize view
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_header_sample, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.radioButton.text = arrayList[position].name
        holder.radioButton.isChecked = (position == selectedPosition)

        if (filterRedList.isNullOrEmpty() || filterRedList?.size == 3) {
            if (selectedPosition == -1) {
                if (position == 2) {
                    selectedPosition = 2
                    holder.radioButton.isChecked = true
                }
            }
        } else {
            if (filterRedList!![0].name.equals(arrayList[position].name)) {
                if (selectedPosition == -1) {
                    selectedPosition = holder.adapterPosition
                    holder.radioButton.isChecked = true
                }
            }
        }

        holder.radioButton.setOnCheckedChangeListener { _, b ->
            if (b) {
                selectedPosition = holder.adapterPosition
                itemClickListener.onClick(holder.radioButton.text.toString())
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    //    fun getSelected() : Data? {
//        return if (selectedPosition != -1) {
//            arrayList.get(selectedPosition)
//        } else null
//    }
    fun getSelected(): ArrayList<Data>? {
        return if (selectedPosition != -1) {
            val returnData: ArrayList<Data> = arrayListOf()
            returnData.add(this.arrayList[selectedPosition])
            returnData
        } else
            null

    }

    override fun getItemCount(): Int {
        return if (arrayList.isNotEmpty()) {
            arrayList.size
        } else 0

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var radioButton: RadioButton

        init {
            radioButton = itemView.findViewById(R.id.radio_button)
        }
    }
}

