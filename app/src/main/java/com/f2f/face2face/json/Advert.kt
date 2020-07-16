package com.f2f.face2face.json

import android.util.Log

import com.google.gson.internal.LinkedTreeMap

class Picture(
    var base64pic: String,
    val mime_type: String,
    var order_id: String
)

class AddAdvert_RequestData(
    val title: String,
    val description: String,
    val price: String,
    val category: Int,
    val lang: Int,
    val type_payments_id: Int,
    val keywords: List<Int>,
    val pictures: List<Picture>,
    val is_offer: Int,
    val publish_status: Int
) : RequestData

class SetAdvertStatus_RequestData(
    val publish_status: Int,
    val is_offer: Int
) : RequestData

class GetAdvert_RequestData(
    val is_offer: Int
) : RequestData

class AdvertInfo(
    val title: String,
    val description: String,
    val price: String,
    val category_id: Int?,
    val lang_id: Int?,
    val type_payments_id: Int?,
    val publish_status: Int,
    val keywords: ArrayList<Int>,
    val pictures: ArrayList<Picture>,
    // --------------------
    val id: Int?,
    val small_avatar: String?,
    val date_time: String?,
    val name: String?,
    val surname: String?,
    val email: String?
) {
    companion object {
        fun parseList(list: List<LinkedTreeMap<String, Any>>, result: ArrayList<AdvertInfo>) {
            result.clear()
            list.forEach { ld ->
                run {

//            list.forEach { ld ->
//                run {
                    val title = ld.get("title").toString()
                    val description = ld.get("description").toString()
                    val price = ld.get("price").toString()

                    val id_str = ld.get("id").toString()
                    var id: Int?=null
                    if(!id_str.contains("null")){
                        id = id_str.replace(".0", "").toInt()
                    }

                    val category_str = ld.get("category_id").toString()
                    var category_id: Int? = null
                    if (!category_str.contains("null")) {
                        category_id = category_str.replace(".0", "").toInt()
                    }

                    val lang_str = ld.get("lang_id").toString()
                    var lang_id: Int? = null
                    if (!lang_str.contains("null")) {
                        lang_str.replace(".0", "").toInt()
                    }

                    val type_payments_str = ld.get("type_payments_id").toString()
                    var type_payments_id: Int? = null
                    if (!type_payments_str.contains("null")) {
                        type_payments_str.replace(".0", "").toInt()
                    }

                    val publish_status_str = ld.get("publish_status").toString()
                    var publish_status: Int = 0
                    if (!publish_status_str.contains("null")) {
                        publish_status_str.replace(".0", "").toInt()
                    }

                    val keywords = ArrayList<Int>()
                    if (ld.get("keywords") != null) {
                        val listItems = ld.get("keywords") as ArrayList<Any>
                        if (listItems != null) {
                            listItems.forEach { li ->
                                run {
                                    keywords.add(li.toString().replace(".0", "").toInt())
                                }
                            }
                        }
                    }

                    val pictures = ArrayList<Picture>()
                    if (ld.get("pictures") != null) {
                        val listItems = ld.get("pictures") as LinkedTreeMap<String, Any>

                        val mime_type = listItems.get("mime_type").toString()

                        val order_str = listItems.get("order_id").toString()
                        var order_id = 0
                        if (!order_str.contains("null")){
                            order_str.replace(".0", "").toInt()
                        }
                        val base64pic = listItems.get("base64pic").toString()
                        pictures.add(Picture(base64pic, mime_type, order_id.toString()))

                    }

                    val small_avatar = ld.get("small_avatar").toString()
                    val date_time = ld.get("date_time").toString()
                    val name = ld.get("name").toString()
                    val surname = ld.get("surname").toString()
                    val email= ld.get("email").toString()

                    result.add(
                        AdvertInfo(
                            title = title,
                            description = description,
                            price = price,
                            category_id = category_id,
                            type_payments_id = type_payments_id,
                            keywords = keywords,
                            pictures = pictures,
                            lang_id = lang_id,
                            publish_status = publish_status,
                            id = id,
                            small_avatar = small_avatar,
                            date_time = date_time,
                            name = name,
                            surname = surname,
                            email = email
                        )
                    )
//                }
//            }
                }
            }
        }

        fun parse(ld: LinkedTreeMap<String, Any>, result: ArrayList<AdvertInfo>) {

//            list.forEach { ld ->
//                run {
            val title = ld.get("title").toString()
            val description = ld.get("description").toString()
            val price = ld.get("price").toString()

            val category_str = ld.get("category_id").toString()
            var category_id: Int? = null
            if (!category_str.contains("null")) {
                category_id = category_str.replace(".0", "").toInt()
            }

            val lang_str = ld.get("lang_id").toString()
            var lang_id: Int? = null
            if (!lang_str.contains("null")) {
                lang_str.replace(".0", "").toInt()
            }

            val type_payments_str = ld.get("type_payments_id").toString()
            var type_payments_id: Int? = null
            if (!type_payments_str.contains("null")) {
                type_payments_str.replace(".0", "").toInt()
            }

            val publish_status_str = ld.get("publish_status").toString()
            var publish_status: Int = 0
            if (!publish_status_str.contains("null")) {
                publish_status_str.replace(".0", "").toInt()
            }

            val keywords = ArrayList<Int>()
            if (ld.get("keywords") != null) {
                val listItems = ld.get("keywords") as ArrayList<Any>
                if (listItems != null) {
                    listItems.forEach { li ->
                        run {
                            keywords.add(li.toString().replace(".0", "").toInt())
                        }
                    }
                }
            }

            val pictures = ArrayList<Picture>()
            if (ld.get("pictures") != null) {
                val listItems = ld.get("pictures") as ArrayList<LinkedTreeMap<String, Any>>
                if (listItems != null) {
                    listItems.forEach { li ->
                        run {
                            val mime_type = li.get("mime_type").toString()
                            val order_id = li.get("order_id").toString().replace(".0", "").toInt()
                            val base64pic = li.get("base64pic").toString()
                            pictures.add(Picture(base64pic, mime_type, order_id.toString()))
                        }
                    }
                }
            }

            result.add(
                AdvertInfo(
                    title = title,
                    description = description,
                    price = price,
                    category_id = category_id,
                    type_payments_id = type_payments_id,
                    keywords = keywords,
                    pictures = pictures,
                    lang_id = lang_id,
                    publish_status = publish_status,
                    id=null,
                    small_avatar = "",
                    date_time = "",
                    name = "",
                    surname = "",
                    email = ""
                )
            )
//                }
//            }
        }
    }
}

class GetAdvertById_RequestData(
    val id: Int
)

class GetAdvertById_ResponseData(
    val title: String,
    val description: String,
    val price: String,
    val category_id: Int,
    val lang_id: Int,
    val type_payments_id: Int,
    val publish_status: Int,
    val keywords: List<Int>,
    val pictures: List<Picture>
)

class Empty_RequestData() : RequestData

////{"is_offer":1,"lang":null,"limit":20,"filterText":null,"keywords":null,"offset":0,"category":null,"type_payments_id":null}
class GetAdvertByFiltr_RequestData(
    val filterText: String?,
    val category: Int?,
    val lang: Int?,
    val type_payments_id: Int?,
    val keywords: List<Int>?,
    val offset: Int,
    val limit: Int,
    val is_offer: Int
) : RequestData

class GetAdvertByFiltr_ResponseData(
    val id: Int,
    val title: String,
    val price: String,
    val type_payments_id: Int,
    val date_time: String,
    val name: Int,
    val surname: String,
    val small_avatar: String,
    val select_status: Int,
    val pictures: List<Picture>
)

class GetAdvertByFiltrOffsetId_RequestData(
    val offset_id: Int,
    val filterText: Int,
    val category: Int,
    val lang: Int,
    val type_payments_id: Int,
    val keywords: List<Int>,
    val limit: Int,
    val is_offer: Int
)

class GetAdvertByFiltrOffsetId_ResponseData(
    val id: String,
    val title: String,
    val price: String,
    val type_payments_id: Int,
    val date_time: String,
    val name: Int,
    val surname: String,
    val small_avatar: String,
    val select_status: Int,
    val pictures: List<Picture>
)

class SetSelection_RequestData(
    val advert_id: Int,
    val select_status: Int
):RequestData

class SetSelection_ResponseData(
    val select_status: Int
)

class GetAdvertBySelection_RequestData(
    val offset: Int,
    val limit: Int,
    val is_offer: Int
) : RequestData

class GetAdvertBySelection_ResponseData(
    val id: String,
    val title: String,
    val price: String,
    val type_payments_id: Int,
    val date_time: String,
    val name: Int,
    val surname: String,
    val small_avatar: String,
    val select_status: Int,
    val pictures: List<Picture>
)

class GetTop20Promo_ResponseData(
    val top20: List<String>,
    val url_promo: String
)