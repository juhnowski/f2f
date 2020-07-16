package com.f2f.face2face

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.f2f.face2face.json.ServerResponse
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_instagramm_account.*
import android.webkit.WebView


class InstagrammAccount : AppCompatActivity() {

    val TAG = "InstagrammAccount"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instagramm_account)
        val webview = findViewById<WebView>(R.id.webview)
        val client_id = getInstaClientId(this@InstagrammAccount)
        val redirect_uri = getInstaAddress(this@InstagrammAccount)
        val session = getSession(this@InstagrammAccount)
        Log.d(TAG, "client_id=$client_id")
        Log.d(TAG, "redirect_uri=$redirect_uri")
        Log.d(TAG, "session=$session")

        webview.visibility = View.VISIBLE

        class MyJavaScriptInterface(val context: Context) {

            @JavascriptInterface
            fun processContent(html: String) {
                Log.d(TAG, "html = $html")
                var result: String? = ""
                if (html.startsWith("<head></head><body>{\"status\":0")) {
                    result = "Аккаунт успешно привязан"
                } else {
                    result = html.replace("<head></head><body>{\"status\":0,\"message\":\"\",\"error\":\"", "", true)
                    result = result.replace("\",\"data\":[]}</body>", "", true)
                }
                Log.d(TAG, result)

                val intent = Intent(context, InstagramResult::class.java)
                intent.putExtra(EXTRA_INSTAGRAM_RESULT, result)
                startActivity(intent)
                overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)

            }
        }

        webview.getSettings().setJavaScriptEnabled(true)
        webview.addJavascriptInterface(MyJavaScriptInterface(this@InstagrammAccount), "INTERFACE")
        webview.setWebViewClient(object : WebViewClient() {
            //            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
//                Log.d(TAG,"shouldInterceptRequest url=$url")
//                return super.shouldInterceptRequest(view, url)
//            }
//
//            override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
//                Log.d(TAG,"shouldOverrideKeyEvent event=$event")
//                return super.shouldOverrideKeyEvent(view, event)
//            }
//
//            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
//                Log.d(TAG,"doUpdateVisitedHistory url=$url")
//                super.doUpdateVisitedHistory(view, url, isReload)
//            }
//
//            override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
//                Log.d(TAG,"onRenderProcessGone")
//                return super.onRenderProcessGone(view, detail)
//            }
//
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Log.d(TAG, "onPageStarted url=$url")
                super.onPageStarted(view, url, favicon)
                if (url?.contains("https://face2face.global/check_instagram")!!) {
                    view?.visibility = View.INVISIBLE
                }
            }
//
//            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                Log.d(TAG,"shouldOverrideUrlLoading url=$url")
//                return super.shouldOverrideUrlLoading(view, url)
//            }


            override fun onPageFinished(view: WebView, url: String) {


                if (url.contains("https://face2face.global/check_instagram")) {
                    val pb = findViewById<ProgressBar>(R.id.pb_instagramm_account)
                    pb.visibility = View.INVISIBLE
                    view.visibility = View.INVISIBLE
                }
                Log.d(TAG, "onPageFinished url=$url")
                view.loadUrl("javascript:window.INTERFACE.processContent(document.documentElement.innerHTML);")
                //view.evaluateJavascript("alert(document.documentElement.innerHTML)", ValueCallback((data:String)->))
            }
        })

        webview.loadUrl("https://api.instagram.com/oauth/authorize/?client_id=$client_id&redirect_uri=$redirect_uri&response_type=code&state=$session")
        webview.visibility = View.VISIBLE

        val pb = findViewById<ProgressBar>(R.id.pb_instagramm_account)
        pb.isIndeterminate = true
        pb.visibility = View.VISIBLE
    }

    fun process(result: String) {
        Log.d(TAG, "process result=$result")
        val webview = findViewById<WebView>(R.id.webview)
        webview.visibility = View.INVISIBLE

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                super.onBackPressed()
                overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_instagramm_account, menu)
        return true
    }

}
