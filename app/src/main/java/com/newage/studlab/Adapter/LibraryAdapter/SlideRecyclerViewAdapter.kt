package com.newage.studlab.Adapter.LibraryAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.StudLibrary.Slide
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_slide.view.*
import java.lang.Exception


class SlideRecyclerViewAdapter(val context:Context?, var slideList: ArrayList<Slide>, val downloadClick: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_slide, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(slideList[position])

        holder.downloadIcon.setOnClickListener {
            downloadClick(slideList[position].slideUri)
        }
    }

    override fun getItemCount(): Int {
        return slideList.size
    }

    fun filterSlideList(filteredList: ArrayList<Slide>) {
        slideList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val downloadIcon: ImageView = itemView.slide_download_icon

        @SuppressLint("SetTextI18n")
        fun bindItems(slide: Slide) {
            val slideIcon :ImageView = itemView.slid_file_icon
            val slideCourseCode = itemView.slide_course_code
            val slideFileTitle = itemView.slide_file_title
            val slideFileSize =itemView.slide_file_size
            val slideUploadBy = itemView.slide_upload_by_date
            val slideDownloadTime = itemView.slide_download_time

            //Picasso.get().load(slide.slideIcon).into(slideIcon)
            slideCourseCode.text = "${slide.courCode} (${slide.chapter})"
            slideFileTitle.text = slide.slideTitle
            slideFileSize.text = "${slide.slideFileSize} MB"
            slideUploadBy.text = "Upload By ${slide.uploadBy} [${slide.uploadDate}]"
            slideDownloadTime.text = slide.slideDownloadTime

            if(slide.slideIcon == "PDF"){
                slideIcon.setImageResource(R.drawable.pdf_icon_svg)
            }else if(slide.slideIcon == "PPT" || slide.slideIcon == "PPTX"){
                slideIcon.setImageResource(R.drawable.ppt_icon_svg)
            }
        }
    }

}