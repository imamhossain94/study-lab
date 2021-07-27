package com.newage.studlab.Adapter.BubtAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Model.BubtModel.AllNotice
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_notic.view.*
import java.util.*
import kotlin.collections.ArrayList


class NoticeRecyclerViewAdapter(val context: Context?, private var noticeList: ArrayList<AllNotice>, val clickDeleteListener: (AllNotice) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_notic, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(noticeList[position])

        holder.itemButton.setOnClickListener {
            clickDeleteListener(noticeList[position])
        }

        holder.viewButton.setOnClickListener {
            clickDeleteListener(noticeList[position])
        }
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    fun filterNoticeList(filterName:ArrayList<AllNotice>){
        this.noticeList = filterName
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemButton:Button = itemView.item_button
        val viewButton:Button = itemView.view_button

        @SuppressLint("SetTextI18n")
        fun bindItems(notice: AllNotice) {

            val noticeBackColor = itemView.notice_back_color
            val title = itemView.notice_title
            val date = itemView.published_date

            title.text = notice.title
            date.text = notice.published_on



            val calendar = Calendar.getInstance()

            val dd =  calendar.time.toString().substring(8,10).toLowerCase(Locale.getDefault()).trim()
            val mm =  calendar.time.toString().substring(4,7).toLowerCase(Locale.getDefault()).trim()
            val yyyy =  calendar.time.toString().substring(30,34).toLowerCase(Locale.getDefault()).trim()

            if(notice.published_on.toLowerCase(Locale.getDefault()).trim() == "$dd $mm $yyyy".toLowerCase(Locale.getDefault()).trim()){
                noticeBackColor.setCardBackgroundColor(Color.parseColor("#02BE50"))
            }else{
                noticeBackColor.setCardBackgroundColor(Color.parseColor("#304FFE"))
            }
            //Toast.makeText(appContext, notice.published_on.toLowerCase(Locale.getDefault()).trim(),Toast.LENGTH_LONG).show()
            //Toast.makeText(appContext,"$dd $mm $yyyy".toLowerCase(Locale.getDefault()).trim(),Toast.LENGTH_LONG).show()
        }
    }
}