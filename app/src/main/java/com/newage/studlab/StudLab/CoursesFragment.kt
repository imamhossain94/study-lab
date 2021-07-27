package com.newage.studlab.StudLab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.CourseRecyclerViewAdapter
import com.newage.studlab.Model.Course
import com.newage.studlab.R
import com.newage.studlab.StudLab.StudLabActivity.Companion.courList
import com.newage.studlab.StudLab.StudLabActivity.Companion.semesterName


class CoursesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    lateinit var recyclerView: RecyclerView
    var courseList =ArrayList<Course>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            recyclerView = it.findViewById(R.id.course_recycler_view)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadCourse(semesterName)
    }

    private fun loadCourse(semseter:String){
        recyclerView.adapter = CourseRecyclerViewAdapter(requireContext(),courList, clickListener = {

        })
    }


}