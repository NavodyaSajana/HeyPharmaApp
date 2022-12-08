package com.project.heypharma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saclim.heypharmaapp.R
import kotlinx.android.synthetic.main.screen_login.*

class LoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_login)


        lblLoginRegister.setOnClickListener(){

            val intent = Intent(this, RegistrationScreen::class.java)
            startActivity(intent)
        }
    }
}