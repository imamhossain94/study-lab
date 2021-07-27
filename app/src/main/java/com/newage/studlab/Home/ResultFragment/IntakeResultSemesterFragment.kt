package com.newage.studlab.Home.ResultFragment

import HomeFragment
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
import com.newage.studlab.Adapter.ResultAdapter.IntakeResultSemesterRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.intakeResult
import com.newage.studlab.Application.StudLab.Companion.intakeSemesterResult
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.Intake
import com.newage.studlab.Model.Results
import com.newage.studlab.Plugins.StudLabAssistant.Companion.ordinalSemesterToText
import com.newage.studlab.R

class IntakeResultSemesterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intake_result_semester, container, false)
    }

    var programCode:String = ""
    var intakeNo:String = ""


    companion object{
        lateinit var semesterName:String
        val newResultList =ArrayList<Results>()
    }


    lateinit var programTitle:TextView

    lateinit var backButton:ImageView
    lateinit var resNotFound:TextView
    lateinit var downButton:ImageView
    lateinit var sortBySpinner: Spinner
    lateinit var searchBox: EditText
    lateinit var adapter: IntakeResultSemesterRecyclerViewAdapter

    lateinit var recyclerView: RecyclerView
    var resultList =ArrayList<Results>()
    lateinit var sortBy:Array<String>

    var newIntakeResult = HashMap<String, java.util.ArrayList<Results>>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newIntakeResult = intakeResult

        activity?.let {

            programTitle = it.findViewById(R.id.fregment_title)
            backButton = it.findViewById(R.id.fragment_back_button)
            downButton = it.findViewById(R.id.fragment_sort_by_button)
            sortBySpinner = it.findViewById(R.id.sort_by_spinner)
            searchBox = it.findViewById(R.id.search_box)
            resNotFound = it.findViewById(R.id.fragment_empty)

            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)


        fragmentName = "intakeResult"
        fragmentPosition = 0.1


        sortBy = resources.getStringArray(R.array.sort_by_1)

        if(intakeSemesterResult.size != 0){
            adapter = IntakeResultSemesterRecyclerViewAdapter(context, intakeSemesterResult, clickListener = {
                resultClickListner(it)
            })
        }else{
            adapter = IntakeResultSemesterRecyclerViewAdapter(context, intakeSemesterResult, clickListener = {
                resultClickListner(it)
            })
        }


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
        val filteredList: ArrayList<Intake> = ArrayList()
        for (item in intakeSemesterResult) {
            if (ordinalSemesterToText(item.semesterName).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter.filterMyResultList(filteredList)
    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.sort_by_1,R.layout.spinner_item_transparent)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sortBySpinner.adapter = bloodSpinnerAdapter

        sortBySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val order = sortBy[position]

                if(order == "Sort by semester"){
                    intakeSemesterResult.sortBy { it.semesterName }
                }else if(order == "Sort by student"){
                    intakeSemesterResult.sortBy { it.totalStudents}
                }else if(order == "Sort by pass"){
                    intakeSemesterResult.sortBy { it.totalPass }
                }else if(order == "Sort by fail"){
                    intakeSemesterResult.sortBy { it.totalFail }
                }
                adapter.filterMyResultList(intakeSemesterResult)
            }
        }
    }

    private fun resultClickListner(position: String){
        semesterName = position
        loadIntakeResult(ordinalSemesterToText(semesterName))
    }


    fun position(i: Int): String? {
        val sufixes =
            arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
        return when (i % 100) {
            11, 12, 13 -> i.toString() + "th"
            else -> i.toString() + sufixes[i % 10]
        }
    }

    private fun loadIntakeResult(semesterName:String){
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)
        if(newIntakeResult.containsKey(semesterName) && !newIntakeResult.getValue(semesterName).isNullOrEmpty()){
            resNotFound.visibility = View.INVISIBLE

            resultList = intakeResult.getValue(semesterName)

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
            transaction.replace(R.id.main_home_fragment_container, IntakeResultFragment())
            transaction.commit()
        }else if(!newIntakeResult.containsKey(semesterName)){
            transaction.replace(R.id.main_home_fragment_container, IntakeResultFragment())
            transaction.commit()
        }

    }






}