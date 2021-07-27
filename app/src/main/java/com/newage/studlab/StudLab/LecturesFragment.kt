package com.newage.studlab.StudLab

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.studlab.Adapter.LibraryAdapter.LectureRecyclerViewAdapter
import com.newage.studlab.Adapter.LibraryAdapter.SlideRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Model.StudLibrary.Lecture
import com.newage.studlab.Model.StudLibrary.Slide
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import kotlin.collections.ArrayList

class LecturesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lectures, container, false)
    }

    lateinit var recyclerView:RecyclerView
    lateinit var adapter: LectureRecyclerViewAdapter

    val lectureList = ArrayList<Lecture>()
    var filteredList = ArrayList<Lecture>()

    @SuppressLint("SimpleDateFormat")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            recyclerView = it.findViewById(R.id.lecture_recycler_view)

        }
        recyclerView.layoutManager = LinearLayoutManager(context)


        if(lectureList.isNotEmpty()){
            filteredList.clear()
            filteredList = lectureList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Lecture>

            adapter = LectureRecyclerViewAdapter(StudLab.appContext, filteredList, downloadClick = {
                downloadSlide(it)
            })
        }else{
            adapter = LectureRecyclerViewAdapter(StudLab.appContext, lectureList, downloadClick = {
                downloadSlide(it)
            })
        }
        recyclerView.adapter = adapter

        loadLecture()
    }



    private fun loadLecture() {
        val ref = FirebaseDatabase.getInstance().getReference("/Library/Lecture")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                lectureList.clear()
                var lecture:Lecture? = null
                for (productSnapshot in dataSnapshot.children) {
                    lecture = productSnapshot.getValue(Lecture::class.java)
                    lectureList.add(lecture!!)
                }

                if(lectureList.isNotEmpty()){
                    filteredList.clear()
                    filteredList = lectureList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Lecture>
                    adapter.filterLectureList(filteredList)
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })
    }

    @SuppressLint("DefaultLocale")
    private fun downloadSlide(url: String) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)

        val data = filteredList.filter { it.lectureUri == url }

        val lecture = Lecture(
            data[0].proCode,
            data[0].semName,
            data[0].courCode,
            data[0].lectureNo,
            data[0].chapter,
            data[0].lectureTitle,
            data[0].lectureBy,
            data[0].lectureIcon,
            data[0].lectureUri,
            data[0].uploadBy,
            data[0].uploadDate,
            data[0].lectureDate,
            data[0].lectureFileSize,
            (data[0].lectureDownloadTime.toInt() + 1).toString())

        val name = "${data[0].proCode} ${data[0].semName} ${data[0].courCode} ${data[0].lectureTitle.replace("."," ")} ${data[0].chapter} ${data[0].lectureBy} ${data[0].uploadBy}".toLowerCase()

        val ref = FirebaseDatabase.getInstance().getReference("/Library/Lecture/$name")
        ref.setValue(lecture)
            .addOnSuccessListener {
                Toasty.success(StudLab.appContext, "Downloading...", Toast.LENGTH_SHORT, true).show()
            }
            .addOnFailureListener{
                Toasty.error(StudLab.appContext, "Failed", Toast.LENGTH_SHORT, true).show()
            }
    }
}