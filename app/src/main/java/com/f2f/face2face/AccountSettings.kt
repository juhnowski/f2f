package com.f2f.face2face

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.TextInputEditText
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.f2f.face2face.json.*

class AccountSettings : AppCompatActivity(), ResponseProcessable {

    val TAG = "AccountSettings"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)
        FullScreencall()
        val pb = findViewById<ProgressBar>(R.id.pb_account_settings)
        pb.visibility = View.INVISIBLE
    }

    override fun onBackPressed() {
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

    fun change_avatar(v: View){
        startActivity(Intent(this,ChangeAvatar::class.java))
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun change_password(v:View){
        startActivity(Intent(this,ChangePassword::class.java))
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun instagramm_account(v:View){
        startActivity(Intent(this,InstagrammAccount::class.java))
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun change_account(v:View){
        startActivity(Intent(this,ChangeAccount::class.java))
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun logoff(v:View){
        val sharedPref = this.getSharedPreferences(PREFS_FILENAME,0) ?: return
        with (sharedPref.edit()) {
            // putInt(getString(R.string.saved_high_score_key), newHighScore)
            putBoolean("isLoggedIn",false)
            commit()
        }
        Log.d(TAG,"go to Login")
        startActivity(Intent(this,Login::class.java))
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    fun delete_account(v:View){
        ServerTask(prepareJSON(),this@AccountSettings,this).execute()
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareJSON(): String {

        val pb = findViewById<ProgressBar>(R.id.pb_account_settings)
        pb.visibility = View.VISIBLE

        val msg = ServerRequest(
            getInfo(this@AccountSettings),
            NAME_DELETE_ACCOUNT,
            Empty_RequestData()
        ).json()

        Log.d(TAG,"msg=$msg")

        return msg
    }

    override fun handleResponse(resp: Response) {
        val pb = findViewById<ProgressBar>(R.id.pb_account_settings)
        pb.visibility = View.INVISIBLE
        Log.d(TAG, "handleResponse Delete Account")
        if (resp?.status.equals("0")) {
            Log.d(TAG, "status = 0")
            Log.d(TAG, "data = ${resp?.data.toString()}")
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(this@AccountSettings, false)
                gotoLogin(this@AccountSettings)
            }

            Log.d(TAG, "handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}")
            Toast.makeText(this@AccountSettings, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(TAG, "handleResponse done")
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }
}