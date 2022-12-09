package com.saclim.heypharmaapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_scan_here.*
import org.json.JSONObject

private lateinit var txtMedication:TextView
private lateinit var txtDiciese:TextView
private lateinit var txtMedDescription:TextView
private lateinit var medImg:ImageView
private lateinit var btnScanDetailsCancel:MaterialButton

private lateinit var drugDetailsObject:JSONObject
private lateinit var firebaseDatabase: FirebaseDatabase
private lateinit var databaseReference: DatabaseReference



private lateinit var progressDialog: ProgressDialog

class ScanDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_details)
        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

        val bundle = intent.extras

        txtMedication = findViewById(R.id.txtMedication)
        txtDiciese = findViewById(R.id.txtDiciese)
        txtMedDescription = findViewById(R.id.txtMedDescription)
        medImg = findViewById(R.id.medImg)
        btnScanDetailsCancel = findViewById(R.id.btnScanDetailsCancel)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        if (bundle != null) {
            if(bundle.getString("medicationName")!=null){
                val medicationName = bundle.getString("medicationName").toString()
                makeToast(medicationName)
                txtMedication.setText(medicationName)
                setDetails(medicationName)
            }
        }

        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Payment -> {
                    val intent= Intent(this, Payment::class.java)
                    finish()
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.Profile -> {
                    val intent= Intent(this, Profile::class.java)
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

        btnScanDetailsCancel.setOnClickListener{
            val intent= Intent(this, ScanHere::class.java)
            finish()
            startActivity(intent)
        }


    }
    private fun setDetails(drugName:String){
        progressDialog.setMessage("Drug Details Loading...")
        progressDialog.show()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Drug")

        databaseReference.child(drugName).get().addOnSuccessListener{ result->
        txtDiciese.setText("Disease : ${result.child("Disease").value.toString()}")
        txtMedDescription.setText(result.child("Description").value.toString())
        val imageUrl = result.child("Image").value.toString()
        Glide.with(applicationContext)
            .load(imageUrl)
            .override(130, 130)
            .into(medImg)
            progressDialog.dismiss()
        }
    }
    private fun makeToast(value:String){
        Toast.makeText(this,value,Toast.LENGTH_SHORT).show()
    }
}