package com.f2f.face2face

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View

class Subscription : AppCompatActivity() {

    val TAG = "Subscription"
    private var recycler: RecyclerView? = null
    private var adapter: SubscriptionListAdapter? = null
    val subscriptionList = ArrayList<SubscriptionInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"Subscription start")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        initSubscription()
        recycler = findViewById<View>(R.id.recyclerview_subscription_list) as RecyclerView?
        adapter = SubscriptionListAdapter(this, subscriptionList)
        recycler!!.layoutManager = LinearLayoutManager(this)
        recycler!!.adapter = adapter
        Log.d(TAG,"Subscription created")
    }

    fun initSubscription() {
        subscriptionList.add(0, SubscriptionInfo("Подписка на 1 месяц","Все функции приложения","99\u20BD"))
        subscriptionList.add(1, SubscriptionInfo("Подписка на 3 месяца","Все функции приложения","269\u20BD"))
        subscriptionList.add(2, SubscriptionInfo("Подписка на 6 месяцев","Все функции приложения","499\u20BD"))
        subscriptionList.add(3, SubscriptionInfo(" ","Все функции приложения","949\u20BD"))
        Log.d(TAG,"Subscription inited")
    }

    override fun onBackPressed() {
        Log.d(TAG,"onBackPressed")
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    fun doSubscription(v:View){
        onBackPressed()
    }

    fun showPrivacyPolicy(v:View){
        val intent = Intent(this, PrivacyPolicy::class.java)
        startActivity(intent)
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun showPublicOffert(v:View){
        val intent = Intent(this, PublicOffert::class.java)
        startActivity(intent)
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }
}


