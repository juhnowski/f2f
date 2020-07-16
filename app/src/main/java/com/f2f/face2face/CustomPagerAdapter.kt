package com.f2f.face2face

import android.content.Context
import android.widget.LinearLayout
import android.view.ViewGroup
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat.getSystemService
import android.view.LayoutInflater
import android.support.v4.view.PagerAdapter
import android.util.Base64
import android.view.View
import android.widget.ImageView
import com.f2f.face2face.json.Picture


class CustomPagerAdapter(private val context: Context, private val pictures: ArrayList<Picture>) : PagerAdapter() {

    override fun getCount(): Int {
        return pictures.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false)
        val itemView = LayoutInflater.from(context).inflate(R.layout.pager_item, null)

        val imageView = itemView.findViewById(R.id.advert_view_pager_image_view) as ImageView
//        imageView.setImageResource(mResources[position])
        val imageAsBytes = Base64.decode(pictures.get(position).base64pic, 0)
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}
