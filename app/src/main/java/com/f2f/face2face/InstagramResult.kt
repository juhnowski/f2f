package com.f2f.face2face

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class InstagramResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instagram_result)
        val text = findViewById<TextView>(R.id.textView_instagram_result)
        var result: String = intent.getStringExtra(EXTRA_INSTAGRAM_RESULT)
        text.setText(result)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }
}
