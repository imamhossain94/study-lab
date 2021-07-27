package com.newage.studlab.Adapter.UserAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.Intake
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_student_intake.view.*

class StudentIntakeRecyclerViewAdapter(val context: Context?, var intakeList: ArrayList<Intake>, val clickListener: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_student_intake, parent, false))
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(intakeList[position])

        holder.itemClick.setOnClickListener {
            clickListener(intakeList[position].intake_Title)
        }

    }

    override fun getItemCount(): Int {
        return intakeList.size
    }

    fun filterMyResultList(filteredList: ArrayList<Intake>) {
        intakeList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemClick = itemView.item_button


        @SuppressLint("SetTextI18n")
        fun bindItems(intk: Intake) {
            val intakeNo = itemView.intake_no
            val intakeTitle = itemView.intake_title
            val totalStudent = itemView.intake_total_student
            val semesterTitle = itemView.intake_semester_title


            intakeTitle.text = intk.intake_Title
            totalStudent.text = "Total Students: ${intk.totalStudents}"
            semesterTitle.text = intk.semesterName
            intakeNo.text = intk.intake_Title.replace("Intake ", "")

        }


    }
}