package com.newage.studlab.StudLab

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.newage.studlab.Application.StudLab.Companion.courseList
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Model.Course
import com.newage.studlab.Model.SatusModel.Status
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.Plugins.StudLabAssistant.Companion.titleToDeptCode
import com.newage.studlab.R
import com.newage.studlab.StudLab.Tools.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.bottom_sheet_add_library_elements.*
import kotlinx.android.synthetic.main.activity_stud_lab.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StudLabActivity : AppCompatActivity() {

    companion object{
        var current_user_uid:String = ""
        var program:String = ""

        lateinit var semesterName:String
        var coursCode:String = ""

        lateinit var semester:Array<String>
        var course = arrayListOf<String>()
        var courList = arrayListOf<Course>()
    }


    var position = 0
    private var previousPosition = 0

    private fun switchContent(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()


        if(position != previousPosition){

            if(previousPosition > position){
                transaction.setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right,0,0)
                transaction.replace(R.id.stud_lab_fragment_container, fragment)
            }else{
                transaction.setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,0,0)
                transaction.replace(R.id.stud_lab_fragment_container, fragment)
            }

            transaction.commit()
        }

        previousPosition = position
    }

    private fun updateFragment(){
        val bottomNav = findViewById<ChipNavigationBar>(R.id.bottom_navigation_stud)
        bottomNav.setItemSelected(R.id.nav_book)

        bottomNav.setOnItemSelectedListener(object : ChipNavigationBar.OnItemSelectedListener {
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(id: Int) {

                val option =
                    when (id) {

                        R.id.nav_book ->{
                            position = 0
                            studlab_program_code.text = "SWIPE UP TO UPLOAD BOOKS FOR $program"
                            switchContent(BooksFragment())
                        }
                        R.id.nav_slide ->{
                            position = 1
                            studlab_program_code.text = "SWIPE UP TO UPLOAD SLIDES FOR $program"
                            switchContent(SlidesFragment())
                        }
                        R.id.nav_sheet ->{
                            position = 2
                            studlab_program_code.text = "SWIPE UP TO UPLOAD SHEETS FOR $program"
                            switchContent(SheetsFragment())
                        }
                        R.id.nav_lecture->{
                            position = 3
                            studlab_program_code.text = "SWIPE UP TO UPLOAD LECTURES FOR $program"
                            switchContent(LecturesFragment())
                        }
                        R.id.nav_suggestion ->{
                            position = 4
                            studlab_program_code.text = "SWIPE UP TO UPLOAD SUGGESTIONS FOR $program"
                            switchContent(SuggestionFragment())
                        }
                        else->{
                            position = 0
                            studlab_program_code.text = "SWIPE UP TO UPLOAD BOOKS FOR $program"
                            switchContent(BooksFragment())
                        }
                    }
            }
        })
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stud_lab)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        current_user_uid = currentUser!!.user_id
        setVariableData()
        allButtonClickListener()
        updateFragment()

        if (savedInstanceState == null) {
            val fragment = BooksFragment()
            supportFragmentManager.beginTransaction().replace(R.id.stud_lab_fragment_container, fragment)
                .commit()
        }

        semester = resources.getStringArray(R.array.semesters)
        semesterName = semester[0]

        updateSpinner()
    }

    @SuppressLint("SetTextI18n")
    private fun setVariableData(){
        program = StudLabAssistant.selectedProgram
        studlab_program_code.text = "SWIPE UP TO UPLOAD BOOKS FOR $program"
    }

    private fun allButtonClickListener(){
/*        studlab_back_to_home.setOnClickListener{
            this.finish()
        }*/

        add_button.setOnClickListener{
            if(program == titleToDeptCode(currentUser!!.user_prog_or_dept)){
                if(position == 0){
                    startActivity(Intent(this, ToolsBook::class.java))
                }else if(position == 1){
                    startActivity(Intent(this, ToolsSlide::class.java))
                }else if(position == 2){
                    startActivity(Intent(this, ToolsSheet::class.java))
                }else if(position == 3){
                    startActivity(Intent(this, ToolsLecture::class.java))
                }else if(position == 4){
                    startActivity(Intent(this, ToolsSuggestion::class.java))
                }
            }else{
                Toasty.error(this, "Access denied: department code not matched!", Toast.LENGTH_SHORT, true).show()
            }

        }

    }

    private fun updateSpinner(){
        val semesterSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.semesters,R.layout.spinner_item_studlab)
        semesterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        semester_spinner.adapter = semesterSpinnerAdapter

        semester_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                semesterName = semester[position]
                loadCourse(semesterName)
            }
        }
    }



    private fun loadCourse(semseter:String){

        course.clear()
        courList.clear()

        if(courseList.size != 0){
            courList = courseList.filter { it-> it.programCode == program && it.semesterTitle == semseter } as ArrayList<Course>

            if(courList.size != 0){
                courList.forEach{
                    course.add(it.courseCode)
                }
            }
        }

        if(course.size == 0){
            course.add("No Course")
            Toasty.info(this, "No course found", Toast.LENGTH_SHORT, true).show()
        }

        val courseAdapterSpinner: ArrayAdapter<String> = ArrayAdapter(this, R.layout.spinner_item_studlab, course)
        courseAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_item)
        course_spinner.adapter = courseAdapterSpinner

        course_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                coursCode = course[pos]
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(0, 0,0,0)

                if(position == 0){
                    studlab_program_code.text = "SWIPE UP TO UPLOAD BOOKS FOR $program"
                    transaction.replace(R.id.stud_lab_fragment_container, BooksFragment())
                    transaction.commit()
                }else if(position == 1){
                    studlab_program_code.text = "SWIPE UP TO UPLOAD SLIDES FOR $program"
                    transaction.replace(R.id.stud_lab_fragment_container, SlidesFragment())
                    transaction.commit()
                }else if(position == 2){
                    studlab_program_code.text = "SWIPE UP TO UPLOAD SHEETS FOR $program"
                    transaction.replace(R.id.stud_lab_fragment_container, SheetsFragment())
                    transaction.commit()
                }else if(position == 3){
                    studlab_program_code.text = "SWIPE UP TO UPLOAD LECTURES FOR $program"
                    transaction.replace(R.id.stud_lab_fragment_container, LecturesFragment())
                    transaction.commit()
                }else if(position == 4){
                    studlab_program_code.text = "SWIPE UP TO UPLOAD SUGGESTIONS FOR $program"
                    transaction.replace(R.id.stud_lab_fragment_container, SuggestionFragment())
                    transaction.commit()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onlineStatus("online")
    }

    override fun onPause() {
        super.onPause()
        onlineStatus("offline")
    }

    @SuppressLint("SimpleDateFormat", "DefaultLocale")
    private fun onlineStatus(state:String){
        val ref = FirebaseDatabase.getInstance().getReference("/Status/${currentUser!!.user_id}")
        val calendar = Calendar.getInstance().time.toString()
        val lastSeen = "${SimpleDateFormat("hh:mm a").format(Date()).toLowerCase()}-${calendar.substring(4,10)}, ${calendar.substring(30,34)}"
        val status = Status(currentUser!!.user_id,state,lastSeen)
        ref.setValue(status)
        //Fri Jun 19 14:52:02 GMT+06:00 2020
    }

}



