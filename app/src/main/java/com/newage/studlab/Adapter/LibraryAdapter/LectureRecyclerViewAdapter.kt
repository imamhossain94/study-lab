package com.newage.studlab.Adapter.LibraryAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.StudLibrary.Lecture
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_lecture.view.*
import java.lang.Exception


class LectureRecyclerViewAdapter(val context:Context?, var lectureList: ArrayList<Lecture>, val downloadClick: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_lecture, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(lectureList[position])

        (holder as MyViewHolder).downloadIcon.setOnClickListener {
            downloadClick(lectureList[position].lectureUri)
        }
    }

    override fun getItemCount(): Int {
        return lectureList.size
    }


    fun filterLectureList(filteredList: ArrayList<Lecture>) {
        lectureList = filteredList
        notifyDataSetChanged()
    }



    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val downloadIcon = itemView.lecture_download_icon

        @SuppressLint("SetTextI18n")
        fun bindItems(lecture: Lecture) {
            val lectureIcon = itemView.lecture_file_icon
            val lectureCourseCode = itemView.lecture_course_code
            val lectureByDate = itemView.lecture_by_date_title
            val lectureUploadBy = itemView.lecture_upload_by_date
            val lectureFileSize = itemView.lecture_file_Size
            val lectureDownloadTime = itemView.lecture_download_time

            lectureCourseCode.text = "${lecture.courCode} (${lecture.chapter}, ${lecture.lectureNo})"
            lectureByDate.text = "${lecture.lectureTitle} by ${lecture.lectureBy} on ${lecture.lectureDate}"
            lectureUploadBy.text = "Upload By ${lecture.uploadBy} [${lecture.uploadDate}]"
            lectureFileSize.text = lecture.lectureFileSize
            lectureDownloadTime.text = lecture.lectureDownloadTime


            if(lecture.lectureIcon == "PDF"){
                lectureIcon.setImageResource(R.drawable.pdf_icon_svg)
            }else if(lecture.lectureIcon == "DOC" || lecture.lectureIcon == "DOCX"){
                lectureIcon.setImageResource(R.drawable.doc_icon_svg)
            }else if(lecture.lectureIcon == "ZIP"){
                lectureIcon.setImageResource(R.drawable.zip_icon_svg)
            }

        }
    }

}