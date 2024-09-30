package com.bannking.app.model

import com.bannking.app.model.retrofitResponseModel.soundModel.Voices

interface AudioPlayListener {
    fun clickedItem(position: Int, voices: Voices)
}