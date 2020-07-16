package com.f2f.face2face

import android.util.Log
import com.f2f.face2face.json.*
import com.google.gson.internal.LinkedTreeMap
import java.util.*
import kotlin.collections.ArrayList


object Storage {

    val TAG = "Storage"
    @JvmField
    val CATEGORY: ArrayList<Category> = ArrayList<Category>()
    @JvmField
    val KEYWORDS: ArrayList<Keywords> = ArrayList<Keywords>()
    @JvmField
    val LANGUIGE: ArrayList<Languige> = ArrayList<Languige>()
    @JvmField
    val PAYMENTS: ArrayList<Payments> = ArrayList<Payments>()
    @JvmField
    val ORDERS: ArrayList<Orders> = ArrayList<Orders>()

    init {
        ORDERS.add(Orders(id = 0, name = "По дате"))
        ORDERS.add(Orders(id = 1, name = "По цене"))
        ORDERS.add(Orders(id = 1, name = "По ключевым словам"))
    }

    @JvmStatic
    fun parseCategory(list: ArrayList<LinkedTreeMap<String, Any>>) {
        list.forEach { ld ->
            run {
                val catId = ld.get("id").toString().replace(".0", "").toInt()
                val catName = ld.get("name").toString()
                val catParentCategory = ld.get("parent_category").toString()
                var catParentId = 0
                if (!catParentCategory.equals("null")) {
                    catParentId = catParentCategory.replace(".0", "").toInt()
                }

                CATEGORY.add(Category(catId, catName, catParentId))
                if (ld.get("listItems") != null) {
                    val listItems = ld.get("listItems") as ArrayList<LinkedTreeMap<String, Any>>
                    if (listItems != null) {
                        parseCategory(listItems)
                    }
                }
            }
        }
    }

    @JvmStatic
    fun parseKeywords(list: ArrayList<LinkedTreeMap<String, Any>>) {
        list.forEach { ld ->
            run {
                val keyId = ld.get("id").toString().replace(".0", "").toInt()
                val keyName = ld.get("name").toString()
                val keyParentCategory = ld.get("parent_category").toString()
                var keyParentId = 0
                if (!keyParentCategory.equals("null")) {
                    keyParentId = keyParentCategory.replace(".0", "").toInt()
                }
                Log.d(TAG, "Keyword: id=${keyId} name=${keyName} parent_category=${keyParentCategory}")
                KEYWORDS.add(Keywords(keyId, keyName, keyParentId))
            }
        }
    }
}


/*
var arrCategory = ld.get("category") as ArrayList<LinkedTreeMap<String, Any>>
                Log.d(TAG,"arrCategory = ${arrCategory.get(0).toString()}")
                //2018-12-12 14:47:07.065 6605-6605/com.f2f.face2face D/LogInBySession: arrCategory = {id=1.0, name=Your Theme Blog, parent_category=null, listItems=null}

 */


/*
class Category(val id: Int, val name: String, val parent_category: Int, val listItems: List<Category>?)
class Keywords(val id: Int, val name: String, val parent_category: Int)
class Languige(val id: Int, val name: String)
class Payments(val id: Int, val name: String)
 */

/*
val ld = resp.data as LinkedTreeMap<String, Any>
            save(
                true,
                ld.get("session").toString(),
                ld.get("name").toString(),
                ld.get("surname").toString(),
                ld.get("insta_address").toString(),
                ld.get("insta_client_id").toString()
            )
 */