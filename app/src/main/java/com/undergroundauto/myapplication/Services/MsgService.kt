package com.undergroundauto.myapplication.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.undergroundauto.myapplication.Controller.App
import com.undergroundauto.myapplication.Models.Channel
import com.undergroundauto.myapplication.Models.Msg
import com.undergroundauto.myapplication.Utilities.URL_GET_CHANNELS
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

}