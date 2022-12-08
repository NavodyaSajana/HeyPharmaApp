package com.saclim.heypharmaapp


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.saclim.heypharmaapp.R
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.fragment_on_boarding3.*


class IntroActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        viewpager.adapter = ViewPagerAdapter(supportFragmentManager)



    }


}