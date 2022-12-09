package com.saclim.heypharmaapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.saclim.heypharmaapp.R
import kotlinx.android.synthetic.main.screen_login.*



class LoginScreen : AppCompatActivity() {

    private lateinit var firebaseAuth:FirebaseAuth

    private lateinit var loginUsername:TextInputEditText
    private lateinit var loginPassword:TextInputEditText

    private lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_login)



        loginUsername = findViewById(R.id.loginUsername)
        loginPassword = findViewById(R.id.loginPassword)

        clearErrorMessages()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        lblLoginRegister.setOnClickListener{
            clearErrorMessages()
            val intent = Intent(this, RegistrationScreen::class.java)
            startActivity(intent)
        }

        btnLoginLogin.setOnClickListener{
            try{
                val email=loginUsername.text.toString()
                val pass=loginPassword.text.toString()

                if(email.isNotEmpty() && pass.isNotEmpty()){
                    clearErrorMessages()
                    progressDialog.setMessage("Logging...!")
                    progressDialog.show()
                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener { result->
                        if(result.isSuccessful){
                            progressDialog.dismiss()
                            Toast.makeText(this,"Login Success",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Dashboard::class.java)
                            startActivity(intent)

                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(this,result.exception.toString(),Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    clearErrorMessages()
                    progressDialog.dismiss()
                    if(email.isNullOrEmpty()) {
                        textInputUsername.helperText = "*Enter Email..."
                    }else if(pass.isNullOrEmpty()) {
                        textInputPassword.helperText = "*Enter Password..."
                    }
                }
            }catch(e:Exception){
                Toast.makeText(this,e.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun clearErrorMessages(){
        textInputUsername.helperText = ""
        textInputPassword.helperText = ""
    }
}