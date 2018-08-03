package com.undergroundauto.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
    fun loginLoginBClicked(view: View){

    }
    fun registerBClicked(view: View){
   val registerIntent = Intent(this,RegisterActivity::class.java)
        startActivity(registerIntent)
    }

}
