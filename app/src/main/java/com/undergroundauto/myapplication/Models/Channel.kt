package com.undergroundauto.myapplication.Models

class Channel (val name:String,val desc:String,val id:String){
    override fun toString(): String {
        return "#$name"
    }
}