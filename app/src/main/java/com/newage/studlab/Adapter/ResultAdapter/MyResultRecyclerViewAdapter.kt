package com.newage.studlab.Adapter.ResultAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.Results
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_my_result.view.*


class MyResultRecyclerViewAdapter(val context: Context?, var MyResultList: ArrayList<Results>, val clickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_my_result, parent, false)
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(MyResultList[position])

        holder.button.setOnClickListener {
            clickListener(position)
        }

    }

    override fun getItemCount(): Int {
        return MyResultList.size
    }

    fun filterMyResultList(filteredList: ArrayList<Results>) {
        MyResultList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val button:Button = itemView.item_button

        val pos = itemView.position
        val semesterImage = itemView.result_semester_image

        @SuppressLint("SetTextI18n")
        fun bindItems(res: Results) {

            val userName = itemView.result_user_name
            val userId = itemView.result_user_id
            val userResult = itemView.user_result

            pos.text = res.program_Code
            //pos.text = res.Semester_Title
           // userPosition.text = pointToGrade(res.Student_Cgpa)
            userName.text = res.Student_Name
            userId.text = res.Student_ID
            userResult.text = "SGPA: ${res.Student_Sgpa}   CGPA: ${res.Student_Cgpa}"


            if(res.Semester_Title == "First Semester" || res.Semester_Title == "1st Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Fone.png?alt=media&token=fdb37b8c-b019-4573-b5e9-dc50fdf51ff8")
            }else if(res.Semester_Title == "Second Semester" || res.Semester_Title == "2nd Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Ftwo.png?alt=media&token=fc5cbba8-11b4-4ec4-93d1-ccd5bad09b12")
            }else if(res.Semester_Title == "Third Semester" || res.Semester_Title == "3rd Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Fthree.png?alt=media&token=5abf6e41-b853-48f9-b5b6-320d124c106b")
            }else if(res.Semester_Title == "Fourth Semester" || res.Semester_Title == "4th Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Ffour.png?alt=media&token=2a5781e5-597c-4f22-826f-0c418b4e8401")
            }else if(res.Semester_Title == "Fifth Semester" || res.Semester_Title == "5th Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Ffive.png?alt=media&token=7e2d7bb0-f8b4-47e3-ae40-4ffae9dffb1e")
            }else if(res.Semester_Title == "Sixth Semester" || res.Semester_Title == "6th Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Fsix.png?alt=media&token=02053f62-4fef-47b8-9fbe-35c4c3cb3465")
            }else if(res.Semester_Title == "Seventh Semester" || res.Semester_Title == "7th Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Fseven.png?alt=media&token=501bd297-0ec2-4e28-80f4-f752b758220d")
            }else if(res.Semester_Title == "Eighth Semester" || res.Semester_Title == "8th Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Feight.png?alt=media&token=062958b2-3be2-40d5-a893-6cc60d3fa534")
            }else if(res.Semester_Title == "Ninth Semester" || res.Semester_Title == "9th Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Fnine.png?alt=media&token=ba9f4eac-9201-4aaa-b5e8-11f240af5bfe")
            }else if(res.Semester_Title == "Tenth Semester" || res.Semester_Title == "10th Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Ften.png?alt=media&token=86b01dc5-2769-43d1-97ec-cdc69d9d55df")
            }else if(res.Semester_Title == "Eleventh Semester" || res.Semester_Title == "11th Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Feleven.png?alt=media&token=24b69bde-e387-4b2d-a959-38c73f720dfe")
            }else if(res.Semester_Title == "Twelfth Semester" || res.Semester_Title == "12th Semester"){
                loadImages("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/app%20image%2Fsemesters%2Ftwelve.png?alt=media&token=14e8c635-1749-4ee8-bff0-b542e20cdb74")
            }
        }

        private fun loadImages(filePath:String){
            Picasso.get().load(filePath).networkPolicy(NetworkPolicy.OFFLINE).into(semesterImage, object: com.squareup.picasso.Callback{
                override fun onSuccess() {
                }
                override fun onError(e: Exception?) {
                    Picasso.get().load(filePath).into(semesterImage)
                }
            })
        }
    }
}