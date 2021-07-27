package com.newage.studlab.Home.UserFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
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
import com.newage.studlab.Adapter.UserAdapter.StudentIntakeRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.userList
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Home.UserFragment.StudentProgramFragment.Companion.intakeList
import com.newage.studlab.Home.UserFragment.StudentProgramFragment.Companion.programCode
import com.newage.studlab.Model.Intake
import com.newage.studlab.Model.Results
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.Plugins.StudLabAssistant.Companion.textSemesterToOrdinal
import com.newage.studlab.R

class StudentIntakeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_student_intake, container, false)
    }

    companion object{
        var intakeNo:String = ""
        var lastSemester:String = ""

        val resList = ArrayList<Results>()
        val newList = ArrayList<Users>()
    }

    lateinit var progCode: String
    lateinit var backButton: ImageView
    lateinit var resNotFound: TextView
    lateinit var downButton:ImageView
    lateinit var sortBySpinner: Spinner
    lateinit var searchBox: EditText
    lateinit var adapter: StudentIntakeRecyclerViewAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var sortBy:Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progCode = programCode

        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            downButton = it.findViewById(R.id.fragment_sort_by_button)
            sortBySpinner = it.findViewById(R.id.sort_by_spinner)
            resNotFound = it.findViewById(R.id.fragment_empty)
            searchBox = it.findViewById(R.id.search_box)
            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        sortBy = resources.getStringArray(R.array.sort_by_4)

        fragmentName = "students"
        fragmentPosition = 0.1

        if(intakeList.isNotEmpty()){
            adapter = StudentIntakeRecyclerViewAdapter(context, intakeList, clickListener = {
                studentIntakeClickListner(it)
            })
            recyclerView.adapter = adapter
            resNotFound.visibility = View.INVISIBLE
        }else{
            adapter = StudentIntakeRecyclerViewAdapter(context, intakeList, clickListener = {
                studentIntakeClickListner(it)
            })
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
            transaction.replace(R.id.main_home_fragment_container, StudentProgramFragment())
            transaction.commit()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        val filteredList: ArrayList<Intake> = ArrayList()
        for (item in intakeList) {
            if (item.intake_Title.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter.filterMyResultList(filteredList)
    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(appContext,R.array.sort_by_4,R.layout.spinner_item_transparent)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sortBySpinner.adapter = bloodSpinnerAdapter

        sortBySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val order = sortBy[position]

                if(order == "Sort by intake"){
                    intakeList.sortBy { it.intake_Title }
                }else if(order == "Sort by student"){
                    intakeList.sortByDescending { it.totalStudents.toInt()}
                }else if(order == "Sort by semester"){
                    intakeList.sortBy{ textSemesterToOrdinal(it.semesterName)}
                }
                adapter.filterMyResultList(intakeList)
            }
        }
    }

    private fun studentIntakeClickListner(position:String){
        val data: Intake? = intakeList.find { it.intake_Title == position }
        intakeNo = position
        lastSemester = data!!.semesterName
        loadResult()
    }


    private fun loadResult() {
        val ref = FirebaseDatabase.getInstance().getReference("/Results/$programCode/$intakeNo/$lastSemester")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                resList.clear()

                var res: Results? = null

                if (dataSnapshot.exists()) {

                    for (ds in dataSnapshot.children) {
                        res = ds.getValue(Results::class.java)
                        resList.add(res!!)
                    }
                    loadUser()

                }else{
                    val transaction = fragmentManager!!.beginTransaction()
                    transaction.setCustomAnimations(0, 0,0,0)

                    transaction.replace(R.id.main_home_fragment_container,
                        StudentUserFragment()
                    )
                    transaction.commit()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadUser(){

        var user:Users? = null
        newList.clear()

        var count =0
        for (i in 0 until resList.size){

            var res = "https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/user%20image%2FAvater%2Funknown_user.png?alt=media&token=1cc70853-2ee7-4fe0-80b4-eef8ee2e6b83"

            for(j in 0 until userList.size){
                if(userList[j].user_intake == intakeNo.replace("Intake ", "") && userList[j].user_id == resList[i].Student_ID){
                    res = userList[j].user_image
                    break
                }
            }

            user =  Users(resList[i].Student_ID,resList[i].Student_Name,resList[i].program_Code,res)
            newList.add(user)
            count++
        }

        if(count == resList.size){
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)

            transaction.replace(R.id.main_home_fragment_container,
                StudentUserFragment()
            )
            transaction.commit()
        }
    }

}