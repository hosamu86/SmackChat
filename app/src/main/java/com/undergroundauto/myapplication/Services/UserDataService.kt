package com.undergroundauto.myapplication.Services

import android.graphics.Color
import android.provider.ContactsContract
import java.util.*

object UserDataService {
    var id = ""
    var avatarColor = ""
    var avatarName =""
    var email = ""
    var name = ""

    fun logout(){
        var id = ""
        var avatarColor = ""
        var avatarName =""
        var email = ""
        var name = ""
        AuthService.authToken=""
        AuthService.userEmail=""
        AuthService.isLoggedin=false

    }


    fun returnAvatarColor(components: String):Int{
        val strippedColor =components.replace("[","")
                .replace("]","")
                .replace(",","")
        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)
        if(scanner.hasNextDouble()){
            r = (scanner.nextDouble()* 255).toInt()
            g = (scanner.nextDouble()* 255).toInt()
            b = (scanner.nextDouble()* 255).toInt()
        }
        return Color.rgb(r,g,b)

    }

}