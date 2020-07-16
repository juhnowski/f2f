package com.f2f.face2face.json

class Category(val id: Int, val name: String, val parent_category: Int)
class Keywords(val id: Int, val name: String, val parent_category: Int)
class Languige(val id: Int, val name: String)
class Payments(val id: Int, val name: String)
class Orders(val id: Int, val name: String)

class CategoryList():ArrayList<Category>() {}

class CategoryParser(val msg: String?) {
    val list = gson.fromJson(msg, CategoryList::class.java)
}

class KeywordsList():ArrayList<Keywords>(){}
class KeywordsParser(val msg: String?) {
    val list = gson.fromJson(msg, KeywordsList::class.java)
}

class LanguigeList():ArrayList<Languige>(){}
class LanguigeParser(val msg: String?) {
    val list = gson.fromJson(msg, LanguigeList::class.java)
}

class PaymentsList():ArrayList<Payments>(){}
class PaymentsParser(val msg: String?) {
    val list = gson.fromJson(msg, PaymentsList::class.java)
}
