package com.f2f.face2face

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.internal.LinkedTreeMap

class Racer(val name:String, val surname: String, val promo_count:Int){
    companion object {
        fun parseList(list: List<LinkedTreeMap<String, Any>>, result: ArrayList<Racer>) {
            result.clear()
            list.forEach { ld ->
                run {

                    val name = ld.get("name").toString()
                    val surname = ld.get("surname").toString()

                    val promo_count_str = ld.get("promo_count").toString()
                    var promo_count: Int = -1
                    if (!promo_count_str.contains("null")) {
                        promo_count = promo_count_str.replace(".0", "").toInt()
                    }

                    result.add(Racer(name, surname, promo_count))
                }
            }
        }
    }
}

class RacerHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var nameText: TextView
    var promo_count:TextView

    init {
        nameText = itemView.findViewById(R.id.race_sender_name)
        promo_count = itemView.findViewById(R.id.promo_count)
    }

    fun bind(racer:Racer){
        nameText.setText("${racer.name} ${racer.surname}")
        promo_count.setText(racer.promo_count.toString())
    }
}

class RacerListAdapter(private val context: Context, private val racerList: List<Racer>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view:View
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_racer,parent,false)
        return RacerHolder(view)
    }

    override fun getItemCount(): Int {
        return racerList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val racer = racerList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as RacerHolder).bind(racer)
    }
}