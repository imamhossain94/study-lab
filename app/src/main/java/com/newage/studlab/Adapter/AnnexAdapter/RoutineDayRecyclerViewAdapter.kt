package com.newage.studlab.Adapter.AnnexAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.AnnexModel.RoutineDay
import com.newage.studlab.Model.StudLibrary.Book
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_routine_day.view.*
import java.util.*
import kotlin.collections.ArrayList

class RoutineDayRecyclerViewAdapter(val context: Context?, private var dayList: ArrayList<RoutineDay>, val clickListener: (RoutineDay) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_routine_day, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(dayList[position])

        holder.dayButton.setOnClickListener {
            clickListener(dayList[position])
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    fun filterDayList(filteredList: ArrayList<RoutineDay>) {
        dayList = filteredList
        notifyDataSetChanged()
    }


    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dayButton: Button = itemView.detail_button
        val routineDayColor:CardView = itemView.routine_day_background


        @SuppressLint("SetTextI18n")
        fun bindItems(routine: RoutineDay) {

            val dayTitle = itemView.routine_day
            val dayTheoryClassesCout = itemView.routine_theory_class_count
            val dayLabClassesCout = itemView.routine_lab_class_count
            val currentClass = itemView.current_class
            val nextClass = itemView.next_class


            dayTitle.text = routine.routine_day
            dayTheoryClassesCout.text = routine.routine_classCount
            dayLabClassesCout.text = routine.routine_labCount
            if (routine.routine_currentClass == ""){
                currentClass.text = "null"
            }else{
                currentClass.text = routine.routine_currentClass
            }

            if (routine.routine_nextClass == ""){
                nextClass.text = "null"
            }else{
                nextClass.text = routine.routine_nextClass
            }


            val calendar = Calendar.getInstance()

            if(routine.routine_day.substring(0,3).toLowerCase(Locale.getDefault()) == calendar.time.toString().substring(0,3).toLowerCase(Locale.getDefault())){
                routineDayColor.setCardBackgroundColor(Color.parseColor("#00C853"))
            }else{
                routineDayColor.setCardBackgroundColor(Color.parseColor("#5377DC"))
            }


        }
    }

}


