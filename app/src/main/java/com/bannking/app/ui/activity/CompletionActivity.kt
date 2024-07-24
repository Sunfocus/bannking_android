package com.bannking.app.ui.activity

import android.content.Intent
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityCompletionBinding
import com.bannking.app.model.viewModel.CommonViewModel
import com.bannking.app.utils.SessionManager

class CompletionActivity :
    BaseActivity<CommonViewModel, ActivityCompletionBinding>(CommonViewModel::class.java) {

    private fun setOnClickListener() {
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