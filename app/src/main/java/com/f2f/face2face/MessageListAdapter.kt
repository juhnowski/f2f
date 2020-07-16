package com.f2f.face2face

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.*
import android.view.LayoutInflater
import com.f2f.face2face.json.AdvertInfo
import com.f2f.face2face.json.Picture
import com.google.gson.internal.LinkedTreeMap


val VIEW_TYPE_MESSAGE_SENT = 1
val VIEW_TYPE_MESSAGE_RECEIVED = 2;

class BaseMessage(val createdAt:String, val message: String, val viewType: Int) {
    companion object {

        var chat_user_id = -1

        fun parse(ld: LinkedTreeMap<String, Any>, result: ArrayList<BaseMessage>) {
            if (ld.size > 0) {
                result.clear()
            } else {
                Log.d("resp.data", "Empty data")
                return
            }
            parseChatUsersInfo(ld.get("chat_users_info") as List<LinkedTreeMap<String, Any>>)
            parseChatMessages(ld.get("chat_messages") as List<LinkedTreeMap<String, Any>>, result)
        }

        fun parseChatUsersInfo(list: List<LinkedTreeMap<String, Any>>) {
            if (list.size > 0) {
                val ld = list.get(0) as LinkedTreeMap<String, Any>
                val id_str = ld.get("id").toString()
                if (!id_str.contains("null")) {
                    chat_user_id = id_str.replace(".0", "").toInt()
                }
            }
        }

        fun parseChatMessages(list: List<LinkedTreeMap<String, Any>>, result: ArrayList<BaseMessage>) {
            result.clear()
            list.forEach { ld ->
                run {

                    val user_from_str = ld.get("user_from_id").toString()
                    var user_from_id: Int? = null
                    if (!user_from_str.contains("null")) {
                        user_from_id = user_from_str.replace(".0", "").toInt()
                    }
                    var message_view_type = VIEW_TYPE_MESSAGE_SENT
                    if (user_from_id == chat_user_id) {
                        message_view_type = VIEW_TYPE_MESSAGE_RECEIVED
                    }

                    val line = ld.get("line").toString()
                    val dtime_create = ld.get("dtime_create").toString().substring(0, 16)

                    result.add(
                        BaseMessage(
                            createdAt = dtime_create,
                            message = line,
                            viewType = message_view_type
                        )
                    )

                }
            }
        }

    }
}

class ReceivedMessageHolder(itemView: View) : ViewHolder(itemView) {
    var messageText: TextView
    var timeText: TextView

    init {
        messageText = itemView.findViewById(R.id.text_message_body)
        timeText = itemView.findViewById(R.id.text_message_time)
    }

    fun bind(message: BaseMessage) {
        messageText.setText(message.message)

        // Format the stored timestamp into a readable String using method.
        timeText.setText(message.createdAt)

    }
}

class SentMessageHolder(itemView: View) : ViewHolder(itemView) {
    var messageText: TextView
    var timeText: TextView

    init {
        messageText = itemView.findViewById<View>(R.id.text_message_body) as TextView
        timeText = itemView.findViewById<View>(R.id.text_message_time) as TextView
    }

    fun bind(message: BaseMessage) {
        messageText.setText(message.message)

        // Format the stored timestamp into a readable String using method.
        timeText.setText(message.createdAt)
    }
}

class MessageListAdapter(private val mContext: Context, private val mMessageList: List<BaseMessage>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    // Determines the appropriate ViewType according to the sender of the message.
    override fun getItemViewType(position: Int): Int {
        val message = mMessageList[position] as BaseMessage
        return message.viewType
//        return if (message.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
//            // If the current user is the sender of the message
//            VIEW_TYPE_MESSAGE_SENT
//        } else {
//            // If some other user sent the message
//            VIEW_TYPE_MESSAGE_RECEIVED
//        }
    }

    // Inflates the appropriate layout according to the ViewType.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            return SentMessageHolder(view)
        } else  { //if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            return ReceivedMessageHolder(view)
        }


    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    override fun onBindViewHolder(p0: ViewHolder, position: Int) {
        val message = mMessageList[position] as BaseMessage
        val holder: ViewHolder = p0
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }
}


