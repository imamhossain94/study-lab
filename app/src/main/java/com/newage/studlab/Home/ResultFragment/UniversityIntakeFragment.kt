package com.newage.studlab.Home.ResultFragment

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
import com.newage.studlab.Adapter.ResultAdapter.UniversityResultIntakeRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Home.ResultFragment.UniversityResultProgramFragment.Companion.intakeList
import com.newage.studlab.Home.ResultFragment.UniversityResultProgramFragment.Companion.programCode
import com.newage.studlab.Model.Intake
import com.newage.studlab.Model.Results
import com.newage.studlab.Plugins.StudLabAssistant.Companion.textSemesterToOrdinal
import com.newage.studlab.R

class UniversityIntakeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_university_intake, container, false)
    }

    companion object{
        var intakeNo:String = ""
        var lastSemester:String = ""
        var resultList =ArrayList<Results>()
        var newResultList =ArrayList<Results>()
    }

    lateinit var backButton: ImageView
    lateinit var resNotFound: TextView
    lateinit var downButton:ImageView
    lateinit var sortBySpinner: Spinner
    lateinit var searchBox: EditText
    lateinit var adapter: UniversityResultIntakeRecyclerViewAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var sortBy:Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            downButton = it.findViewById(R.id.fragment_sort_by_button)
            sortBySpinner = it.findViewById(R.id.sort_by_spinner)
            backButton = it.findViewById(R.id.fragment_back_button)
            resNotFound = it.findViewById(R.id.fragment_empty)
            searchBox = it.findViewById(R.id.search_box)
            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        sortBy = resources.getStringArray(R.array.sort_by_6)


        fragmentName = "universityResult"
        fragmentPosition = 0.1


        if(intakeList.isNotEmpty()){
            adapter = UniversityResultIntakeRecyclerViewAdapter(context, intakeList, clickListener = {
                studentIntakeClickListner(it)
            })
            resNotFound.visibility = View.INVISIBLE
        }else{
            adapter = UniversityResultIntakeRecyclerViewAdapter(context, intakeList, clickListener = {
                studentIntakeClickListner(it)
            })
            resNotFound.visibility = View.VISIBLE
        }

        recyclerView.adapter = adapter

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })

        allButtonClickingEventListeners()

        if(intakeList.size != 0)
            Handler().postDelayed({
                handleSpinner()
            },600)
    }

    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, UniversityResultProgramFragment())
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
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(appContext,R.array.sort_by_6,R.layout.spinner_item_transparent)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sortBySpinner.adapter = bloodSpinnerAdapter

        sortBySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val order = sortBy[position]
                if(order == "Sort by intake"){
                    intakeList.sortBy { it.intake_Title }
                }else if(order == "Sort by semester"){
                    intakeList.sortBy { textSemesterToOrdinal(it.semesterName) }
                }else if(order == "Sort by student"){
                    intakeList.sortByDescending { it.totalStudents.toInt() }
                }else if(order == "Sort by pass"){
                    intakeList.sortByDescending { it.totalPass.toInt() }
                }else if(order == "Sort by fail"){
                    intakeList.sortByDescending { it.totalFail.toInt() }
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
                resultList.clear()

                var res: Results? = null

                if (dataSnapshot.exists()) {

                    for (ds in dataSnapshot.children) {
                        res = ds.getValue(Results::class.java)
                        resultList.add(res!!)
                    }
                    loadIntakeResult()
                }else{
                    val transaction = fragmentManager!!.beginTransaction()
                    transaction.setCustomAnimations(0, 0,0,0)

                    transaction.replace(R.id.main_home_fragment_container,
                        UniversityResultFragment()
                    )
                    transaction.commit()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun position(i: Int): String? {
        val suffixes =
            arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
        return when (i % 100) {
            11, 12, 13 -> i.toString() + "th"
            else -> i.toString() + suffixes[i % 10]
        }
    }

    private fun loadIntakeResult(){
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)

        newResultList.clear()
        resultList.sortByDescending { it.Student_Cgpa }

        var height = resultList[0].Student_Cgpa
        var n = 1
        for (i in 0 until resultList.size){
            if(resultList[i].Student_Cgpa == height){
                val results = Results(resultList[i].Student_ID,resultList[i].Student_Name,
                    resultList[i].Student_Cgpa, resultList[i].Student_Sgpa,resultList[i].program_Code,
                    resultList[i].Intake_No,position(n)!!)

                newResultList.add(results)
            }else if(resultList[i].Student_Cgpa < height){
                n++
                height = resultList[i].Student_Cgpa
                val results = Results(resultList[i].Student_ID,resultList[i].Student_Name,
                    resultList[i].Student_Cgpa, resultList[i].Student_Sgpa,
                    resultList[i].program_Code,resultList[i].Intake_No,position(n)!!)

                newResultList.add(results)
            }
        }
        transaction.replace(R.id.main_home_fragment_container, UniversityResultFragment())
        transaction.commit()
    }


}