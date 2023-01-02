package com.saclim.heypharmaapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.SimpleDateFormat
import java.util.*


class PaymentDetails : AppCompatActivity() {
    private lateinit var txtCardNo:EditText
    private lateinit var txtAddressNo:TextView
    private lateinit var txtCardBank:EditText
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var errorDialog: SweetAlertDialog
    private lateinit var successDialog: SweetAlertDialog
    private lateinit var confirmDialog: SweetAlertDialog
    private lateinit var btnBuy: MaterialButton
    private var presID:String = ""
    private var billAmount:String = ""
    private var orderID:String="HPO-0"
    private lateinit var radiobtnCod: MaterialRadioButton
    private lateinit var radioCard: MaterialRadioButton
    private var paymentType:String="Cash On Delivery"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_details)

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

        val extra = intent.extras
        if(extra!=null){
            val data = extra.getStringArray("data")
            presID=data!![0]
            billAmount=data!![1]
        }

        txtCardNo = findViewById(R.id.txtCardNo)
        txtAddressNo = findViewById(R.id.txtAddressName)
        txtCardBank = findViewById(R.id.txtCardBank)
        btnBuy = findViewById(R.id.btnBuy)
        radiobtnCod = findViewById(R.id.radioBtnCod)
        radioCard = findViewById(R.id.radioCard)

        loadShippingAddress()

        radiobtnCod.isChecked=true
        radioCard.isChecked=false

        radiobtnCod.setOnClickListener {
            if(radioCard.isChecked) {
                radioCard.isChecked = false
                paymentType="Cash On Delivery"
            }
        }

        radioCard.setOnClickListener {
            if(radiobtnCod.isChecked) {
                radiobtnCod.isChecked = false
                paymentType="Bank Card"
            }
        }

        btnBuy.setOnClickListener {
            showConfirmMessage("Are you sure to place the order?")
            confirmDialog.setConfirmButton("Yes",SweetAlertDialog.OnSweetClickListener {
                confirmDialog.dismissWithAnimation()
                createNewOrder()
            })
            confirmDialog.setCancelButton("No",SweetAlertDialog.OnSweetClickListener {
                confirmDialog.dismissWithAnimation()
            })
        }


    }

    private fun createNewOrder(){

        showLoadingMessage("Placing the order...")
        generateOrderNumber()
        firebaseAuth = FirebaseAuth.getInstance()
        val current = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = formatter.format(current)

        val NewOrder = Order(orderID,firebaseAuth.currentUser!!.uid,presID,billAmount,paymentType,txtCardNo.text.toString(),txtCardBank.text.toString(),currentDate)
        databaseReference = FirebaseDatabase.getInstance().getReference("Order")
        databaseReference.child(orderID).setValue(NewOrder).addOnCompleteListener { result->
            if(result.isSuccessful){
                loadingDialog.dismissWithAnimation()
                changePrescriptionStatus()
                showSuccessMessage("Your Order Placed Successfully")
                successDialog.setConfirmButton("Ok",SweetAlertDialog.OnSweetClickListener {
                    successDialog.dismissWithAnimation()
                    val intent= Intent(this, MyPrescription::class.java)
                    finish()
                    startActivity(intent)
                })
            }else{
                loadingDialog.dismissWithAnimation()
                showErrorMessage("Something went wrong while ordering try again...")
            }
        }
    }

    private fun changePrescriptionStatus(){
        showLoadingMessage("Updating Prescription Details...")
        databaseReference = FirebaseDatabase.getInstance().getReference("Prescription")
        databaseReference.child(presID).child("status").setValue("Ordered").addOnCompleteListener { result->
            if(result.isSuccessful){
                loadingDialog.dismissWithAnimation()
            }else{
                loadingDialog.dismissWithAnimation()
            }
        }
    }
    private fun generateOrderNumber(){
        orderID="HPO-${(0..9999999).shuffled().last()}"
    }
    private fun checkIfAlreadyExsist(){

    }
    private fun loadShippingAddress(){
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("ShippingAddress").child(firebaseAuth.currentUser!!.uid)

        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val shipAddress = snapshot.getValue<ShippingAddress>()
                if(shipAddress!=null){
                    txtAddressNo.setText("To : ${shipAddress.fullName}\n${shipAddress.addressLine1},${shipAddress.addressLine2}" +
                            "\n${shipAddress.city},${shipAddress.province}\n${shipAddress.shipTelephone}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
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