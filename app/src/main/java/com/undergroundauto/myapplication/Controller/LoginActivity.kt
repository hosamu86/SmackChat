package com.undergroundauto.myapplication.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.undergroundauto.myapplication.R
import com.undergroundauto.myapplication.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginProgressBar.visibility = View.INVISIBLE

    }
    fun loginLoginBClicked(view: View){
        hideKeyboard()
        enableSpinner(true)
        val email = loginEmail.text.toString()
        val password = loginPass.text.toString()


        if (email.isNotEmpty() && password.isNotEmpty() ){
        AuthService.loginUser(this,email,password){loginSuccess->
            if (loginSuccess){
                AuthService.findUserByEmail(this){findSuccess->
                    if (findSuccess){
                        enableSpinner(false)
                        finish()
                    }else{errorToast()}
                }
            }else{ errorToast() }
        }
        }else{
            Toast.makeText(this,"please fill your info my broda ", Toast.LENGTH_SHORT).show()
        enableSpinner(false)
        }

    }
    fun registerBClicked(view: View){
   val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
        finish()
    }
    fun  enableSpinner(enable: Boolean){
        if (enable){
            loginProgressBar.visibility = View.VISIBLE
        }else {
            loginProgressBar.visibility = View.INVISIBLE
        }
        loginLoginB.isEnabled= !enable
        registerB.isEnabled=!enable

    }
    fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken,0)
        }
    }
    fun errorToast() {
        Toast.makeText(this,"Something went wrong bruh", Toast.LENGTH_LONG).show()
        enableSpinner(false)

    }

}
