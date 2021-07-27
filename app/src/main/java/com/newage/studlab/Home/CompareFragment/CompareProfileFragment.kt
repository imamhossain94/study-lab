package com.newage.studlab.Home.CompareFragment

import HomeFragment
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.db.williamchart.ExperimentalFeature
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.*
import com.github.mikephil.charting.model.GradientColor
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.firebase.database.*
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.Results
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialogue_compare_profile.view.*
import kotlinx.android.synthetic.main.fragment_compare_profile.*
import java.text.DecimalFormat


@ExperimentalFeature
class CompareProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_compare_profile, container, false)
    }

    lateinit var comparingProgress: ProgressDialog

    //---------------top----------------
    lateinit var backButton: ImageView
    lateinit var compareButton: ImageView

    //---------stud_1-------------------
    lateinit var studOneGrade:TextView
    lateinit var studOneName:TextView
    lateinit var studOneId:TextView
    lateinit var studOneDept:TextView

    //---------stud_1-------------------
    lateinit var studTwoGrade:TextView
    lateinit var studTwoName:TextView
    lateinit var studTwoId:TextView
    lateinit var studTwoDept:TextView

    //---------head-------------
    lateinit var compareByTitle:TextView
    private lateinit var switchCompareModeButton: ImageView

    //-------line_chart----------------


    //-------bar_chart-----------------
    private lateinit var barChart: BarChart
    private var barWidth:Float = 0.3f
    private var barSpace:Float = 0f
    private var groupSpace:Float = 0.4f

    //-------------Info----------------

    private var firstStdID:String = ""
    private var secondStdID:String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        comparingProgress = ProgressDialog(context!!).apply {
            setTitle("Please wait..")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        fragmentName = "compareProfile"
        fragmentPosition = 0.0

        activity?.let {
            //--------------top----------------
            backButton = it.findViewById(R.id.fragment_back_button)
            compareButton = it.findViewById(R.id.compare_button)

            //---------stud_1-------------------
            studOneGrade = it.findViewById(R.id.stud_1_grade)
            studOneName = it.findViewById(R.id.stud_1_name)
            studOneId = it.findViewById(R.id.stud_1_id)
            studOneDept = it.findViewById(R.id.stud_1_dept)

            //---------stud_1-------------------
            studTwoGrade = it.findViewById(R.id.stud_2_grade)
            studTwoName = it.findViewById(R.id.stud_2_name)
            studTwoId = it.findViewById(R.id.stud_2_id)
            studTwoDept = it.findViewById(R.id.stud_2_dept)

            //---------head-------------
            compareByTitle = it.findViewById(R.id.compare_by_title)
            switchCompareModeButton = it.findViewById(R.id.switch_compare_mode)

            //-------line_chart----------------


            //-------bar_chart-----------------
            barChart = it.findViewById(R.id.bar_chart)
        }

        allButtonClickingEventListeners()
        activity!!.showCompareDialog()
    }

    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }

        compareButton.setOnClickListener{

            activity!!.showCompareDialog()

        }

    }

    //--------------------------------Compare Dialogue--------------------------------
    private fun Activity.showCompareDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogue_compare_profile, null)
        dialogBuilder.setView(dialogView)

        val dialogueView = dialogView.compare_profile_contasiner

        val studIdOne = dialogView.id_1
        val studIdTwo = dialogView.id_2

        val compareBtn = dialogView.compare_button

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        dialogueView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(true)
        alertDialog.show()

        //blinkingView(annexLogo,true)

        compareBtn.setOnClickListener{

            if(studIdOne.length() == 11 && studIdTwo.length() == 11){
                firstStdID = studIdOne.text.toString()
                secondStdID = studIdTwo.text.toString()

                getStdOneIntakeResult(StudLabAssistant.idToDeptCodeIntake(firstStdID), StudLabAssistant.idToDeptCodeIntake(secondStdID))
                //getStdTwoIntakeResult(StudLabAssistant.idToDeptCodeIntake(studIdTwo.text.toString()))

                alertDialog.dismiss()
            }else{
                Toasty.warning(requireContext(), "Invalid ID", Toast.LENGTH_SHORT, true).show()

            }

        }

    }




    //first students results-------------------------
    private var stdOneFlag = false
    private val getStdOneIntakeResult = HashMap<String, ArrayList<Results>>()

    //-----------Find Positions---------------
    fun position(i: Int): String? {
        val suffixes =
            arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
        return when (i % 100) {
            11, 12, 13 -> i.toString() + "th"
            else -> i.toString() + suffixes[i % 10]
        }
    }

    private fun getStdOneIntakeResult(programIntakeFirst:String, programIntakeSecond:String) {

        comparingProgress.show()
        comparingProgress.setMessage("downloading results")


        val ref = FirebaseDatabase.getInstance().getReference("/Results/$programIntakeFirst")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            val to: GenericTypeIndicator<ArrayList<Results>> = object :
                GenericTypeIndicator<ArrayList<Results>>() {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for (ds in dataSnapshot.children) {
                        getStdOneIntakeResult[ds.key!!] = ds.getValue<ArrayList<Results>>(to)!!
                    }
                    getStdTwoIntakeResult(programIntakeSecond)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) { comparingProgress.dismiss() }
        })
    }

    //-----------Calculation First Students Results---------------

    private fun calculateStdOneResult(semesterName:String, id:String): Results? {
        val resultList = getStdOneIntakeResult.getValue(semesterName)
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

        return if(newResultList.find { it.Student_ID == id}?.let { it } != null){
            stdOneFlag = true
            newResultList.find { it.Student_ID == id}?.let { it }
        }else{
            Results()
        }
    }

    private fun getStdOneResult(id:String):ArrayList<Results> {

        val resList =ArrayList<Results>()
        for(key in getStdOneIntakeResult.keys){
            if(getStdOneIntakeResult.containsKey(key)){
                resList.add(calculateStdOneResult(key, id)!!)
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







    //second students results-------------------------
    private var stdTwoFlag = false
    private val getStdTwoIntakeResult = HashMap<String, ArrayList<Results>>()


    private fun getStdTwoIntakeResult(programIntake:String) {

        val ref = FirebaseDatabase.getInstance().getReference("/Results/$programIntake")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            val to: GenericTypeIndicator<ArrayList<Results>> = object :
                GenericTypeIndicator<ArrayList<Results>>() {}
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for (ds in dataSnapshot.children) {
                        getStdTwoIntakeResult[ds.key!!] = ds.getValue<ArrayList<Results>>(to)!!
                    }
                    getComparedResults()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) { comparingProgress.dismiss() }
        })
    }


    //-----------Calculation Second Students Results---------------

    private fun calculateStdTwoResult(semesterName:String, id:String): Results? {
        val resultList = getStdTwoIntakeResult.getValue(semesterName)
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

        return if(newResultList.find { it.Student_ID == id}?.let { it } != null){
            stdTwoFlag = true
            newResultList.find { it.Student_ID == id}?.let { it }
        }else{
            Results()
        }
    }

    private fun getStdTwoResult(id:String):ArrayList<Results> {

        val resList =ArrayList<Results>()
        for(key in getStdTwoIntakeResult.keys){
            if(getStdTwoIntakeResult.containsKey(key)){
                resList.add(calculateStdTwoResult(key, id)!!)
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

    //--------------------------%%%%%%%-----------------%%%%%%---------------------
    private fun getComparedResults(){

        stdOneFlag = false
        stdTwoFlag = false

        val firstStud = getStdOneResult(firstStdID)
        val secondStud = getStdTwoResult(secondStdID)

        if(firstStud.isNotEmpty() && secondStud.isNotEmpty()){

            if(stdOneFlag && stdTwoFlag){
                //-----------------------------------
                val oneSize = firstStud.size
                val twoSize = secondStud.size

                //----------------linked hash map--------------------

                val barSetOne = ArrayList<BarEntry>()
                val barSetTwo = ArrayList<BarEntry>()

                val lineSetOne:MutableList<Entry> = ArrayList()
                val lineSetTwo:MutableList<Entry> = ArrayList()

                if(oneSize == twoSize){

                    for (i in 0 until oneSize){
                        lineSetOne.add(Entry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))
                        lineSetTwo.add(Entry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
                        barSetOne.add(BarEntry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))
                        barSetTwo.add(BarEntry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
                    }

                }else if(oneSize > twoSize){
                    for (i in 0 until oneSize){

                        barSetOne.add(BarEntry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))
                        lineSetOne.add(Entry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))

                        if(i<=twoSize-1){
                            barSetTwo.add(BarEntry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
                            lineSetTwo.add(Entry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
                        }else{
                            lineSetTwo.add(Entry(i.toFloat(), 0f))
                            barSetTwo.add(BarEntry(i.toFloat(), 0f))
                        }
                    }
                }else if(oneSize < twoSize){
                    for (i in 0 until twoSize){
                        lineSetTwo.add(Entry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
                        barSetTwo.add(BarEntry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))

                        if(i<=oneSize-1){
                            lineSetOne.add(Entry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))
                            barSetOne.add(BarEntry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))

                        }else{
                            lineSetOne.add(Entry(i.toFloat(), 0f))
                            barSetOne.add(BarEntry(i.toFloat(), 0f))
                        }
                    }
                }

                setLineData(lineSetOne, lineSetTwo, firstStud, secondStud)
                barChart(barSetOne, barSetTwo, firstStud, secondStud)
                comparingProgress.dismiss()
            }else if(!stdOneFlag){
                Toasty.error(requireContext(), "No data for $firstStdID", Toast.LENGTH_SHORT, true).show()
                comparingProgress.dismiss()
            }else if(!stdTwoFlag){
                Toasty.error(requireContext(), "No data for $secondStdID", Toast.LENGTH_SHORT, true).show()
                comparingProgress.dismiss()
            }

        }else{
            comparingProgress.dismiss()
            Toast.makeText(appContext,"data not found",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLineData(lineEntriesOne:MutableList<Entry>,  lineEntriesTwo:MutableList<Entry>, firstStud:ArrayList<Results>, secondStud:ArrayList<Results>) {

        val startColorBlue = ContextCompat.getColor(context!!, R.color.colorBlue2)
        val endColorBlueTransparent = ContextCompat.getColor(context!!, R.color.colorBlueTransparent)
        val startColorGreen = ContextCompat.getColor(context!!, R.color.color_success)
        val endColorGreenTransparent = ContextCompat.getColor(context!!, R.color.colorGreenTransparent)

        val blueGradientColors: MutableList<GradientColor> = ArrayList()
        blueGradientColors.add(GradientColor(endColorBlueTransparent, startColorBlue))

        val greenGradientColors: MutableList<GradientColor> = ArrayList()
        greenGradientColors.add(GradientColor(endColorGreenTransparent, startColorGreen))


        val lineDataSetOne = LineDataSet(lineEntriesOne,"")
        val lineDataSetTwo = LineDataSet(lineEntriesTwo, "")

/*        dataSet.color = ContextCompat.getColor(context!!, R.color.colorPrimary)
        dataSet.valueTextColor = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)*/

        //lineDataSetTwo.color = ContextCompat.getColor(context!!, R.color.colorBlue2)
        //lineDataSetTwo.valueTextColor = ContextCompat.getColor(context!!, R.color.colorGreen)


        lineDataSetOne.mode = LineDataSet.Mode.CUBIC_BEZIER;
        lineDataSetOne.color = Color.BLUE
        lineDataSetOne.setCircleColor(Color.BLUE)
        lineDataSetOne.valueFormatter =  DefaultValueFormatter(2)
        lineDataSetOne.lineWidth = 3f // Increase here for line width
        lineDataSetOne.circleRadius = 4f
        lineDataSetOne.setDrawCircleHole(true)
        lineDataSetOne.formSize = 0f
        lineDataSetOne.valueTextSize = 0f
/*        lineDataSetOne.setDrawFilled(true)
        lineDataSetOne.formLineWidth = 10f

        lineDataSetOne.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        lineDataSetOne.formSize = 15f*/

//---------------------------------------------------------
        lineDataSetTwo.mode = LineDataSet.Mode.CUBIC_BEZIER;
        lineDataSetTwo.color = Color.parseColor("#4CAF50")
        lineDataSetTwo.setCircleColor(Color.parseColor("#4CAF50"))
        lineDataSetTwo.valueFormatter =  DefaultValueFormatter(2)
        lineDataSetTwo.lineWidth = 3f // Increase here for line width
        lineDataSetTwo.circleRadius = 4f
        lineDataSetTwo.setDrawCircleHole(true)
        lineDataSetTwo.formSize = 0f
        lineDataSetTwo.valueTextSize = 0f
/*        lineDataSetTwo.setDrawFilled(true)

        lineDataSetTwo.formLineWidth = 10f

        lineDataSetTwo.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        lineDataSetTwo.formSize = 15f*/


        val xAxis = line_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(true)

        //var xLabels = line_chart.xLabels
        //xLabels.setPosition(XLabelPosition.BOTTOM)

        //xAxis.valueFormatter = SemesterFormatter()

        val chartData = LineData()
        chartData.addDataSet(lineDataSetOne)
        chartData.addDataSet(lineDataSetTwo)


        line_chart.description = null
        line_chart.xAxis.isEnabled = false
        line_chart.axisLeft.isEnabled = false
        line_chart.axisLeft.setDrawAxisLine(true)
        line_chart.axisRight.isEnabled = false
        line_chart.data = chartData
        line_chart.animateX(1500)
        line_chart.invalidate()

    }

    //---------------------------------------------------------------- bar chart ----------------------------------------------
    private fun barChart(studValOne:ArrayList<BarEntry>, studValTwo:ArrayList<BarEntry>, firstStud:ArrayList<Results>, secondStud:ArrayList<Results>) {


        val valueFormat: ValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String? {
                return "%.2f".format(value)
            }
        }




        barChart.description = null
        barChart.setPinchZoom(false)
        barChart.setScaleEnabled(true)
        barChart.setDrawBarShadow(false)
        barChart.setDrawGridBackground(false)

        val xVal = ArrayList<String>()
        for(i in 0 until studValOne.size){
            when (i) {
                0 -> {
                    xVal.add("1st")
                }
                1 -> {
                    xVal.add("2nd")
                }
                2 -> {
                    xVal.add("3rd")
                }
                else -> {
                    xVal.add("${i+1}th")
                }
            }
        }

        //-----------------------------
        val startColorBlue = ContextCompat.getColor(context!!, R.color.colorBlue2)
        val endColorBlueTransparent = ContextCompat.getColor(context!!, R.color.colorBlueTransparent)
        val startColorGreen = ContextCompat.getColor(context!!, R.color.color_success)
        val endColorGreenTransparent = ContextCompat.getColor(context!!, R.color.colorGreenTransparent)

        val blueGradientColors: MutableList<GradientColor> = ArrayList()
        blueGradientColors.add(GradientColor(endColorBlueTransparent, startColorBlue))

        val greenGradientColors: MutableList<GradientColor> = ArrayList()
        greenGradientColors.add(GradientColor(endColorGreenTransparent, startColorGreen))

        val set1 = BarDataSet(studValOne,"")
        set1.color = Color.BLUE
        set1.gradientColors = blueGradientColors
        set1.formSize = 0f
        set1.valueTextSize = 8f
        //set1.valueFormatter = valueFormat

        val set2 = BarDataSet(studValTwo,"")
        set2.color = Color.GREEN
        set2.gradientColors = greenGradientColors
        set2.formSize = 0f
        set2.valueTextSize = 8f
        //set2.valueFormatter =  valueFormat

        val data = BarData(set1, set2)
        data.setValueFormatter(LargeValueFormatter())
        data.setValueFormatter(valueFormat)

        barChart.data = data
        barChart.barData.barWidth = barWidth
        barChart.xAxis.axisMinimum = 0f
        barChart.xAxis.axisMaximum = 4f
        barChart.groupBars(0f, groupSpace, barSpace)
        barChart.data.isHighlightEnabled = false
        barChart.invalidate()


        //---------------------------------------------------
        val l: Legend = barChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(true)
        l.yOffset = 20f
        l.xOffset = 0f
        l.yEntrySpace = 0f
        l.textSize = 8f

        val xAxis: XAxis = barChart.xAxis
        xAxis.granularity = .3f
        xAxis.isGranularityEnabled = false
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.axisMaximum = xVal.size.toFloat()
        xAxis.labelCount = studValOne.size
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(xVal)

        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.valueFormatter = LargeValueFormatter()
        leftAxis.setDrawGridLines(true)
        leftAxis.spaceTop = 40f
        leftAxis.axisMinimum = 0f

        barChart.axisRight.isEnabled = false
        barChart.axisLeft.isEnabled = false
        barChart.axisLeft.setLabelCount(studValOne.size, true);
        barChart.animateY(1500)

        loadProfiler(firstStud, secondStud)

    }

    @SuppressLint("SetTextI18n")
    private fun loadProfiler(firstStud:ArrayList<Results>, secondStud:ArrayList<Results>){
        studOneGrade.text = firstStud[firstStud.size-1].Student_Cgpa
        studOneName.text = firstStud[0].Student_Name
        studOneId.text = firstStud[0].Student_ID
        studOneDept.text = StudLabAssistant.titleToDeptCode(StudLabAssistant.idToDeptCodeIntake(firstStud[0].Student_ID))


        //---------stud_1-------------------
        studTwoGrade.text = secondStud[secondStud.size-1].Student_Cgpa
        studTwoName.text = secondStud[0].Student_Name
        studTwoId.text = secondStud[0].Student_ID
        studTwoDept.text = StudLabAssistant.titleToDeptCode(StudLabAssistant.idToDeptCodeIntake(secondStud[0].Student_ID))

        //---------head-------------
        //compareByTitle.text = "Compared by CGPA\nCommulative Grade Point Avarage"

        switchCompareModeButton.setOnClickListener{

            if(compareByTitle.text.toString().contains("CGPA")){
                compareByTitle.text = "Compared by SGPA\nSemester Grade Point Avarage"
                loadSGpa(firstStud, secondStud)
            }else if(compareByTitle.text.toString().contains("SGPA")){
                compareByTitle.text = "Compared by CGPA\nCommulative Grade Point Avarage"
                loadCGpa(firstStud, secondStud)
            }
        }
    }

    private fun loadCGpa(firstStud:ArrayList<Results>, secondStud:ArrayList<Results>){
        val oneSize = firstStud.size
        val twoSize = secondStud.size

        val barSetOne = ArrayList<BarEntry>()
        val barSetTwo = ArrayList<BarEntry>()

        val lineSetOne:MutableList<Entry> = ArrayList()
        val lineSetTwo:MutableList<Entry> = ArrayList()

        if(oneSize == twoSize){

            for (i in 0 until oneSize){
                lineSetOne.add(Entry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))
                lineSetTwo.add(Entry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
                barSetOne.add(BarEntry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))
                barSetTwo.add(BarEntry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
            }

        }else if(oneSize > twoSize){
            for (i in 0 until oneSize){

                barSetOne.add(BarEntry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))
                lineSetOne.add(Entry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))

                if(i<=twoSize-1){
                    barSetTwo.add(BarEntry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
                    lineSetTwo.add(Entry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
                }else{
                    lineSetTwo.add(Entry(i.toFloat(), 0f))
                    barSetTwo.add(BarEntry(i.toFloat(), 0f))
                }
            }
        }else if(oneSize < twoSize){
            for (i in 0 until twoSize){
                lineSetTwo.add(Entry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))
                barSetTwo.add(BarEntry(i.toFloat(), "0${secondStud[i].Student_Cgpa}".toFloat()))

                if(i<=oneSize-1){
                    lineSetOne.add(Entry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))
                    barSetOne.add(BarEntry(i.toFloat(), "0${firstStud[i].Student_Cgpa}".toFloat()))

                }else{
                    lineSetOne.add(Entry(i.toFloat(), 0f))
                    barSetOne.add(BarEntry(i.toFloat(), 0f))
                }
            }
        }
        setLineData(lineSetOne, lineSetTwo, firstStud, secondStud)
        barChart(barSetOne, barSetTwo, firstStud, secondStud)
    }

    private fun loadSGpa(firstStud:ArrayList<Results>, secondStud:ArrayList<Results>){
        val oneSize = firstStud.size
        val twoSize = secondStud.size

        val barSetOne = ArrayList<BarEntry>()
        val barSetTwo = ArrayList<BarEntry>()

        val lineSetOne:MutableList<Entry> = ArrayList()
        val lineSetTwo:MutableList<Entry> = ArrayList()

        if(oneSize == twoSize){

            for (i in 0 until oneSize){
                lineSetOne.add(Entry(i.toFloat(), "0${firstStud[i].Student_Sgpa}".toFloat()))
                lineSetTwo.add(Entry(i.toFloat(), "0${secondStud[i].Student_Sgpa}".toFloat()))
                barSetOne.add(BarEntry(i.toFloat(), "0${firstStud[i].Student_Sgpa}".toFloat()))
                barSetTwo.add(BarEntry(i.toFloat(), "0${secondStud[i].Student_Sgpa}".toFloat()))
            }

        }else if(oneSize > twoSize){
            for (i in 0 until oneSize){

                barSetOne.add(BarEntry(i.toFloat(), "0${firstStud[i].Student_Sgpa}".toFloat()))
                lineSetOne.add(Entry(i.toFloat(), "0${firstStud[i].Student_Sgpa}".toFloat()))

                if(i<=twoSize-1){
                    barSetTwo.add(BarEntry(i.toFloat(), "0${secondStud[i].Student_Sgpa}".toFloat()))
                    lineSetTwo.add(Entry(i.toFloat(), "0${secondStud[i].Student_Sgpa}".toFloat()))
                }else{
                    lineSetTwo.add(Entry(i.toFloat(), 0f))
                    barSetTwo.add(BarEntry(i.toFloat(), 0f))
                }
            }
        }else if(oneSize < twoSize){
            for (i in 0 until twoSize){
                lineSetTwo.add(Entry(i.toFloat(), "0${secondStud[i].Student_Sgpa}".toFloat()))
                barSetTwo.add(BarEntry(i.toFloat(), "0${secondStud[i].Student_Sgpa}".toFloat()))

                if(i<=oneSize-1){
                    lineSetOne.add(Entry(i.toFloat(), "0${firstStud[i].Student_Sgpa}".toFloat()))
                    barSetOne.add(BarEntry(i.toFloat(), "0${firstStud[i].Student_Sgpa}".toFloat()))

                }else{
                    lineSetOne.add(Entry(i.toFloat(), 0f))
                    barSetOne.add(BarEntry(i.toFloat(), 0f))
                }
            }
        }
        setLineData(lineSetOne, lineSetTwo, firstStud, secondStud)
        barChart(barSetOne, barSetTwo, firstStud, secondStud)
    }

}
