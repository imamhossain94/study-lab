package com.newage.studlab.Home.AnnexFragment

import AnnexHomeFragment.Companion.results
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.AnnexAdapter.ResultsSemesterRecyclerViewAdapter
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.AnnexModel.AnnexResults
import com.newage.studlab.Model.AnnexModel.CourseResults
import com.newage.studlab.Model.Results
import com.newage.studlab.R

class AnnexResultSemesterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_annex_result_semester, container, false)
    }


    companion object{
        lateinit var thisSemester:AnnexResults
    }

    //lateinit var backButton: ImageView
    lateinit var resNotFound: TextView

    lateinit var totalCourse: TextView
    private lateinit var completedCourses: TextView
    lateinit var sGpa: TextView
    private lateinit var cGpa: TextView



    lateinit var recyclerView: RecyclerView
    var resultList =ArrayList<Results>()

    //val intakeResult = HashMap<String, ArrayList<Results>>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentPosition = 0.0
        fragmentName = "AnnexResultSemesterFragment"

        activity?.let {

//            backButton = it.findViewById(R.id.back_button)
            resNotFound = it.findViewById(R.id.fragment_empty)

            totalCourse = it.findViewById(R.id.total_courses)
            completedCourses = it.findViewById(R.id.completed_courses)
            cGpa = it.findViewById(R.id.cgpa)
            sGpa = it.findViewById(R.id.sgpa)


            recyclerView = it.findViewById(R.id.result_course_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)




        allButtonClickingEventListeners()

        if(results.size != 0)
        recyclerView.adapter = ResultsSemesterRecyclerViewAdapter(context,results, clickListener = {
            resultSemesterClickEvent(it)
        })

        var tc = 0
        var tp = 0

        for(i in 0 until results.size){
            for (j in 0 until results[i].results.size){
                if(results[i].results[j].grade != "F"){
                    tp += 1
                }
            }
            tc += results[i].results.size
        }

        totalCourse.text = tc.toString()
        completedCourses.text = tp.toString()

        if(results[results.size - 1].sgpa.isNotEmpty()){
            sGpa.text = results[results.size - 1].sgpa
        }else{
            sGpa.text = "---"
        }

        if(results[results.size - 1].cgpa.isNotEmpty()){
            cGpa.text =  results[results.size - 1].cgpa
        }else{
            cGpa.text =  "---"
        }

    }

    private fun allButtonClickingEventListeners(){

    }

    private fun resultSemesterClickEvent(res:AnnexResults){

        thisSemester = res

        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)
        transaction.replace(R.id.main_home_fragment_container, AnnexSemesterCourseResultFragment())
        transaction.commit()

    }


}