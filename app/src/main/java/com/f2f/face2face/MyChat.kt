package com.f2f.face2face

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.f2f.face2face.json.*
import com.google.gson.internal.LinkedTreeMap
import kotlinx.android.synthetic.main.activity_my_chat.*

class MyChat : AppCompatActivity(), ResponseProcessable {

    val TAG = "MyChat"
    //private var recycler:RecyclerView? = null
    private var adapter: SenderListAdapter? = null
    val senderList = ArrayList<Sender>()
    internal var swipeChatController: SwipeChatController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_chat)

        val pb = findViewById<ProgressBar>(R.id.pb_my_chat)
        pb.visibility = View.INVISIBLE

        initSenders()
        val recycler = findViewById<View>(R.id.recyclerview_sender_list) as RecyclerView?
        Log.d(TAG, "recycler found")
        adapter = SenderListAdapter(this,senderList)

//        recycler!!.layoutManager = LinearLayoutManager(this)
//         recycler!!.adapter = adapter
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById(R.id.recyclerview_sender_list) as RecyclerView

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        swipeChatController = SwipeChatController(object : SwipeChatControllerActions(this,this@MyChat) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onRightClicked(position: Int) {
                senderList.removeAt(position)
                adapter!!.notifyItemRemoved(position)
                adapter!!.notifyItemRangeChanged(position, adapter!!.getItemCount())
                val del_chat_id = senderList.get(position).chat_id
                if(del_chat_id!=null) {
                    ServerTask(prepareDeleteJson(del_chat_id), this@MyChat, HandlerDeleteChat(app, context)).execute()
                }
            }

//            @RequiresApi(Build.VERSION_CODES.O)
//            override fun onLeftClicked(position: Int) {
//                val advert_id = advertList.get(position).id
//                if (advert_id!=null) {
//                    ServerTask(prepareSetSelectionJson(advert_id), this@Face2Face, HandlerSetSelection(app, context)).execute()
//                }
//            }
        }, this@MyChat)

        val itemTouchhelper = ItemTouchHelper(swipeChatController as SwipeChatController)
        itemTouchhelper.attachToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                (swipeChatController as SwipeChatController).onDraw(c)
            }
        })
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

    fun chat(v: View){
        //mChat_id
       // current_chat_id = 61
       /// startActivity(Intent(this,MessageListActivity::class.java))
        //overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initSenders(){

//        senderList.add(0, Sender("John Silver",5))
//        senderList.add(1,Sender("Crazy",0))
//        senderList.add(2,Sender("Honest Gave",1))
//        senderList.add(3,Sender("Facked Batman",2))

        ServerTask(prepareJson(),this@MyChat,this).execute()
    }


    override fun handleResponse(resp: Response) {
        Log.d(TAG,"handleResponse")
        val pb = findViewById<ProgressBar>(R.id.pb_my_chat)
        pb.visibility = View.INVISIBLE
        if (resp?.status.equals("0")) {
            Log.d(TAG,"status = 0")
            Log.d(TAG,"resp.data = ${resp.data}")
            //senderList.add(0, Sender("John Silver",5))
             Sender.parseList(resp?.data as List<LinkedTreeMap<String, Any>>, senderList)
            adapter?.notifyDataSetChanged()
        } else {
            if(resp?.status.equals("4")){
                setLoggedIn(this@MyChat,false)
                gotoLogin(this@MyChat)
            }
            Log.d(TAG,"${resp?.message}")
            Toast.makeText(this@MyChat, resp?.error, Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG,"handleResponse done")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareJson():String{
        val pb = findViewById<ProgressBar>(R.id.pb_my_chat)
        pb.visibility = View.VISIBLE

        val result = ServerRequest(
            getInfo(this@MyChat),
            NAME_GET_CHATS,
            GetChats_RequestData()
        ).json()
        Log.d(TAG,"message=$result")
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareDeleteJson(chat_id:Int):String{
        val pb = findViewById<ProgressBar>(R.id.pb_my_chat)
        pb.visibility = View.VISIBLE

        val result = ServerRequest(
            getInfo(this@MyChat),
            NAME_DELETE_CHAT,
            DeleteChat_RequestData(chat_id)
        ).json()
        Log.d(TAG,"message=$result")
        return result
    }
}

class HandlerDeleteChat(val app: MyChat, val context: Context):ResponseProcessable {
    override fun handleResponse(resp: Response) {
        val pb = app.findViewById<ProgressBar>(R.id.pb_my_chat)
        pb.visibility = View.INVISIBLE
        Log.d(app.TAG, "handleResponse HandlerDeleteChat start")
        if (resp?.status.equals("0")) {
            Log.d(app.TAG, "status = 0")
            Log.d(app.TAG, "data = ${resp?.data.toString()}")
            Toast.makeText(context, "Чат удален", Toast.LENGTH_SHORT).show()
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