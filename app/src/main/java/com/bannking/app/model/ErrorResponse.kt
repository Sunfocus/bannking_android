package com.bannking.app.model

data class ErrorResponse (var status:Int, var message:String)



data class ErrorResponseOtp(
    val `data`: List<ErrorResponseData>,
    val message: String,
    val status: Int
)

data class ErrorResponseData(
    val location: String,
    val msg: String,
    val path: String,
    val type: String,
    val value: String
)