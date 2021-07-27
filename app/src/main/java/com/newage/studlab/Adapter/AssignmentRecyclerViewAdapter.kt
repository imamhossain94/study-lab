package com.newage.studlab.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.AssignmentModel
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_assignment.view.*

class AssignmentRecyclerViewAdapter(val context: Context?, private var donorList: ArrayList<AssignmentModel>,
                                    val clickEditListener: (AssignmentModel) -> Unit,
                                    private val clickSaveListener: (AssignmentModel) -> Unit,
                                    val clickDeleteListener: (AssignmentModel) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_assignment, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(donorList[position])

        holder.editButton.setOnClickListener {
            clickEditListener(donorList[position])
        }

        holder.saveButton.setOnClickListener {
            clickSaveListener(donorList[position])
        }

        holder.deleteButton.setOnClickListener {
            clickDeleteListener(donorList[position])
        }
    }

    override fun getItemCount(): Int {
        return donorList.size
    }

    fun filterAssignmentList(filterName:ArrayList<AssignmentModel>){
        this.donorList = filterName
        notifyDataSetChanged()
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val editButton:ImageView = itemView.assignment_edit_button
        val saveButton:ImageView = itemView.assignment_save_button
        val deleteButton:ImageView = itemView.assignment_delete_button

        @SuppressLint("SetTextI18n")
        fun bindItems(assignment: AssignmentModel) {
            val title = itemView.assignment_title
            val date = itemView.assignment_sub_date
            val message = itemView.assignment_text_message

            val imageOne:ImageView = itemView.assignment_image_1
            val imageTwo:ImageView = itemView.assignment_image_2
            val imageThree:ImageView = itemView.assignment_image_3

            title.text = assignment.assignmentTitle
            date.text = assignment.submissionDate
            message.text = assignment.assignmentText

            if(assignment.imageOne.isNotEmpty() && assignment.imageTwo.isNotEmpty() && assignment.imageThree.isNotEmpty()){
                imageOne.visibility = View.VISIBLE
                imageTwo.visibility = View.VISIBLE
                imageThree.visibility = View.VISIBLE

                imageOne.setImageBitmap(base64toBitmap(assignment.imageOne))
                imageTwo.setImageBitmap(base64toBitmap(assignment.imageTwo))
                imageThree.setImageBitmap(base64toBitmap(assignment.imageThree))

            }else if(assignment.imageOne.isNotEmpty() && assignment.imageTwo.isNotEmpty()){
                imageOne.visibility = View.VISIBLE
                imageTwo.visibility = View.VISIBLE
                imageThree.visibility = View.INVISIBLE

                imageOne.setImageBitmap(base64toBitmap(assignment.imageOne))
                imageTwo.setImageBitmap(base64toBitmap(assignment.imageTwo))

            }else if(assignment.imageOne.isNotEmpty()){
                imageOne.visibility = View.VISIBLE
                imageTwo.visibility = View.INVISIBLE
                imageThree.visibility = View.INVISIBLE

                imageOne.setImageBitmap(base64toBitmap(assignment.imageOne))

            }


        }


        private fun base64toBitmap(imageString:String):Bitmap{
            val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }

    }
}