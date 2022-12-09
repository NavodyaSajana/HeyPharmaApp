package com.saclim.heypharmaapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.text.isDigitsOnly
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
private lateinit var registerRePassword:TextInputEditText

private lateinit var firebaseAuth:FirebaseAuth
private lateinit var firebaseDatabase:FirebaseDatabase
private lateinit var databaseReference:DatabaseReference

private lateinit var progressDialog:ProgressDialog

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
        registerRePassword=findViewById(R.id.RegisterRePassword)

        clearErrorMessages()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        btnRegCancel.setOnClickListener(){

            val intent = Intent(this,LoginScreen::class.java)
            startActivity(intent)
        }

        ////signup activity

        firebaseAuth = FirebaseAuth.getInstance()

        btnRegRegister.setOnClickListener{
            try{
                val name = registerName.text.toString()
                val dob = registerDob.text.toString()
                val tp = registerTp.text.toString()
                val address = registerAddress.text.toString()
                val email = registerUserName.text.toString()
                val pass = registerPassword.text.toString()
                val comPass = registerRePassword.text.toString()

                val validation = Validation()

                if(name.isNullOrEmpty()){
                    clearErrorMessages()
                    textInputName.setHelperText("*Enter Your Name")
                }else if(validation.isNumeric(name)==true){
                    clearErrorMessages()
                    textInputName.setHelperText("*Name Cannot Have Numbers")
                }else if(dob.isNullOrEmpty()){
                    clearErrorMessages()
                    textInputDob.setHelperText("*Enter Your DOB")
                }else if(tp.isNullOrEmpty()){
                    clearErrorMessages()
                    textInputTp.setHelperText("*Enter Your Telephone Number")
                }else if(!tp.isDigitsOnly()){
                    clearErrorMessages()
                    textInputTp.setHelperText("*Telephone Number Cannot Have Letters")
                }else if(tp.length<10){
                    clearErrorMessages()
                    textInputTp.setHelperText("*Telephone Number length is 10")
                }else if(address.isNullOrEmpty()){
                    clearErrorMessages()
                    textInputAddress.setHelperText("*Enter Your Address")
                }else if(email.isNullOrEmpty()){
                    clearErrorMessages()
                    textInputUsernameRg.setHelperText("*Enter Your Email")
                }else if(pass.isNullOrEmpty()){
                    clearErrorMessages()
                    textInputPasswordRg.setHelperText("*Enter Valied Password")
                }else if(comPass.isNullOrEmpty()){
                    clearErrorMessages()
                    textInputRePassword.setHelperText("*Confirm Your Password")
                }else if(comPass!=pass){
                    clearErrorMessages()
                    textInputRePassword.setHelperText("*Passwords are mismatching")
                }
                else{
                    progressDialog.setMessage("Registering User...")
                    progressDialog.show()
                    clearErrorMessages()
                    firebaseDatabase = FirebaseDatabase.getInstance()
                    databaseReference = firebaseDatabase.getReference("Member")

                    val tpNw = Integer.parseInt(registerTp.text.toString())

                    val registerMember = Member(name,dob,tpNw,address,email)

                    databaseReference.child(tpNw.toString()).setValue(registerMember).addOnCompleteListener{result->
                        if(result.isSuccessful){
                            progressDialog.dismiss()
                            Toast.makeText(this,"User Registered Successfully",Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()

                            progressDialog.setMessage("Creating Account...")
                            progressDialog.show()
                            firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{result->
                                if(result.isSuccessful){
                                    progressDialog.dismiss()
                                    Toast.makeText(this,"User Added Successfully",Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this,LoginScreen::class.java)
                                    startActivity(intent)
                                }else{
                                    progressDialog.dismiss()
                                    Toast.makeText(this,result.exception.toString(),Toast.LENGTH_SHORT).show()
                                }
                            }
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this,result.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }catch(e:Exception){
                Toast.makeText(this,e.message.toString(),Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun clearErrorMessages(){
        textInputName.setHelperText("")
        textInputDob.setHelperText("")
        textInputTp.setHelperText("")
        textInputAddress.setHelperText("")
        textInputUsernameRg.setHelperText("")
        textInputPasswordRg.setHelperText("")
        textInputRePassword.setHelperText("")
    }

}