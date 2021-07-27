package com.newage.studlab.Home.ResultFragment

import HomeFragment
import android.annotation.SuppressLint
import android.app.ProgressDialog
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
import com.google.firebase.database.*
import com.newage.studlab.Adapter.ResultAdapter.UniversityResultProgramRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.programList
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.Intake
import com.newage.studlab.Model.Program
import com.newage.studlab.Model.Results
import com.newage.studlab.R
import java.util.*

class UniversityResultProgramFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_university_result_program, container, false)
    }

    lateinit var progress: ProgressDialog

    companion object{
        var programCode:String = ""
        var intakeNo:String = ""
        var cc:Long = 0
        var count:Long = 0
        var path:String = ""

        val semistResult = HashMap<String, ArrayList<Results>>()
        var intakeList = ArrayList<Intake>()

    }

    lateinit var backButton: ImageView
    lateinit var resNotFound: TextView
    lateinit var downButton:ImageView
    lateinit var sortBySpinner: Spinner
    lateinit var searchBox: EditText
    lateinit var adapter: UniversityResultProgramRecyclerViewAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var sortBy:Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progress = ProgressDialog(context!!).apply {
            setTitle("Loading....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }




        activity?.let {
            downButton = it.findViewById(R.id.fragment_sort_by_button)
            sortBySpinner = it.findViewById(R.id.sort_by_spinner)
            backButton = it.findViewById(R.id.fragment_back_button)
            resNotFound = it.findViewById(R.id.fragment_empty)
            searchBox = it.findViewById(R.id.search_box)
            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        sortBy = resources.getStringArray(R.array.sort_by_3)


        fragmentName = "universityResult"
        fragmentPosition = 0.0



        if(programList.size != 0)
            adapter = UniversityResultProgramRecyclerViewAdapter(context, programList, clickListener = {
                programClickListener(it)
            })

        recyclerView.adapter = adapter

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int
            ) {}

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })

        allButtonClickingEventListeners()

        if(StudLab.programList.size != 0)
            Handler().postDelayed({
                handleSpinner()
            },600)
    }

    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        val filteredList: ArrayList<Program> = ArrayList()
        for (item in programList) {
            if (item.programCode.toLowerCase().contains(text.toLowerCase())
                || item.progFaculty.toLowerCase().contains(text.toLowerCase())||
                item.programTitle.contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter.filterMyResultList(filteredList)
    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.sort_by_3,R.layout.spinner_item_transparent)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sortBySpinner.adapter = bloodSpinnerAdapter

        sortBySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val order = sortBy[position]

                if(order == "Sort by program"){
                    programList.sortBy { it.programCode }
                }else if(order == "Sort by faculty"){
                    programList.sortByDescending { it.facultyNumber.toInt() }
                }else if(order == "Sort by student"){
                    programList.sortByDescending { it.studentNumber.toInt() }
                }
                adapter.filterMyResultList(programList)
            }
        }
    }

    private fun programClickListener(position: String) {
        programCode = position
        loadIntake()
    }

    private fun loadIntake(){

        progress.show()

        val ref = FirebaseDatabase.getInstance().getReference("/Results/$programCode")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                intakeList.clear()
                cc = 0
                if(dataSnapshot.exists()){
                    for (ds in dataSnapshot.children) {
                        loadIntakeResult(ds.key!!)
                        cc = ds.childrenCount
                    }
                }else{
                    progress.dismiss()
                    val transaction = fragmentManager!!.beginTransaction()
                    transaction.setCustomAnimations(0, 0,0,0)
                    transaction.replace(R.id.main_home_fragment_container,
                        UniversityIntakeFragment()
                    )
                    transaction.commit()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun loadIntakeResult(intake:String) {
        val ref = FirebaseDatabase.getInstance().getReference("/Results/$programCode/$intake")

        ref.addValueEventListener(object : ValueEventListener {

            val to: GenericTypeIndicator<ArrayList<Results>> = object :
                GenericTypeIndicator<ArrayList<Results>>() {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                semistResult.clear()
                count = 0
                if (dataSnapshot.exists()){
                    for (ds in dataSnapshot.children) {
                        semistResult[ds.key!!] = ds.getValue<ArrayList<Results>>(to)!!
                        count++
                    }
                    intakeInfo(intake,semistResult)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun intakeInfo(intake:String, semistResult:HashMap<String, ArrayList<Results>>){
        var lastSeams = ""

        when (semistResult.size) {
            1 -> {
                lastSeams = "First Semester"
            }
            2 -> {
                lastSeams = "Second Semester"
            }
            3 -> {
                lastSeams = "Third Semester"
            }
            4 -> {
                lastSeams = "Fourth Semester"
            }
            5 -> {
                lastSeams = "Fifth Semester"
            }
            6 -> {
                lastSeams = "Sixth Semester"
            }
            7 -> {
                lastSeams = "Seventh Semester"
            }
            8 -> {
                lastSeams = "Eight Semester"
            }
            9 -> {
                lastSeams = "Ninth Semester"
            }
            10 -> {
                lastSeams = "Tenth Semester"
            }
            11 -> {
                lastSeams = "Eleventh Semester"
            }
            12 -> {
                lastSeams = "Twelfth Semester"
            }
        }

        val totalStudent = semistResult.getValue(lastSeams).size
        var pass = 0

        for (i in 0 until semistResult.getValue(lastSeams).size-1){
            if(semistResult.getValue(lastSeams)[i].Student_Cgpa >= "2.00"){
                pass++
            }
        }
        val fail = totalStudent - pass
        intakeList.add(Intake(lastSeams,intake,totalStudent.toString(),pass.toString(),fail.toString()))
        if(cc == count){

            progress.dismiss()

            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container,
                UniversityIntakeFragment()
            )
            transaction.commit()
        }
    }






}