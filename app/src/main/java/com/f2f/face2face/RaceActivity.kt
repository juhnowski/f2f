package com.f2f.face2face

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.f2f.face2face.json.*
import com.google.gson.internal.LinkedTreeMap

class RaceActivity : AppCompatActivity(), ResponseProcessable {

    val TAG = "RaceActivity"
    private var recycler: RecyclerView? = null
    private var adapter: RacerListAdapter? = null
    val racerList = ArrayList<Racer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)

        val pb = findViewById<ProgressBar>(R.id.pb_race)
        pb.visibility = View.INVISIBLE

        initRacers()
        recycler = findViewById<View>(R.id.recyclerview_race_list) as RecyclerView?
        Log.d(TAG, "recycler found")
        adapter = RacerListAdapter(this, racerList)
        recycler!!.layoutManager = LinearLayoutManager(this)
        Log.d(TAG, "recycler set layout manager")
        //TODO fix it!!!
        recycler!!.adapter = adapter

    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
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
            R.id.race_rules -> {
                startActivity(Intent(this, Promo::class.java))
                overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_race, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initRacers() {
//        racerList.add(0, Racer("John Silver"))
//        racerList.add(1,Racer("Crazy"))
//        racerList.add(2,Racer("Honest Gave"))
//        racerList.add(3,Racer("Facked Batman"))

        ServerTask(prepareJson(), this@RaceActivity, this).execute()
    }

    override fun handleResponse(resp: Response) {
        Log.d(TAG, "handleResponse")
        val pb = findViewById<ProgressBar>(R.id.pb_race)
        pb.visibility = View.INVISIBLE
        if (resp?.status.equals("0")) {
            Log.d(TAG, "status = 0")
            Log.d(TAG, "resp.data = ${resp.data}")
            val ld = resp?.data as LinkedTreeMap<String, Any>
            val top20 = ld.get("top20") as List<LinkedTreeMap<String, Any>>
            Racer.parseList(top20 , racerList)
            adapter?.notifyDataSetChanged()
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(this@RaceActivity, false)
                gotoLogin(this@RaceActivity)
            }
            Log.d(TAG, "${resp?.message}")
            Toast.makeText(this@RaceActivity, resp?.error, Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG, "handleResponse done")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareJson(): String {
        val pb = findViewById<ProgressBar>(R.id.pb_race)
        pb.visibility = View.VISIBLE

        val result = ServerRequest(
            getInfo(this@RaceActivity),
            NAME_GET_TOP_20_PROMO,
            Empty_RequestData()
        ).json()
        Log.d(TAG, "message=$result")
        return result
    }

}
