package com.newage.studlab.Adapter.ProfileAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.ProfileModel.ProfileImage
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_profile_image.view.*
import java.lang.Exception


class ProfilePictureRecyclerViewAdapter(val context:Context, var imageList: ArrayList<ProfileImage>, val clickListener: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_profile_image, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(imageList[position])

        holder.button.setOnClickListener {
            clickListener(imageList[position].image)
        }

    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun filterAvatarList(filteredList: ArrayList<ProfileImage>){
        this.imageList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button = itemView.image_button

        fun bindItems(profile: ProfileImage) {
            val image = itemView.image


            Picasso.get().load(profile.image).networkPolicy(NetworkPolicy.OFFLINE).into(image, object: com.squareup.picasso.Callback{
                override fun onSuccess() { }
                override fun onError(e: Exception?) {
                    Picasso.get().load(profile.image).into(image)
                }
            })

        }
    }

}