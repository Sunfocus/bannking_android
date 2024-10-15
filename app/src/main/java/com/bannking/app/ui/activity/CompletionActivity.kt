package com.bannking.app.ui.activity

import android.content.Intent
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityCompletionBinding
import com.bannking.app.model.viewModel.CommonViewModel
import com.bannking.app.utils.SessionManager
import com.bannking.app.utils.SharedPref

class CompletionActivity :
    BaseActivity<CommonViewModel, ActivityCompletionBinding>(CommonViewModel::class.java) {

    private lateinit var pref: SharedPref
    private fun setOnClickListener() {
        pref = SharedPref(this@CompletionActivity)
        pref.saveBoolean("HideTransactions", true)
        binding!!.btnGetStarted.setOnClickListener {
            sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
            startActivity(Intent(this@CompletionActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun getBinding(): ActivityCompletionBinding {
        return ActivityCompletionBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: CommonViewModel) {
    }

    override fun initialize() {
        setOnClickListener()
    }

    override fun setMethod() {
    }

    override fun observe() {
    }
}