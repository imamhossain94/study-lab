package com.newage.studlab.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.Results
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.Plugins.StudLabAssistant.Companion.pointToGrade
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user.view.*
import java.lang.Exception

class StudentUserRecyclerViewAdapter(val context: Context?, var resultList: ArrayList<Users>, val clickListener: (Users) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user, parent, false))
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(resultList[position])

        holder.itemButton.setOnClickListener {
            clickListener(resultList[position])
        }

    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    fun filterMyResultList(filteredList: ArrayList<Users>) {
        resultList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemButton = itemView.item_button
        val userImage = itemView.user_image
        @SuppressLint("SetTextI18n")
        fun bindItems(usr: Users) {
            val userName = itemView.user_name
            val userId = itemView.user_id
            val userPost = itemView.user_post


            userName.text = usr.user_name
            userId.text = usr.user_id
            userPost.text = usr.user_prog_or_dept

            loadUserImage(usr.user_image)

        }

        private fun loadUserImage(path:String){
            Picasso.get().load(path).networkPolicy(NetworkPolicy.OFFLINE).into(userImage, object: com.squareup.picasso.Callback{
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    Picasso.get().load(path).into(userImage)
                }

            })
        }
    }

}