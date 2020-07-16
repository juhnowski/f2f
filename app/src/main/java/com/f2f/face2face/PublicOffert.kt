package com.f2f.face2face

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class PublicOffert : AppCompatActivity() {

    val TAG = "PublicOffert"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_offert)

        val pb = findViewById<ProgressBar>(R.id.pb_public_offert)
        pb.isIndeterminate = true

        val webview = findViewById<WebView>(R.id.webview_public_offert)
        webview.getSettings().setJavaScriptEnabled(true)
        webview.setWebViewClient(object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.d(TAG, "onPageStarted url=$url")
                super.onPageStarted(view, url, favicon)
                val pb = findViewById<ProgressBar>(R.id.pb_public_offert)
                pb.visibility = View.VISIBLE
            }


            override fun onPageFinished(view: WebView, url: String) {
                val pb = findViewById<ProgressBar>(R.id.pb_public_offert)
                pb.visibility = View.INVISIBLE
            }
        })

        webview.loadUrl("http://face2face.global/privacy_policy.html")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }
}
