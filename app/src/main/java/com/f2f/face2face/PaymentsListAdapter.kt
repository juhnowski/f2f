package com.f2f.face2face

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.f2f.face2face.json.Payments

var mPosition_payment = -1
var advertNew_mPosition_payment = -1

class PaymentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var name: TextView

    init {
        name = itemView.findViewById(R.id.item_payment_text_view)
    }

    fun bind(pay: Payments) {
        name.setText(pay.name)
    }
}

open class PaymentsListAdapter(private val context: Context, private val paymentsList: List<Payments>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "PaymentsListAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view: View
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        return PaymentsHolder(view)
    }

    override fun getItemCount(): Int {
        return paymentsList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val pay = paymentsList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as PaymentsHolder).bind(pay)

        if (mPosition_payment === position) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            mPosition_payment = position
            notifyDataSetChanged()
        })
    }

}

class AdvertNewPaymentsListAdapter(private val context: Context, private val paymentsList: List<Payments>) :
    PaymentsListAdapter(context,paymentsList) {

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val pay = paymentsList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as PaymentsHolder).bind(pay)

        if (advertNew_mPosition_payment === position) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            advertNew_mPosition_payment = position
            notifyDataSetChanged()
        })
    }

}