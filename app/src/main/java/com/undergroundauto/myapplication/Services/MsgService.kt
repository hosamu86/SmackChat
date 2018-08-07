package com.undergroundauto.myapplication.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.undergroundauto.myapplication.Controller.App
import com.undergroundauto.myapplication.Models.Channel
import com.undergroundauto.myapplication.Models.Msg
import com.undergroundauto.myapplication.Utilities.URL_GET_CHANNELS
import com.undergroundauto.myapplication.Utilities.URL_GET_MESSAGES
import org.json.JSONException

object MsgService {
    val channels = ArrayList<Channel>()
    val msgs = ArrayList<Msg>()
    fun getChannel(complete: (Boolean)-> Unit){
        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS,null,Response.Listener {response->
            try {
                for (x in 0 until response.length() ){
                    val channel = response.getJSONObject(x)
                    val name = channel.getString("name")
                    val desc = channel.getString("description")
                    val channelId = channel.getString("_id")

                    val newChannel = Channel(name,desc,channelId)
                    this.channels.add(newChannel)
                }
                complete(true)

            }catch (e : JSONException){
                Log.d("JSON","EXC"+e.localizedMessage)
                complete(false)
            }

        },Response.ErrorListener {error ->
            Log.d("ERROR","Could not retrieve channels ")
            complete(false)

        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization","Bearer ${App.prefs.authToken}")
                return headers
            }


        }
        App.prefs.requestQueue.add(channelsRequest)

    }

    fun getMsgs(channelId:String, complete: (Boolean) -> Unit){
        val url = "$URL_GET_MESSAGES$channelId"
        val messagesRequest = object : JsonArrayRequest(Method.GET,url,null,Response.Listener {response ->
            clearMsgs()
            try {
                for (x in 0 until response.length()){
                    val message = response.getJSONObject(x)

                    val msgBody = message.getString("messageBody")
                    val channelId1 = message.getString("channelId")
                    val id = message.getString("_id")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timeStamp = message.getString("timeStamp")

                    val newMsg = Msg(msgBody,userName,channelId1,userAvatar,userAvatarColor,id,timeStamp)
                    this.msgs.add(newMsg)
                }
                complete(true)

            }catch (e:JSONException){
                Log.d("JSON","EXC"+e.localizedMessage)
                complete(false)
            }

        },Response.ErrorListener {
            Log.d("ERROR","Could not retrieve messages ")

            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String,String>()
                headers.put("Authorization","Bearer ${App.prefs.authToken}")
                return headers
            }
        }
        App.prefs.requestQueue.add(messagesRequest)
    }
    fun clearMsgs(){
        msgs.clear()
    }
    fun clearChannel(){
        channels.clear()
    }
}