package com.otoniel.testreign.utils

import android.view.View
import android.webkit.WebView

import android.widget.ProgressBar

import android.webkit.WebViewClient


class CustomWebViewClient(private val progressBar: ProgressBar) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        // TODO Auto-generated method stub
        view.loadUrl(url)
        return true
    }

    override fun onPageFinished(view: WebView, url: String) {
        // TODO Auto-generated method stub
        super.onPageFinished(view, url)
        progressBar.visibility = View.GONE
    }

    init {
        progressBar.visibility = View.VISIBLE
    }
}