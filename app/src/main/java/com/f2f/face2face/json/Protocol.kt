package com.f2f.face2face.json

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.f2f.face2face.*
import com.google.gson.GsonBuilder
import com.google.gson.internal.LinkedTreeMap
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val API_VERSION = "1.0"
val NAME_SIGN_UP = "SignUp"
val NAME_LOG_IN = "LogIn"
val NAME_LOG_IN_BY_SESSION = "LogInBySession"
val NAME_CHANGE_PASSWORD = "ChangePassword"
val NAME_UPDATE_PROFILE = "UpdateProfile"
val NAME_LOG_OUT = "LogOut"
val NAME_DELETE_ACCOUNT = "DeleteAccount"
val NAME_RESET_PASSWORD = "ResetPassword"

val NAME_ADD_ADVERT = "AddAdvert"
val NAME_SET_ADVERT_STATUS = "SetAdvertStatus"
val NAME_GET_ADVERT = "GetAdvert"
val NAME_GET_ADVERT_BY_ID = "GetAdvertById"
val NAME_GET_ADVERT_BY_FILTR = "GetAdvertByFiltr"
val NAME_GET_ADVERT_BY_FILTR_OFFSET_ID= "GetAdvertByFiltrOffsetId"
val NAME_SET_SELECTION = "SetSelection"
val NAME_GET_ADVERT_BY_SELECTION = "GetAdvertBySelection"

val NAME_CREATE_CHAT_1_ON_1 = "CreateChat1on1"
val NAME_GET_CHATS = "GetChats"
val NAME_ADD_MESSAGE = "AddMessage"
val NAME_GET_MESSAGES = "GetMessages"

val NAME_GET_MESSAGES_BY_ADVERT = "GetMessagesByAdvert"
val NAME_DELETE_CHAT = "DeleteChat"
val NAME_GET_TOP_20_PROMO = "GetTop20Promo"


val stub:String = "{\n" +
        "    \"info\" :\n" +
        "    {\n" +
        "        \"apiVersion\" : \"1.0\",\n" +
        "        \"uuid\" : \"00000000-0000-0000-0000-000000000000\",\n" +
        "        \"deviceId\" : \"kjhkjhjkh\",\n" +
        "        \"timestamp\" : \"2016-08-01T12:00:00+0300\",\n" +
        "        \"email\" : \"juhnowski@gmail.com\",\n" +
        "        \"session\" : \"323232\",\n" +
        "        \"lang\" : \"ru_RU\"\n" +
        "\n" +
        "    },\n" +
        "    \"name\" : \"LogIn\",\n" +
        "    \"data\" :\n" +
        "    {\n" +
        "        \"password\":\"q123456\",\n" +
        "        \"promocode\":\"\"\n" +
        "    }\n" +
        "}"


//{
//    "status": 1,
//    "message": "User with this login already exists",
//    "error": "Пользователь с таким login уже существует",
//    "data": [],
//}
class Info(
    val apiVersion: String,
    val uuid: String,
    val deviceId: String,
    val timestamp: String,
    val email: String,
    val session: String,
    val lang: String
)

class SignUpData(val password: String, val promocode: String):RequestData

interface RequestData
val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create()

class ServerRequest(val info: Info, val name: String, val data: RequestData) {
    fun json(): String {
        return gson.toJson(this)

    }
}

class Response(val status: String, val message: String, val error: String, val data: Any)

class ServerResponse(val msg: String?) {
    val response = gson.fromJson(msg, Response::class.java)
}

class LoginData(
    val session: String,
    val promocode_count: String,
    val promocode: String,
    val name: String,
    val surname: String,
    val avatar: String,
    val instagram: String?,
    val support_email: String,
    val insta_address: String,
    val insta_client_id: String,
    val category: List<Category>,
    val keywords: List<Keywords>,
    val languige: List<Languige>,
    val payments: List<Payments>
){

    companion object Factory {
        fun parse(json:String): LoginData {
            return gson.fromJson(json, LoginData::class.java)
        }
    }

}


class ServerTask(val message: String, val context: Context, val act: ResponseProcessable) : AsyncTask<Unit, Unit, String>() {
    val TAG = "ServerTask"

    override fun doInBackground(vararg params: Unit?): String? {
        val url = URL(SERVER_URL)

        val httpClient = url.openConnection() as HttpURLConnection

        httpClient.requestMethod = "POST"
        httpClient.connectTimeout = 300000
        httpClient.doOutput = true

        val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)
//        Log.d(TAG, "postData=${message}")
        httpClient.setRequestProperty("charset", "utf-8")
        httpClient.setRequestProperty("Content-lenght", postData.size.toString())
        httpClient.setRequestProperty("Content-Type", "application/json")

        Log.d(TAG,"connecting...")
        httpClient.connect()

        if(getLoggedIn(context)){
            //connect by session

        }

        try {
            val outputStream: DataOutputStream = DataOutputStream(httpClient.outputStream)
            outputStream.write(postData)
            outputStream.flush()
        } catch (exception: Exception) {
            return "{\"status\":1,\"message\":\"Отсутствует интернет соединение\",\"error\":\"Отсутствует интернет соединение\",\"data\":[]}"
        }
        Log.d(TAG, "sent request")

        if (httpClient.responseCode == HttpURLConnection.HTTP_OK) {
            Log.d(TAG,"responseCode=${httpClient.responseCode}")
            try {
                val stream = BufferedInputStream(httpClient.inputStream)
                val data: String = readStream(inputStream = stream)
                Log.d(TAG,"data=${data.toString()}")
                return data
            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка соединнения с сервером", Toast.LENGTH_SHORT).show()
                Log.e(TAG, e.message)
            } finally {
                httpClient.disconnect()
            }
        } else {
            Log.e(TAG, "ERROR ${httpClient.responseCode}")
            Toast.makeText(context, "Сервер вернул код ошибки: ${httpClient.responseCode}", Toast.LENGTH_SHORT)
                .show()
        }
        return null
    }

    fun readStream(inputStream: BufferedInputStream): String {
        Log.d(TAG,"readStream")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }


    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        Log.d(TAG,"onPostExecute result=$result")
        val resp = ServerResponse(result).response
        act.handleResponse(resp)

    }
}

interface ResponseProcessable {
    fun handleResponse(resp:Response)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getInfo(context:Context):Info{
    return Info(
        API_VERSION,
        getUUID(context),
        getDeviceID(context),
        DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now()),//"2016-08-01T12:00:00+0300",
        getEmail(context),
        getSession(context),
        getLang(context)
    )
}