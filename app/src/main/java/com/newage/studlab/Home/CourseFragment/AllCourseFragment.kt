package com.newage.studlab.Home.CourseFragment

import AnnexHomeFragment
import HomeFragment
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.CourseRecyclerViewAdapter
import com.newage.studlab.Adapter.ResultAdapter.MyResultRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.courseList
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.position
import com.newage.studlab.Model.Course
import com.newage.studlab.Plugins.StudLabAssistant.Companion.titleToDeptCode
import com.newage.studlab.R
import com.newage.studlab.StudLab.Tools.ToolsCourse
import es.dmoral.toasty.Toasty

class AllCourseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_course, container, false)
    }

    lateinit var backButton: ImageView
    lateinit var plusButton: ImageView
    lateinit var resNotFound: TextView
    lateinit var semesterName:TextView
    lateinit var recyclerView: RecyclerView

    lateinit var semesterSpinner:Spinner

    private val futureCourseList = arrayListOf<Course>()


    lateinit var adapter: CourseRecyclerViewAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            plusButton = it.findViewById(R.id.fragment_sort_by_button)
            resNotFound = it.findViewById(R.id.fragment_empty)
            semesterName = it.findViewById(R.id.search_box)
            recyclerView = it.findViewById(R.id.fragment_recycler_view)

            semesterSpinner = it.findViewById(R.id.semester_Spinner_all_course)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)

        fragmentName = "allCourse"


        for(i in 0 until courseList.size){
            if(courseList[i].programCode == titleToDeptCode(currentUser!!.user_prog_or_dept)){
                futureCourseList.add(courseList[i])
            }
        }


        if(futureCourseList.size != 0){
            resNotFound.visibility = View.INVISIBLE
            adapter = CourseRecyclerViewAdapter(appContext, futureCourseList, clickListener = {

                Toasty.info(requireContext(), "Sorry: detailed information will available in future update.", Toast.LENGTH_LONG, true).show()

            })
        }else{
            resNotFound.visibility = View.VISIBLE
            adapter = CourseRecyclerViewAdapter(appContext, futureCourseList, clickListener = {

                Toasty.info(requireContext(), "Sorry: detailed information will available in future update.", Toast.LENGTH_LONG, true).show()

            })
        }

        recyclerView.adapter = adapter

        allButtonClickingEventListeners()

        Handler().postDelayed({
            handleSpinner()
        },700)

    }

    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)

            if(position == 0){
                transaction.replace(R.id.main_home_fragment_container, HomeFragment())
                transaction.commit()
            }else{
                transaction.replace(R.id.main_home_fragment_container, AnnexHomeFragment())
                transaction.commit()
            }


        }

        plusButton.setOnClickListener{
            startActivity(Intent(requireContext(), ToolsCourse::class.java))
        }
    }

    private fun handleSpinner(){
        val semesterListAdapter = ArrayAdapter.createFromResource(appContext,R.array.semesters_profile,R.layout.spinner_item_transparent)
        semesterListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        semesterSpinner.adapter = semesterListAdapter

        val semester = resources.getStringArray(R.array.semesters_profile)

        semesterSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val smtrName = semester[position]

                semesterName.text = smtrName
                val newCourseList:ArrayList<Course>

                if(smtrName != "Select Semester"){
                    newCourseList = futureCourseList.filter { it-> it.semesterTitle == smtrName } as ArrayList<Course>
                }else{
                    newCourseList = futureCourseList.filter { it-> it.programCode == titleToDeptCode(currentUser!!.user_prog_or_dept) } as ArrayList<Course>
                }

                adapter.filterCourseList(newCourseList)

            }
        }
    }
}