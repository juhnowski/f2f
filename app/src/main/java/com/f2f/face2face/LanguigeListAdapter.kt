package com.f2f.face2face

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.f2f.face2face.json.Languige

var mPosition_languige = -1
var advertNew_mPosition_languige = -1

class LanguigeHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var name: TextView

    init {
        name = itemView.findViewById(R.id.item_languige_text_view)
    }

    fun bind(languige: Languige) {
        name.setText(languige.name)
    }
}

open class LanguigeListAdapter(private val context: Context, private val languigeList: List<Languige>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view: View
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_languige, parent, false)
        return LanguigeHolder(view)
    }

    override fun getItemCount(): Int {
        return languigeList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val lan = languigeList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as LanguigeHolder).bind(lan)

        if (mPosition_languige === position) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            mPosition_languige = position
            notifyDataSetChanged()
        })
    }

}

class AdvertNewLanguigeListAdapter(private val context: Context, private val languigeList: List<Languige>) :
    LanguigeListAdapter(context, languigeList) {

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val lan = languigeList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as LanguigeHolder).bind(lan)

        if (advertNew_mPosition_languige === position) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            advertNew_mPosition_languige = position
            notifyDataSetChanged()
        })
    }

}