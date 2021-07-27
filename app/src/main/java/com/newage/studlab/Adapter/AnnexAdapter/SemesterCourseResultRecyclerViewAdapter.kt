package com.newage.studlab.Adapter.AnnexAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.AnnexModel.CourseResults
import com.newage.studlab.Plugins.StudLabAssistant.Companion.gradeToPoint
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_semester_course_result.view.*

class SemesterCourseResultRecyclerViewAdapter(val context: Context?, private val courseResultsList: ArrayList<CourseResults>, val clickListener: (CourseResults) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_semester_course_result, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(courseResultsList[position])


        holder.itemButton.setOnClickListener {
            clickListener(courseResultsList[position])
        }
    }

    override fun getItemCount(): Int {
        return courseResultsList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemButton:TextView = itemView.course_title


        @SuppressLint("SetTextI18n")
        fun bindItems(result: CourseResults) {

            val courseTitle = itemView.course_title
            val courseGrade = itemView.course_grade
            val courseCode = itemView.course_code
            val courseType = itemView.course_type
            val creditHoure = itemView.course_credit_houre
            val gradePoint = itemView.course_grade_point

            courseTitle.text = result.title
            courseGrade.text = result.grade
            courseCode.text = result.code
            courseType.text = result.type
            creditHoure.text = result.credit
            gradePoint.text = "±${gradeToPoint(result.grade)}"

        }
    }

}


