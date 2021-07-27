package com.newage.studlab.Adapter.UserAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.studlab.Model.Program
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_home_main.*
import kotlinx.android.synthetic.main.item_program.view.*
import java.lang.Exception

class TeacherUserProgramRecyclerViewAdapter(val context: Context?, var progList: ArrayList<Program>, val clickListener: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_program, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(progList[position])

        holder.itemClick.setOnClickListener {
            clickListener(progList[position].programCode)
        }

    }

    override fun getItemCount(): Int {
        return progList.size
    }

    fun filterMyResultList(filteredList: ArrayList<Program>) {
        progList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemClick = itemView.item_button
        @SuppressLint("SetTextI18n")
        fun bindItems(prog: Program) {

            val progImage = itemView.prog_dept_image
            val progTitle = itemView.prog_dept_title
            val progFaculty = itemView.prog_faculty_title
            val progUsersCount = itemView.prog_user_count

            progTitle.text = prog.programTitle
            progFaculty.text = prog.progFaculty
            progUsersCount.text = "Faculty: ${prog.facultyNumber}"

            if(prog.progImage != "")
            Picasso.get().load(prog.progImage).networkPolicy(NetworkPolicy.OFFLINE).into(progImage, object: com.squareup.picasso.Callback{
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    Picasso.get().load(prog.progImage).into(progImage)
                }
            })
        }
    }

}


