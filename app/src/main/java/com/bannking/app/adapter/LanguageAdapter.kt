package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.core.BaseActivity
import com.bannking.app.model.retrofitResponseModel.languageModel.Data
import com.bannking.app.utils.CheckBoxListener
import com.bannking.app.utils.Constants
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class LanguageAdapter : RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
    lateinit var arrayList: ArrayList<Data>
    private lateinit var itemClickListener: CheckBoxListener
    lateinit var context: Context


    constructor(context: Context, arrayList: ArrayList<Data>, itemClickListener: CheckBoxListener) {
        this.arrayList = arrayList
        this.context = context
        this.itemClickListener = itemClickListener
    }

    constructor()

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Data>) {
        this.arrayList = list
        notifyDataSetChanged()
    }

    private var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_language, parent, false)
        )
    }

    @Suppress("DEPRECATION")
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val position = holder.adapterPosition

        if (UiExtension.isDarkModeEnabled()) {
            holder.txtName.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.txtName.setTextColor(ContextCompat.getColor(context, R.color.black))

        }

        holder.txtName.text = arrayList[position].name


        if (BaseActivity.userModel!!.languageId.toString() == arrayList[position].id) {
            holder.radioButton.isChecked = true
            selectedPosition = position
        }

//        holder.radioButton.isChecked = position == selectedPosition

        holder.radioButton.setOnCheckedChangeListener { _, b ->
            /*  if (BaseActivity.isPremium){*/
            if (b) {
                selectedPosition = holder.adapterPosition
                itemClickListener.onClickCheckBox(
                    arrayList[position].id,
                    Constants.CLICKLANGUAGE,
                    name = arrayList[position].name
                )
            }
            /* }else{
                 val dialogClass=DialogClass(context)
                 dialogClass.showAccountNotSubscriptionDialog(context.resources.getString(R.string.str_non_premium_message))
                 holder.radioButton.isChecked=false
                 notifyDataSetChanged()
             }*/

        }

        Glide.with(context)
            .asBitmap()
            .load(Constants.IMG_BASE_URL + arrayList[position].img)
            .placeholder(R.drawable.glide_dot)
            .error(R.drawable.glide_warning)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    holder.imgFlag.setImageBitmap(resource)
                }

            })

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var radioButton: RadioButton
        var imgFlag: ImageView
        var txtName: TextView

        init {
            radioButton = itemView.findViewById<View>(R.id.radio_button) as RadioButton
            imgFlag = itemView.findViewById<View>(R.id.img_flag) as ImageView
            txtName = itemView.findViewById<View>(R.id.txt_name) as TextView
        }
    }
}