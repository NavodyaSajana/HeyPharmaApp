package com.saclim.heypharmaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.saclim.heypharmaapp.R
import kotlinx.android.synthetic.main.activity_registration_screen.*
import kotlinx.android.synthetic.main.screen_login.*

private lateinit var registerUserName:TextInputEditText
private lateinit var registerPassword:TextInputEditText
private lateinit var registerName:TextInputEditText
private lateinit var registerDob:TextInputEditText
private lateinit var registerTp:TextInputEditText
private lateinit var registerAddress:TextInputEditText

private lateinit var firebaseAuth:FirebaseAuth
private lateinit var firebaseDatabase:FirebaseDatabase
private lateinit var databaseReference:DatabaseReference

class RegistrationScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_screen)

        registerUserName=findViewById(R.id.RegisterUserName)
        registerPassword=findViewById(R.id.RegisterPassword)
        registerName=findViewById(R.id.RegisterName)
        registerDob=findViewById(R.id.RegisterDob)
        registerTp=findViewById(R.id.RegisterTp)
        registerAddress=findViewById(R.id.RegisterAddress)

        btnRegCancel.setOnClickListener(){

            val intent = Intent(this,LoginScreen::class.java)
            startActivity(intent)
        }

        ////signup activity

        firebaseAuth = FirebaseAuth.getInstance()

        btnRegRegister.setOnClickListener{
            val name = registerName.text.toString()
            /*val dob = registerDob.text.toString()
            val tp = Integer.parseInt(registerTp.text.toString())
            val address = registerAddress.toString()
            val email = registerUserName.text.toString()
            val pass = registerPassword.text.toString()*/

            val validation = Validation()

            if(name.isNullOrEmpty()){
                textInputName.setHint("*Enter Your Name")
            }else if(validation.isNumeric(name)==true){
                textInputName.setHint("*Name Cannot Have Numbers")
            }

            /*if(email.isNotEmpty() && pass.isNotEmpty()){
                firebaseDatabase = FirebaseDatabase.getInstance()
                databaseReference = firebaseDatabase.getReference("Member")

                val registerMember = Member(name,dob,tp,address,email)

                databaseReference.child(tp.toString()).setValue(registerMember).addOnCompleteListener{result->
                    if(result.isSuccessful){
                        Toast.makeText(this,"User Registered Successfully",Toast.LENGTH_SHORT).show()

                        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{result->
                            if(result.isSuccessful){
                                Toast.makeText(this,"User Added Successfully",Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this,result.exception.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(this,result.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Email or Password Empty",Toast.LENGTH_SHORT).show()
            }*/
        }
    }

    private fun clearErrorMessages(){
        textInputName.setHint("")
        textInputDob.setHint("")
        textInputTp.setHint("")
        textInputAddress.setHint("")
        textInputUsernameRg.setHint("")
        textInputPasswordRg.setHint("")
        textInputRePassword.setHint("")
    }

}