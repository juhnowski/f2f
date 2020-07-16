package com.f2f.face2face

import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.f2f.face2face.json.*
import com.google.gson.internal.LinkedTreeMap
import java.text.SimpleDateFormat
import java.util.*

var current_chat_id = -1

class MessageListActivity : AppCompatActivity(), ResponseProcessable {
    val TAG = "MessageListActivity"
    private var recycler: RecyclerView? = null
    private var adapter: MessageListAdapter? = null
    val messageList = ArrayList<BaseMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        val pb = findViewById<ProgressBar>(R.id.pb_message_list)
        pb.visibility = View.INVISIBLE

        initMessage()
        recycler = findViewById<View>(R.id.recyclerview_message_list) as RecyclerView?
        adapter = MessageListAdapter(this, messageList)
        recycler!!.layoutManager = LinearLayoutManager(this)
        recycler!!.adapter = adapter
        Log.d(TAG, "onCreate")
    }

    override fun onBackPressed() {
        Log.d(TAG,"onBackPressed")
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                super.onBackPressed()
                overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initMessage(){
        ServerTask(prepareJson(),this@MessageListActivity,this).execute()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun send(v:View){
        val txt = findViewById<TextView>(R.id.edittext_chatbox)
        val dt = Date()
        messageList.add(messageList.size, BaseMessage(getCurrentTimeStamp(dt.time),txt.text.toString(),VIEW_TYPE_MESSAGE_SENT))
        val msg = txt.text.toString()
        ServerTask(prepareAddMessageJson(msg),this@MessageListActivity,HandlerAddMessage(this,this@MessageListActivity)).execute()

        Log.d(TAG,"send ${txt.text}")
        txt.setText("")
        v.hideKeyboard()
    }

    fun getCurrentTimeStamp(dt:Long): String {
        val sdfDate = SimpleDateFormat("HH:mm:ss")
        return sdfDate.format(Date(dt))
    }

    override fun handleResponse(resp: Response) {
        Log.d(TAG,"handleResponse")
        val pb = findViewById<ProgressBar>(R.id.pb_message_list)
        pb.visibility = View.INVISIBLE

        if (resp?.status.equals("0")) {
            Log.d(TAG,"status = 0")
            Log.d(TAG,"resp.data = ${resp.data}")
            //senderList.add(0, Sender("John Silver",5))
            BaseMessage.parse(resp?.data as LinkedTreeMap<String, Any>, messageList)
            adapter?.notifyDataSetChanged()
        } else {
            if(resp?.status.equals("4")){
                setLoggedIn(this@MessageListActivity,false)
                gotoLogin(this@MessageListActivity)
            }
            Log.d(TAG,"${resp?.message}")
            Toast.makeText(this@MessageListActivity, resp?.error, Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG,"handleResponse done")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareJson():String{
        val pb = findViewById<ProgressBar>(R.id.pb_message_list)
        pb.visibility = View.VISIBLE

        val result = ServerRequest(
            getInfo(this@MessageListActivity),
            NAME_GET_MESSAGES,
            GetMessages_RequestData(current_chat_id)
        ).json()
        Log.d(TAG,"message=$result")
        return result

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareAddMessageJson(msg:String):String{
        val pb = findViewById<ProgressBar>(R.id.pb_message_list)
        pb.visibility = View.VISIBLE

        val txt = findViewById<TextView>(R.id.edittext_chatbox)

        val result = ServerRequest(
            getInfo(this@MessageListActivity),
            NAME_ADD_MESSAGE,
            AddMessage_RequestData(current_chat_id,msg)
        ).json()
        Log.d(TAG,"message=$result")
        return result

    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

class HandlerAddMessage(val app: MessageListActivity, val context:Context):ResponseProcessable {
    override fun handleResponse(resp: Response) {
        val pb = app.findViewById<ProgressBar>(R.id.pb_message_list)
        pb.visibility = View.INVISIBLE
        Log.d(app.TAG, "handleResponse AddMessage start")
        if (resp?.status.equals("0")) {
            Log.d(app.TAG, "status = 0")
            Log.d(app.TAG, "data = ${resp?.data.toString()}")
            Toast.makeText(context, "Сообщение отправлено", Toast.LENGTH_SHORT).show()
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(context, false)
                gotoLogin(context)
            }

            Log.d(app.TAG, "handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}")
            Toast.makeText(context, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(app.TAG, "handleResponse done")
    }
}