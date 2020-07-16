package com.f2f.face2face

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.f2f.face2face.json.Category

var mPosition_category = -1
var advertNew_mPosition_category = -1

class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var name: TextView

    init {
        name = itemView.findViewById(R.id.item_category_text_view)
    }

    fun bind(category: Category) {
        name.setText(category.name)
    }
}

open class CategoriesListAdapter(private val context: Context, private val categoryList: List<Category>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view: View
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val cat = categoryList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as CategoryHolder).bind(cat)

        if (mPosition_category === position) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            mPosition_category = position
            notifyDataSetChanged()
        })
    }

}

class AdvertNewCategoriesListAdapter(private val context: Context, private val categoryList: List<Category>) :
    CategoriesListAdapter(context,categoryList) {
    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val cat = categoryList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as CategoryHolder).bind(cat)

        if (advertNew_mPosition_category === position) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            advertNew_mPosition_category = position
            notifyDataSetChanged()
        })
    }

}