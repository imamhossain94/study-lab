package com.newage.studlab.Adapter.RoutineAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.RoutineModel.Routines
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_routine.view.*
import java.lang.Exception


class ExamRoutinesRecyclerViewAdapter(val context:Context,
                                      private val routinesList: ArrayList<Routines>,
                                      val clickListenerInfo: (Routines) -> Unit,
                                      val clickListenerDownload: (Routines) -> Unit,
                                      val clickListenerPin: (Routines) -> Unit,
                                      val clickListenerDelete: (Routines) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_routine, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(routinesList[position])

        holder.infoButton.setOnClickListener {
            clickListenerInfo(routinesList[position])
        }

        holder.downButton.setOnClickListener {
            clickListenerDownload(routinesList[position])
        }

        holder.pinButton.setOnClickListener {
            clickListenerPin(routinesList[position])
        }

        holder.delButton.setOnClickListener {
            clickListenerDelete(routinesList[position])
        }

    }

    override fun getItemCount(): Int {
        return routinesList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val infoButton = itemView.info_button
        val delButton = itemView.delete_button
        val pinButton = itemView.pin_button
        val downButton = itemView.download_button
        val routineImage = itemView.routine_image

        fun bindItems(routine: Routines) {
            val routineTitle = itemView.routine_title
            routineTitle.text = routine.uploadDate
            Picasso.get().load(routine.routineImage).networkPolicy(NetworkPolicy.OFFLINE).into(routineImage, object: com.squareup.picasso.Callback{
                override fun onSuccess() { }
                override fun onError(e: Exception?) {
                    Picasso.get().load(routine.routineImage).into(routineImage)
                }
            })
        }
    }

}