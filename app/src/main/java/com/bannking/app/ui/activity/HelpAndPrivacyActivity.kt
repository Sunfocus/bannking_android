package com.bannking.app.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bannking.app.R
import com.bannking.app.databinding.ActivityHelpAndPrivacyBinding

class HelpAndPrivacyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpAndPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_and_privacy)
    }
}