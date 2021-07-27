package com.newage.studlab.Home.CourseFragment

import HomeFragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.CourseRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.courseList
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Model.Course
import com.newage.studlab.Model.Results
import com.newage.studlab.Plugins.StudLabAssistant.Companion.numberSemesterToText
import com.newage.studlab.Plugins.StudLabAssistant.Companion.textSemesterToNumber
import com.newage.studlab.Plugins.StudLabAssistant.Companion.titleToDeptCode
import com.newage.studlab.R
import com.newage.studlab.StudLab.Tools.ToolsCourse
import es.dmoral.toasty.Toasty

class PreviousCourseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_previous_course, container, false)
    }

    lateinit var backButton: ImageView
    lateinit var plusButton: ImageView
    lateinit var resNotFound: TextView
    lateinit var semesterName: TextView
    lateinit var recyclerView: RecyclerView

    val previousCourseList = arrayListOf<Course>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentName = "previousCourse"

        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            plusButton = it.findViewById(R.id.fragment_sort_by_button)
            resNotFound = it.findViewById(R.id.fragment_empty)
            semesterName = it.findViewById(R.id.search_box)
            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)

        if(currentUser!!.user_semester == "First Semester"){
            semesterName.text = "${currentUser!!.user_semester} Running"

            for(i in 0 until courseList.size){
                if(courseList[i].semesterTitle == currentUser!!.user_semester && courseList[i].programCode == titleToDeptCode(currentUser!!.user_prog_or_dept)){
                    previousCourseList.add(courseList[i])
                }
            }
        }else{
            semesterName.text = numberSemesterToText((textSemesterToNumber(currentUser!!.user_semester).toInt() - 1).toString())

            for(i in 0 until courseList.size){
                if(courseList[i].semesterTitle == numberSemesterToText((textSemesterToNumber(currentUser!!.user_semester).toInt() - 1).toString()) && courseList[i].programCode == titleToDeptCode(currentUser!!.user_prog_or_dept)){
                    previousCourseList.add(courseList[i])
                }
            }
        }


        if(previousCourseList.size != 0){
            resNotFound.visibility = View.INVISIBLE
            recyclerView.adapter = CourseRecyclerViewAdapter(requireContext(), previousCourseList, clickListener = {

                Toasty.info(requireContext(), "Sorry: detailed information will available in future update.", Toast.LENGTH_LONG, true).show()

            })
        }else{
            resNotFound.visibility = View.VISIBLE
        }
        allbuttonClickingEventListeners()
    }

    private fun allbuttonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }

        plusButton.setOnClickListener{
            startActivity(Intent(requireContext(), ToolsCourse::class.java))
        }
    }

}