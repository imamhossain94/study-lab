package com.newage.studlab.Adapter

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.BloodModel.Blood
import com.newage.studlab.Model.NotificationModel
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationRecyclerViewAdapter(val context: Context?, private var donorList: ArrayList<NotificationModel>, val clickDeleteListener: (NotificationModel) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(donorList[position])

        holder.deleteButton.setOnClickListener {
            clickDeleteListener(donorList[position])
        }
    }

    override fun getItemCount(): Int {
        return donorList.size
    }

    fun filterNotificationList(filterName:ArrayList<NotificationModel>){
        this.donorList = filterName
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val deleteButton:ImageView = itemView.notification_delete_button


        @SuppressLint("SetTextI18n")
        fun bindItems(notification: NotificationModel) {
            val title = itemView.notification_title
            val date = itemView.notification_date
            val icon = itemView.notification_icon
            val messages:TextView = itemView.notification_messages

            title.text = notification.title
            date.text = notification.arrivalTime
            messages.text = notification.message

            when (notification.notificationType) {
                "blood" -> {
                    icon.setImageResource(R.drawable.blood_drop_red)
                }
                "library" -> {
                    icon.setImageResource(R.drawable.book_icon)
                }
                else -> {
                    icon.setImageResource(R.drawable.intro_notification)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                messages.justificationMode = JUSTIFICATION_MODE_INTER_WORD
            }

        }
    }
}