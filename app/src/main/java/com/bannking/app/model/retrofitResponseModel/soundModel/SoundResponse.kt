package com.bannking.app.model.retrofitResponseModel.soundModel

data class SoundResponse(
    val `data`: Data,
    val success: Boolean
)

data class Data(
    val count: Int,
    val voices_list: ArrayList<Voices>
)

data class Voices(
    val Country: String,
    val Engine: String,
    val Language: String,
    val LanguageName: String,
    val VoiceEffects: List<String>,
    val VoiceGender: String,
    val VoiceId: String,
    val VoiceWebname: String
)