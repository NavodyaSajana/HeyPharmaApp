package com.saclim.heypharmaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_dashboard.*

class Profile : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loadingDialog:SweetAlertDialog
    private lateinit var errorDialog:SweetAlertDialog
    private lateinit var textInputProfile:TextInputEditText
    private lateinit var textInputProfileDob:TextInputEditText
    private lateinit var textInputProfileTp:TextInputEditText
    private lateinit var textInputProfileAddress:TextInputEditText
    private lateinit var textInputProfileEmail:TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        textInputProfile = findViewById(R.id.textInputProfile)
        textInputProfileDob = findViewById(R.id.textInputProfileDob)
        textInputProfileTp = findViewById(R.id.textInputProfileTp)
        textInputProfileAddress = findViewById(R.id.textInputProfileAddress)
        textInputProfileEmail = findViewById(R.id.textInputProfileEmail)

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(3).isChecked=true

        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Payment -> {
                    val intent= Intent(this, Payment::class.java)
                    finish()
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.Home -> {
                    val intent= Intent(this,Dashboard::class.java)
                    finish()
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.Logout -> {
                    val intent= Intent(this, LoginScreen::class.java)
                    finish()
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        loadMemberDetails()

    }

    private fun loadMemberDetails(){
        showLoadingMessage("Loading Member Details...")
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Member").child(firebaseAuth.currentUser!!.uid)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val member = snapshot.getValue<Member>()
                if (member != null) {
                    textInputProfile.setText(member.name)
                    textInputProfileDob.setText(member.dob)
                    //textInputProfileTp.setText(member.telephone)
                    textInputProfileAddress.setText(member.address)
                    textInputProfileEmail.setText(member.email)
                }
                loadingDialog.dismissWithAnimation()
            }
            override fun onCancelled(error: DatabaseError) {
                loadingDialog.dismissWithAnimation()
                showErrorMessage("Unable to find user details try again")
            }
        })
    }

    private fun showLoadingMessage(message:String){
        loadingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            .setTitleText("Please Wait...")
            .setContentText(message)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }
    private fun showErrorMessage(errorText:String){
        errorDialog = SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
        errorDialog.setCancelable(true)
        errorDialog.setTitleText("Error...!")
        errorDialog.setContentText(errorText)
        errorDialog.show()
    }
}