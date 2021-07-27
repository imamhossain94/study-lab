package com.newage.studlab.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Model.StudLibrary.Book
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_book.view.*
import java.lang.Exception


class BookRecyclerViewAdapter(val context:Context, var bookList: ArrayList<Book>, val downloadClick: (Book) -> Unit, val reactClick: (Book) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(bookList[position])

        holder.downloadButton.setOnClickListener {
            downloadClick(bookList[position])
        }

        holder.reactButton.setOnClickListener{
            reactClick(bookList[position])
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    fun filterBookList(filteredList: ArrayList<Book>) {
        bookList = filteredList
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val downloadButton: ImageView = itemView.book_download_image_button
        val reactButton: ImageView = itemView.react_button

        @SuppressLint("SetTextI18n")
        fun bindItems(book: Book) {
            val title = itemView.book_title_edition
            val writerName = itemView.book_pdf_writer_name
            val description = itemView.book_course_code_description
            val bookRating = itemView.book_rating
            val bookPdfSize = itemView.book_pdf_size
            val bookDownloadTime = itemView.book_download_time
            val bookLoved = itemView.book_heart_rate
            val bookCoverImage = itemView.book_cover_image

            title.text = book.bookTitleEdition
            writerName.text = "By ${book.bookWriterName}"
            description.text = book.bookDescription
            bookRating.rating = book.bookRating.toFloat()
            bookPdfSize.text = "${book.bookPdfSize} MB"
            bookDownloadTime.text = book.bookTotalDownload
            bookLoved.text = book.bookTotalLoved

            //reactButton.isEnabled = !book.bookLovedBY.contains(currentUser!!.user_id)

            if(book.bookLovedBY.contains(currentUser!!.user_id)){
                reactButton.setImageResource(R.drawable.heart_icon_filed_svg)
            }else{
                reactButton.setImageResource(R.drawable.heart_icon_svg)
            }


            if(book.bookCoverImage.isNotEmpty())
            Picasso.get().load(book.bookCoverImage).networkPolicy(NetworkPolicy.OFFLINE).into(bookCoverImage, object: com.squareup.picasso.Callback{
                override fun onSuccess() { }
                override fun onError(e: Exception?) {
                    Picasso.get().load(book.bookCoverImage).into(bookCoverImage)
                }
            })
        }
    }

}