package com.newage.studlab.Adapter.BloodAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.BloodModel.Blood
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_blood.view.*

class BloodDonorRecyclerViewAdapter(val context: Context?, private var donorList: ArrayList<Blood>,
                                    val clickListener: (Blood) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_blood, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(donorList[position])

        holder.bloodButton.setOnClickListener {
            clickListener(donorList[position])
        }
    }

    override fun getItemCount(): Int {
        return donorList.size
    }

    fun filterBloodDonorList(filterName:ArrayList<Blood>){
        this.donorList = filterName
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val bloodButton:Button = itemView.item_button

        @SuppressLint("SetTextI18n")
        fun bindItems(donor: Blood) {
            val userBloodGroup = itemView.blood_group
            val userName = itemView.blood_user_name
            val userId = itemView.blood_user_id
            val bloodDate = itemView.blood_date

            userBloodGroup.text = donor.bloodGroup
            userName.text = donor.bloodName
            userId.text = donor.bloodId

            if(donor.bloodLastDonateDate.isNotEmpty() || donor.bloodLastDonateDate != "" || donor.bloodLastDonateDate != "null"){
                bloodDate.text = "Last donate on ${donor.bloodLastDonateDate}"
            }else{
                bloodDate.text = "No donation record"
            }

        }
    }
}