package com.newage.studlab.Home.BloodFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.studlab.Adapter.BloodAdapter.BloodDonorRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.homeInfo
import com.newage.studlab.Home.BloodFragment.BloodHomeFragment.Companion.bloodList
import com.newage.studlab.Home.BloodFragment.BloodHomeFragment.Companion.bloodRequestList
import com.newage.studlab.Model.AppsInfoModel.HomeInfo
import com.newage.studlab.Model.BloodModel.Blood
import com.newage.studlab.Model.BloodModel.Donation
import com.newage.studlab.Model.TokenModel.DeviceToken
import com.newage.studlab.R
import com.newage.studlab.Services.FcmNotificationService.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_blood.*
import kotlinx.android.synthetic.main.dialogue_blood_profile_info.view.*
import kotlinx.android.synthetic.main.dialogue_cancel.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BloodActivity : AppCompatActivity() {


    private lateinit var apiService: APIService

    var isRequested:Boolean = false

    var picker: DatePickerDialog? = null
    lateinit var blood:Array<String>
    lateinit var bloodGroup:String
    lateinit var bloodGroupRequest:String

    //view deceleration
    lateinit var backButton:ImageView
    lateinit var bloodSearchSpinner:Spinner
    lateinit var totalDonor:TextView

    lateinit var recyclerViewer:RecyclerView
    lateinit var bloodButton:ImageView
    lateinit var bloodRequestSendButton:ImageView

    //blood form
    lateinit var bloodRequestSpinner:Spinner
    lateinit var bloodNeedDate:EditText
    lateinit var calenderButton : ImageView
    lateinit var donationLocation:EditText
    lateinit var phoneNumber:EditText

    //containers
    lateinit var bloodFromContainer:CardView
    lateinit var bloodTitleContainer:CardView

    lateinit var adapter: BloodDonorRecyclerViewAdapter
    var filteredList = ArrayList<Blood>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blood)

        backButton = findViewById(R.id.back_to_blood_fragment)
        bloodSearchSpinner = findViewById(R.id.blood_search_spinner)
        totalDonor = findViewById(R.id.total_donor)

        recyclerViewer = findViewById(R.id.blood_donor_recyclerView)
        bloodButton = findViewById(R.id.blood_button)
        bloodRequestSendButton = findViewById(R.id.blood_request_send_button)

        //blood form
        bloodRequestSpinner = findViewById(R.id.blood_request_group_spinner)
        bloodNeedDate = findViewById(R.id.blood_need_on_date)
        calenderButton = findViewById(R.id.calender_image)
        donationLocation = findViewById(R.id.blood_request_location)
        phoneNumber = findViewById(R.id.phone_number)

        //containers
        bloodFromContainer = findViewById(R.id.blood_form_container)
        bloodTitleContainer = findViewById(R.id.blood_title_container)

        apiService = Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        blood = resources.getStringArray(R.array.blood)
        recyclerViewer.layoutManager = LinearLayoutManager(this)

        if(bloodList.size != 0){
            fragment_empty!!.visibility = View.INVISIBLE
            adapter = BloodDonorRecyclerViewAdapter(this, bloodList, clickListener = {

                donorClickListeners(it)

            })

        }else{
            fragment_empty!!.visibility = View.VISIBLE
            adapter = BloodDonorRecyclerViewAdapter(this, bloodList, clickListener = {

                donorClickListeners(it)

            })
        }

        recyclerViewer.adapter = adapter


        total_donor!!.text = bloodList.size.toString()

        checkBloodRequestList()

        buttonClickListener()
        textChangesEventListeners()
        handleSpinner()


    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.blood,R.layout.spinner_item_smoke_red)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)

        bloodSearchSpinner.adapter = bloodSpinnerAdapter
        bloodSearchSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                bloodGroup = blood[position]

                if(bloodGroup != "All Group"){
                    filteredList = bloodList.filter { it.bloodGroup == bloodGroup } as ArrayList<Blood>
                }else{
                    filteredList = bloodList
                }

                if(filteredList.isNotEmpty()){
                    fragment_empty!!.visibility = View.INVISIBLE
                }else{
                    fragment_empty!!.visibility = View.VISIBLE
                }

                total_donor!!.text = filteredList.size.toString()

                adapter.filterBloodDonorList(filteredList)

            }
        }

        bloodRequestSpinner.adapter = bloodSpinnerAdapter
        bloodRequestSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                bloodGroupRequest = blood[position]
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun buttonClickListener(){

        backButton.setOnClickListener{
            this.finish()
        }

        bloodButton.setOnClickListener{
            if(!isRequested){
                if(blood_form_container!!.visibility == View.VISIBLE){
                    blood_form_container!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_right_to_left));
                    blood_form_container!!.visibility = View.INVISIBLE

                    Handler().postDelayed({
                        blood_title_container!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_right_to_left));
                        blood_title_container!!.visibility = View.INVISIBLE
                    },100)
                }else if(blood_form_container!!.visibility == View.INVISIBLE){
                    blood_title_container!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.enter_left_to_right));
                    blood_title_container!!.visibility = View.VISIBLE

                    Handler().postDelayed({
                        blood_form_container!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.enter_left_to_right));
                        blood_form_container!!.visibility = View.VISIBLE
                    },100)
                }

            }else{
                this.showDeleteDialog()
            }
        }

        calenderButton.setOnClickListener{

            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            // date picker dialog
            picker = DatePickerDialog(this, OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                bloodNeedDate.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)

                }, year, month, day)
            picker!!.show()

        }

        bloodRequestSendButton.setOnClickListener{

            if(bloodGroupRequest != "All Group" && bloodNeedDate.text.toString().isNotEmpty() && donationLocation.text.toString().isNotEmpty() && phoneNumber.text.toString().isNotEmpty() && phoneNumber.text.toString().length == 11){


                blood_form_container!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_right_to_left));
                blood_form_container!!.visibility = View.INVISIBLE

                Handler().postDelayed({
                    blood_title_container!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.exit_right_to_left));
                    blood_title_container!!.visibility = View.INVISIBLE
                },100)


                val formatter = SimpleDateFormat.getDateInstance()
                val formattedDate = formatter.format(Calendar.getInstance().time)

                val blood = Blood(currentUser!!.user_name,currentUser!!.user_id,bloodGroupRequest,
                    formattedDate,bloodNeedDate.text.toString(),
                    donationLocation.text.toString(),"+88${phoneNumber.text}","false")

                uploadBloodRequest(blood)

            }else if(bloodGroupRequest == "All Group"){
                Toasty.warning(this, "Select blood group", Toast.LENGTH_SHORT, true).show()
            }else if(bloodNeedDate.text.toString().isEmpty()){
                Toasty.warning(this, "Pick blood needed date", Toast.LENGTH_SHORT, true).show()

            }else if(donationLocation.text.toString().isEmpty()){
                Toasty.warning(this, "Hospital & location is empty", Toast.LENGTH_SHORT, true).show()

            }else if(phoneNumber.text.toString().isEmpty() || phoneNumber.text.toString().length != 11){
                Toasty.warning(this, "Contact number is required", Toast.LENGTH_SHORT, true).show()

            }

        }
    }


    private fun textChangesEventListeners(){
        phoneNumber.addTextChangedListener(object :
            TextWatcher { override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length == 11) {
                phoneNumber.onEditorAction(EditorInfo.IME_ACTION_DONE)
            }
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun uploadBloodRequest(blood:Blood){

        val homeInfoRef = FirebaseDatabase.getInstance().getReference("/AppsInfo/home")

        val ref = FirebaseDatabase.getInstance().getReference("/Blood/Request/${blood.bloodId}")
        ref.setValue(blood)
            .addOnSuccessListener {
                this.runOnUiThread{

                    createNotification("Blood needed: ${blood.bloodGroup}","Requested by ${blood.bloodName}, Blood needed before ${blood.bloodNeededDate} on ${blood.bloodHospitalName}")
                    bloodButton.setImageResource(R.drawable.cross)
                    Toasty.success(this, "Success", Toast.LENGTH_SHORT, true).show()
                    isRequested = true
                }

                val updateHomeInfo = HomeInfo(homeInfo!!.totalStudent,homeInfo!!.totalTeacher,homeInfo!!.totalFaculty,homeInfo!!.totalProgram,(homeInfo!!.totalBloodRequest.toInt() + 1).toString(),
                    homeInfo!!.totalResponse)
                homeInfoRef.setValue(updateHomeInfo)
            }
            .addOnFailureListener{
                this.runOnUiThread{
                    Toasty.success(this, "Failed", Toast.LENGTH_SHORT, true).show()
                }
            }
    }


    //Send notification ----------------------------------------------------------------------------
    private fun createNotification(title:String, message: String){
        val ref = FirebaseDatabase.getInstance().getReference("/Tokens")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){

                    var tokens: DeviceToken?
                    for (productSnapshot in dataSnapshot.children) {

                        tokens = productSnapshot.getValue(DeviceToken::class.java)

                        sendNotifications(tokens!!.tokenId,title,message)

                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw (databaseError.toException() as Throwable?)!!
            }
        })
    }



    private fun sendNotifications(userToken: String, title: String, message: String) {
        val data = Data(title, message)
        val sender = NotificationSender(data, userToken)

        apiService.sendNotifcation(sender).enqueue(object : Callback<MyResponse> {
            override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                if (response.code() == 200) {
                    if (response.body()!!.success != 1) {
                        Toast.makeText(this@BloodActivity, "Failed ", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
            }
        })
    }

    private fun checkBloodRequestList() {

        if (bloodRequestList.isNotEmpty()) {
            val blood = bloodRequestList.filter { it.bloodId == currentUser!!.user_id }

            if (blood.isNotEmpty()) {
                blood_form_container!!.visibility = View.INVISIBLE
                blood_title_container!!.visibility = View.INVISIBLE
                bloodButton.setImageResource(R.drawable.cross)
                isRequested = true

                if (blood[0].bloodRequestGrant != "false") {
                    this@BloodActivity.showBloodConfirmationDialog(blood[0])
                }

            } else {
                blood_title_container!!.visibility = View.VISIBLE
                blood_form_container!!.visibility = View.VISIBLE
                isRequested = false
                bloodButton.setImageResource(R.drawable.blood_drop_white)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun Activity.showDeleteDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogue_cancel, null)
        dialogBuilder.setView(dialogView)

        val dialogueView = dialogView.containers

        val yesButton = dialogView.yes_button
        val noButton = dialogView.no_button

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        dialogueView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

        yesButton.setOnClickListener{
            cancelRequest()
            alertDialog.dismiss()
        }

        noButton.setOnClickListener{
            alertDialog.dismiss()
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun Activity.showBloodConfirmationDialog(blood:Blood) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogue_cancel, null)
        dialogBuilder.setView(dialogView)

        val dialogueView = dialogView.containers

        val title = dialogView.delete_title
        val message = dialogView.delete_message


        val yesButton = dialogView.yes_button
        val noButton = dialogView.no_button

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        dialogueView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

        title.text = "Blood Bank"
        message.text = "Hey ${currentUser!!.user_name}, We noticed that someone responded to your request. Did you get the blood?"

        yesButton.setOnClickListener{

            val formatter = SimpleDateFormat.getDateInstance()
            val formattedDate = formatter.format(Calendar.getInstance().time)


            val donationRef = FirebaseDatabase.getInstance().getReference("/Blood/Donation/${blood.bloodGroup+" req "+blood.bloodId +" dnr "+blood.bloodRequestGrant + " on " + formattedDate}")

            val donation = Donation(blood.bloodId,blood.bloodRequestGrant,formattedDate,"'${blood.bloodGroup}' blood donated by ${blood.bloodRequestGrant}, received by ${blood.bloodId} on ${blood.bloodLastDonateDate} at ${blood.bloodHospitalName}. Request date: ${blood.bloodReqDate}")

            donationRef.setValue(donation).addOnSuccessListener {
                cancelRequest()
                Toasty.success(this@BloodActivity, "Congrats.", Toast.LENGTH_SHORT, true).show()
                alertDialog.dismiss()
            }.addOnFailureListener{
                alertDialog.dismiss()
            }
        }

        noButton.setOnClickListener{
            alertDialog.dismiss()
        }
    }

    private fun cancelRequest(){
        val ref = FirebaseDatabase.getInstance().reference.child("/Blood/Request/${currentUser!!.user_id}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (productSnapshot in dataSnapshot.children) {
                        productSnapshot.ref.removeValue()
                    }

                    this@BloodActivity.runOnUiThread() {
                        isRequested = false
                        bloodButton.setImageResource(R.drawable.blood_drop_white)

                        blood_title_container!!.startAnimation(AnimationUtils.loadAnimation(this@BloodActivity, R.anim.enter_left_to_right));
                        blood_title_container!!.visibility = View.VISIBLE

                        Handler().postDelayed({
                            blood_form_container!!.startAnimation(AnimationUtils.loadAnimation(this@BloodActivity, R.anim.enter_left_to_right));
                            blood_form_container!!.visibility = View.VISIBLE
                        },100)

                        Toasty.success(applicationContext, "Success", Toast.LENGTH_SHORT, true)
                            .show()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                this@BloodActivity.runOnUiThread(){
                    Toasty.error(applicationContext, "Failed", Toast.LENGTH_SHORT, true).show()
                }
                throw databaseError.toException()
            }
        })
    }

    //----------------------------------------------------------------------------------------------

    private fun donorClickListeners(blood:Blood){
        this.showBloodProfileDialog(blood)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun Activity.showBloodProfileDialog(blood:Blood) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogue_blood_profile_info, null)
        dialogBuilder.setView(dialogView)

        val dialogueView = dialogView.blood_profile_container

        val bloodGroup = dialogView.blood_prof_info
        val bloodName = dialogView.donor_prof_name
        val bloodId = dialogView.donor_prof_id
        val bloodTotalDonate = dialogView.total_donate
        val bloodLastDonate = dialogView.last_donate

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        dialogueView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.show()

        bloodGroup.text = blood.bloodGroup
        bloodName.text = blood.bloodName
        bloodId.text = blood.bloodId
        bloodTotalDonate.text = "Total Blood Donate: ${blood.bloodTotalDonate} times"
        bloodLastDonate.text = "Last Blood Donate Date: ${blood.bloodLastDonateDate}"
    }

}
