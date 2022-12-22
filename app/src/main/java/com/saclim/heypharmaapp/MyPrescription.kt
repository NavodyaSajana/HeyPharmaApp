package com.saclim.heypharmaapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_dashboard.bottomNavigationView
import kotlinx.android.synthetic.main.activity_my_prescription.*
import kotlinx.android.synthetic.main.my_prescriptions_recycler_item.*
import java.io.File

class MyPrescription : AppCompatActivity() {
    
    private lateinit var databaseReference:DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var prescriptionList:ArrayList<Presciption>
    private lateinit var myPrescriptionRecyclePresciption:RecyclerView
    private lateinit var loadingDialog:SweetAlertDialog
    private lateinit var errorDialog:SweetAlertDialog
    private lateinit var pharmacyList:ArrayList<Pharmacy>
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseStorage: FirebaseStorage
    private var imageUri: Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_prescription)

        prescriptionList = arrayListOf<Presciption>()
        myPrescriptionRecyclePresciption = findViewById(R.id.MyPrescriptionRecycle)
        myPrescriptionRecyclePresciption.adapter=prescriptionAdapter()
        myPrescriptionRecyclePresciption.layoutManager = LinearLayoutManager(this)
        myPrescriptionRecyclePresciption.setHasFixedSize(true)
        pharmacyList = arrayListOf<Pharmacy>()

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

        loadPrescriptionDetails()

        
        
    }
    private fun loadPrescriptionDetails(){
        showLoadingMessage("Loading Prescription Details...")

        firebaseAuth = FirebaseAuth.getInstance()
        val userID:String = firebaseAuth.currentUser!!.uid.toString()

        databaseReference = FirebaseDatabase.getInstance().getReference("Prescription")



        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(prescriptionSnapshot in snapshot.children){
                        val prescription = prescriptionSnapshot.getValue(com.saclim.heypharmaapp.Presciption::class.java)
                        if(prescription==null){showErrorMessage("Prescription Null")}

                        if(prescription!!.Member_id==userID){
                            prescriptionList.add(prescription)
                        }
                    }
                    myPrescriptionRecyclePresciption.adapter?.notifyDataSetChanged()
                    loadingDialog.dismissWithAnimation()
                }else{
                    loadingDialog.dismissWithAnimation()
                    showErrorMessage("Prescription Details are not available...")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loadingDialog.dismissWithAnimation()
                showErrorMessage("Data is not available go back and try again")
            }
        })

    }
    private inner class prescriptionAdapter : RecyclerView.Adapter<PrescriptionViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrescriptionViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.my_prescriptions_recycler_item,
                parent,false)

            return PrescriptionViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PrescriptionViewHolder, position: Int) {
            val currentItem = prescriptionList[position]

            databaseReference = FirebaseDatabase.getInstance().getReference("Pharmacy").child(currentItem.Phar_Id.toString())
            databaseReference.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pharmacy = snapshot.getValue<Pharmacy>()
                    if (pharmacy != null) {
                        holder.txtRePrePhaName.text=pharmacy.Name
                        holder.txtRePreTp.text=pharmacy.telephone
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

            holder.txtRePreId.text=currentItem.Prescription_id
            holder.txtRePrePrice.text="LKR"
            holder.txtRePreStatus.text=currentItem.Status

            storageReference = FirebaseStorage.getInstance().getReference(currentItem.Pres_Image.toString())

            val localFile:File = File.createTempFile("tempFile",".jpeg || .png")
            storageReference.getFile(localFile).addOnSuccessListener {
                val bitmap:Bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                Glide.with(applicationContext)
                    .load(bitmap)
                    .override(100,110)
                    .into(holder.imgPrescription)
            }

        }

        override fun getItemCount(): Int {
            return prescriptionList.size
        }

    }
    private inner class PrescriptionViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val txtRePreId : TextView = itemView.findViewById(R.id.txtRePreId)
        val txtRePrePhaName : TextView = itemView.findViewById(R.id.txtRePrePhaName)
        val txtRePreTp : TextView = itemView.findViewById(R.id.txtRePreTp)
        val txtRePrePrice : TextView = itemView.findViewById(R.id.txtRePrePrice)
        val txtRePreStatus : TextView = itemView.findViewById(R.id.txtRePreStatus)
        val imgPrescription : ImageView = itemView.findViewById(R.id.imgPrescription)

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
}