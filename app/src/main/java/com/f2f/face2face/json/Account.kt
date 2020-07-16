package com.f2f.face2face.json

class ChangePassword_RequestData(
    val password: String,
    val new_password: String
):RequestData

class UpdateProfile_RequestData(
    val name: String,
    val surname: String,
    val email: String,
    val avatar: String,
    val promocode: String
):RequestData