package com.f2f.face2face

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.f2f.face2face.json.*
import kotlinx.android.synthetic.main.activity_main.*

class SearchOptions : AppCompatActivity(), ResponseProcessable {

    val TAG = "SearchOptions"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_options)
        val pb = findViewById<ProgressBar>(R.id.pb_search_option)
        pb.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        val categories = findViewById<TextView>(R.id.search_options_categories)
        val languige = findViewById<TextView>(R.id.search_options_languige)
        val max_price = findViewById<ConstraintLayout>(R.id.search_options_group_max_price)
        val min_price = findViewById<ConstraintLayout>(R.id.search_options_group_min_price)
        val pay = findViewById<TextView>(R.id.search_options_price_or_barter)
        val keywords = findViewById<TextView>(R.id.search_options_keywords)
        val orders = findViewById<TextView>(R.id.search_options_orders)
       // val textToSearch = findViewById<TextInputEditText>(R.id.search_options_text_to_serch)

        if (mPosition_category === -1){
            categories.text = ""
        } else {
            categories.text = Storage.CATEGORY.get(mPosition_category).name
        }

        if (mPosition_languige === -1){
            languige.text = ""
        } else {
            languige.text = Storage.LANGUIGE.get(mPosition_languige).name
        }

        if (mPosition_payment === -1){
            pay.text = ""
        } else {
            pay.text = Storage.PAYMENTS.get(mPosition_payment).name
            if (pay.text.contains("Barter") || pay.text.contains("Batrer")){
                min_price.visibility = View.INVISIBLE
                max_price.visibility = View.INVISIBLE
            } else {
                min_price.visibility = View.VISIBLE
                max_price.visibility = View.VISIBLE
            }
        }

        keywords.text = ""
        if(keywordsSelected.size > 0){
            keywordsSelected.forEach() {
                if (keywords.text.length > 0 ){
                    keywords.text = keywords.text.toString() + ","
                }
                keywords.text = keywords.text.toString() + Storage.KEYWORDS.get(it).name
                Log.d(TAG,"${Storage.KEYWORDS.get(it).name} keywords.text=${keywords.text.toString()}")
            }
        } else {
            keywords.text = ""
        }

        if (mPosition_order === -1){
            orders.text = ""
        } else {
            orders.text = Storage.ORDERS.get(mPosition_order).name
        }
    }

    fun change_categories(v: View?){
        val inten = Intent(this,CategoriesList::class.java)
        inten.putExtra("param","0");
        startActivity(inten)
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun change_languiges(v: View?){
        val inten = Intent(this,LanguigeList::class.java)
        inten.putExtra("param","0");
        startActivity(inten)
    }

    fun change_barter(v: View?){
        val inten = Intent(this,PaymentList::class.java)
        inten.putExtra("param","0");
        startActivity(inten)
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun change_orders(v: View?){
        startActivity(Intent(this,OrdersList::class.java))
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    fun change_keywords(v:View?){
        val inten = Intent(this,KeywordsList::class.java)
        inten.putExtra("param","0");
        startActivity(inten)
        overridePendingTransition( R.xml.right_to_left1, R.xml.left_to_right_1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun prepareGetAdvertByFiltr(): String {

        val pb = findViewById<ProgressBar>(R.id.pb_search_option)
        pb.visibility = View.VISIBLE
        val textToSearch = findViewById<TextInputEditText>(R.id.search_options_text_to_serch)

        val msg = ServerRequest(
            getInfo(this@SearchOptions),
            NAME_GET_ADVERT_BY_FILTR,
            GetAdvertByFiltr_RequestData(
            filterText = textToSearch.text.toString(),
            category = mPosition_category,
            lang = mPosition_languige,
            type_payments_id = mPosition_payment,
            keywords = keywordsSelected.toList(),
            offset = 0,
            limit = 10,
            is_offer = 1)
        ).json()

        Log.d(TAG,"msg=$msg")
        return msg

        return ""
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_options, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search_options_find -> {
                ServerTask(prepareGetAdvertByFiltr(),this@SearchOptions,this).execute()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun handleResponse(resp: Response) {
        val pb = findViewById<ProgressBar>(R.id.pb_search_option)
        pb.visibility = View.INVISIBLE
        Log.d(TAG, "handleResponse GetAdvertByFiltr")
        if (resp?.status.equals("0")) {
            Log.d(TAG, "status = 0")
            Log.d(TAG, "data = ${resp?.data.toString()}")
        } else {
            if (resp?.status.equals("4")) {
                setLoggedIn(this@SearchOptions, false)
                gotoLogin(this@SearchOptions)
            }

            Log.d(TAG, "handleResponse error message: ${resp?.message} status = ${resp?.status} error=${resp?.error}")
            Toast.makeText(this@SearchOptions, resp?.error, Toast.LENGTH_SHORT).show()

        }
        Log.d(TAG, "handleResponse done")
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.xml.left_to_right, R.xml.right_to_left)
    }

}
