package com.f2f.face2face

import android.app.ActionBar
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.f2f.face2face.json.*

class ChangePassword : AppCompatActivity(), ResponseProcessable {

    val TAG = "ChangePassword"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val pb = findViewById<ProgressBar>(R.id.pb_change_password)
        pb.isIndeterminate = true
        pb.visibility= View.INVISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                super.onBackPressed()
                overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
                return true
            }
            R.id.save_new_password -> {
                if (check_new_password()) {
                    val pb = findViewById<ProgressBar>(R.id.pb_change_password)
                    pb.visibility= View.VISIBLE
                    ServerTask(prepareJson(),this@ChangePassword,this).execute()
                }
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun check_new_password(): Boolean {
        val pwd1 = findViewById<EditText>(R.id.editText_changePassword_New1).text.toString()
        val pwd2 = findViewById<EditText>(R.id.editText_changePassword_New2).text.toString()
        Log.d(TAG, "pwd1=$pwd1 pwd2=$pwd2")
        val result = pwd1.equals(pwd2)
        if (!result) {
            Toast.makeText(this@ChangePassword, "Новые пароли не совпадают", Toast.LENGTH_SHORT).show()
        }
        return result
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_change_password, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareJson():String{
        val old = findViewById<EditText>(R.id.editText_changePassword_Old).text.toString()
        val pwd = findViewById<EditText>(R.id.editText_changePassword_New1).text.toString()
        Log.d(TAG,"old=$old new=$pwd")
        val msg = ServerRequest(
            getInfo(this@ChangePassword),
            NAME_CHANGE_PASSWORD,
            ChangePassword_RequestData(password = old, new_password = pwd)
        ).json()
        Log.d(TAG,"msg=$msg")
        return msg
    }

    override fun handleResponse(resp: Response) {
        val pb = findViewById<ProgressBar>(R.id.pb_change_password)
        pb.visibility= View.INVISIBLE
        Log.d(TAG,"handleResponse start")
        if (resp?.status.equals("0")) {
            Log.d(TAG,"status = 0")
            super.onBackPressed()
        } else {
            if(resp?.status.equals("4")){
                setLoggedIn(this@ChangePassword,false)
                gotoLogin(this@ChangePassword)
            }

            Log.d(TAG,"handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}")
            Toast.makeText(this@ChangePassword, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(TAG,"handleResponse done")


    }
}
