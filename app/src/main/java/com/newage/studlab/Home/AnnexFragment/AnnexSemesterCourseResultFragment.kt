package com.newage.studlab.Home.AnnexFragment

import AnnexHomeFragment
import AnnexHomeFragment.Companion.results
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.AnnexAdapter.SemesterCourseResultRecyclerViewAdapter
import com.newage.studlab.Home.AnnexFragment.AnnexResultSemesterFragment.Companion.thisSemester
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.R

class AnnexSemesterCourseResultFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_annex_semester_course_result, container, false)
    }

    lateinit var resNotFound: TextView
    lateinit var backButton:ImageView

    //----------------------item-----------------------
    lateinit var title: TextView
    lateinit var totalCourse: TextView
    lateinit var totalPassed: TextView
    lateinit var sGpa: TextView
    lateinit var cGpa: TextView

    lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentPosition = 0.1
        fragmentName = "AnnexSemesterCourseResultFragment"

        activity?.let {

            backButton = it.findViewById(R.id.back_button)
            resNotFound = it.findViewById(R.id.fragment_empty)

            title = it.findViewById(R.id.title)
            totalCourse = it.findViewById(R.id.total_course)
            totalPassed = it.findViewById(R.id.total_passed)
            sGpa = it.findViewById(R.id.s_gpa)
            cGpa = it.findViewById(R.id.c_gpa)

            recyclerView = it.findViewById(R.id.smester_course_result_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)




        allbuttonClickingEventListeners()

        if(results.size != 0){
            for(i in 0 until results.size){
                var toPas = 0

                if(results[i].semester == thisSemester.semester){
                    recyclerView.adapter = SemesterCourseResultRecyclerViewAdapter(context,results[i].results, clickListener = {

                    })

                    totalCourse.text = results[i].results.size.toString()

                    for(j in 0 until results[i].results.size){
                        if(results[i].results[j].grade != "F"){
                            toPas += 1
                        }
                    }

                    totalPassed.text = toPas.toString()
                    break
                }
            }

            title.text = thisSemester.semester
            sGpa.text = thisSemester.sgpa
            cGpa.text = thisSemester.cgpa
        }

    }

    private fun allbuttonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, AnnexHomeFragment())
            transaction.commit()
        }
    }

}