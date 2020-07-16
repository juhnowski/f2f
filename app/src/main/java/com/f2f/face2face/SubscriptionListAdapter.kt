package com.f2f.face2face

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SubscriptionInfo(val name:String, val functionality:String, val rub:String)

class SubscriptionHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var nameText:TextView //subscription_item_name
    var funcText:TextView//subscription_item_func
    var rubText:TextView//subscription_item_rub

    init{
        nameText = itemView.findViewById(R.id.subscription_item_name)
        funcText = itemView.findViewById(R.id.subscription_item_func)
        rubText  = itemView.findViewById(R.id.subscription_item_rub)
    }

    fun bind(subscription:SubscriptionInfo){
        nameText.setText(subscription.name)
        funcText.setText(subscription.functionality)
        rubText.setText(subscription.rub)
    }
}

class SubscriptionListAdapter(private val context: Context, private val subscriptionList: List<SubscriptionInfo>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view:View
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_subscription_list,parent,false)
        return SubscriptionHolder(view)
    }

    override fun getItemCount(): Int {
        return subscriptionList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val subscription = subscriptionList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as SubscriptionHolder).bind(subscription)
    }
}