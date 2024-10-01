package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.model.AudioPlayListener
import com.bannking.app.model.retrofitResponseModel.soundModel.Voices

class AudioTypeAdapter(
    private var mContext: Context,
    private var VoicesParameter: ArrayList<Voices>,
    private val onClickItem: AudioPlayListener,
    private var selectedPosition: Int
) : RecyclerView.Adapter<AudioTypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioTypeViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_audio_avail, parent, false)
        return AudioTypeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return VoicesParameter.size
    }

    fun updateSelectedPosition(position: Int) {
        selectedPosition = position
    }
    override fun onBindViewHolder(
        holder: AudioTypeViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        /**Set dark mode**/

//        setDarkMode(holder)

        if (UiExtension.isDarkModeEnabled()) {
            if (position == selectedPosition) {
                holder.clAudioItem.setBackgroundResource(R.drawable.drawable_selected_night)
            } else {
                holder.clAudioItem.setBackgroundResource(R.drawable.drawable_unselected_night)
            }
            holder.tvAudioName.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            holder.tvSubAudioName.setTextColor(ContextCompat.getColor(mContext, R.color.white))
        } else {
            if (position == selectedPosition) {
                holder.clAudioItem.setBackgroundResource(R.drawable.drawable_selected)
            } else {
                holder.clAudioItem.setBackgroundResource(R.drawable.bg_gender_audio)
            }
            holder.tvAudioName.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            holder.tvSubAudioName.setTextColor(ContextCompat.getColor(mContext, R.color.black))
        }

        holder.ivPlayAudio.setOnClickListener {
            onClickItem.clickedItem(position, VoicesParameter[position])
        }
        holder.tvAudioName.text = VoicesParameter[position].VoiceWebname
        holder.tvSubAudioName.text = VoicesParameter[position].VoiceGender
        holder.tvAudioTypeName.text = VoicesParameter[position].VoiceId

    }

    private fun setDarkMode(holder: AudioTypeViewHolder) {
        if (UiExtension.isDarkModeEnabled()) {
            holder.clAudioItem.setBackgroundResource(R.drawable.drawable_selected_night)
            holder.tvAudioName.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            holder.tvSubAudioName.setTextColor(ContextCompat.getColor(mContext, R.color.white))
        } else {
            holder.clAudioItem.setBackgroundResource(R.drawable.bg_gender_audio)
            holder.tvAudioName.setTextColor(ContextCompat.getColor(mContext, R.color.black))
            holder.tvSubAudioName.setTextColor(ContextCompat.getColor(mContext, R.color.grey))
        }
    }

}


class AudioTypeViewHolder(dataItem: View) : RecyclerView.ViewHolder(dataItem) {
    val ivPlayAudio: AppCompatImageView = dataItem.findViewById(R.id.ivPlayAudio)
    val clAudioItem: ConstraintLayout = dataItem.findViewById(R.id.clAudioItem)
    val tvAudioName: AppCompatTextView = dataItem.findViewById(R.id.tvAudioName)
    val tvSubAudioName: AppCompatTextView = dataItem.findViewById(R.id.tvSubAudioName)
    val tvAudioTypeName: AppCompatTextView = dataItem.findViewById(R.id.tvAudioTypeName)
}