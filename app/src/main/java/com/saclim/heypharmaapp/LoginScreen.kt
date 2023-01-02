package com.saclim.heypharmaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.screen_login.*


class LoginScreen : AppCompatActivity() {

    private lateinit var firebaseAuth:FirebaseAuth

    private lateinit var loginUsername:TextInputEditText
    private lateinit var loginPassword:TextInputEditText

    private lateinit var loadingDialog:SweetAlertDialog
    private lateinit var errorDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_login)

        loadingDialog = SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
        loadingDialog.setTitleText("Please Wait...!")
        loadingDialog.setCancelable(false)

        loginUsername = findViewById(R.id.loginUsername)
        loginPassword = findViewById(R.id.loginPassword)

        clearErrorMessages()

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

                    loadingDialog.setContentText("Login to the HeyPharma")
                    loadingDialog.show()
                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener { result->
                        if(result.isSuccessful){
                            loadingDialog.dismiss()
                            val intent = Intent(this, DashboardHome::class.java)
                            startActivity(intent)

                        }else{
                            loadingDialog.dismiss()
                            try {
                                throw result.exception!!
                            }catch(e:FirebaseAuthException){
                                when (e.errorCode) {
                                    "ERROR_USER_NOT_FOUND" -> {
                                        showErrorMessage("The email address you entered is wrong")
                                    }
                                    "ERROR_WRONG_PASSWORD" -> {
                                        showErrorMessage("The password is not matching with the email account")
                                    }
                                    else -> {
                                        showErrorMessage("Something went wrong please contact technical support")
                                    }
                                }
                            }
                        }
                    }
                }else{
                    clearErrorMessages()
                    loadingDialog.dismiss()
                    if(email.isNullOrEmpty()) {
                        textInputUsername.helperText = "*Enter Email..."
                    }else if(pass.isNullOrEmpty()) {
                        textInputPassword.helperText = "*Enter Password..."
                    }
                }
            }catch(e:FirebaseAuthException){
                showErrorMessage(e.errorCode)
            }catch(e:Exception){
                showErrorMessage("Something went wrong contact technical support")
            }
        }
    }
    private fun clearErrorMessages(){
        textInputUsername.helperText = ""
        textInputPassword.helperText = ""
    }
    private fun showErrorMessage(errorText:String){
        errorDialog = SweetAlertDialog(this,SweetAlertDialog.ERROR_TYPE)
        errorDialog.setCancelable(true)
        errorDialog.setTitleText("Error...!")
        errorDialog.setContentText(errorText)
        errorDialog.show()
    }
}