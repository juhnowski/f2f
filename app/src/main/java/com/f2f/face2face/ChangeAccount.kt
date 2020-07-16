package com.f2f.face2face

import android.app.ActionBar
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.f2f.face2face.json.*
import android.view.ViewTreeObserver
import android.support.v4.view.ViewCompat.setY
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import android.widget.ProgressBar

class ChangeAccount : AppCompatActivity(), ResponseProcessable {
    val TAG = "ChangeAccount"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_account)

        val name = this.findViewById<EditText>(R.id.change_account_name)
        val email = this.findViewById<EditText>(R.id.change_account_email)
        val surname =  this.findViewById<EditText>(R.id.change_account_surname)
        val pb = this.findViewById<ProgressBar>(R.id.pb_change_account)
        pb.isIndeterminate = true

        name.setText(getName(this@ChangeAccount))
        Log.d(TAG,"getName=${getName(this@ChangeAccount)}")
        email.setText(getEmail(this@ChangeAccount))
        Log.d(TAG,"getEmail(this@ChangeAccount)")
        surname.setText(getSurname(this@ChangeAccount))
        Log.d(TAG,"getSurname(this@ChangeAccount)")
        pb.visibility= View.INVISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_change_account, menu)
        return true
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
            R.id.save_new_account -> {
                val pb = this.findViewById<ProgressBar>(R.id.pb_change_account)
                pb.visibility= View.VISIBLE
                Log.d(TAG,"start ServerTask")
                ServerTask(prepareJson(),this@ChangeAccount,this).execute()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareJson():String{
        val name = this.findViewById<EditText>(R.id.change_account_name)
        val email = this.findViewById<EditText>(R.id.change_account_email)
        val surname =  this.findViewById<EditText>(R.id.change_account_surname)
        return ServerRequest(
            getInfo(this@ChangeAccount),
            NAME_UPDATE_PROFILE,
            UpdateProfile_RequestData(name=name.text.toString(),email=email.text.toString(),surname=surname.text.toString(),avatar="",promocode="")
        ).json()
    }

    override fun handleResponse(resp: Response) {
        Log.d(TAG,"handleResponse")
        val pb = this.findViewById<ProgressBar>(R.id.pb_change_account)
        pb.visibility= View.INVISIBLE
        if (resp?.status.equals("0")) {
            save()
            Log.d(TAG,"status = 0")
            super.onBackPressed()
        } else {
            if(resp?.status.equals("4")){
                setLoggedIn(this@ChangeAccount,false)
                gotoLogin(this@ChangeAccount)
            }
            Log.d(TAG,"${resp?.message}")
            Toast.makeText(this@ChangeAccount, resp?.error, Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG,"handleResponse done")
    }

    fun save(){
        val name = this.findViewById<EditText>(R.id.change_account_name)
        val email = this.findViewById<EditText>(R.id.change_account_email)
        val surname =  this.findViewById<EditText>(R.id.change_account_surname)

        val sharedPref = this.getSharedPreferences(PREFS_FILENAME, 0) ?: return
        with(sharedPref.edit()) {
            putString(EMAIL,email.text.toString())
            putString(NAME,name.text.toString())
            putString(SURNAME,surname.text.toString())
            commit()
        }
    }
}
