package com.f2f.face2face

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View

class OrdersList : AppCompatActivity() {

    val TAG = "OrdersList"
    private var recycler: RecyclerView? = null
    private var adapter: OrdersListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_list)

        recycler = findViewById<View>(R.id.recycler_view_orders) as RecyclerView?
        adapter = OrdersListAdapter(this, Storage.ORDERS)
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
