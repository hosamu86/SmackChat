package com.undergroundauto.myapplication.Controller

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import android.widget.Toast
import com.undergroundauto.myapplication.R
import com.undergroundauto.myapplication.Services.AuthService
import com.undergroundauto.myapplication.Services.UserDataService
import com.undergroundauto.myapplication.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    var userAvatar = "profileDefault"
    var avatarColor  = "[0.5,0.5,0.5,1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerSpinner.visibility =  View.INVISIBLE
    }
    fun imageGenrator(view: View){
        val random = Random()
        val colorValue = random.nextInt(2)
        val avatar =  random.nextInt(28)
        if (colorValue == 0 ){
            userAvatar = "light$avatar"
        }else{
            userAvatar = "dark$avatar"
        }
        val resourceID = resources.getIdentifier(userAvatar,"drawable",packageName)
        createImageView.setImageResource(resourceID)




    }
    fun backgroundColor(view: View){
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)
        createImageView.setBackgroundColor(Color.rgb(r,g,b))
        val savedR = r.toDouble()/255
        val savedG = g.toDouble()/255
        val savedB = b.toDouble()/255
        avatarColor = "[$savedR,$savedG,$savedB,1]"
    }
    fun registerRegistrationClicked(view: View){
        enableSpinner(true)
        val userName = usernameRegisteration.text.toString()
        val email = emailRegistration.text.toString()
        val password = passRegistration.text.toString()

        if (userName.isNotEmpty()&&email.isNotEmpty()&&password.isNotEmpty()){
            AuthService.registerUser(email,password){
                registerSuccess->if (registerSuccess){
                AuthService.loginUser(email,password){loginSuccess ->
                    if (loginSuccess){
                        AuthService.createUser(userName,email,userAvatar,avatarColor){createSuccess ->
                            if(createSuccess){
                                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                LocalBroadcastManager.getInstance(this).sendBroadcast(userDataChange)
                                enableSpinner(false)
                                finish()
                            }else{
                                errorToast()
                            }


                        }

                    }else{
                        errorToast()
                    }

                }

            }else{
                errorToast()
            }
            }
        }else {
            Toast.makeText(this,"Please Fill the required info ",Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }


        }
    fun errorToast() {
            Toast.makeText(this,"Something went wrong bruh",Toast.LENGTH_LONG).show()
            enableSpinner(false)

    } fun  enableSpinner(enable: Boolean){
        if (enable){
            registerSpinner.visibility = View.VISIBLE
        }else {
            registerSpinner.visibility = View.INVISIBLE
        }
        registerRegistration.isEnabled= !enable
        createImageView.isEnabled=!enable
        backgroundColor.isEnabled=!enable
    }
}
