package com.newage.studlab.Calculators

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.Cgpa
import com.newage.studlab.R
import kotlinx.android.synthetic.main.cgpa_calculator_row.view.*

class CgpaAdapter(val context: Context, val cgpaList: ArrayList<Cgpa>, val clickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cgpa_calculator_row, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(cgpaList[position])

        (holder as MyViewHolder).clickableView1.setOnClickListener {
            clickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return cgpaList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clickableView1 = itemView.cgpa_course_row
        fun bindItems(row: Cgpa) {
            val courseNo = itemView.cgpa_course_row
            val gradeEarned = itemView.cgpa_grade_earned_row
            val gradePoint = itemView.cgpa_grade_point_row
            val creditHour = itemView.cgpa_credit_hour_row
            val gradeInToCredit = itemView.cgpa_grade_x_credit_row
            val sgpa = itemView.cgpa_sgpa_row

            courseNo.text = "C"+row.course
            gradeEarned.text = row.gradeEarned
            gradePoint.text = "%.2f".format(row.gradePoint)
            creditHour.text = "%.2f".format(row.creditHour)
            gradeInToCredit.text = "%.2f".format(row.gradeXCredit)
            sgpa.text = "%.2f".format(row.sGpa)
        }
    }

}