package com.f2f.face2face

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Picture
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.design.widget.TextInputEditText
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.text.SpannableStringBuilder
import android.text.Editable
import android.widget.*
import com.f2f.face2face.json.*
import com.google.gson.internal.LinkedTreeMap


val advertList = ArrayList<AdvertInfo>()

class Face2Face : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ResponseProcessable {

    val TAG = "Face2Face"
    //var recycler: RecyclerView? = null
    var adapter: AdvertListAdapter? = null
    internal var swipeController: SwipeController? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        FullScreencall()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val isSubscribed = getSubscriptionKey(this@Face2Face)
        Log.d(TAG, "isSubscribed = $isSubscribed")

        if (!isSubscribed) {
            val intent = Intent(this, Subscription::class.java)
            startActivity(intent)
        }

        val pb = findViewById<ProgressBar>(R.id.pb_main)
        pb.isIndeterminate = true
        pb.visibility = View.INVISIBLE

        initAdvert()
        val recycler = findViewById<View>(R.id.recyclerview_advert_list) as RecyclerView?

        adapter = AdvertListAdapter(this, advertList)
//        recycler!!.layoutManager = LinearLayoutManager(this)
//        recycler!!.adapter = adapter
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById(R.id.recyclerview_advert_list) as RecyclerView

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        swipeController = SwipeController(object : SwipeControllerActions(this, this@Face2Face) {
            override fun onRightClicked(position: Int) {
                advertList.removeAt(position)
                adapter!!.notifyItemRemoved(position)
                adapter!!.notifyItemRangeChanged(
                    position, adapter!!.getItemCount()
                )
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLeftClicked(position: Int) {
                val advert_id = advertList.get(position).id
                if (advert_id != null) {
                    ServerTask(
                        prepareSetSelectionJson(advert_id),
                        this@Face2Face,
                        HandlerSetSelection(app, context)
                    ).execute()
                }
            }
        }, this@Face2Face)

        val itemTouchhelper = ItemTouchHelper(swipeController as SwipeController)
        itemTouchhelper.attachToRecyclerView(recyclerView)

        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                (swipeController as SwipeController).onDraw(c)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initAdvert() {

        ServerTask(prepareGetAdvertByFiltr(), this@Face2Face, HandlerGetAdvertByFiltr(this, this@Face2Face)).execute()

        advertList.add(
            0, AdvertInfo(
                "A great cake for an advertising post",
                "I need a blogger with at least 3,000 subscribers and the direct...",
                "123",
                1,
                1,
                1,
                1,
                ArrayList(),
                ArrayList<com.f2f.face2face.json.Picture>(),
                id = null,
                small_avatar = "",
                date_time = "",
                name = "",
                surname = "",
                email = ""
            )
        )
    }

    //{"is_offer":1,"lang":null,"limit":20,"filterText":null,"keywords":null,"offset":0,"category":null,"type_payments_id":null}
    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareGetAdvertByFiltr(): String {

        val pb = findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.VISIBLE

        val msg = ServerRequest(
            getInfo(this@Face2Face),
            NAME_GET_ADVERT_BY_FILTR,
            GetAdvertByFiltr_RequestData(
                filterText = null,
                category = null,
                lang = null,
                type_payments_id = null,
                keywords = null,
                offset = 0,
                limit = 20,
                is_offer = 1
            )
        ).json()

        Log.d(TAG, "msg=$msg")
        return msg

        return ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareGetAdvertBySelectionJson(): String {

        val pb = findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.VISIBLE

        val msg = ServerRequest(
            getInfo(this@Face2Face),
            NAME_GET_ADVERT_BY_SELECTION,
            GetAdvertBySelection_RequestData(0, 10, 1)
        ).json()

        Log.d(TAG, "msg=$msg")
        return msg

        return ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareJson(): String {

        val pb = findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.VISIBLE

        val msg = ServerRequest(
            getInfo(this@Face2Face),
            NAME_GET_ADVERT,
            GetAdvert_RequestData(1)
        ).json()

        Log.d(TAG, "msg=$msg")
        return msg

        return ""
    }

    override fun handleResponse(resp: Response) {
        val pb = findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.INVISIBLE
        Log.d(TAG, "handleResponse start")
        if (resp?.status.equals("0")) {
            Log.d(TAG, "status = 0")
            Log.d(TAG, "data = ${resp?.data.toString()}")
            //TODO: check message "message":"Ad not found" -> check error code
            if ((resp?.data.toString().contains("Ad not found")) || (resp?.data.toString().contains("[]"))) {
                startActivity(Intent(this, AdvertNew::class.java))
                overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
            } else {
                AdvertInfo.parse(resp?.data as LinkedTreeMap<String, Any>, advertList)
                adapter?.notifyDataSetChanged()
            }
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(this@Face2Face, false)
                gotoLogin(this@Face2Face)
            }

            Log.d(TAG, "handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}")
            Toast.makeText(this@Face2Face, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(TAG, "handleResponse done")
    }

    fun FullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
//            super.onBackPressed()
//            overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        val mnuTextName = findViewById<TextView>(R.id.textView_menu_Name)
        if (getName(this@Face2Face) != null) {
            mnuTextName.setText(getName(this@Face2Face).toString())
        } else {
            mnuTextName.setText(" ")
        }

        val mnuTextSurname = findViewById<TextView>(R.id.textView_menu_Surname)
        if (getSurname(this@Face2Face) != null) {
            mnuTextSurname.setText(getSurname(this@Face2Face).toString())
        } else {
            mnuTextSurname.setText("")
        }

        val verified = findViewById<TextView>(R.id.textView_verified)
        if (getInstaClientId(this@Face2Face).length > 0) {
            verified.visibility = View.VISIBLE
        } else {
            verified.visibility = View.INVISIBLE
        }

        val imageView = findViewById<ImageView>(R.id.nav_header_main_avatar)
        val base64ava = getAvatar(this@Face2Face)
        if (base64ava.length > 4) {
            Log.d(TAG, "base64ava.length=${base64ava.length}")
            val imageAsBytes = Base64.decode(base64ava, 0)
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))
        } else {
            Log.d(TAG, "load from resource")
            imageView.setImageResource(R.mipmap.face);
        }

        val promocodeEditText = findViewById<EditText>(R.id.promocode_EditText)
        var promoStr = getPromocode(this@Face2Face).toString()
        if (promoStr == null) {
            promoStr = ""
        }
        val editable = SpannableStringBuilder(promoStr)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_options -> {
                startActivity(Intent(this, SearchOptions::class.java))
                overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_account_settings -> {
                startActivity(Intent(this, AccountSettings::class.java))
                overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
            }

            R.id.nav_my_chats -> {
                startActivity(Intent(this, MyChat::class.java))
                overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
            }

            R.id.nav_race -> {
                startActivity(Intent(this, RaceActivity::class.java))
                overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
            }

            R.id.nav_my_offer -> {
                Log.d(TAG, "Start my Offers")
                ServerTask(prepareJson(), this@Face2Face, this).execute()
            }

            R.id.nav_my_selection -> {
                ServerTask(
                    prepareGetAdvertBySelectionJson(),
                    this@Face2Face,
                    HandlerGetAdvertBySelection(this, this@Face2Face)
                ).execute()
            }
//            R.id.nav_slideshow -> {
//
//            }
//            R.id.nav_manage -> {
//
//            }
//            R.id.nav_share -> {
//
//            }
//            R.id.nav_send -> {
//
//            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun account_settings(v: View?) {
        Log.d(TAG, "")
        val intent = Intent(this, Face2Face::class.java)
        startActivity(intent)
    }

    fun advert_view(v: View?) {
        startActivity(Intent(this, AdvertView::class.java))
        overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun send_email(v: View) {
        val bodyText = "Напишите здесь свой вопрос."
        val subject = "Вопрос в техническую поддержку"
        val mailto = "mailto:support@face2face.global?subject=${Uri.encode(subject)}&body=${Uri.encode(bodyText)}"

        val emailIntent = Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this@Face2Face, "Не найдено приложение для отправки почты", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun advert_change_offer(v: View?) {
        val cb = findViewById<CheckBox>(R.id.advert_cb_is_offer)
        Log.d(TAG, "isOffer changed isChecked=${cb.isChecked}")
        var offer = 0
        if (cb.isChecked) {
            offer = 1
        }

        ServerTask(
            prepareSetAdvertStatusJson(offer),
            this@Face2Face,
            HandlerSetAdvertStatus(this, this@Face2Face)
        ).execute()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareSetAdvertStatusJson(offer: Int): String {

        val pb = findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.VISIBLE

        val msg = ServerRequest(
            getInfo(this@Face2Face),
            NAME_SET_ADVERT_STATUS,
            SetAdvertStatus_RequestData(1, offer)
        ).json()

        Log.d(TAG, "msg=$msg")
        return msg
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareSetSelectionJson(advert_id: Int): String {

        val pb = findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.VISIBLE

        val msg = ServerRequest(
            getInfo(this@Face2Face),
            NAME_SET_SELECTION,
            SetSelection_RequestData(advert_id, 1)
        ).json()

        Log.d(TAG, "msg=$msg")
        return msg
    }


}

class HandlerSetAdvertStatus(val app: Face2Face, val context: Context) : ResponseProcessable {
    override fun handleResponse(resp: Response) {
        val pb = app.findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.INVISIBLE
        Log.d(app.TAG, "handleResponse SetAdvertStatus start")
        if (resp?.status.equals("0")) {
            Log.d(app.TAG, "status = 0")
            Log.d(app.TAG, "data = ${resp?.data.toString()}")
            Toast.makeText(context, "Статус объявления изменен", Toast.LENGTH_SHORT).show()
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(context, false)
                gotoLogin(context)
            }

            Log.d(
                app.TAG,
                "handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}"
            )
            Toast.makeText(context, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(app.TAG, "handleResponse done")
    }
}


//2018-12-25 00:27:08.425 6821-6821/com.f2f.face2face D/ServerTask: onPostExecute result={"status":0,"message":"","error":"","data":[]}
class HandlerGetAdvertBySelection(val app: Face2Face, val context: Context) : ResponseProcessable {
    override fun handleResponse(resp: Response) {
        val pb = app.findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.INVISIBLE
        Log.d(app.TAG, "handleResponse GetAdvertBySelection start")
        if (resp?.status.equals("0")) {
            Log.d(app.TAG, "status = 0")
            Log.d(app.TAG, "data = ${resp?.data.toString()}")
            AdvertInfo.parseList(resp?.data as List<LinkedTreeMap<String, Any>>, advertList)
            app.adapter?.notifyDataSetChanged()
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(context, false)
                gotoLogin(context)
            }

            Log.d(
                app.TAG,
                "handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}"
            )
            Toast.makeText(context, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(app.TAG, "handleResponse done")
    }
}

class HandlerGetAdvertByFiltr(val app: Face2Face, val context: Context) : ResponseProcessable {
    override fun handleResponse(resp: Response) {
        val pb = app.findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.INVISIBLE
        Log.d(app.TAG, "handleResponse HandlerGetAdvertByFiltr start")
        if (resp?.status.equals("0")) {
            Log.d(app.TAG, "status = 0")
            Log.d(app.TAG, "data = ${resp?.data.toString()}")
            AdvertInfo.parseList(resp?.data as List<LinkedTreeMap<String, Any>>, advertList)
            app.adapter?.notifyDataSetChanged()
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(context, false)
                gotoLogin(context)
            }

            Log.d(
                app.TAG,
                "handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}"
            )
            Toast.makeText(context, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(app.TAG, "handleResponse done")
    }
}

class HandlerSetSelection(val app: Face2Face, val context: Context) : ResponseProcessable {
    override fun handleResponse(resp: Response) {
        val pb = app.findViewById<ProgressBar>(R.id.pb_main)
        pb.visibility = View.INVISIBLE
        Log.d(app.TAG, "handleResponse HandlerSetSelection start")
        if (resp?.status.equals("0")) {
            Log.d(app.TAG, "status = 0")
            Log.d(app.TAG, "data = ${resp?.data.toString()}")
            Toast.makeText(context, "Объявление сохранено", Toast.LENGTH_SHORT).show()
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(context, false)
                gotoLogin(context)
            }

            Log.d(
                app.TAG,
                "handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}"
            )
            Toast.makeText(context, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(app.TAG, "handleResponse done")
    }
}
