package com.undergroundauto.myapplication.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.undergroundauto.myapplication.R
import com.undergroundauto.myapplication.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
    fun loginLoginBClicked(view: View){
        val email = loginEmail.text.toString()
        val password = loginPass.text.toString()

        AuthService.loginUser(this,email,password){loginSuccess->
            if (loginSuccess){
                AuthService.findUserByEmail(this){findSuccess->
                    if (findSuccess){
                        finish()
                    }

                }
            }
        }

    }
    fun registerBClicked(view: View){
   val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
        finish()
    }

}
