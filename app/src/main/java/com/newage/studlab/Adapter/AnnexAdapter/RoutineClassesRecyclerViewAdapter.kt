package com.newage.studlab.Adapter.AnnexAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.AnnexModel.RoutineClass
import com.newage.studlab.Model.AnnexModel.RoutineDay
import com.newage.studlab.Model.AnnexModel.SmartRoutine
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_routine_class.view.*
import kotlinx.android.synthetic.main.item_routine_day.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RoutineClassesRecyclerViewAdapter(val context: Context?, val classList: ArrayList<SmartRoutine>, val clickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_routine_class, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(classList[position])


        holder.clickableView2.setOnClickListener {
            clickListener(position)
        }
        holder.clickableView3.setOnClickListener {
            clickListener(position)
        }
        holder.clickableView4.setOnClickListener {
            clickListener(position)
        }
    }

    override fun getItemCount(): Int {
        return classList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val clickableView2 = itemView.subject_code
        val clickableView3 = itemView.subject_code
        val clickableView4 = itemView.subject_code
        val routineClassColor: CardView = itemView.routine_class_background

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bindItems(cls: SmartRoutine) {

            val subjectCode = itemView.subject_code
            val classSchedule = itemView.schedule
            val classBuilding = itemView.building
            val classIntakeSection = itemView.intake_section
            val teacherCode = itemView.teachers_code

            subjectCode.text = cls.Subject_Code
            classSchedule.text = cls.Schedule
            classBuilding.text = "${cls.Building} R-${cls.Room_No}"
            classIntakeSection.text = "${cls.Intake}-${cls.Section}"
            teacherCode.text = cls.Teacher_Code


            val scheduleList = cls.Schedule.split("to")
            val startTime = scheduleList[0].replace(":", "").replace("AM", "").replace("PM", "").replace(" ", "").toInt()
            val endTime = scheduleList[1].replace(":", "").replace("AM", "").replace("PM", "").replace(" ", "").toInt()

            val time = SimpleDateFormat("hhmm")
            val currentTime = time.format(Date()).toInt()

            if(currentTime in startTime..endTime){
                routineClassColor.setCardBackgroundColor(Color.parseColor("#00C853"))
            }else{
                routineClassColor.setCardBackgroundColor(Color.parseColor("#5377DC"))
            }
        }
    }

}


