package com.f2f.face2face

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.os.Build
import android.widget.EditText
import com.f2f.face2face.json.*
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat.startActivity
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.internal.LinkedTreeMap
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


val PREFS_FILENAME = "com.f2f.face2face.login.prefs"
val SERVER_URL = "https://face2face.global/request.php"
val USER_TOKEN = "USER_TOKEN"

class Login : AppCompatActivity(), ResponseProcessable {

    val TAG = "Login"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        FullScreencall()
        hide_button()
        val loginBySession = LogInBySession(this@Login, this)
        loginBySession.login()

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume login=${getLoggedIn(this@Login)}")
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
    fun login(v: View?) {
        logIn_signUp(v, "LogIn")
    }

    fun register(v: View?) {
        val intent = Intent(this, Registration::class.java)
        startActivity(intent)
        overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun restorePassword(v: View?) {
        hide_button()
        val emailText = findViewById<EditText>(R.id.editText_Login).text.toString()

        if (checkEmail(emailText)) {
            val rp = ResetPassword(this@Login, this)
            rp.reset(emailText)
        } else {
            if (emailText.trim().length == 0) {
                Toast.makeText(this@Login, "Введите e-mail", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@Login, "Введен некорректный e-mail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun logIn_signUp(v: View?, name: String) {
        Log.d(TAG, "$name started")

        val emailText = findViewById<EditText>(R.id.editText_Login).text.toString()
        if (checkEmail(emailText)) {
            saveEmail(emailText)
            val passwordText = findViewById<EditText>(R.id.editText_Password)
            val message = ServerRequest(
                getInfo(this@Login),
                name,
                SignUpData(passwordText.text.toString(), "")
            ).json()

            Log.d(TAG, "Message = ${message}")
            hide_button()
            ServerTask(message, this@Login, this).execute()
            Log.d(TAG, "$name finished")
        } else {
            if (emailText.trim().length == 0) {
                Toast.makeText(this@Login, "Введите e-mail", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@Login, "Введен некорректный e-mail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun hide_button() {
        val tvRegister = findViewById<TextView>(R.id.textView_Register)
        val tvLogin = findViewById<TextView>(R.id.textView_Login)
        val pb = findViewById<ProgressBar>(R.id.pb_login)
        pb.visibility = View.VISIBLE
        tvRegister.visibility = View.INVISIBLE
        tvLogin.visibility = View.INVISIBLE
    }

    fun show_button() {
        val tvRegister = findViewById<TextView>(R.id.textView_Register)
        val tvLogin = findViewById<TextView>(R.id.textView_Login)
        val pb = findViewById<ProgressBar>(R.id.pb_login)
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

    fun saveEmail(email: String) {
        val sharedPref = this.getSharedPreferences(PREFS_FILENAME, 0) ?: return
        with(sharedPref.edit()) {
            putString(EMAIL, email)
            commit()
        }
    }


    fun gotoMain() {
        val intent = Intent(this, Face2Face::class.java)
        val message = "some token"
        intent.putExtra(USER_TOKEN, message)
        startActivity(intent)
        overridePendingTransition(R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    override fun handleResponse(resp: Response) {
        show_button()
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
            setAvatar(this@Login, ld.get("avatar").toString())
            setPromocode(this@Login, ld.get("promocode").toString())


            var arrCategory = ld.get("category") as ArrayList<LinkedTreeMap<String, Any>>
            Storage.parseCategory(arrCategory)
            Log.d(TAG,"Storage.Category.size = ${Storage.CATEGORY.size}")

            var arrKeywords = ld.get("keywords") as ArrayList<LinkedTreeMap<String, Any>>
            Storage.parseKeywords(arrKeywords)
            Log.d(TAG,"Storage.Keywords.size = ${Storage.KEYWORDS.size}")

            var lang = ld.get("languige").toString()
            Log.d(TAG,"langCategory = ${lang}")
            val lp = LanguigeParser(lang)
            Storage.LANGUIGE.clear()
            Storage.LANGUIGE.addAll(lp.list)
            Log.d(TAG,"kp.list.size = ${lp.list.size}")
            lp.list.forEach { Log.d(TAG, "${(it as Languige).id} = ${(it as Languige).name}")}

            var pay = ld.get("payments").toString()
            Log.d(TAG,"pay = ${pay}")
            val pp = PaymentsParser(pay)
            Storage.PAYMENTS.clear()
            Storage.PAYMENTS.addAll(pp.list)
            Log.d(TAG,"pp.list.size = ${pp.list.size}")
            pp.list.forEach { Log.d(TAG, "${(it as Payments).id} = ${(it as Payments).name}")}

            Log.d(TAG, "logged in session = ${ld.get("session").toString()}")
            gotoMain()
        } else {
            Log.d(TAG, "${resp?.message}")
            if (resp?.status.equals("1")) {
                val restoreText = findViewById<TextView>(R.id.textView_Restore)
                restoreText.visibility = View.VISIBLE
                Log.d(TAG, "need restore password")
            }
            Toast.makeText(this@Login, resp?.error, Toast.LENGTH_SHORT).show()
        }
        show_button()
    }

    class LogInBySession(val context: Context, val login: Login) : ResponseProcessable {

        val TAG = "LogInBySession"

        override fun handleResponse(resp: Response) {
            Log.d(TAG, "handleResponse")
            setLoggedIn(context, true)
            setSession(context, "session_12345")
            setName(context, "Сергей")
            setSurname(context, "Длиннофамильный")

//            if (resp?.status.equals("0")) {
//                Log.d(TAG, "status = 0")
//                val ld = resp.data as LinkedTreeMap<String, Any>
//                setLoggedIn(context, true)
//                setSession(context, ld.get("session").toString())
//                setName(context, ld.get("name").toString())
//                setSurname(context, ld.get("surname").toString())
//
//                var arrCategory = ld.get("category") as ArrayList<LinkedTreeMap<String, Any>>
//                Storage.parseCategory(arrCategory)
//                Log.d(TAG,"Storage.Category.size = ${Storage.CATEGORY.size}")
//
//                var arrKeywords = ld.get("keywords") as ArrayList<LinkedTreeMap<String, Any>>
//                Storage.parseKeywords(arrKeywords)
//                Log.d(TAG,"Storage.Keywords.size = ${Storage.KEYWORDS.size}")
//
//                var lang = ld.get("languige").toString()
//                Log.d(TAG,"langCategory = ${lang}")
//                val lp = LanguigeParser(lang)
//                Storage.LANGUIGE.clear()
//                Storage.LANGUIGE.addAll(lp.list)
//                Log.d(TAG,"kp.list.size = ${lp.list.size}")
//                lp.list.forEach { Log.d(TAG, "${(it as Languige).id} = ${(it as Languige).name}")}
//
//                var pay = ld.get("payments").toString()
//                Log.d(TAG,"pay = ${pay}")
//                val pp = PaymentsParser(pay)
//                Storage.PAYMENTS.clear()
//                Storage.PAYMENTS.addAll(pp.list)
//                Log.d(TAG,"pp.list.size = ${pp.list.size}")
//                pp.list.forEach { Log.d(TAG, "${(it as Payments).id} = ${(it as Payments).name}")}
//
//                Log.d(TAG, "logged in by session")
//                login.gotoMain()
//            } else {
//                Log.d(TAG, "handleResponse error message = ${resp?.message}")
//                if (resp?.status.equals("1")) {
//                    setLoggedIn(context, false)
//                    setSession(context, "")
//                    Log.d(TAG, "Session has been expired")
//                }
//                Toast.makeText(context, resp?.error, Toast.LENGTH_SHORT).show()
//            }

            login.show_button()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun login() {

            if (!getLoggedIn(context)) {
                Log.d(TAG, "loggedIn = false")
                login.show_button()
                return
            }

            login.hide_button()

            val msg = ServerRequest(
                getInfo(context),
                NAME_LOG_IN_BY_SESSION,
                LogInBySession_RequestData(false, "receipt_data")
            ).json()

            Log.d(TAG, "Login message = $msg")
            ServerTask(msg, context, this).execute()
        }

    }

    class ResetPassword(val context: Context, val login: Login) : ResponseProcessable {

        val TAG = "ResetPassword"

        override fun handleResponse(resp: Response) {
            Log.d(TAG, "handleResponse")
            if (resp?.status.equals("0")) {
                Log.d(TAG, "status = 0")
                Toast.makeText(context, "Письмо с новым паролем отправлено Вам на почту.", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "handleResponse error message = ${resp?.message}")
                Toast.makeText(context, resp?.error, Toast.LENGTH_SHORT).show()
            }
            login.show_button()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun reset(resetEmail: String) {

            val msg = ServerRequest(
                getInfo(context),
                NAME_RESET_PASSWORD,
                ResetPassword_RequestData(email = resetEmail, promocode = "")
            ).json()

            Log.d(TAG, "ResetPassword message = $msg")
            ServerTask(msg, context, this).execute()
        }
    }
}
