package com.bannking.app.model

data class PostVoiceResponse(
    val path: String,
    val success: Boolean
)
data class PostVoiceErrorResponse ( val success: Boolean, val message:String)
