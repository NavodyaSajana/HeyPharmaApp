package com.saclim.heypharmaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_dashboard.*

class Payment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(1).isChecked=true

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
                R.id.Profile -> {
                    val intent= Intent(this, Profile::class.java)
                    finish()
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }


            }
            false
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }
}