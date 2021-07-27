package com.newage.studlab.Adapter.AnnexAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.AnnexModel.AnnexFees
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_fees_semester.view.*

class FeesSemesterRecyclerViewAdapter(val context: Context?, val semesterList: ArrayList<AnnexFees>, val clickListener: (AnnexFees) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fees_semester, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(semesterList[position])


        holder.itemButton.setOnClickListener {
            clickListener(semesterList[position])
        }

    }

    override fun getItemCount(): Int {
        return semesterList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemButton:Button = itemView.item_button

        fun bindItems(sem: AnnexFees) {

            val semesterName = itemView.fees_semester
            val totalDemand = itemView.total_demand
            val totalWaiver = itemView.total_waiver
            val totalPaid = itemView.total_paid
            val totalDue = itemView.total_due

            semesterName.text = sem.Semester
            totalDemand.text = sem.Demand
            totalWaiver.text = sem.Waiver
            totalPaid.text = sem.Paid
            totalDue.text = sem.Due

        }
    }

}


