package com.saclim.heypharmaapp

import android.content.Intent
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.w3c.dom.Text

private lateinit var loadingDialog:SweetAlertDialog
private lateinit var databaseReference:DatabaseReference
private lateinit var myPrescriptionRecycle:RecyclerView
private lateinit var pharmacyList:ArrayList<Pharmacy>
private lateinit var errorDialog:SweetAlertDialog

class BuyMedicine : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_medicine)

        myPrescriptionRecycle = findViewById(R.id.MyPrescriptionRecycle)
        pharmacyList = arrayListOf<Pharmacy>()
        myPrescriptionRecycle.adapter=pharmacyAdapter()
        myPrescriptionRecycle.layoutManager = LinearLayoutManager(this)
        myPrescriptionRecycle.setHasFixedSize(true)

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

        loadPharmacyDetails()

    }

    private fun loadPharmacyDetails(){
        showLoadingMessage("Loading Pharmacy Details...")

        databaseReference = FirebaseDatabase.getInstance().getReference("Pharmacy")

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(pharmacySnapshot in snapshot.children){
                        val pharmacy = pharmacySnapshot.getValue(com.saclim.heypharmaapp.Pharmacy::class.java)
                        if(pharmacy==null){showErrorMessage("Pharmacy Null")}
                        pharmacyList.add(pharmacy!!)

                    }
                    myPrescriptionRecycle.adapter?.notifyDataSetChanged()
                    loadingDialog.dismissWithAnimation()
                }else{
                    loadingDialog.dismissWithAnimation()
                    showErrorMessage("Pharmacy Details are not available...")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                loadingDialog.dismissWithAnimation()
                showErrorMessage("Data is not available go back and try again")
            }
        })

    }
    private inner class pharmacyAdapter : RecyclerView.Adapter<PharmacyViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PharmacyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.select_pharmacy_recycler_item,
                parent,false)

            return PharmacyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: PharmacyViewHolder, position: Int) {

            val currentItem = pharmacyList[position]
            holder.txtReSelectPhaName.text=currentItem.Name
            holder.txtReSelectPhaTp.text=currentItem.telephone
            holder.txtReSelectPhaLocation.text=currentItem.Location
            Glide.with(applicationContext)
                .load(currentItem.ImagePharmacy)
                .override(100, 70)
                .into(holder.imgSelectPharmacy)
            holder.cardView.setOnClickListener{
                val selectedItem = pharmacyList[position]
                Toast.makeText(applicationContext,selectedItem.Name.toString(),Toast.LENGTH_SHORT).show()
            }
            /*holder.select_pharmacy_recycle_click.setOnClickListener (View.OnClickListener {
                if(it.callOnClick()){
                    val selectedItem = pharmacyList[position]
                    Toast.makeText(applicationContext,selectedItem.Name.toString(),Toast.LENGTH_SHORT).show()
                }
            })*/


        }

        override fun getItemCount(): Int {
            return pharmacyList.size
        }

    }
    private inner class PharmacyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val txtReSelectPhaName : TextView = itemView.findViewById(R.id.txtReSelectPhaName)
        val txtReSelectPhaTp : TextView = itemView.findViewById(R.id.txtReSelectPhaTp)
        val txtReSelectPhaLocation : TextView = itemView.findViewById(R.id.txtReSelectPhaLocation)
        val imgSelectPharmacy : ImageView = itemView.findViewById(R.id.imgSelectPharmacy)
        val cardView : CardView = itemView.findViewById(R.id.main_tab)
        val select_pharmacy_recycle_click : LinearLayout = findViewById(R.id.select_pharmacy_recycle_click)
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