package com.bannking.app.core

import com.google.gson.JsonObject

class CommonResponseModel(apiResponse: JsonObject?, code: Int) {
    var apiResponse: JsonObject? = null
    var code = 0

    init {
        this.apiResponse = apiResponse
        this.code = code
    }
}