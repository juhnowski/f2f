package com.f2f.face2face

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.f2f.face2face.json.Keywords

val keywordsSelected = HashSet<Int>()
val advertNew_keywordsSelected = HashSet<Int>()

class KeywordHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
    var name: TextView

    init {
        name = itemView.findViewById(R.id.item_keyword_text_view)
    }

    fun bind(keywords: Keywords){
        name.setText(keywords.name)
    }
}

open class KeywordsListAdapter(private val context: Context, private val keywordsList: List<Keywords>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view: View
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_keyword, parent, false)
        return KeywordHolder(view)
    }

    override fun getItemCount(): Int {
        return keywordsList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val keywords = keywordsList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as KeywordHolder).bind(keywords)

        if ( keywordsSelected.contains(position) ) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {

            if (keywordsSelected.contains(position)) {
                keywordsSelected.remove(position)
            } else {
                keywordsSelected.add(position)
            }

            notifyDataSetChanged()
        })
    }

}

class AdvertNewKeywordsListAdapter(private val context: Context, private val keywordsList: List<Keywords>) :
    KeywordsListAdapter(context, keywordsList) {

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val keywords = keywordsList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as KeywordHolder).bind(keywords)

        if ( advertNew_keywordsSelected.contains(position) ) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {

            if (advertNew_keywordsSelected.contains(position)) {
                advertNew_keywordsSelected.remove(position)
            } else {
                advertNew_keywordsSelected.add(position)
            }

            notifyDataSetChanged()
        })
    }

}