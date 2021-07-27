package com.newage.studlab.Home.ResultFragment

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
import com.hadiidbouk.charts.BarData
import com.hadiidbouk.charts.ChartProgressBar
import com.hadiidbouk.charts.OnBarClickedListener
import com.newage.studlab.Adapter.ResultAdapter.IntakeResultRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.intakeResult
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Home.ResultFragment.IntakeResultSemesterFragment.Companion.newResultList
import com.newage.studlab.Model.Results
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.Plugins.StudLabAssistant.Companion.pointToGrade
import com.newage.studlab.R
import kotlinx.android.synthetic.main.dialogue_user_result.view.*

class IntakeResultFragment : Fragment(), OnBarClickedListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intake_result, container, false)
    }

    var programCode:String = ""
    var intakeNo:String = ""


    lateinit var backButton:ImageView
    lateinit var resNotFound:TextView
    lateinit var downButton:ImageView
    lateinit var sortBySpinner: Spinner
    lateinit var searchBox: EditText
    lateinit var adapter: IntakeResultRecyclerViewAdapter

    //-------------------------------------------------------------------------
    var gBoardTitle:TextView? = null
    var description:TextView? = null
    var maxMinGrade:TextView? = null
    var avarage:TextView? = null
    var switchButton:ImageView? = null
    var cGpaView: ChartProgressBar? = null
    var sGpaView: ChartProgressBar? = null


    lateinit var recyclerView: RecyclerView
    var resultList =ArrayList<Results>()
    private var newIntakeResult = HashMap<String, java.util.ArrayList<Results>>()
    lateinit var sortBy:Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newIntakeResult = intakeResult

        activity?.let {

            downButton = it.findViewById(R.id.fragment_sort_by_button)
            sortBySpinner = it.findViewById(R.id.sort_by_spinner)
            searchBox = it.findViewById(R.id.search_box)

            backButton = it.findViewById(R.id.fragment_back_button)
            resNotFound = it.findViewById(R.id.fragment_empty)
            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)

        sortBy = resources.getStringArray(R.array.sort_by_2)


        fragmentName = "intakeResult"
        fragmentPosition = 0.0



        if(newResultList.size != 0)
            adapter = IntakeResultRecyclerViewAdapter(context, newResultList, clickListener = {
                resultClickListener(it)
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

        if(newResultList.size != 0)
        Handler().postDelayed({
            handleSpinner()
        },600)
    }

    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, IntakeResultSemesterFragment())
            transaction.commit()
        }
    }


    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        val filteredList: ArrayList<Results> = ArrayList()
        for (item in newResultList) {
            if (pointToGrade(item.Student_Cgpa).toLowerCase().contains(text.toLowerCase())
                || item.Student_Name.toLowerCase().contains(text.toLowerCase())||
                item.Student_ID.contains(text)) {
                filteredList.add(item)
            }
        }
        adapter.filterMyResultList(filteredList)
    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.sort_by_2,R.layout.spinner_item_transparent)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sortBySpinner.adapter = bloodSpinnerAdapter

        sortBySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val order = sortBy[position]

                if(order == "Sort by id"){
                    newResultList.sortBy { it.Student_ID }
                }else if(order == "Sort by name"){
                    newResultList.sortBy { it.Student_Name}
                }else if(order == "Sort by cgpa"){
                    newResultList.sortByDescending { it.Student_Cgpa }
                }else if(order == "Sort by sgpa"){
                    newResultList.sortByDescending { it.Student_Sgpa }
                }
                adapter.filterMyResultList(newResultList)
            }
        }
    }

    private fun resultClickListener(res: Results){
        activity!!.showStudentResult(res.Student_ID, res.Student_Name, res.program_Code, res.Intake_No.toString(), res.Student_Cgpa);

    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun Activity.showStudentResult(id:String, name:String, dept:String, intakeNo:String, cg:String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.dialogue_user_result, null)
        dialogBuilder.setView(dialogView)

        val dialogueView = dialogView.student_result_container

        val stdGrade = dialogView.student_result
        val stdName = dialogView.stud_name
        val stdId = dialogView.stud_id
        val stdDept = dialogView.std_dept

        gBoardTitle = dialogView.gpa_board_title
        description = dialogView.gpa_board_description
        maxMinGrade = dialogView.gpa_max_min
        avarage = dialogView.selected_gpa
        switchButton = dialogView.gpa_switch_button
        cGpaView = dialogView.cgpa_progress_bar
        sGpaView = dialogView.sgpa_progress_bar

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        dialogueView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.setCancelable(true)
        alertDialog.show()

        //blinkingView(annexLogo,true)

        //load data
        stdGrade.text = pointToGrade(cg)
        stdName.text = name
        stdId.text = id
        stdDept.text = dept

        gBoardTitle!!.text = "CGPA"
        description!!.text = "Cumulative Grade Point Average"

        loadUserIntakeResult(id, dept, intakeNo)
    }



    private val dataList: ArrayList<BarData> = ArrayList()
    @SuppressLint("SetTextI18n")
    private fun loadCGpa(id:String){
        val userResult = loadIntakeStudentResult(id)
        //selectedGpa.text = profileUserCGpa.text.toString()

        dataList.clear()
        var data: BarData

        for(i in 0 until userResult.size){
            if(i == 0){
                data = BarData("1st", "0${userResult[i].Student_Cgpa}".toFloat(), userResult[i].Student_Cgpa)
                dataList.add(data)
            }else if(i == 1){
                data = BarData("2nd", "0${userResult[i].Student_Cgpa}".toFloat(), userResult[i].Student_Cgpa)
                dataList.add(data)
            }else if(i == 1){
                data = BarData("3rd", "0${userResult[i].Student_Cgpa}".toFloat(), userResult[i].Student_Cgpa)
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

        cGpaView!!.setDataList(dataList)
        cGpaView!!.build()
        cGpaView!!.setOnBarClickedListener(this)
        userResult.sortByDescending { it.Student_Cgpa }
        maxMinGrade!!.text = "MAX - ${userResult[0].Student_Cgpa} MIN - ${userResult[userResult.size-1].Student_Cgpa}"
    }

    @SuppressLint("SetTextI18n")
    private fun loadSGpa(id:String){
        val userResult = loadIntakeStudentResult(id)

        if(userResult[userResult.size-1].Student_Sgpa.isNotEmpty()){
            avarage!!.text = userResult[userResult.size-1].Student_Sgpa
        }else{
            avarage!!.text = "0.0"
        }

        dataList.clear()
        var data: BarData

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

        sGpaView!!.setDataList(dataList)
        sGpaView!!.build()
        sGpaView!!.setOnBarClickedListener(this)
        userResult.sortByDescending { it.Student_Sgpa }
        maxMinGrade!!.text = "MAX - ${userResult[0].Student_Sgpa} MIN - ${userResult[userResult.size-1].Student_Sgpa}"
    }

    override fun onBarClicked(p0: Int) {
        avarage!!.text = dataList[p0].barValue.toString()
    }


    val intakeStudentResult = HashMap<String, ArrayList<Results>>()
    //private var intakeStudentSemesterResult = ArrayList<Intake>()

    private fun loadUserIntakeResult( id:String , dept:String , intakePram:String){

        val intake = "Intake $intakePram"
        val ref = FirebaseDatabase.getInstance().getReference("/Results/$dept/${intake}")

        ref.addValueEventListener(object : ValueEventListener {
            val to: GenericTypeIndicator<java.util.ArrayList<Results>> = object :
                GenericTypeIndicator<java.util.ArrayList<Results>>() {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for (ds in dataSnapshot.children) {
                        intakeStudentResult[ds.key!!] = ds.getValue(to)!!
                        intakeStudentInfo(id)

                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    @SuppressLint("SetTextI18n")
    private fun intakeStudentInfo(id:String){
        Handler().postDelayed({
            loadCGpa(id)
        },600)


        switchButton!!.setOnClickListener{
            if(gBoardTitle!!.text == "CGPA"){
                sGpaView!!.visibility = View.VISIBLE
                cGpaView!!.visibility = View.INVISIBLE
                gBoardTitle!!.text = "SGPA"
                description!!.text = "Semester Grade Point Average"
                loadSGpa(id)
            }else{
                sGpaView!!.visibility = View.INVISIBLE
                cGpaView!!.visibility = View.VISIBLE
                gBoardTitle!!.text = "CGPA"
                description!!.text = "Cumulative Grade Point Average"
                loadCGpa(id)
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

    private fun calculateIntakeStudentResult(semesterName:String, id:String): Results? {
        val resultList = intakeStudentResult.getValue(semesterName)
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

    private fun loadIntakeStudentResult(id:String):ArrayList<Results> {
        val resList =ArrayList<Results>()
        for(key in intakeStudentResult.keys){
            if(intakeStudentResult.containsKey(key)){

                resList.add(calculateIntakeStudentResult(key, id)!!)
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