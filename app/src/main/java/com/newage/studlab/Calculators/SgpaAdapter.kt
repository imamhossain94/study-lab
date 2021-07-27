package com.newage.studlab.Calculators

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.Sgpa
import com.newage.studlab.R
import kotlinx.android.synthetic.main.sgpa_calculator_row.view.*

class SgpaAdapter(val context: Context, val sgpaList: ArrayList<Sgpa>, val clickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.sgpa_calculator_row, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(sgpaList[position])

        (holder as MyViewHolder).clickableView1.setOnClickListener {
            clickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return sgpaList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clickableView1 = itemView.grade_x_credit_row
        fun bindItems(row: Sgpa) {
            val courseNo = itemView.coure_no_row
            val grade = itemView.grade_earned_row
            val gradePoint = itemView.grade_point_row
            val creditHour = itemView.credit_hour_row
            val gradeInToCredit = itemView.grade_x_credit_row

            courseNo.text = "C"+row.course
            grade.text = row.grade
            gradePoint.text = "%.2f".format(row.gradePoint)
            creditHour.text = "%.2f".format(row.creditHour)
            gradeInToCredit.text = "%.2f".format(row.gradeXCredit)
        }
    }

}