package com.bannking.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.model.LanguageDataResponse

class LanguageRegionAdapter(
    private val click: ClickLanguageRegion,
    private val mContext: Context,
    val list: ArrayList<LanguageDataResponse>
) : RecyclerView.Adapter<LanguageRegionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageRegionViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.item_language_region, parent, false)
        return LanguageRegionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: LanguageRegionViewHolder, position: Int) {
        darkModeUi(holder)
        holder.tvCountryName.text = list[position].languageName

        holder.itemView.setOnClickListener {
            click.onItemRegionClick(position, list[position].languageCode)
        }

    }

    private fun darkModeUi(holder: LanguageRegionViewHolder) {
        if (UiExtension.isDarkModeEnabled()) {
            holder.clayout.setBackgroundResource(R.drawable.edittext_stroke_blue)
            holder.tvCountryName.setTextColor(ContextCompat.getColor(mContext, R.color.white))
        } else {
            holder.clayout.setBackgroundResource(R.drawable.bg_gender_audio)
            holder.tvCountryName.setTextColor(ContextCompat.getColor(mContext, R.color.black))
        }
    }

    interface ClickLanguageRegion {
        fun onItemRegionClick(position: Int, languageCode: String)
    }
}

class LanguageRegionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvCountryName: AppCompatTextView = itemView.findViewById(R.id.tvCountryName)
    val clayout: ConstraintLayout = itemView.findViewById(R.id.clayout)

}
