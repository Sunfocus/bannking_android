package com.bannking.app.ui.activity

import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityBankBinding
import com.bannking.app.model.viewModel.SoundViewModel

class BankActivity :
    BaseActivity<SoundViewModel, ActivityBankBinding?>(SoundViewModel::class.java) {
    override fun getBinding(): ActivityBankBinding {
        return ActivityBankBinding.inflate(layoutInflater)
    }

    override fun initialize() {

    }

    override fun setMethod() {

    }

    override fun observe() {

    }

    override fun initViewModel(viewModel: SoundViewModel) {
    }
}