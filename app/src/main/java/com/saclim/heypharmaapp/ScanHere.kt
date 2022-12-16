package com.saclim.heypharmaapp


import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_scan_here.*
import kotlinx.android.synthetic.main.activity_scan_here.bottomNavigationView
import java.lang.System.lineSeparator


class ScanHere : AppCompatActivity() {

    private lateinit var cameraCaptureImage:ImageView
    private lateinit var fab:FloatingActionButton
    private lateinit var textMedicationName: TextInputEditText


    private companion object{
        private const val CAMERA_REQUEST_CODE=100
        private const val STORAGE_REQUEST_CODE=101
    }

    private var imageUri: Uri?=null

    private lateinit var cameraPermission:Array<String>
    private lateinit var storagePermission:Array<String>

    private lateinit var progressDialog: ProgressDialog

    private lateinit var textRecognizer: TextRecognizer

    private lateinit var firebaseDatabase:FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_here)

        fab = findViewById(R.id.fab)
        cameraCaptureImage = findViewById(R.id.CameraCaptureImage )
        textMedicationName = findViewById(R.id.textMedicationName)

        cameraPermission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)



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

        btnScanHere.setOnClickListener{
            showInputImageDialog()

        }

        fab.setOnClickListener{
            progressDialog.setMessage("Searching The Drug...")
            progressDialog.show()
            val medicationItem = textMedicationName.text.toString().toUpperCase()

            if(!medicationItem.isNullOrEmpty()){
                firebaseDatabase = FirebaseDatabase.getInstance()
                databaseReference = firebaseDatabase.getReference("Drug")

                databaseReference.child(medicationItem).get().addOnSuccessListener { result->
                    progressDialog.dismiss()
                    if(result.value!=null){
                        val intent= Intent(this, ScanDetails::class.java)
                        intent.putExtra("medicationName",medicationItem)
                        finish()
                        startActivity(intent)
                    }else{
                        showToast("Sorry This Drug is Not Available")
                    }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    showToast(it.toString())
                }
            }
        }

    }

    private fun recognizeTextFromImage(){
        progressDialog.setMessage("Preparing Image...")
        progressDialog.show()

        try{
            val inputImage = InputImage.fromFilePath(this,imageUri!!)
            progressDialog.setMessage("Recognizing Text...")

            val textTaskResult = textRecognizer.process(inputImage)
                .addOnSuccessListener { text->
                    progressDialog.dismiss()

                    val recognizedText = text.text
                    textMedicationName.setText(recognizedText.toString())
                    Toast.makeText(this,textMedicationName.text.toString(),Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener{ e->
                    progressDialog.dismiss()
                    showToast("Failed to recognize image due to ${e.message}")
                }
        }
        catch (e: Exception){
            progressDialog.dismiss()
            showToast("Failed to prepare image due to ${e.message}")
        }
    }
    private fun showInputImageDialog(){
        val popupMenu = PopupMenu(this,btnScanHere)

        popupMenu.menu.add(Menu.NONE,1,1,"CAMERA")
        popupMenu.menu.add(Menu.NONE,2,2,"GALLERY")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->

            val id = menuItem.itemId
            if(id==1){
                if(checkCameraPermission()){
                    pickImageCamera()
                }
                else{
                    requstCameraPermission()
                }
            }
            else if(id==2){
                if(checkStoragePermission()){
                    pickImageGallery()
                }
                else{
                    requestStoragePermission()
                }
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data

                cameraCaptureImage.setImageURI(imageUri)
                if(imageUri!=null){
                    recognizeTextFromImage()
                }else{
                    Toast.makeText(this,"Pick an Image...",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                showToast("Cancelled...!")
            }
        }

    private fun pickImageCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Sample Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                cameraCaptureImage.setImageURI(imageUri)
                if(imageUri!=null){
                    recognizeTextFromImage()
                }else{
                    Toast.makeText(this,"Pick an Image...",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                showToast("Cancelled...!")
            }
        }

    private fun checkStoragePermission() : Boolean{
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkCameraPermission() : Boolean{
        val cameraResult = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val storageResult = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        return cameraResult && storageResult
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission, STORAGE_REQUEST_CODE)

    }

    private fun requstCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission, CAMERA_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_REQUEST_CODE->{
                if(grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted && storageAccepted){
                        pickImageCamera()
                    }else{
                        showToast("Camera & Storage Permission are Required")
                    }
                }
            }
            STORAGE_REQUEST_CODE ->{
                if(grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if(storageAccepted){
                        pickImageGallery()
                    }
                    else{
                        showToast("Storage Permission Are Required...!")
                    }
                }
            }
        }
    }
    private fun showToast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }
}