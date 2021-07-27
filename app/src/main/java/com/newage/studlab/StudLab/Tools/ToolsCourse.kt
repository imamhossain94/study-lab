package com.newage.studlab.StudLab.Tools

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.newage.studlab.Adapter.CourseSlimRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Home.StudLabFragment
import com.newage.studlab.Model.Course
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_studlab_tools_course.*
import www.sanju.motiontoast.MotionToast

class ToolsCourse : AppCompatActivity() {

    var programCode:String = ""
    lateinit var semesterName:String
    lateinit var coursCode:String
    lateinit var courseCredit:String
    lateinit var courseTitle:String
    lateinit var semester:Array<String>
    lateinit var credit:Array<String>

    val cList = arrayListOf<Course>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_studlab_tools_course)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        course_recycler_view.layoutManager = LinearLayoutManager(this)
        semester = resources.getStringArray(R.array.semesters)
        semesterName = semester[0]

        credit = resources.getStringArray(R.array.credit)
        courseCredit = credit[0]

        programCode = StudLabAssistant.titleToDeptCode(currentUser!!.user_prog_or_dept)
        course_tools_title.text = "${StudLabAssistant.titleToDeptCode(currentUser!!.user_prog_or_dept)} Course Manager"

        handleSpinner()
        loadCourse(semesterName)
        loadCredit()
        allButtonClickListener()

    }


    private fun allButtonClickListener(){
        course_tools_info_icon.setOnClickListener{
            Toasty.info(this, "Add a course", Toast.LENGTH_SHORT, true).show()
        }

        add_course_button.setOnClickListener{

            coursCode = course_code_inout.text.toString()
            courseTitle = course_title_input.text.toString()

            if(coursCode.isEmpty()){
                course_code_inout.error = "Enter course code"
            }
            if(courseTitle.isEmpty()){
                course_code_inout.error = "Enter course title"
            }

            if(coursCode.isNotEmpty() && courseTitle.isNotEmpty()){
                addCourse()
            }
        }
    }


    private fun handleSpinner(){

        val semesterListAdapter = ArrayAdapter.createFromResource(this,R.array.semesters,R.layout.spinner_item_tool_upload)
        semesterListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        semester_spinner.adapter = semesterListAdapter

        semester_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                semesterName = semester[position]

                add_a_semester_course_msg.text = "Add a $semesterName course"
                loadCourse(semesterName)
            }
        }

    }

    private fun loadCourse(semseter:String){
        cList.clear()
        for(i in 0 until StudLab.courseList.size){
            if(StudLab.courseList[i].semesterTitle == semseter && StudLab.courseList[i].programCode == programCode){
                val course = StudLab.courseList[i]
                cList.add(course)
            }
        }

        if(cList.size !=0){
            course_recycler_view.adapter = CourseSlimRecyclerViewAdapter(this@ToolsCourse,cList, clickListener = {

            })
        }else{
            Toasty.info(this, "No course found", Toast.LENGTH_SHORT, true).show()
            course_recycler_view.adapter = CourseSlimRecyclerViewAdapter(this@ToolsCourse,cList, clickListener = {

            })
        }
    }


    private fun loadCredit(){
        val creditListAdapter = ArrayAdapter.createFromResource(this,R.array.credit,R.layout.spinner_item_tool_upload)
        creditListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        credit_spinner.adapter = creditListAdapter

        credit_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                courseCredit = credit[position]
            }
        }
    }



    private fun addCourse(){

        val ref = FirebaseDatabase.getInstance().getReference("/Course/$coursCode")
        val crc = Course(coursCode, courseTitle, courseCredit.toDouble(),programCode, semesterName)

        ref.setValue(crc)
            .addOnSuccessListener {
                runOnUiThread{
                    loadCourse(semesterName)
                    Toasty.success(this, "Course added", Toast.LENGTH_SHORT, true).show()
                }
            }
        ref.setValue(crc).addOnFailureListener{
            runOnUiThread{

            }
        }

    }


}
