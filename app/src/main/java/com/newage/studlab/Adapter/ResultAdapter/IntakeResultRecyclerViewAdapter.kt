package com.newage.studlab.Adapter.ResultAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.Results
import com.newage.studlab.Plugins.StudLabAssistant.Companion.pointToGrade
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_intake_result.view.*

class IntakeResultRecyclerViewAdapter(val context: Context?, var resultList: ArrayList<Results>, val clickListener: (Results) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_intake_result, parent, false)
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(resultList[position])

        holder.button.setOnClickListener {
            clickListener(resultList[position])
        }

    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    fun filterMyResultList(filteredList: ArrayList<Results>) {
        resultList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val button: Button = itemView.item_button
        val pos: TextView = itemView.position

        @SuppressLint("SetTextI18n")
        fun bindItems(res: Results) {
            val userPosition = itemView.position_or_grade
            val userName = itemView.result_user_name
            val userId = itemView.result_user_id
            val userResult = itemView.user_result


            pos.text = res.Semester_Title
            userPosition.text = pointToGrade(res.Student_Cgpa)
            userName.text = res.Student_Name
            userId.text = res.Student_ID
            userResult.text = "SGPA: ${res.Student_Sgpa}   CGPA: ${res.Student_Cgpa}"
        }
    }
}