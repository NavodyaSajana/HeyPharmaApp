package com.saclim.heypharmaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.saclim.heypharmaapp.R
import kotlinx.android.synthetic.main.activity_registration_screen.*
import kotlinx.android.synthetic.main.screen_login.*

private lateinit var registerUserName:TextInputEditText
private lateinit var registerPassword:TextInputEditText
private lateinit var firebaseAuth:FirebaseAuth
// private lateinit var btnRegRegister:MaterialButton

class RegistrationScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_screen)

        registerUserName=findViewById(R.id.RegisterUserName)
        registerPassword=findViewById(R.id.RegisterPassword)
        //btnRegRegister=findViewById(R.id.btnRegRegister)

        btnRegCancel.setOnClickListener(){

            val intent = Intent(this,LoginScreen::class.java)
            startActivity(intent)
        }

        ////signup activity

        firebaseAuth = FirebaseAuth.getInstance()

        btnRegRegister.setOnClickListener{
            val email = registerUserName.text.toString()
            val pass = registerPassword.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()){
                firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{result->
                    if(result.isSuccessful){
                        Toast.makeText(this,"User Added Successfully",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,result.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Email or Password Empty",Toast.LENGTH_SHORT).show()
            }
        }
    }
}