package com.saclim.heypharmaapp

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_scan_here.*
import kotlinx.android.synthetic.main.available_pharmacy_recycler_item.*


private lateinit var txtMedication:TextView
private lateinit var txtDiciese:TextView
private lateinit var txtMedDescription:TextView
private lateinit var medImg:ImageView
private lateinit var btnScanDetailsCancel:MaterialButton
private lateinit var scanDetailsRecycle:RecyclerView
private lateinit var pharmacyList:ArrayList<Pharmacy>

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


        scanDetailsRecycle = findViewById(R.id.ScanDetailsRecycle)
        scanDetailsRecycle.adapter=pharmacyAdapter()
        scanDetailsRecycle.layoutManager = LinearLayoutManager(this)
        scanDetailsRecycle.setHasFixedSize(true)
        pharmacyList = arrayListOf<Pharmacy>()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        if (bundle != null) {
            if(bundle.getString("medicationName")!=null){
                val medicationName = bundle.getString("medicationName").toString()
                makeToast(medicationName)
                txtMedication.text = medicationName
                setDetails(medicationName)
            }
        }
        bottomNavigationView.background = null

        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Payment -> {
                    val intent= Intent(this, Payment::class.java)
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
                R.id.Home->{
                    val intent=Intent(this,DashboardHome::class.java)
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
            txtDiciese.text = "Disease : ${result.child("Disease").value.toString()}"
            txtMedDescription.text = result.child("Description").value.toString()
            val imageUrl = result.child("Image").value.toString()
            Glide.with(applicationContext)
                .load(imageUrl)
                .override(130, 130)
                .into(medImg)
            setPharmacyDetails(drugName)
            progressDialog.dismiss()
        }
    }

    private fun setPharmacyDetails(drugName:String) {
        var pharmacyNumbers = ArrayList<String>()
        progressDialog.setMessage("Pharmacy Details Loading...")
        progressDialog.show()
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("Drug/${drugName}/AvailablePharmacy")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(pharmaSnapshot in snapshot.children){
                    val data = pharmaSnapshot.getValue().toString()
                    pharmacyNumbers.add(data)
                }
                loadPharmacyDetails(pharmacyNumbers)
                progressDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast("Unable to find Pharmacy")
                progressDialog.dismiss()
            }
        })
    }
    private fun loadPharmacyDetails(pharmacyNumbers:ArrayList<String>){
        progressDialog.setMessage("Pharmacy Details Loading...")
        progressDialog.show()
        val end = pharmacyNumbers.size-1

        databaseReference = FirebaseDatabase.getInstance().getReference("Pharmacy")

        databaseReference.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                pharmacyList.clear()
                if(snapshot.exists()){
                    for(pharmacySnapshot in snapshot.children){
                        val pharmacy = pharmacySnapshot.getValue(Pharmacy::class.java)

                        for(c in 0..end){
                            if(pharmacy!!.telephone.toString()==pharmacyNumbers.get(c)){
                                pharmacyList.add(pharmacy!!)
                            }
                        }
                    }
                    scanDetailsRecycle.adapter?.notifyDataSetChanged()
                    progressDialog.dismiss()
                }else{
                    progressDialog.dismiss()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                makeToast("Data is not available go back and try again")
            }
        })

    }
    private inner class pharmacyAdapter : RecyclerView.Adapter<PharmacyViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.available_pharmacy_recycler_item,
            parent,false)

            return PharmacyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PharmacyViewHolder, position: Int) {

            val currentItem = pharmacyList[position]
            holder.txtRePharmaName.text=currentItem.Name
            holder.txtRePharmaTP.text=currentItem.telephone
            holder.txtRePharmaLocation.text=currentItem.Location
            Glide.with(applicationContext)
                .load(currentItem.ImagePharmacy)
                .override(100, 70)
                .into(holder.imgPharmacy)
            holder.avb_pharmacy_recycle_item.setOnClickListener {
                val selectedItem = pharmacyList[position]
                Toast.makeText(applicationContext,selectedItem.Name.toString(),Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int {
            return pharmacyList.size
        }

    }
    private inner class PharmacyViewHolder(itemView : View):RecyclerView.ViewHolder(itemView){
        val txtRePharmaName:TextView = itemView.findViewById(R.id.txtRePharmaName)
        val txtRePharmaTP:TextView = itemView.findViewById(R.id.txtRePharmaTP)
        val txtRePharmaLocation:TextView = itemView.findViewById(R.id.txtRePharmaLocation)
        val imgPharmacy:ImageView = itemView.findViewById(R.id.imgPharmacy)
        val cardView : CardView = itemView.findViewById(R.id.main_tab)
        val avb_pharmacy_recycle_item : LinearLayout = itemView.findViewById(R.id.avb_pharmacy_recycle_item)
    }

    private fun makeToast(value:String){
        Toast.makeText(this,value,Toast.LENGTH_SHORT).show()
    }
}