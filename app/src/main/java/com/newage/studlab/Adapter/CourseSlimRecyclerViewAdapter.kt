package com.newage.studlab.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.Course
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_course_slim.view.*

class CourseSlimRecyclerViewAdapter(val context:Context, val courseList: ArrayList<Course>, val clickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_course_slim, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(courseList[position])

        holder.clickableView1.setOnClickListener {
            clickListener(position)
        }

    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clickableView1 = itemView.item_button

        fun bindItems(code: Course) {
            val courseCode = itemView.course_code
            val courseTitle = itemView.course_title
            val courseCredit = itemView.course_credit
            courseCode.text = code.courseCode
            courseTitle.text = code.courseTitle
            courseCredit.text = code.courseCredit.toString()
        }
    }

}