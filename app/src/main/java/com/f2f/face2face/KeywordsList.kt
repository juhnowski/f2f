package com.f2f.face2face

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View

class KeywordsList : AppCompatActivity() {

    val TAG = "KeywordsList"
    private var recycler: RecyclerView? = null
    private var adapter: KeywordsListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keywords_list)

        recycler = findViewById<View>(R.id.recycler_view_keywords) as RecyclerView?

        if (intent.getStringExtra("param").equals("1")) {
            adapter = AdvertNewKeywordsListAdapter(this, Storage.KEYWORDS)
        } else {
            adapter = KeywordsListAdapter(this, Storage.KEYWORDS)
        }

        recycler!!.layoutManager = LinearLayoutManager(this)
        recycler!!.adapter = adapter
    }

    override fun onBackPressed() {
        Log.d(TAG,"onBackPressed")
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
}
