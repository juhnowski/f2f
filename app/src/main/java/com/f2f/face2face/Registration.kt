package com.f2f.face2face

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.f2f.face2face.json.*
import com.google.gson.internal.LinkedTreeMap

class Registration : AppCompatActivity(), ResponseProcessable {

    val TAG = "Registration"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        FullScreencall()
        val pb = findViewById<ProgressBar>(R.id.pb_Register_login)
        pb.visibility = View.INVISIBLE
    }

    fun login(v: View) {
        onBackPressed()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.menu_splash_screen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun signup(v: View?) {

        val emailText = findViewById<EditText>(R.id.editText_Register_Login).text.toString()
        val promoText = findViewById<EditText>(R.id.editText_Register_Promocod).text.toString()

        if (checkEmail(emailText)) {
            hide_button()
            saveEmail(emailText)
            val passwordText = findViewById<EditText>(R.id.editText_Register_Password)
            val message = ServerRequest(
                getInfo(this@Registration),
                "SignUp",
                SignUpData(passwordText.text.toString(), promoText)
            ).json()

            Log.d(TAG, "Message = ${message}")
            ServerTask(message, this@Registration, this).execute()
            Log.d(TAG, "SignUp finished")
        } else {
            if (emailText.trim().length == 0) {
                Toast.makeText(this@Registration, "Введите e-mail", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@Registration, "Введен некорректный e-mail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveEmail(email: String) {
        val sharedPref = this.getSharedPreferences(PREFS_FILENAME, 0) ?: return
        with(sharedPref.edit()) {
            putString(EMAIL, email)
            commit()
        }
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed")
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    fun gotoMain() {
        val intent = Intent(this, Face2Face::class.java)
        startActivity(intent)
        overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun hide_button() {
        val tvRegister = findViewById<TextView>(R.id.textView_Register_Register)
        val tvLogin = findViewById<TextView>(R.id.textView_Register_Login)
        val pb = findViewById<ProgressBar>(R.id.pb_Register_login)
        pb.visibility = View.VISIBLE
        tvRegister.visibility = View.INVISIBLE
        tvLogin.visibility = View.INVISIBLE
    }

    fun show_button() {
        val tvRegister = findViewById<TextView>(R.id.textView_Register_Register)
        val tvLogin = findViewById<TextView>(R.id.textView_Register_Login)
        val pb = findViewById<ProgressBar>(R.id.pb_Register_login)
        pb.visibility = View.INVISIBLE
        tvRegister.visibility = View.VISIBLE
        tvLogin.visibility = View.VISIBLE
    }

    fun save(
        flg: Boolean,
        session: String?,
        name: String?,
        surname: String?,
        insta_address: String?,
        insta_client_id: String?
    ) {
        val sharedPref = this.getSharedPreferences(PREFS_FILENAME, 0) ?: return
        with(sharedPref.edit()) {
            putBoolean(SHARED_KEY_IS_LOGGED_IN, flg)
            putString(SESSION, session)
            putString(NAME, name)
            putString(SURNAME, surname)
            putString(INSTA_ADDRESS, insta_address)
            putString(INSTA_CLIENT_ID_KEY, insta_client_id)
            commit()
        }
    }

    override fun handleResponse(resp: Response) {
        if (resp?.status.equals("0")) {
            val ld = resp.data as LinkedTreeMap<String, Any>
            save(
                true,
                ld.get("session").toString(),
                ld.get("name").toString(),
                ld.get("surname").toString(),
                ld.get("insta_address").toString(),
                ld.get("insta_client_id").toString()
            )
            setAvatar(this@Registration, ld.get("avatar").toString())
            Log.d(TAG, "logged in session = ${ld.get("session").toString()}")
            gotoMain()
        } else {
            Log.d(TAG, "${resp?.message}")
            Toast.makeText(this@Registration, resp?.error, Toast.LENGTH_SHORT).show()
        }
        show_button()
    }
}
