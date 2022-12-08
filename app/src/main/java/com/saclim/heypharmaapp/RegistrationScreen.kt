package com.saclim.heypharmaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saclim.heypharmaapp.R
import kotlinx.android.synthetic.main.activity_registration_screen.*
import kotlinx.android.synthetic.main.screen_login.*

class RegistrationScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_screen)

        btnRegCancel.setOnClickListener(){

            val intent = Intent(this,LoginScreen::class.java)
            startActivity(intent)
        }
    }
}