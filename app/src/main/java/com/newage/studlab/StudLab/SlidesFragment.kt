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
import com.newage.studlab.Adapter.LibraryAdapter.SlideRecyclerViewAdapter
import com.newage.studlab.Adapter.LibraryAdapter.SuggestionRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Model.StudLibrary.Slide
import com.newage.studlab.Model.StudLibrary.Suggestion
import com.newage.studlab.R
import es.dmoral.toasty.Toasty

class SlidesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_slides, container, false)
    }

    lateinit var recyclerView:RecyclerView
    lateinit var adapter: SlideRecyclerViewAdapter

    val slideList = ArrayList<Slide>()
    var filteredList = ArrayList<Slide>()


    @SuppressLint("SimpleDateFormat")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            recyclerView = it.findViewById(R.id.slide_recycler_view)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)

        if(slideList.isNotEmpty()){
            filteredList.clear()
            filteredList = slideList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Slide>

            adapter = SlideRecyclerViewAdapter(StudLab.appContext, filteredList, downloadClick = {
                downloadSlide(it)
            })
        }else{
            adapter = SlideRecyclerViewAdapter(StudLab.appContext, slideList, downloadClick = {
                downloadSlide(it)
            })
        }
        recyclerView.adapter = adapter



        loadSlide()
    }


    private fun loadSlide() {

        val ref = FirebaseDatabase.getInstance().getReference("/Library/Slide")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                slideList.clear()
                var slide: Slide? = null
                for (productSnapshot in dataSnapshot.children) {
                    slide = productSnapshot.getValue(Slide::class.java)
                    slideList.add(slide!!)
                }

                if(slideList.isNotEmpty()){
                    filteredList.clear()
                    filteredList = slideList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Slide>
                    adapter.filterSlideList(filteredList)
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

        val data = filteredList.filter { it.slideUri == url }

        val slide = Slide(
            data[0].proCode,
            data[0].semName,
            data[0].courCode,
            data[0].chapter,
            data[0].slideTitle,
            data[0].slideUri,
            data[0].slideIcon,
            data[0].uploadBy,
            data[0].uploadDate,
            data[0].slideFileSize,
            (data[0].slideDownloadTime.toInt() + 1).toString())

        val name = "${data[0].proCode} ${data[0].semName} ${data[0].courCode} ${data[0].slideTitle.replace("."," ")} ${data[0].chapter}".toLowerCase()

        val ref = FirebaseDatabase.getInstance().getReference("/Library/Slide/$name")
        ref.setValue(slide)
            .addOnSuccessListener {
                Toasty.success(StudLab.appContext, "Downloading...", Toast.LENGTH_SHORT, true).show()
            }
            .addOnFailureListener{
                Toasty.error(StudLab.appContext, "Failed", Toast.LENGTH_SHORT, true).show()
            }
    }


}