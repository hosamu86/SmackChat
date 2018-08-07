package com.undergroundauto.myapplication.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.undergroundauto.myapplication.Models.Msg
import com.undergroundauto.myapplication.R
import com.undergroundauto.myapplication.Services.UserDataService


class MsgAdapter(val context :Context,val msgs:ArrayList<Msg>): RecyclerView.Adapter<MsgAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.message_list_view,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return msgs.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindMsg(context,msgs[position])
    }

    inner class ViewHolder(itemView:View? ) :RecyclerView.ViewHolder(itemView){
      val userImage = itemView?.findViewById<ImageView>(R.id.msgUserImage)
        val timeStamp = itemView?.findViewById<TextView>(R.id.timeStampMsg)
        val userName = itemView?.findViewById<TextView>(R.id.msgUserName)
        val msgBody = itemView?.findViewById<TextView>(R.id.msgBodyLabel)

        fun bindMsg (context: Context,msg: Msg){
            val resourceId = context.resources.getIdentifier(msg.userAvatar,"drawable",context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(msg.userAvatarColor))
            userName?.text = msg.userName
            timeStamp?.text = msg.timeStamp
            msgBody?.text = msg.msg

        }
    }

}