package com.newage.studlab.Plugins
import android.annotation.SuppressLint
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.intakeResult
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Model.AnnexModel.RoutineDay
import com.newage.studlab.Model.AnnexModel.SmartRoutine
import com.newage.studlab.Model.Results
import com.newage.studlab.Plugins.StudLabAssistant.Companion.textSemesterToOrdinal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StudLabGenerate {

    companion object {
        var myResult =ArrayList<Results>()




    }

    fun position(i: Int): String? {
        val suffixes =
            arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
        return when (i % 100) {
            11, 12, 13 -> i.toString() + "th"
            else -> i.toString() + suffixes[i % 10]
        }

    }

    private fun calculateMyResult(semesterName:String): Results? {
        val resultList = intakeResult.getValue(semesterName)
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
        return newResultList.find { it.Student_ID == currentUser!!.user_id}?.let { it }!!
    }

    public fun loadMyResult():ArrayList<Results> {
        val resList =ArrayList<Results>()
        for(key in intakeResult.keys){
            if(intakeResult.containsKey(key)){
                resList.add(calculateMyResult(key)!!)
            }
        }
        val newResList = ArrayList<Results>(12)

        for(i in 0 until resList.size){
            val res = Results(resList[i].Student_ID,resList[i].Student_Name,
                resList[i].Student_Cgpa, resList[i].Student_Sgpa,resList[i].program_Code,
                resList[i].Intake_No,textSemesterToOrdinal(resList[i].Semester_Title))
            newResList.add(res)
        }
        newResList.sortBy { it.Semester_Title }
        return newResList
    }

    @SuppressLint("SimpleDateFormat")
    fun createSmartRoutineService():ArrayList<RoutineDay>{
        val dayList = ArrayList<RoutineDay>()
        val databaseHandler = DatabaseHelper(appContext)
        val smartRtn:ArrayList<SmartRoutine>? = databaseHandler.getSmartRoutine()
        val time = SimpleDateFormat("hhmm")
        val currentTime = time.format(Date()).toInt()
        //val currentTime = "1040".toInt()

        var dName = "Saturday"
        var offDay = 0
        if(smartRtn != null)
        for(i in 0 until smartRtn.size){

            if(smartRtn[i].Day == dName){

                val scheduleList = smartRtn[i].Schedule.split("to")
                val startTime = scheduleList[0].replace(":", "").replace("AM", "").replace("PM", "").replace(" ", "").toInt()
                val endTime = scheduleList[1].replace(":", "").replace("AM", "").replace("PM", "").replace(" ", "").toInt()

                val totalClasses = smartRtn.filter { s -> s.Subject_Code != "" && s.Day == smartRtn[i].Day } as ArrayList<SmartRoutine>

                var theoryClass = 0
                var labClasses = 0
                if(totalClasses.isNotEmpty()){
                    theoryClass = totalClasses.filter { s -> "${s.Subject_Code[4]}${s.Subject_Code[5]}${s.Subject_Code[6]}".toInt() %2 != 0 && s.Day == smartRtn[i].Day }.size
                    labClasses = totalClasses.size - theoryClass
                }

                if(currentTime in 430..530){
                    if(currentTime in startTime..endTime && smartRtn[i].Subject_Code != ""){
                        dayList.add(RoutineDay(smartRtn[i].Day,"$theoryClass","$labClasses",smartRtn[i].Subject_Code,"null"))
                    }else if(currentTime in startTime..endTime && smartRtn[i].Subject_Code == ""){
                        dayList.add(RoutineDay(smartRtn[i].Day,"$theoryClass","$labClasses","null","null"))
                    }
                }else if(currentTime in 115..425 || currentTime in 830..1245){
                    if(currentTime in startTime..endTime && smartRtn[i].Subject_Code != ""){
                        dayList.add(RoutineDay(smartRtn[i].Day,"$theoryClass","$labClasses",smartRtn[i].Subject_Code,smartRtn[i+1].Subject_Code))

                    }else if(currentTime in startTime..endTime && smartRtn[i].Subject_Code == ""){
                        dayList.add(RoutineDay(smartRtn[i].Day,"$theoryClass","$labClasses",smartRtn[i].Subject_Code,smartRtn[i+1].Subject_Code))

                    }
                }else if(currentTime !in 115..425 && currentTime !in 830..1245){
                    offDay++
                    if (offDay == 7){
                        dayList.add(RoutineDay(smartRtn[i].Day,"$theoryClass","$labClasses","null","null"))
                        offDay = 0
                    }
                }
            }else{
                dName = smartRtn[i].Day
            }
        }
        return dayList
    }

}