package com.bannking.app.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.databinding.ActivityHelpAndPrivacyBinding

class HelpAndPrivacyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpAndPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpAndPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiColor()
        initUi()
    }


    private fun uiColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding.clMainHelp.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding.ivTerm.setColorFilter(this.resources.getColor(R.color.white))
            binding.ivPrivacy.setColorFilter(this.resources.getColor(R.color.white))
            binding.tvHelpPV.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.clMainHelp.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_card_background
                )
            )
            binding.tvHelpPV.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.imgBack.setColorFilter(this.resources.getColor(R.color.black))
            binding.ivTerm.setColorFilter(this.resources.getColor(R.color.black))
            binding.ivPrivacy.setColorFilter(this.resources.getColor(R.color.black))
        }
    }

    private fun initUi() {
        binding.imgBack.setOnClickListener {
            finish()
        }
        binding.LLHelp.setOnClickListener {
            val intent = Intent(this@HelpAndPrivacyActivity, HelpVideoActivity::class.java)
            startActivity(intent)
        }

        binding.LLTerm.setOnClickListener {
            val intent = Intent(this@HelpAndPrivacyActivity, UrlActivity::class.java)
            intent.putExtra("PrivacyTerm","Term")
            startActivity(intent)
        }
        binding.LLPrivacy.setOnClickListener {
            val intent = Intent(this@HelpAndPrivacyActivity, UrlActivity::class.java)
            intent.putExtra("PrivacyTerm","Privacy")
            startActivity(intent)
        }
    }
}