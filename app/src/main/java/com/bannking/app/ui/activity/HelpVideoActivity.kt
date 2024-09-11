package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.databinding.ActivityHelpVideoBinding


class HelpVideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uiColor()
        initUI()
    }

    private fun uiColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding.clHelpVideo.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding.txtFragHeader.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.clHelpVideo.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_card_background
                )
            )
            binding.txtFragHeader.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
            binding.imgBack.setColorFilter(this.resources.getColor(R.color.black))

        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initUI() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.vvHelp.webViewClient = WebViewClient()
        binding.vvHelp.settings.javaScriptEnabled = true
        binding.vvHelp.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.vvHelp.settings.pluginState = WebSettings.PluginState.ON
        binding.vvHelp.settings.mediaPlaybackRequiresUserGesture = false
        binding.vvHelp.webChromeClient = WebChromeClient()
        binding.vvHelp.loadUrl("https://www.youtube.com/watch?v=ltDVcC2s7tU")

    }
}