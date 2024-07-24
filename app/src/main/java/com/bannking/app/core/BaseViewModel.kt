package com.bannking.app.core

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

open class BaseViewModel(app: Application) : AndroidViewModel(app) {
    var headerTitleList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
}
