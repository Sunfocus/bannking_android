package com.bannking.app.ui.activity

import android.os.AsyncTask
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityPrivacyPolicyBinding
import com.bannking.app.model.viewModel.PrivacyPolicyViewModel
import com.bannking.app.utils.SessionManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException


class PrivacyPolicyActivity :
    BaseActivity<PrivacyPolicyViewModel, ActivityPrivacyPolicyBinding>(PrivacyPolicyViewModel::class.java) {

    lateinit var viewModel: PrivacyPolicyViewModel
    private val mimeType = "text/html"
    val encoding = "UTF-8"

    companion object {
        var webView: WebView? = null
        var url: String? = null
    }

    override fun getBinding(): ActivityPrivacyPolicyBinding {
        return ActivityPrivacyPolicyBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: PrivacyPolicyViewModel) {
        this.viewModel = viewModel
    }


    override fun initialize() {
//        binding!!.webView.loadDataWithBaseURL(
//            "",
//            utils.reMoveCommaString(htmlData),
//            mimeType,
//            encoding,
//            ""
//        )
        webView = binding!!.webView
        url = sessionManager.getString(SessionManager.APP_PRIVACY_POLICY)
        MyAsynTask().execute()

    }


    override fun setMethod() {
        setOnClickListener()
//        viewModel.getPrivacyPolicyData()
    }

    class MyAsynTask :
        AsyncTask<Void?, Void?, Document?>() {
        override fun doInBackground(vararg p0: Void?): Document? {
            var document: Document? = null
            try {
                document = Jsoup.connect(url).get()
                document.getElementsByClass("navbar header-nav navbar-expand-lg")
                    .remove() // add the class names which you want to remove from WebView.
                document.getElementsByClass("footer footer-dark").remove()
//                document.getElementsByClass("footer-area pt-100").remove()
                document.getElementsByClass("footer-area pt-100").remove()
                document.getElementsByClass("floating-bottom-mobile-menu-overlay").remove()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return document
        }

        override fun onPostExecute(document: Document?) {
            super.onPostExecute(document)
            webView!!.loadDataWithBaseURL(url, document.toString(), "text/html", "utf-8", "")
            webView!!.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            webView!!.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    view.loadUrl(url!!)
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }
        }
    }


    override fun observe() {
//        with(viewModel) {
//            privacyPoliceData.observe(this@PrivacyPolicyActivity) {
//                if (it != null) {
//                    if (it.code in 199..299) {
//                        if (!it.apiResponse?.get("status")
//                                .toString().contains(Constants.STATUSSUCCESS, true)
//                        ) {
//                            dialogClass.showServerErrorDialog()
//                        } else {
//                            val htmlData = it.apiResponse?.get("data").toString()
//                            binding!!.webView.loadDataWithBaseURL(
//                                "",
//                                utils.reMoveCommaString(htmlData),
//                                mimeType,
//                                encoding,
//                                ""
//                            )
//
//                        }
//                    } else if (it.code in 400..500) {
//                        dialogClass.showServerErrorDialog()
//                    }
//                }
//            }
//
//            progressObservable.observe(this@PrivacyPolicyActivity) {
//                if (it != null) {
//                    if (it)
//                        dialogClass.showLoadingDialog()
//                    else
//                        dialogClass.hideLoadingDialog()
//                }
//            }
//        }
    }

    private fun setOnClickListener() {
        with(binding!!) {
            imgBack.setOnClickListener { finish() }
        }
    }

}