package com.saclim.heypharmaapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import cn.pedant.SweetAlert.SweetAlertDialog
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

private lateinit var loadingDialog:SweetAlertDialog
private lateinit var errorDialog: SweetAlertDialog
private lateinit var informationDialog: SweetAlertDialog


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
                    textInputPasswordRg.setHelperText("*Enter Valid Password")
                }else if(comPass.isNullOrEmpty()){
                    clearErrorMessages()
                    textInputRePassword.setHelperText("*Confirm Your Password")
                }else if(comPass!=pass){
                    clearErrorMessages()
                    textInputRePassword.setHelperText("*Passwords are mismatching")
                }
                else{
                    showLoadingMessage("Registering New User...")
                    clearErrorMessages()
                    firebaseDatabase = FirebaseDatabase.getInstance()
                    databaseReference = firebaseDatabase.getReference("Member")
                    val registerMember = Member(name,dob,Integer.parseInt(tp),address,email)

                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{ result->
                        if(result.isSuccessful){
                            databaseReference.child(firebaseAuth.currentUser!!.uid.toString()).setValue(registerMember).addOnCompleteListener { result->
                                if(result.isSuccessful){
                                    loadingDialog.dismissWithAnimation()
                                    showInformationMessage("User Registered Successfully")
                                }else{
                                    loadingDialog.dismissWithAnimation()
                                    showErrorMessage("Unable to Register User Please Contact Administration")
                                }
                            }
                        }else{
                            loadingDialog.dismissWithAnimation()
                            showErrorMessage("User Registration Failed Try Again")
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

    private fun showLoadingMessage(message:String){
        loadingDialog = SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Please Wait...")
            .setContentText(message)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun showErrorMessage(errorText:String){
        errorDialog = SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
        errorDialog.setCancelable(true)
        errorDialog.setTitleText("Error...!")
        errorDialog.setContentText(errorText)
        errorDialog.show()
    }
    private fun showInformationMessage(inforText:String){
        informationDialog = SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
        informationDialog.setTitleText("Done...!")
        informationDialog.setContentText(inforText)
        informationDialog.show()
    }
}