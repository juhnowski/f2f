package com.f2f.face2face

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.f2f.face2face.json.Orders
import com.f2f.face2face.json.Payments

var mPosition_order = -1;

class OrdersHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var name: TextView

    init {
        name = itemView.findViewById(R.id.textView_item_oreder)
    }

    fun bind(ord: Orders) {
        name.setText(ord.name)
    }
}

class OrdersListAdapter(private val context: Context, private val ordersList: List<Orders>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "OrdersListAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view: View
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrdersHolder(view)
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val ord = ordersList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as OrdersHolder).bind(ord)

        if (mPosition_order === position) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            mPosition_order = position
            notifyDataSetChanged()
        })
    }

}