package com.newage.studlab.Adapter.AnnexAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.AnnexModel.AnnexResults
import com.newage.studlab.Model.AnnexModel.CourseResults
import com.newage.studlab.Model.Results
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_results_semester.view.*

class ResultsSemesterRecyclerViewAdapter(val context: Context?, private val resultSList: ArrayList<AnnexResults>, val clickListener: (AnnexResults) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        var resL = ArrayList<AnnexResults>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        resL = resultSList
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_results_semester, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(resultSList[position])


        holder.itemButton.setOnClickListener {
            clickListener(resultSList[position])
        }

    }

    override fun getItemCount(): Int {
        return resultSList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemButton:Button = itemView.item_button
        private val totalCourse: TextView = itemView.res_total_courses
        private val totalPassed: TextView = itemView.res_passed_courses

        fun bindItems(res: AnnexResults) {

            val semesterName = itemView.results_semester

            val sGpa = itemView.res_sgpa
            val cGpa = itemView.res_cgpa

            semesterName.text = res.semester

            if(res.sgpa.isNotEmpty()){
                sGpa.text = res.sgpa
            }else{
                sGpa.text = "---"
            }

            if(res.cgpa.isNotEmpty()){
                cGpa.text = res.cgpa
            }else{
                cGpa.text = "---"
            }
            
            retTotCourse(res.results)
        }

        private fun retTotCourse(res:ArrayList<CourseResults>){
            var toPas = 0

            for(i in 0 until res.size){
                if(res[i].grade != "F"){
                    toPas += 1
                }
            }

            totalCourse.text = res.size.toString()
            totalPassed.text = toPas.toString()
        }


    }

}


