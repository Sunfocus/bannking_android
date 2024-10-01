package com.bannking.app.model.retrofitResponseModel.soundModel

data class UpdateSoundResponse(
    val `data`: UpdateSoundDataUSer,
    val message: String,
    val status: Int
)

data class UpdateSoundDataUSer(
    val country_id: Int,
    val country_name: String,
    val createdAt: String,
    val currency_id: Int,
    val email: String,
    val engine: String,
    val face_id_status: Boolean,
    val free_trial_consumed: Int,
    val id: Int,
    val is_email_verified: Int,
    val language_code: String,
    val language_id: Int,
    val language_region: String,
    val login_type: String,
    val name: String,
    val notification: Int,
    val notification_status: Boolean,
    val status: Int,
    val subscriptionStatus: Int,
    val subscriptionType: String,
    val updatedAt: String,
    val username: String,
    val voice_gender: String,
    val voice_id: String
)