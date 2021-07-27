package com.newage.studlab.Home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hadiidbouk.charts.BarData
import com.hadiidbouk.charts.ChartProgressBar
import com.hadiidbouk.charts.OnBarClickedListener
import com.newage.studlab.Adapter.ProfileAdapter.ProfilePictureRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Model.BloodModel.Blood
import com.newage.studlab.Model.ProfileModel.ProfileImage
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.Plugins.StudLabAssistant.Companion.numberSemesterToText
import com.newage.studlab.Plugins.StudLabGenerate
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment(), OnBarClickedListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    val avatarList = ArrayList<ProfileImage>()
    var selectedImage = ""

    lateinit var progress: ProgressDialog

    lateinit var imageProgress: ProgressBar

    //user profile attributes
    lateinit var profileUserImage: ImageView
    lateinit var profileUserName: TextView
    lateinit var profileUserId: TextView
    lateinit var profileUserDept: TextView
    lateinit var profileUserIntake: TextView
    lateinit var profileUserSection: TextView
    lateinit var profileUserShift: TextView
    lateinit var profileUserSemester: TextView
    lateinit var profileUserSgpa: TextView
    lateinit var profileUserCGpa: TextView

    //Gpa attributes
    lateinit var gpaTitle: TextView
    lateinit var gpaDescription: TextView
    lateinit var gpaMaxMin: TextView
    lateinit var selectedGpa: TextView
    lateinit var gpaChageButton: ImageView
    lateinit var cGpaView: ChartProgressBar
    lateinit var sGpaView: ChartProgressBar

    //upload image attributed
    lateinit var recyclerView: RecyclerView
    lateinit var previewImage: ImageView
    lateinit var uploadImageButton: ImageView

    //Edit profile attributes
    lateinit var semesterNo: TextView
    lateinit var userSemesterSpinner: Spinner
    lateinit var sectionNo: TextView
    lateinit var userSectionSpinner: Spinner
    lateinit var shiftName: TextView
    lateinit var userShiftSpinner: Spinner
    lateinit var userGender:TextView
    lateinit var userDob: TextView
    lateinit var contactNumber: EditText
    lateinit var addressNo: EditText
    lateinit var bloodGroup: TextView
    lateinit var userBloodGroupSpinner: Spinner
    lateinit var userLastBloodDonate: TextView
    lateinit var userTotalBloodDonate: EditText

    //Update profile
    lateinit var updateButton:Button

    //initial adapter
    lateinit var adapter: ProfilePictureRecyclerViewAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progress = ProgressDialog(context!!).apply {
            setTitle("Updating....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }


        activity?.let {

            imageProgress = it.findViewById(R.id.profile_progress)

            //user profile attributes
            profileUserImage = it.findViewById(R.id.profile_user_image)
            profileUserName = it.findViewById(R.id.profile_user_name)
            profileUserId = it.findViewById(R.id.profile_user_id)
            profileUserDept = it.findViewById(R.id.profile_user_dept)
            profileUserIntake = it.findViewById(R.id.profile_user_intake)
            profileUserSection = it.findViewById(R.id.profile_user_section)
            profileUserShift = it.findViewById(R.id.profile_user_shift)
            profileUserSemester = it.findViewById(R.id.profile_user_semester)
            profileUserSgpa = it.findViewById(R.id.profile_user_sgpa)
            profileUserCGpa = it.findViewById(R.id.profile_user_cgpa)

            //Gpa attributes
            gpaTitle = it.findViewById(R.id.gpa_board_title)
            gpaDescription = it.findViewById(R.id.gpa_board_description)
            gpaMaxMin = it.findViewById(R.id.gpa_max_min)
            selectedGpa = it.findViewById(R.id.selected_gpa)
            gpaChageButton = it.findViewById(R.id.gpa_switch_button)
            cGpaView = it.findViewById(R.id.cgpa_progress_bar)
            sGpaView = it.findViewById(R.id.sgpa_progress_bar)


            //upload image attributed
            recyclerView = it.findViewById(R.id.profile_image_recycler_view)
            previewImage = it.findViewById(R.id.preview_image)
            uploadImageButton = it.findViewById(R.id.add_image_button)


            //Edit profile attributes
            semesterNo = it.findViewById(R.id.user_semester_text_view)
            userSemesterSpinner = it.findViewById(R.id.user_semester_spinner)
            sectionNo = it.findViewById(R.id.user_semester_textview)
            userSectionSpinner = it.findViewById(R.id.user_section_spinner)
            shiftName = it.findViewById(R.id.user_shift_textview)
            userShiftSpinner = it.findViewById(R.id.user_shift_spinner)
            userGender = it.findViewById(R.id.user_gender_text_view)
            userDob = it.findViewById(R.id.user_dob_text_view)
            contactNumber = it.findViewById(R.id.user_contact_edit_text)
            addressNo = it.findViewById(R.id.user_address_edit_text)
            bloodGroup = it.findViewById(R.id.user_blood_textview)
            userBloodGroupSpinner = it.findViewById(R.id.user_blood_spinner)
            userLastBloodDonate = it.findViewById(R.id.user_last_blood_donate_textview)
            userTotalBloodDonate = it.findViewById(R.id.user_total_blood_donate_edit_text)

            //Update profile
            updateButton = it.findViewById(R.id.update_profile_button)

        }

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        if(avatarList.isNotEmpty()){
            adapter = ProfilePictureRecyclerViewAdapter(appContext, avatarList, clickListener = {
                avatarClickingEventListeners(it)
            })
        }else{
            adapter = ProfilePictureRecyclerViewAdapter(appContext, avatarList, clickListener = {
                avatarClickingEventListeners(it)
            })
        }

        recyclerView.adapter = adapter

        userDob.isEnabled = false
        contactNumber.isEnabled = false
        addressNo.isEnabled = false
        userLastBloodDonate.isEnabled = false
        userTotalBloodDonate.isEnabled = false


        loadUserProfData()
        loadAvatar()
    }

    @SuppressLint("SetTextI18n")
    private fun loadUserProfData(){

        val databaseHandler = DatabaseHelper(appContext)
        val profileData = databaseHandler.getUserData()

        imageProgress.visibility = View.VISIBLE
        val userResult = StudLabGenerate().loadMyResult()


        if(profileData != null){

            //user profile attributes
            if(profileData.user_image != ""){
                Picasso.get().load(profileData.user_image).networkPolicy(NetworkPolicy.OFFLINE).into(profileUserImage, object: com.squareup.picasso.Callback{
                    override fun onSuccess() {
                        imageProgress.visibility = View.INVISIBLE
                    }
                    override fun onError(e: Exception?) {
                        imageProgress.visibility = View.INVISIBLE
                        Picasso.get().load(profileData.user_image).into(profileUserImage)
                    }
                })
                selectedImage = profileData.user_image
            }

            profileUserName.text = profileData.user_name
            profileUserId.text = profileData.user_id
            profileUserDept.text = profileData.user_prog_or_dept
            profileUserIntake.text = profileData.user_intake
            profileUserSection.text = profileData.user_section.replace("Section ","0")
            profileUserShift.text = profileData.user_shift_or_post
            profileUserSemester.text = StudLabAssistant.textSemesterToOrdinalValue(profileData.user_semester)

            if(userResult.isNotEmpty()){
                if(userResult[userResult.size-1].Student_Cgpa.isNotEmpty()){
                    profileUserSgpa.text = userResult[userResult.size-1].Student_Sgpa
                    profileUserCGpa.text = userResult[userResult.size-1].Student_Cgpa
                }else{
                    profileUserSgpa.text = "0.0"
                    profileUserCGpa.text = "0.0"
                }

                //user gpa
                selectedGpa.text = profileUserCGpa.text.toString()
                userResult.sortByDescending { it.Student_Cgpa }
                gpaMaxMin.text = "MAX - ${userResult[0].Student_Cgpa} MIN - ${userResult[userResult.size-1].Student_Cgpa}"

                //Edit profile attributes

            }
            semesterNo.text = profileData.user_semester
            sectionNo.text = profileData.user_section.replace("Section ","0")
            shiftName.text = profileData.user_shift_or_post
            userDob.text = profileData.user_dob
            userGender.text = profileData.user_gender
            contactNumber.setText(profileData.user_phone_new)
            addressNo.setText(profileData.user_address)
            bloodGroup.text = profileData.user_blood
        }



        //blood profile
        val bloodData = databaseHandler.getBloodData()

        if(bloodData != null){
            bloodGroup.text = bloodData.bloodGroup
            userLastBloodDonate.text = bloodData.bloodLastDonateDate
            userTotalBloodDonate.setText(bloodData.bloodTotalDonate)
        }

        loadcGpa()
        allButtonClickingEventListener()
    }

    private fun allSpinner(){
        val semester = resources.getStringArray(R.array.semesters_profile)
        val section = resources.getStringArray(R.array.section_profile)
        val shift = resources.getStringArray(R.array.shift)
        val blood = resources.getStringArray(R.array.blood_profile)

        val semesterSpinner = ArrayAdapter.createFromResource(appContext,R.array.semesters_profile,R.layout.spinner_item_smoke_profile)
        semesterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_item)
        userSemesterSpinner.adapter = semesterSpinner
        userSemesterSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //val order = sortBy[position]
                if(semester[position]!="Select Semester")
                semesterNo.text = semester[position]
            }
        }


        val sectionSpinner = ArrayAdapter.createFromResource(appContext,R.array.section_profile,R.layout.spinner_item_smoke_profile)
        sectionSpinner.setDropDownViewResource(android.R.layout.simple_spinner_item)
        userSectionSpinner.adapter = sectionSpinner
        userSectionSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(section[position]!="Select Section")
                sectionNo.text = section[position]
            }
        }

        val shiftSpinner = ArrayAdapter.createFromResource(appContext,R.array.shift,R.layout.spinner_item_smoke_profile)
        shiftSpinner.setDropDownViewResource(android.R.layout.simple_spinner_item)
        userShiftSpinner.adapter = shiftSpinner
        userShiftSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(shift[position]!="Select Shift")
                shiftName.text = shift[position]
            }
        }

        val bloodSpinner = ArrayAdapter.createFromResource(appContext,R.array.blood_profile,R.layout.spinner_item_smoke_profile)
        bloodSpinner.setDropDownViewResource(android.R.layout.simple_spinner_item)
        userBloodGroupSpinner.adapter = bloodSpinner
        userBloodGroupSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(blood[position]!="Select Blood")
                bloodGroup.text = blood[position]
            }
        }

    }
    //var picker: DatePickerDialog? = null
    @SuppressLint("SetTextI18n")
    private fun allButtonClickingEventListener(){

        gpaChageButton.setOnClickListener{
            if(gpaTitle.text == "SGPA"){
                gpaTitle.text = "CGPA"
                gpaDescription.text = "Cumulative Grade Point Average"
                loadcGpa()
                cGpaView.visibility = View.VISIBLE
                sGpaView.visibility = View.INVISIBLE
            }else if(gpaTitle.text == "CGPA"){
                gpaTitle.text = "SGPA"
                gpaDescription.text = "Semester Grade Point Average"
                loadsGpa()
                sGpaView.visibility = View.VISIBLE
                cGpaView.visibility = View.INVISIBLE
            }
        }

        userDob.setOnClickListener{

            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            // date picker dialog
            val picker = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    userDob.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)

                }, year, month, day)
            picker.show()
        }

        userLastBloodDonate.setOnClickListener{

            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            // date picker dialog
            val picker = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    userLastBloodDonate.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)

                }, year, month, day)
            picker.show()
        }


        updateButton.setOnClickListener{
            if(updateButton.text == "Edit"){
                updateButton.text = "Save"
                allSpinner()


                userSemesterSpinner.isEnabled = true
                userSectionSpinner.isEnabled = true
                userShiftSpinner.isEnabled = true
                userBloodGroupSpinner.isEnabled = true

                userDob.isEnabled = true
                contactNumber.isEnabled = true
                addressNo.isEnabled = true
                userLastBloodDonate.isEnabled = true
                userTotalBloodDonate.isEnabled = true





            }else if(updateButton.text == "Save"){
                if(contactNumber.text.toString().length == 11 && addressNo.text.toString().isNotEmpty() && userTotalBloodDonate.text.toString().isNotEmpty()){

                    val databaseHandler = DatabaseHelper(appContext)
                    val profileData = databaseHandler.getUserData()
                    val bloodData = databaseHandler.getBloodData()

                    if(profileData != null){

                        val updatedProfile = Users(profileData.user_id,profileData.user_name,profileData.user_gender,userDob.text.toString(),profileData.user_phone_new,contactNumber.text.toString(),
                            profileData.user_password,profileData.user_type, selectedImage,profileData.user_faculty_name,profileData.user_prog_or_dept,shiftName.text.toString(), profileData.user_intake,
                            sectionNo.text.toString(),semesterNo.text.toString(), addressNo.text.toString(),profileData.user_status,bloodGroup.text.toString(),profileData.user_room_no,
                            profileData.annex_id,profileData.annex_pass,profileData.annex_session_id)

                        databaseHandler.deleteUserData()
                        databaseHandler.saveUserData(updatedProfile)

                        updateUserProfile(updatedProfile)
                    }

                    if(bloodData != null){
                        val updatedBloodProfile = Blood(bloodData.bloodName,bloodData.bloodId,bloodGroup.text.toString(),userTotalBloodDonate.text.toString(),userLastBloodDonate.text.toString())

                        databaseHandler.deleteBloodData()
                        databaseHandler.saveBloodData(updatedBloodProfile)

                        updateBloodProfile(updatedBloodProfile)
                    }

                    progress.show()
                }else if(contactNumber.text.toString().length < 11){
                    Toasty.warning(appContext, "Invalid number", Toast.LENGTH_SHORT, true).show()

                }else if(contactNumber.text.toString().isEmpty()){
                    Toasty.warning(appContext, "Check contact number", Toast.LENGTH_SHORT, true).show()

                }else if(addressNo.text.toString().isEmpty()){
                    Toasty.warning(appContext, "Address required", Toast.LENGTH_SHORT, true).show()

                }else if(userTotalBloodDonate.text.toString().isEmpty()){
                    Toasty.warning(appContext, "Blood donation should be 0 or more", Toast.LENGTH_SHORT, true).show()

                }

            }
        }
    }


    //update data
    @SuppressLint("SetTextI18n")
    private fun updateUserProfile(user:Users){
        val ref = FirebaseDatabase.getInstance().getReference("/Users/${user.user_id}")
        ref.setValue(user).addOnSuccessListener {
           // updateToken(user)
            loadUserProfData()

            currentUser = user

            userSemesterSpinner.isEnabled = false
            userSectionSpinner.isEnabled = false
            userShiftSpinner.isEnabled = false
            userBloodGroupSpinner.isEnabled = false

            userDob.isEnabled = false
            contactNumber.isEnabled = false
            addressNo.isEnabled = false
            userLastBloodDonate.isEnabled = false
            userTotalBloodDonate.isEnabled = false

            updateButton.text = "Edit"
            progress.dismiss()
        }
        ref.setValue(user).addOnFailureListener {
            progress.dismiss()
            activity!!.runOnUiThread {
                Toasty.error(requireContext(), "Failed", Toast.LENGTH_SHORT, true)
                    .show()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateBloodProfile(blood:Blood){
        val ref = FirebaseDatabase.getInstance().getReference("/Blood/Donor/${blood.bloodId}")
        ref.setValue(blood).addOnSuccessListener {
            // updateToken(user)
            loadUserProfData()

            userDob.isEnabled = false
            contactNumber.isEnabled = false
            addressNo.isEnabled = false
            userLastBloodDonate.isEnabled = false
            userTotalBloodDonate.isEnabled = false

            updateButton.text = "Edit"
            progress.dismiss()
        }.addOnFailureListener {
            progress.dismiss()
            activity!!.runOnUiThread {
                Toasty.error(requireContext(), "Failed", Toast.LENGTH_SHORT, true)
                    .show()
            }
        }
    }

    val dataList: ArrayList<BarData> = ArrayList()
    @SuppressLint("SetTextI18n")
    private fun loadcGpa(){
        val userResult = StudLabGenerate().loadMyResult()
        selectedGpa.text = profileUserCGpa.text.toString()

        dataList.clear()
        var data:BarData

        for(i in 0 until userResult.size){
            if(i == 0){
                data = BarData("1st", "0${userResult[i].Student_Cgpa}".toFloat(), userResult[i].Student_Cgpa)
                dataList.add(data)
            }else if(i == 1){
                data = BarData("2nd", "0${userResult[i].Student_Cgpa}".toFloat(), userResult[i].Student_Cgpa)
                dataList.add(data)
            }else if(i == 1){
                data = BarData("3rd","0${userResult[i].Student_Cgpa}".toFloat(), userResult[i].Student_Cgpa)
                dataList.add(data)
            }else{
                data = BarData("${i+1}th", "0${userResult[i].Student_Cgpa}".toFloat(), userResult[i].Student_Cgpa)
                dataList.add(data)
            }
        }

        for(i in userResult.size until 12){
            data = BarData("${i+1}th", 0.0f, "0.0")
            dataList.add(data)
        }

        cGpaView.setDataList(dataList)
        cGpaView.build()
        cGpaView.setOnBarClickedListener(this)
        userResult.sortByDescending { it.Student_Cgpa }
        gpaMaxMin.text = "MAX - ${userResult[0].Student_Cgpa} MIN - ${userResult[userResult.size-1].Student_Cgpa}"
    }

    @SuppressLint("SetTextI18n")
    private fun loadsGpa(){
        val userResult = StudLabGenerate().loadMyResult()

        if(userResult[userResult.size-1].Student_Sgpa.isNotEmpty()){
            selectedGpa.text = userResult[userResult.size-1].Student_Sgpa
        }else{
            selectedGpa.text = "0.0"
        }

        dataList.clear()
        var data:BarData

        for(i in 0 until userResult.size){
            if(i == 0){
                data = BarData("1st", "0${userResult[i].Student_Sgpa}".toFloat(), userResult[i].Student_Sgpa)
                dataList.add(data)
            }else if(i == 1){
                data = BarData("2nd", "0${userResult[i].Student_Sgpa}".toFloat(), userResult[i].Student_Sgpa)
                dataList.add(data)
            }else if(i == 1){
                data = BarData("3rd", "0${userResult[i].Student_Sgpa}".toFloat(), userResult[i].Student_Sgpa)
                dataList.add(data)
            }else{
                data = BarData("${i+1}th", "0${userResult[i].Student_Sgpa}".toFloat(), userResult[i].Student_Sgpa)
                dataList.add(data)
            }
        }

        for(i in userResult.size until 12){
            data = BarData("${i+1}th", 0.0f, "0.0")
            dataList.add(data)
        }

        sGpaView.setDataList(dataList)
        sGpaView.build()
        sGpaView.setOnBarClickedListener(this)
        userResult.sortByDescending { it.Student_Sgpa }
        gpaMaxMin.text = "MAX - ${userResult[0].Student_Sgpa} MIN - ${userResult[userResult.size-1].Student_Sgpa}"
    }

    override fun onBarClicked(p0: Int) {
        selectedGpa.text = dataList[p0].barValue.toString()
    }



    //load avater
    private fun loadAvatar(){
        val ref = FirebaseDatabase.getInstance().getReference("/Avatar")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    avatarList.clear()
                    var image: ProfileImage?
                    for (productSnapshot in dataSnapshot.children) {
                        image = productSnapshot.getValue(ProfileImage::class.java)
                        avatarList.add(image!!)
                    }

                    adapter.filterAvatarList(avatarList)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()!!
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun avatarClickingEventListeners(image:String){

        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(previewImage, object: com.squareup.picasso.Callback{
            override fun onSuccess() { }
            override fun onError(e: java.lang.Exception?) {
                Picasso.get().load(image).into(previewImage)
            }
        })

        selectedImage = image

        updateButton.text = "Save"

        userSemesterSpinner.isEnabled = true
        userSectionSpinner.isEnabled = true
        userShiftSpinner.isEnabled = true
        userBloodGroupSpinner.isEnabled = true

        userDob.isEnabled = true
        contactNumber.isEnabled = true
        addressNo.isEnabled = true
        userLastBloodDonate.isEnabled = true
        userTotalBloodDonate.isEnabled = true

        allSpinner()
    }






}