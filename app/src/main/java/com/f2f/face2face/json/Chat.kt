package com.f2f.face2face.json

class CreateChat1on1_RequestData(
    val advert_id: Int,
    val text: String
):RequestData

class CreateChat1on1_ResponseData(
    val chat_id: Int
)

class GetChats_RequestData():RequestData

class GetChats_ResponseData(
    val chats: List<Int>
)

class AddMessage_RequestData(
    val chat_id: Int,
    val text: String
):RequestData

class GetMessages_RequestData(
    val chat_id: Int
):RequestData

class GetMessages_ResponseData(
    val chat_messages: List<String>,
    val chat_users_info: List<String>
)

class GetMessagesByAdvert_RequestData(
    val advert_id: Int
)

class GetMessagesByAdvert_ResponseData(
    val chat_id: Int,
    val chat_messages: List<String>,
    val chat_users_info: List<String>
)

class DeleteChat_RequestData(
    val chat_id: Int
):RequestData

class DeleteChat_ResponseData(
    val is_delete : Int
)
