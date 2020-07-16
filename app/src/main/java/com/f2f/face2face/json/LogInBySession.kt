package com.f2f.face2face.json

class LogInBySession_RequestData(
    val is_sandbox: Boolean,
    val receipt_data: String
):RequestData

class LogInBySession_ResponseData(
    val session_id: String,
    val ios_result: String,
    val promocode_count: String,
    val promocode: String,
    val name: String,
    val surname: String,
    val avatar: String,
    val instagram: String,
    val support_email: String,
    val insta_address: String,
    val insta_client_id: String,
    val category: String,
    val keywords: String,
    val languige: String,
    val payments: String
)

class ResetPassword_RequestData(
    val email: String,
    val promocode: String
):RequestData