package com.saclim.heypharmaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_dashboard.Pharmacy
import kotlinx.android.synthetic.main.activity_dashboard.bottomNavigationView
import kotlinx.android.synthetic.main.activity_pharmacy.*

class DashboardHome : AppCompatActivity() {

    private lateinit var findMed:CardView
    private lateinit var myPrescriptions:CardView
    private lateinit var getQuote:CardView
    private lateinit var pharmacy:CardView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        findMed = findViewById(R.id.FindMed)
        myPrescriptions = findViewById(R.id.MyPrescriptions)
        getQuote = findViewById(R.id.GetQuote)
        pharmacy = findViewById(R.id.Pharmacy)

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(0).isChecked=true
        //bottomNavigationView.menu.getItem(2).isEnabled = false

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

                R.id.Home -> {
                    val intent= Intent(this, DashboardHome::class.java)
                    finish()
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }


            }
            false
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        findMed.setOnClickListener{
            val intent= Intent(this, ScanHere::class.java)
            finish()
            startActivity(intent)

        }

        myPrescriptions.setOnClickListener{
            val intent= Intent(this, MyPrescription::class.java)
            finish()
            startActivity(intent)

        }

        getQuote.setOnClickListener{
            val intent= Intent(this, BuyMedicine::class.java)
            finish()
            startActivity(intent)
        }

        pharmacy.setOnClickListener{
            val intent= Intent(this, ShowPharmacy::class.java)
            finish()
            startActivity(intent)
        }

    }




}