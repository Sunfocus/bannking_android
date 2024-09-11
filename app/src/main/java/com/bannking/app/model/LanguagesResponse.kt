package com.bannking.app.model

data class LanguagesResponse(
    val `data`: LanguageData,
    val message: String,
    val status: Int
)

data class LanguageData(
    val country_id: Int,
    val country_name: String,
    val createdAt: String,
    val currency_id: Int,
    val email: String,
    val face_id_status: Boolean,
    val free_trial_consumed: Int,
    val id: Int,
    val is_email_verified: Int,
    val language: Language,
    val language_id: Int,
    val login_type: String,
    val name: String,
    val notification: Int,
    val notification_status: Boolean,
    val status: Int,
    val subscriptionStatus: Int,
    val subscriptionType: String,
    val updatedAt: String,
    val username: String
)

data class Language(
    val createdAt: String,
    val id: Int,
    val img: String,
    val name: String,
    val status: Int,
    val updatedAt: String
)