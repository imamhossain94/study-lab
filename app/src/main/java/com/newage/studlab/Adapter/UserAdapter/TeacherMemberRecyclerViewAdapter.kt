package com.newage.studlab.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.UserModel.Teacher
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_faculty.view.*

class TeacherMemberRecyclerViewAdapter(val context: Context?, private var facultyMemberList: ArrayList<Teacher>, val clickListenerProfile: (Teacher) -> Unit, val clickListenerEmail: (Teacher) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_faculty, parent, false))
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(facultyMemberList[position])

        holder.memImage.setOnClickListener {
            clickListenerProfile(facultyMemberList[position])
        }

        holder.memEmail.setOnClickListener {
            clickListenerEmail(facultyMemberList[position])
        }
    }

    override fun getItemCount(): Int {
        return facultyMemberList.size
    }

    fun filterMyResultList(filteredList: ArrayList<Teacher>) {
        facultyMemberList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val memImage = itemView.faculty_member_image
        val memEmail = itemView.faculty_member_email

        @SuppressLint("SetTextI18n")
        fun bindItems(mem: Teacher) {
            val memName = itemView.faculty_member_name
            val memDesignation = itemView.faculty_member_designation
            val memCode = itemView.faculty_member_code
            val memRoom = itemView.faculty_member_room_no
            val memExt = itemView.faculty_member_ext

            val memPhone = itemView.faculty_member_phone

            memName.text = mem.facultyEmp
            memDesignation.text = mem.facultyDesignation
            memCode.text = mem.facultyFacultyCode
            memRoom.text = mem.facultyRoomNo
            memExt.text = mem.facultyExt
            memEmail.text = mem.facultyEmail
            memPhone.text = mem.facultyPhone

            if(mem.facultyRoomNo == "")
                memRoom.text = "---"
            if(mem.facultyExt == "")
                memExt.text = "---"
            if(mem.facultyEmail == "")
                memEmail.text = "---"
            if(mem.facultyPhone == "")
                memPhone.text = "---"

            loadUserImage(mem.facultyImage)
        }

        private fun loadUserImage(path:String){
            Picasso.get().load(path).networkPolicy(NetworkPolicy.OFFLINE).into(memImage, object: com.squareup.picasso.Callback{
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    Picasso.get().load(path).into(memImage)
                }
            })
        }
    }
}