package com.f2f.face2face

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log

import android.widget.TextView
import android.view.View
import android.view.ViewGroup

import android.view.LayoutInflater
import android.widget.ImageView
import com.f2f.face2face.json.AdvertInfo
import com.f2f.face2face.json.Picture
import com.google.gson.internal.LinkedTreeMap


class Sender(val name: String, val cnt: Int, val chat_id: Int?, val small_avatar: String) {
    companion object {
        fun parseList(list: List<LinkedTreeMap<String, Any>>, result: ArrayList<Sender>) {
            result.clear()
            list.forEach { ld ->
                run {

                    val user_title = ld.get("user_title").toString()

                    val id_str = ld.get("chat_id").toString()
                    var id: Int? = null
                    if (!id_str.contains("null")) {
                        id = id_str.replace(".0", "").toInt()
                    }
                    val small_avatar = ld.get("small_avatar").toString()
                    result.add(
                        Sender(
                            user_title, 0, id, small_avatar
                        )
                    )
                }
            }
        }
    }
}

class SenderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var nameText: TextView
    var cntText: TextView
    var avatar: ImageView

    init {
        nameText = itemView.findViewById(R.id.my_chat_sender_name)
        cntText = itemView.findViewById(R.id.my_chat_cnt)
        avatar = itemView.findViewById(R.id.my_chat_sender_avatar)
    }

    fun bind(sender: Sender) {
        nameText.setText(sender.name)
        cntText.setText(sender.cnt.toString())

        if (sender.small_avatar.length > 4) {
            val imageAsBytes = Base64.decode(sender.small_avatar, 0)
            avatar.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))
        } else {
            avatar.setImageResource(R.drawable.photo);
        }
    }
}

class SenderListAdapter(private val context: Context, private val senderList: List<Sender>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val view: View
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_sender, parent, false)
        return SenderHolder(view)
    }

    override fun getItemCount(): Int {
        return senderList.size
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
        val sender = senderList[position]
        val holder: RecyclerView.ViewHolder = p0
        (holder as SenderHolder).bind(sender)

        p0.itemView.setOnClickListener(View.OnClickListener {
            val chat_id = senderList.get(position).chat_id
            if (chat_id != null) {
                current_chat_id = chat_id
            }
            val intent = Intent(it.context, MessageListActivity::class.java)
            it.context.startActivity(intent)
        })
    }
}