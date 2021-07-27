package com.newage.studlab.Home.BloodFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.studlab.Adapter.BloodAdapter.BloodRequestRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.homeInfo
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Model.AppsInfoModel.HomeInfo
import com.newage.studlab.Model.BloodModel.Blood
import com.newage.studlab.Model.BloodModel.Donation
import com.newage.studlab.Model.TokenModel.DeviceToken
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.R
import com.newage.studlab.Services.FcmNotificationService.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialogue_blood_profile.view.*
import kotlinx.android.synthetic.main.dialogue_blood_request.view.*
import kotlinx.android.synthetic.main.fragment_blood.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BloodHomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_blood, container, false);
    }

    private lateinit var apiService: APIService

    companion object{
        val bloodList = arrayListOf<Blood>()
        val bloodRequestList = arrayListOf<Blood>()
    }

    lateinit var blood:Array<String>
    lateinit var bloodGroup:String

    lateinit var requestNotFound:TextView
    lateinit var recyclerView:RecyclerView

    lateinit var recentRequest:TextView
    lateinit var totalRequest:TextView
    lateinit var totalResponse:TextView

    lateinit var recentDonation:TextView
    lateinit var totalDonation:TextView
    lateinit var totalDonor:TextView


    lateinit var adapter: BloodRequestRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)


        activity?.let {
            requestNotFound = it.findViewById(R.id.fragment_empty)
            recyclerView = it.findViewById(R.id.blood_request_recyclerView)

            recentRequest = it.findViewById(R.id.blood_recent_request)
            totalRequest = it.findViewById(R.id.blood_total_request)
            totalResponse = it.findViewById(R.id.blood_total_response)

            recentDonation = it.findViewById(R.id.blood_recent_donation)
            totalDonation = it.findViewById(R.id.blood_total_donation)
            totalDonor = it.findViewById(R.id.blood_total_donor)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)

        blood = resources.getStringArray(R.array.blood)
        bloodGroup = blood[0]


        if(bloodRequestList.isNotEmpty()){
            requestNotFound.visibility = View.INVISIBLE
            adapter = BloodRequestRecyclerViewAdapter(context, bloodRequestList, clickListener = {
                requestClickListener(it)
            })
            recyclerView.adapter = adapter
        }else{
            requestNotFound.visibility = View.VISIBLE
            adapter = BloodRequestRecyclerViewAdapter(context, bloodRequestList, clickListener = {
                requestClickListener(it)
            })
            recyclerView.adapter = adapter
        }

        handleSpinner()
        buttonClickListener()
    }

    private fun buttonClickListener(){
        search_donor_button.setOnClickListener{
            val databaseHandler = DatabaseHelper(appContext)
            val bloodProfile = databaseHandler.getBloodData()

            if(bloodList.filter { it.bloodId == currentUser!!.user_id }.isEmpty() || bloodProfile == null){
                activity!!.showSettingDialog()
            }else{
                startActivity(Intent(appContext,BloodActivity::class.java))
            }
        }
    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.blood,R.layout.spinner_item_smoke)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        blood_spinner.adapter = bloodSpinnerAdapter

        blood_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                bloodGroup = blood[position]

                val bloodList:ArrayList<Blood>

                if(bloodGroup != "All Group"){
                    bloodList = bloodRequestList.filter { it.bloodGroup == bloodGroup } as ArrayList<Blood>
                }else{
                    bloodList = bloodRequestList
                }

                adapter.filterBloodRequestList(bloodList)

            }
        }
    }



    @SuppressLint("InflateParams", "SetTextI18n", "DefaultLocale")
    private fun Activity.showSettingDialog() {

        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogue_blood_profile, null)
        dialogBuilder.setView(dialogView)

        val routineView = dialogView.containers

        val doneButton = dialogView.cofirm_profile_button
        val minusButton = dialogView.donate_minus_button
        val plusButton = dialogView.donate_plus_button

        val bloodGroupSpinner = dialogView.blood_group_spinner
        val calenderLastDonate = dialogView.blood_last_donate
        val calenderImage = dialogView.calender_image

        val donorName = dialogView.donor_name
        val donorId = dialogView.donor_id

        val totalDonated = dialogView.total_donate

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        routineView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(true)
        alertDialog.show()

        //load some value
        donorName.text = currentUser!!.user_name
        donorId.text = currentUser!!.user_id

        var blodGroup = ""

        val bloodArray:Array<String> = resources.getStringArray(R.array.blood_profile)

        val bloodAdapter = ArrayAdapter.createFromResource(this,R.array.blood_profile,R.layout.spinner_item_smoke_red)
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        bloodGroupSpinner.adapter = bloodAdapter
        bloodGroupSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                blodGroup = bloodArray[position]
            }
        }

        calenderImage.setOnClickListener{
            var picker: DatePickerDialog? = null
            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            picker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    calenderLastDonate.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year

                }, year, month, day)
            picker.show()
        }

        var tDonate = 0

        minusButton.setOnClickListener{
            if(totalDonated.text.toString() == "Total Donate"){
                totalDonated.text = "0"
            }else{
                tDonate -= 1
                totalDonated.text = "$tDonate"
            }
        }

        plusButton.setOnClickListener{
            if(totalDonated.text.toString() == "Total Donate"){
                totalDonated.text = "0"
            }else{
                tDonate += 1
                totalDonated.text = "$tDonate"
            }
        }

        doneButton.setOnClickListener{
            if(totalDonated.text.toString() != "Total Donate" && calenderLastDonate.text.isNotEmpty() && blodGroup != "Select Blood"){

                val blood = Blood(currentUser!!.user_name,currentUser!!.user_id,blodGroup, totalDonated.text.toString(),calenderLastDonate.text.toString())
                uploadBloodProfile(blood)
                alertDialog.dismiss()
            }else if(totalDonated.text.toString() != "Total Donate" && calenderLastDonate.text.isEmpty() && blodGroup != "Select Blood"){

                val blood = Blood(currentUser!!.user_name,currentUser!!.user_id,blodGroup, totalDonated.text.toString(),"Never")

                uploadBloodProfile(blood)
                alertDialog.dismiss()

            }else if(blodGroup == "Select Blood"){
                Toasty.warning(requireContext(), "Select your blood group.", Toast.LENGTH_SHORT, true).show()

            }else if(totalDonated.text.toString() == "Total Donate"){
                Toasty.warning(requireContext(), "Total donate should be zero or more", Toast.LENGTH_SHORT, true).show()

            }
        }
    }

    private fun uploadBloodProfile(blood:Blood){
        val ref = FirebaseDatabase.getInstance().getReference("/Blood/Donor/${blood.bloodId}")
        ref.setValue(blood)
            .addOnSuccessListener {


                val user = Users(
                    currentUser!!.user_id,currentUser!!.user_name,currentUser!!.user_gender,
                    currentUser!!.user_dob,currentUser!!.user_phone_old,currentUser!!.user_phone_new,
                    currentUser!!.user_password,currentUser!!.user_type,currentUser!!.user_image,currentUser!!.user_faculty_name,
                    currentUser!!.user_prog_or_dept,currentUser!!.user_shift_or_post,currentUser!!.user_intake,
                    currentUser!!.user_section,currentUser!!.user_semester,currentUser!!.user_address,currentUser!!.user_status,
                    blood.bloodGroup,currentUser!!.user_room_no, currentUser!!.annex_id,currentUser!!.annex_pass,currentUser!!.annex_session_id)


                val database = DatabaseHelper(requireContext())

                database.deleteBloodData()
                database.saveBloodData(blood)

                database.deleteUserData()
                database.saveUserData(user)

                val ref1 = FirebaseDatabase.getInstance().getReference("/Users/${currentUser!!.user_id}")
                ref1.setValue(user).addOnSuccessListener {}
                ref1.setValue(user).addOnFailureListener{}

                activity!!.runOnUiThread(){
                    Toasty.success(requireContext(), "Success", Toast.LENGTH_SHORT, true).show()
                }
            }
            .addOnFailureListener{
                activity!!.runOnUiThread(){
                    Toasty.success(requireContext(), "Failed", Toast.LENGTH_SHORT, true).show()
                }
            }
    }



    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun Activity.showBloodRequestDialog(name:String, id:String, bGroup:String, address:String, needDate:String, reqDate:String, contactNo:String, bloodRequestStatus:String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogue_blood_request, null)
        dialogBuilder.setView(dialogView)

        val routineView = dialogView.blood_request_containers

        val bloodGroup = dialogView.blood_group
        val requestName = dialogView.request_name
        val requestId = dialogView.request_id

        val requestAddress = dialogView.request_address
        val requestBloodNeededDate = dialogView.request_blood_needed_date
        val requestDate = dialogView.request_date

        val contactNum = dialogView.dialog_contact_info

        val responseButton = dialogView.request_response_button
        val ignoreButton = dialogView.request_ignore_button

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        routineView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(true)
        alertDialog.show()

        //load some value
        bloodGroup.text = bGroup
        requestName.text = name
        requestId.text = id
        requestAddress.text = "Address: $address"
        requestBloodNeededDate.text = "Blood Needed Date: $needDate"
        requestDate.text = "Request Date: $reqDate"


        responseButton.setOnClickListener{
            val ref = FirebaseDatabase.getInstance().getReference("/Blood/Request/$id")
            val infoRef = FirebaseDatabase.getInstance().getReference("/AppsInfo/home")

            if(!bloodRequestStatus.contains(currentUser!!.user_id) && contactNum.text.toString().isNotEmpty() && id != currentUser!!.user_id){
                val blood = Blood(name,id,bGroup, reqDate,needDate, address,contactNo, (bloodRequestStatus+", "+currentUser!!.user_id).replace("false, ",""))
                val updateInfo = HomeInfo(homeInfo!!.totalStudent,homeInfo!!.totalTeacher,homeInfo!!.totalFaculty,homeInfo!!.totalProgram,homeInfo!!.totalBloodRequest, (homeInfo!!.totalResponse.toInt() + 1).toString())

                ref.setValue(blood)
                infoRef.setValue(updateInfo)

                loadBloodRequestList()

                createNotification("Blood response: ${blood.bloodGroup}","Responded by ${currentUser!!.user_id} phone: ${contactNum.text} which was requested by ${blood.bloodName} ",blood.bloodId)

                alertDialog.dismiss()

                onCall(contactNo)
            }else if(contactNum.text.toString().isEmpty() || contactNum.text.toString().length < 11){

                Toasty.error(requireContext(), "Please check your contact number", Toast.LENGTH_SHORT, true).show()

            }else if(bloodRequestStatus.contains(currentUser!!.user_id)){
                Toasty.warning(requireContext(), "You have already responded to this request", Toast.LENGTH_SHORT, true).show()

                alertDialog.dismiss()

                onCall(contactNo)
            }else if(id == currentUser!!.user_id){

                Toasty.info(requireContext(), "This request was made by you.", Toast.LENGTH_SHORT, true).show()

            }

        }

        ignoreButton.setOnClickListener{
            alertDialog.dismiss()
        }

    }

    private fun requestClickListener(blood:Blood){
            activity!!.showBloodRequestDialog(blood.bloodName,blood.bloodId,blood.bloodGroup,blood.bloodHospitalName,blood.bloodNeededDate,blood.bloodReqDate,blood.bloodContactNo,blood.bloodRequestGrant)
    }

    private fun onCall(number:String) {
        val intent =  Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        startActivity(intent)
    }


    //Send responded notification
    //Send notification ----------------------------------------------------------------------------
    private fun createNotification(title:String, message: String, requestId:String){
        val ref = FirebaseDatabase.getInstance().getReference("/Tokens")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){

                    var tokens: DeviceToken?
                    for (productSnapshot in dataSnapshot.children) {

                        tokens = productSnapshot.getValue(DeviceToken::class.java)

                        if(tokens!!.userId == requestId){
                            sendNotifications(tokens.tokenId,title,message)
                        }

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
                        Toast.makeText(appContext, "Failed ", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
            }
        })
    }



    //------------------------------------------------------------LOad all data-------------------------------------------
    private fun loadBloodRequestList(){
        val ref = FirebaseDatabase.getInstance().getReference("/Blood/Request")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    bloodRequestList.clear()
                    var blood: Blood?
                    for (productSnapshot in dataSnapshot.children) {

                        blood = productSnapshot.getValue(Blood::class.java)
                        bloodRequestList.add(blood!!)
                    }
                    requestNotFound.visibility = View.INVISIBLE
                    adapter.filterBloodRequestList(bloodRequestList)

                    recentRequest.text = bloodRequestList.size.toString()
                }else{
                    bloodRequestList.clear()
                    requestNotFound.visibility = View.VISIBLE
                    adapter.filterBloodRequestList(bloodRequestList)

                    recentRequest.text = bloodRequestList.size.toString()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

                activity!!.runOnUiThread{
                    bloodRequestList.clear()
                    requestNotFound.visibility = View.VISIBLE
                    adapter.filterBloodRequestList(bloodRequestList)
                }

                throw databaseError.toException()!!
            }
        })
    }

    private fun loadBloodList(){
        val ref = FirebaseDatabase.getInstance().getReference("/Blood/Donor")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    bloodList.clear()
                    var blood: Blood?
                    for (productSnapshot in dataSnapshot.children) {

                        blood = productSnapshot.getValue(Blood::class.java)
                        bloodList.add(blood!!)
                    }

                    val databaseHandler = DatabaseHelper(appContext)
                    val bloodProfile = databaseHandler.getBloodData()

                    if(bloodList.filter { it.bloodId == currentUser!!.user_id }.isEmpty() || bloodProfile == null){
                        activity!!.showSettingDialog()
                    }

                    totalDonor.text = bloodList.size.toString()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()!!
            }
        })


    }

    var donationList = ArrayList<Donation>()

    private fun loadDonationList(){
        val ref = FirebaseDatabase.getInstance().getReference("/Blood/Donation")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    donationList.clear()
                    var donate: Donation?
                    for (productSnapshot in dataSnapshot.children) {

                        donate = productSnapshot.getValue(Donation::class.java)
                        donationList.add(donate!!)
                    }

                    val formatter = SimpleDateFormat.getDateInstance()
                    val formattedDate = formatter.format(Calendar.getInstance().time)

                    recentDonation.text = donationList.filter { it.confirmationDate == formattedDate }.size.toString()
                    totalDonation.text = donationList.size.toString()

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()!!
            }
        })
    }


    override fun onResume() {
        super.onResume()

        val uid = FirebaseAuth.getInstance().uid
        if(uid != null){
            loadBloodRequestList()
            loadBloodList()
            loadDonationList()
        }

        totalRequest.text = homeInfo!!.totalBloodRequest
        totalResponse.text = homeInfo!!.totalResponse

    }

}