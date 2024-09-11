package com.bannking.app.ui.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.databinding.ActivityUrlBinding

class UrlActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUrlBinding
    private var typeOf = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUrlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent!=null){
            typeOf = intent.extras!!.getString("PrivacyTerm")!!
        }
        initUI()
    }

    private fun uiColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding.clMainUrl.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding.txtFragHeader.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding.clMainUrl.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_card_background
                )
            )
            binding.txtFragHeader.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
            binding.imgBack.setColorFilter(this.resources.getColor(R.color.black))

        }
    }

    private fun initUI() {
        uiColor()
        binding.webView1.apply {
            val layerType=if (Build.VERSION.SDK_INT>=19) View.LAYER_TYPE_HARDWARE else View.LAYER_TYPE_SOFTWARE
            setLayerType(layerType,null)
        }

        binding.webView1.settings.javaScriptEnabled = true
        binding.webView1.settings.builtInZoomControls = true
        binding.webView1.settings.displayZoomControls = false

        binding.imgBack.setOnClickListener {
            clearAllData()
            finish()
        }

        if (typeOf == "Term"){
//            clearAllData()
            binding.txtFragHeader.text = "Terms of Service"
            binding.webView1.loadUrl("https://bannking.com/terms")
        }else{
//            clearAllData()
            binding.txtFragHeader.text = "Privacy Policy"
            binding.webView1.loadUrl("https://bannking.com/privacy")
        }

        binding.webView1.webViewClient = object : WebViewClient() {
            override fun onPageCommitVisible(view: WebView, url: String) {
                super.onPageCommitVisible(view, url)

            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        clearAllData()
    }

    private fun clearAllData()
    {
        WebView(this).clearCache(true)
        WebStorage.getInstance().deleteAllData()
        binding.webView1.clearCache(true)
        binding.webView1.clearFormData()
        binding.webView1.clearHistory()
        binding.webView1.clearSslPreferences()
        binding.webView1.requestLayout()
    }
}