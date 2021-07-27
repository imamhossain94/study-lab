package com.newage.studlab.Adapter.LibraryAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.StudLibrary.Sheet
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_sheets.view.*
import java.lang.Exception


class SheetRecyclerViewAdapter(val context:Context?, var sheetList: ArrayList<Sheet>, val downloadClick: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_sheets, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(sheetList[position])

        holder.downloadIcon.setOnClickListener {
            downloadClick(sheetList[position].sheetUri)
        }

    }

    override fun getItemCount(): Int {
        return sheetList.size
    }

    fun filterSheetList(filteredList: ArrayList<Sheet>) {
        sheetList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val downloadIcon: ImageView = itemView.sheets_download_icon

        @SuppressLint("SetTextI18n")
        fun bindItems(sheet: Sheet) {
            val sheetsIcon = itemView.sheets_file_icon
            val sheetsCourseCode = itemView.sheets_course_code
            val sheetsTitleGivenBy = itemView.sheet_by_title
            val sheetsUploadBy = itemView.sheets_upload_by_date
            val sheetFileSize = itemView.sheets_file_size
            val sheetDownloadTime= itemView.sheets_download_time

            sheetsCourseCode.text = "${sheet.courCode} (${sheet.chapter})"
            sheetsTitleGivenBy.text = "${sheet.sheetTitle} given by ${sheet.sheetBy}"
            sheetsUploadBy.text = "Upload By ${sheet.uploadBy} [${sheet.uploadDate}]"
            sheetFileSize.text = sheet.sheetFileSize
            sheetDownloadTime.text = sheet.sheetDownloadTime

            if(sheet.sheetIcon == "PDF"){
                sheetsIcon.setImageResource(R.drawable.pdf_icon_svg)
            }else if(sheet.sheetIcon == "DOC" || sheet.sheetIcon == "DOCX"){
                sheetsIcon.setImageResource(R.drawable.doc_icon_svg)
            }

        }
    }
}

