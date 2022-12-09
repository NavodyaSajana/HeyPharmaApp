package com.saclim.heypharmaapp


import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.ButtonBarLayout
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tbuonomo.viewpagerdotsindicator.setBackgroundCompat
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_scan_here.*
import kotlinx.android.synthetic.main.activity_scan_here.bottomNavigationView
import java.util.jar.Manifest


class ScanHere : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_here)

        bottomNavigationView.background = null
        bottomNavigationView.menu.getItem(2).isEnabled = false

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


        btnScanHere.isEnabled=false

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),111)
        }

        else{
            btnScanHere.isEnabled=true
        }

        btnScanHere.setOnClickListener(){
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i,101)

            val fab =findViewById<FloatingActionButton>(R.id.fab)

            fab.setImageDrawable(getDrawable(R.drawable.ic_baseline_arrow_forward_ios_24))

            fab.setOnClickListener(){
                val intent= Intent(this, ScanDetails::class.java)
                finish()
                startActivity(intent)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==101)
        {
            var pic = data?.getParcelableExtra<Bitmap>("data")
            CameraCaptureImage.setImageBitmap(pic)

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            btnScanHere.isEnabled=true
        }
    }
}