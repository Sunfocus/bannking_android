package com.bannking.app.model.retrofitResponseModel.userModel

import com.google.gson.annotations.SerializedName


data class UserModel(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("extraData") var extraData: String? = null


)

data class Data(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("language_id") var languageId: Int? = null,
    @SerializedName("is_email_verified") var is_email_verified: Int? = null,
    @SerializedName("subscriptionStatus") var subscriptionStatus: Int? = null,
    @SerializedName("face_id_status") var face_id_status: Boolean? = null,
    @SerializedName("notification_status") var notification_status: Boolean? = null,
    @SerializedName("language") var language: Language? = Language(),
    @SerializedName("currency_id") var currencyId: Int? = null,
    @SerializedName("currency") var currency: Currency? =Currency(),
    @SerializedName("profile_image") var image: String? = null,
    @SerializedName("notification") var notification: Int? = null,
    @SerializedName("premium") var premium: Boolean? = null,
    @SerializedName("voice_id") var voice_id: String? = null,
    @SerializedName("voice_gender") var voice_gender: String? = null,
    @SerializedName("language_code") var language_code: String? = null,
    @SerializedName("language_region") var language_region: String? = null,
    @SerializedName("engine") var engine: String? = null

)

data class Currency( @SerializedName("name") var name: String? = null)
data class Language( @SerializedName("name") var name: String? = null)