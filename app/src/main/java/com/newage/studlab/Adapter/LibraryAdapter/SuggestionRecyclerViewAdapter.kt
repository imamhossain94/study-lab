package com.newage.studlab.Adapter.LibraryAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.StudLibrary.Suggestion
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_suggestion.view.*


class SuggestionRecyclerViewAdapter(val context:Context?, var suggestionList: ArrayList<Suggestion>, val downloadClick: (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(suggestionList[position])

        (holder as MyViewHolder).downloadIcon.setOnClickListener {
            downloadClick(suggestionList[position].fileUri)
        }

    }

    override fun getItemCount(): Int {
        return suggestionList.size
    }

    fun filterSuggestList(filteredList: ArrayList<Suggestion>) {
        suggestionList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val downloadIcon: ImageView = itemView.suggestion_download_icon

        @SuppressLint("SetTextI18n")
        fun bindItems(suggest: Suggestion) {
            val fileIcon = itemView.suggestion_icon
            val courseCode = itemView.suggestion_title
            val title = itemView.suggestion_by
            val uploadBy = itemView.suggestion_upload_by
            val fileSize = itemView.suggestion_file_size
            val downloadTime= itemView.suggestion_download_time

            courseCode.text = "${suggest.courCode} (${suggest.suggestionFor}) Suggestion"
            title.text = "By ${suggest.suggestionBy} on ${suggest.suggestionDate}"
            uploadBy.text = "Upload By ${suggest.uploadBy} [${suggest.uploadDate}]"
            fileSize.text = "${suggest.fileSize} MB"
            downloadTime.text = suggest.fileDownloadTime

            if(suggest.fileIcon == "PDF"){
                fileIcon.setImageResource(R.drawable.pdf_icon_svg)
            }else if(suggest.fileIcon == "DOC" || suggest.fileIcon == "DOCX"){
                fileIcon.setImageResource(R.drawable.doc_icon_svg)
            }

        }
    }
}

