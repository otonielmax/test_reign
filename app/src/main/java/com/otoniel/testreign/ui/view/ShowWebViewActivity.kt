package com.otoniel.testreign.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.otoniel.testreign.R
import com.otoniel.testreign.databinding.ActivityShowWebViewBinding
import com.otoniel.testreign.utils.CustomWebViewClient

class ShowWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowWebViewBinding
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        url = intent.getStringExtra("url")!!
    }

    override fun onStart() {
        super.onStart()

        initWebView()
    }

    fun initWebView() {
        binding.webview.loadUrl(url)
        binding.webview.settings.javaScriptEnabled = true
        binding.webview.webViewClient = CustomWebViewClient(binding.progressBar)
    }
}