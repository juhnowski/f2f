package com.f2f.face2face

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.f2f.face2face.json.Category
import com.f2f.face2face.json.Picture

var mPosition_picture = -1

class PictureHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val TAG = "PictureHolder"
    var pic: ImageView

    init {
        pic = itemView.findViewById(R.id.advert_new_image_view_picture)
    }

    fun bind(picture: Picture) {
        if (picture.base64pic.length>4) {
            val imageAsBytes = Base64.decode(picture.base64pic, 0)
            pic.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))
        } else {
            Log.d(TAG,"load from resource")
            pic.setImageResource(R.drawable.photo);
        }
    }
}

class AdvertNewPicturesAdapter(private val context: Context, private val pictureList: List<Picture>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view: View
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_advert_new, parent, false)
        return PictureHolder(view)
    }

    override fun getItemCount(): Int {
        return pictureList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val pictures = pictureList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as PictureHolder).bind(pictures)

        if (mPosition_picture === position) {
            p0.itemView.setBackgroundResource(R.color.colorPrimary);
        } else {
            p0.itemView.setBackgroundResource(R.color.colorWhite);
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            mPosition_picture = position
            notifyDataSetChanged()
        })

    }

}