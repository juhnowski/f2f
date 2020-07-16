package com.f2f.face2face

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class Promo : AppCompatActivity() {

    val TAG = "Promo"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo)
        val pb = findViewById<ProgressBar>(R.id.pb_promo)
        pb.isIndeterminate = true

        val webview = findViewById<WebView>(R.id.webview_promo)
        webview.getSettings().setJavaScriptEnabled(true)
        webview.setWebViewClient(object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.d(TAG, "onPageStarted url=$url")
                super.onPageStarted(view, url, favicon)
                val pb = findViewById<ProgressBar>(R.id.pb_promo)
                pb.visibility = View.VISIBLE
            }


            override fun onPageFinished(view: WebView, url: String) {
                    val pb = findViewById<ProgressBar>(R.id.pb_promo)
                    pb.visibility = View.INVISIBLE
            }
        })

        webview.loadUrl("http://face2face.global/promo.html")
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }
}
