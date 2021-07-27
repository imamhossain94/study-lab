package com.newage.studlab.Home.UserFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.newage.studlab.Adapter.StudentUserRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.userList
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Home.UserFragment.StudentIntakeFragment.Companion.newList
import com.newage.studlab.Model.Intake
import com.newage.studlab.Model.Results
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.Plugins.StudLabAssistant.Companion.textSemesterToOrdinalValue
import com.newage.studlab.Plugins.StudLabGenerate
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home_main.*
import kotlinx.android.synthetic.main.dialogue_annex_login.view.*
import kotlinx.android.synthetic.main.dialogue_user_profile.view.*
import java.lang.Exception


class StudentUserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_student_user, container, false)
    }

    lateinit var backButton: ImageView
    lateinit var resNotFound: TextView
    lateinit var downButton:ImageView
    lateinit var sortBySpinner: Spinner
    lateinit var searchBox: EditText
    lateinit var adapter: StudentUserRecyclerViewAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var sortBy:Array<String>

    var userSgpa:TextView? = null
    var userCgpa:TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            downButton = it.findViewById(R.id.fragment_sort_by_button)
            sortBySpinner = it.findViewById(R.id.sort_by_spinner)
            resNotFound = it.findViewById(R.id.fragment_empty)
            searchBox = it.findViewById(R.id.search_box)
            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        sortBy = resources.getStringArray(R.array.sort_by_5)

        fragmentName = "students"
        fragmentPosition = 0.2

        if(newList.isNotEmpty()){
            newList.sortBy { it.user_id }
            adapter = StudentUserRecyclerViewAdapter(context, newList, clickListener = {
                userProfileClickListener(it)
            })
            recyclerView.adapter = adapter
            resNotFound.visibility = View.INVISIBLE
        }else{
            resNotFound.visibility = View.VISIBLE
        }

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int
            ) {}

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })

        allbuttonClickingEventListeners()

        if(StudLab.programList.size != 0)
            Handler().postDelayed({
                handleSpinner()
            },600)

    }

    private fun allbuttonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, StudentIntakeFragment())
            transaction.commit()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        val filteredList: ArrayList<Users> = ArrayList()
        for (item in newList) {
            if (item.user_name.toLowerCase().contains(text.toLowerCase())||
                    item.user_id.contains(text)) {
                filteredList.add(item)
            }
        }
        adapter.filterMyResultList(filteredList)
    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(appContext,R.array.sort_by_5,R.layout.spinner_item_transparent)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sortBySpinner.adapter = bloodSpinnerAdapter

        sortBySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val order = sortBy[position]

                if(order == "Sort by id"){
                    newList.sortBy { it.user_id }
                }else if(order == "Sort by name"){
                    newList.sortBy { it.user_name}
                }
                adapter.filterMyResultList(newList)
            }
        }
    }


    private fun userProfileClickListener(user:Users){

        val data = userList.filter { it.user_id == user.user_id }

        if(data.isNotEmpty()){
            activity!!.showUserInfo(data[0])

        }else{
            Toasty.info(appContext, "Data unavailable: This student isn't a registered user.", Toast.LENGTH_SHORT, true).show()
        }

    }


    @SuppressLint("InflateParams")
    private fun Activity.showUserInfo(user:Users) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.dialogue_user_profile, null)
        dialogBuilder.setView(dialogView)

        val dialogueView = dialogView.dialog_user_profile_container

        val userImage = dialogView.profile_user_image

        val userName = dialogView.profile_user_name
        val userId = dialogView.profile_user_id
        val userDept = dialogView.profile_user_dept

        val userIntake = dialogView.profile_user_intake
        val userSec  = dialogView.profile_user_section
        val userShift = dialogView.profile_user_shift

        val userSemester = dialogView.profile_user_semester
        userSgpa = dialogView.profile_user_sgpa
        userCgpa = dialogView.profile_user_cgpa

        val userGender = dialogView.profile_user_gender
        val userBlood = dialogView.profile_user_blood_group
        val userDob = dialogView.profile_user_dob

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        dialogueView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.setCancelable(true)
        alertDialog.show()

        //blinkingView(annexLogo,true)

        //load data

        if(user.user_image.isNotEmpty())
            Picasso.get().load(user.user_image).networkPolicy(NetworkPolicy.OFFLINE).into(userImage, object: com.squareup.picasso.Callback{
                override fun onSuccess() { }
                override fun onError(e: Exception?) {
                    Picasso.get().load(user.user_image).into(userImage)
                }
            })

        userName.text = user.user_name
        userId.text = user.user_id
        userDept.text = user.user_prog_or_dept

        userIntake.text = user.user_intake
        userSec.text = user.user_section
        userShift.text = user.user_shift_or_post

        userSemester.text = textSemesterToOrdinalValue(user.user_semester)


        userGender.text = user.user_gender

        if(user.user_blood.length > 4){
            userBlood.text = "---"
        }else{
            userBlood.text = user.user_blood
        }

        userDob.text = user.user_dob

        loadUserIntakeResult(user)

    }




    val userIntakeResult = HashMap<String, ArrayList<Results>>()

    private fun loadUserIntakeResult(user:Users) {
        val intake = "Intake ${user.user_intake}"
        val ref = FirebaseDatabase.getInstance().getReference("/Results/${StudLabAssistant.titleToDeptCode(user.user_prog_or_dept)}/${intake}")

        ref.addValueEventListener(object : ValueEventListener {

            val to: GenericTypeIndicator<java.util.ArrayList<Results>> = object :
                GenericTypeIndicator<java.util.ArrayList<Results>>() {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    for (ds in dataSnapshot.children) {
                        userIntakeResult[ds.key!!] = ds.getValue(to)!!
                    }
                    intakeInfo(user.user_id)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun intakeInfo(id:String){

        val userResult = loadUserResult(id)
        if(userResult.isNotEmpty()){
            if(userResult[userResult.size-1].Student_Cgpa.isNotEmpty()){

                userSgpa!!.text = userResult[userResult.size-1].Student_Sgpa
                userCgpa!!.text = userResult[userResult.size-1].Student_Cgpa

            }
        }

    }

    fun position(i: Int): String? {
        val suffixes =
            arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
        return when (i % 100) {
            11, 12, 13 -> i.toString() + "th"
            else -> i.toString() + suffixes[i % 10]
        }

    }

    private fun calculateMyResult(semesterName:String, id:String): Results? {
        val resultList = StudLab.intakeResult.getValue(semesterName)
        val newResultList = ArrayList<Results>()

        newResultList.clear()
        resultList.sortByDescending { it.Student_Cgpa }

        var height = resultList[0].Student_Cgpa
        var n = 1
        for (i in 0 until resultList.size){
            if(resultList[i].Student_Cgpa == height){
                val results = Results(resultList[i].Student_ID,resultList[i].Student_Name,
                    resultList[i].Student_Cgpa, resultList[i].Student_Sgpa,position(n)!!,
                    resultList[i].Intake_No,resultList[i].Semester_Title)
                newResultList.add(results)
            }else if(resultList[i].Student_Cgpa < height){
                n++
                height = resultList[i].Student_Cgpa
                val results = Results(resultList[i].Student_ID,resultList[i].Student_Name,
                    resultList[i].Student_Cgpa, resultList[i].Student_Sgpa,position(n)!!,
                    resultList[i].Intake_No,resultList[i].Semester_Title)
                newResultList.add(results)
            }
        }

        if(newResultList.any { it.Student_ID == id }){
            return newResultList.find { it.Student_ID == id}?.let { it }!!
        }else{
            return Results()
        }

    }

    public fun loadUserResult(id:String):ArrayList<Results> {
        val resList =ArrayList<Results>()
        for(key in userIntakeResult.keys){
            if(userIntakeResult.containsKey(key)){

                resList.add(calculateMyResult(key, id)!!)
            }
        }
        val newResList = ArrayList<Results>(12)

        for(i in 0 until resList.size){
            val res = Results(resList[i].Student_ID,resList[i].Student_Name,
                resList[i].Student_Cgpa, resList[i].Student_Sgpa,resList[i].program_Code,
                resList[i].Intake_No,
                StudLabAssistant.textSemesterToOrdinal(resList[i].Semester_Title)
            )
            newResList.add(res)
        }
        newResList.sortBy { it.Semester_Title }
        return newResList
    }









}