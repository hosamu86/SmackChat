package com.undergroundauto.myapplication.Controller

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import com.undergroundauto.myapplication.Models.Channel
import com.undergroundauto.myapplication.Models.Msg
import com.undergroundauto.myapplication.R
import com.undergroundauto.myapplication.Services.AuthService
import com.undergroundauto.myapplication.Services.MsgService
import com.undergroundauto.myapplication.Services.UserDataService
import com.undergroundauto.myapplication.Utilities.BROADCAST_USER_DATA_CHANGE
import com.undergroundauto.myapplication.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity(){
    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter:ArrayAdapter<Channel>

    var selectedChannel : Channel? = null

    private fun setUpAdapter(){
        channelAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,MsgService.channels)
        channels_id.adapter = channelAdapter

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        socket.on("channelCreated",onNewChannel)
        socket.on("messageCreated",onNewMsg)

        channels_id.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MsgService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }




        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setUpAdapter()

        if (App.prefs.isLoggIn){
            AuthService.findUserByEmail(this){

            }
        }




    }

    override fun onResume() {


        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(BROADCAST_USER_DATA_CHANGE))
        super.onResume()
    }


    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        socket.disconnect()
        super.onDestroy()
    }

    private val userDataChangeReceiver  = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.prefs.isLoggIn){
                usernameNav.text= UserDataService.name
                emailNav.text= UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName,"drawable",packageName)
                userImageNav.setImageResource(resourceId)
                loginNav.text= "Logout"
                userImageNav.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))

                MsgService.getChannel(){complete ->
                    if(complete){
                        if (MsgService.channels.count()>0){
                            selectedChannel = MsgService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }

                }


            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun updateWithChannel(){
        mainChannel.text = "#${selectedChannel?.name}"
        //download msgs
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    fun loginNavClicked(view: View){
        if(App.prefs.isLoggIn){
            //logout
            UserDataService.logout()
            usernameNav.text= ""
            emailNav.text=""
            userImageNav.setImageResource(R.drawable.profiledefault)
            userImageNav.setBackgroundColor(Color.TRANSPARENT)
            loginNav.text = "Login"


        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

 }
    fun addChannelNavClicked(view: View){

        if (App.prefs.isLoggIn){
            val builder = AlertDialog.Builder(this)
            val diagView = layoutInflater.inflate(R.layout.add_channel_diag,null )

            builder.setView(diagView).setPositiveButton("Add"){ dialog, which ->
                //preform logic when clicked
                val textFeildDiag = diagView.findViewById<EditText>(R.id.addChannelName)
                val textFeildDiagDesc = diagView.findViewById<EditText>(R.id.addChannelDes)
                val channelName = textFeildDiag.text.toString()
                val channelDesc = textFeildDiagDesc.text.toString()
                //create channel name
                socket.emit("newChannel",channelName,channelDesc)


            }
                    .setNegativeButton("Cancel"){dialog, which ->
                        // cancel


                    }.show()

    }

 }
    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            val channelName = args[0] as String
            val channelDesc = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName,channelDesc,channelId)
            MsgService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()

        }
    }
    private val onNewMsg = Emitter.Listener { args ->
        runOnUiThread {
            val msgBody = args[0] as String
            val channelId = args[2] as String
            val userName = args[3] as String
            val userAvatar = args[4] as String
            val userAvatarColor = args[5] as String
            val id = args[6] as String
            val timeStamp = args[7] as String
            val newMsg= Msg(msgBody,channelId,userName,userAvatar,userAvatarColor,id,timeStamp)
            MsgService.msgs.add(newMsg)
            println(newMsg.msg)

        }
    }

    fun sendMessageBClicked(view: View){
        if (App.prefs.isLoggIn && sendTextField.text.isNotEmpty()&& selectedChannel!= null ){
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            socket.emit("newMessage",sendTextField.text.toString(),userId,channelId,
                    UserDataService.name,UserDataService.avatarName,UserDataService.avatarColor)
            sendTextField.text.clear()
            hideKeyboard()
        }


    }
    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
