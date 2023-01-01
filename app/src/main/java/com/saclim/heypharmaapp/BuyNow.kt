package com.saclim.heypharmaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class BuyNow : AppCompatActivity() {
    private lateinit var prescriptionID:String
    private lateinit var pharmacyID:String
    private var total:Double=0.0
    private var netTotal:Double=0.0
    private var presID:String=""
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var errorDialog: SweetAlertDialog
    private lateinit var confirmDialog: SweetAlertDialog
    private lateinit var successDialog: SweetAlertDialog
    private lateinit var databaseReference: DatabaseReference
    private lateinit var txtPharmacyTp: TextView
    private lateinit var txtPharmacyName: TextView
    private lateinit var txtTotal: TextView
    private lateinit var txtCharges: TextView
    private lateinit var txtNet: TextView
    private lateinit var btnBuyNow: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_now)

        txtPharmacyName = findViewById(R.id.txtPharmacyName)
        txtPharmacyTp = findViewById(R.id.txtPharmacyTp)
        txtTotal = findViewById(R.id.txtTotal)
        txtCharges = findViewById(R.id.txtCharges)
        txtNet = findViewById(R.id.txtNet)
        btnBuyNow = findViewById(R.id.btnBuyNow)

        val extra = intent.extras
        if(extra!=null){
            prescriptionID=extra.getString("prescriptionID").toString()
            loadPrescriptionDetails()

        }else{
            val intent = Intent(this,MyPrescription::class.java)
            finish()
            startActivity(intent)
        }
        btnBuyNow.setOnClickListener {
            showConfirmMessage("Proceed to payment details")
            confirmDialog.setConfirmButton("Ok",SweetAlertDialog.OnSweetClickListener {
                confirmDialog.dismissWithAnimation()
                val dataToSend = arrayOf<String>(presID,netTotal.toString())
                val intent = Intent(this,PaymentDetails::class.java)
                intent.putExtra("data",dataToSend)
                finish()
                startActivity(intent)
            })
            confirmDialog.setCancelButton("Cancel",SweetAlertDialog.OnSweetClickListener {
                confirmDialog.dismissWithAnimation()
            })
        }
    }

    private fun loadPrescriptionDetails(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Prescription").child(prescriptionID)
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val prescription = snapshot.getValue<Presciption>()
                if(prescription!=null){
                    pharmacyID=prescription.Phar_Id.toString()
                    total=prescription.Pres_price!!.toDouble()
                    presID = prescription.Prescription_id.toString()
                    loadPharmacyDetails()
                    calculateBillPrice()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun loadPharmacyDetails(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Pharmacy").child(pharmacyID)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pharmacy = snapshot.getValue<Pharmacy>()
                if (pharmacy != null) {
                    txtPharmacyName.setText(pharmacy.Name)
                    txtPharmacyTp.setText(pharmacy.telephone)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun calculateBillPrice(){
        txtTotal.setText(total.toString())
        txtCharges.setText((total*0.2).toString())
        netTotal = (total+total*0.2)
        txtNet.setText(netTotal.toString())
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
    private fun showConfirmMessage(message:String){
        confirmDialog = SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
        confirmDialog.setCancelable(true)
        confirmDialog.setCustomImage(R.drawable.question_mark)
        confirmDialog.setTitleText("Confirm...!")
        confirmDialog.setContentText(message)
        confirmDialog.show()
    }
    private fun showSuccessMessage(message:String){
        successDialog = SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
        successDialog.setCancelable(true)
        successDialog.setTitleText("Done...!")
        successDialog.setContentText(message)
        successDialog.show()
    }
}