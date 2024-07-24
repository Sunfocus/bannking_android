package com.bannking.app.model

import java.io.Serializable

data class SavedHeaderModel(
    var Headerid: String? = null,
    var Headername: String? = null,
    var budgetid: String? = null,
    var budgetName: String? = null,
) : Serializable