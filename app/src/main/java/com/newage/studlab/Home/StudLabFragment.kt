package com.newage.studlab.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.studlab.Adapter.ResultAdapter.UniversityResultProgramRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.programList
import com.newage.studlab.Home.BloodFragment.BloodHomeFragment
import com.newage.studlab.Model.BloodModel.Blood
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.R
import com.newage.studlab.StudLab.StudLabActivity

class StudLabFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_studlab, container, false)
    }

    lateinit var totalDepartment:TextView
    lateinit var totalBook:TextView
    lateinit var totalSlide:TextView
    lateinit var totalLecture:TextView
    lateinit var totalSheet:TextView
    lateinit var totalSuggestion:TextView

    lateinit var recyclerView:RecyclerView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {

            totalDepartment = it.findViewById(R.id.lab_total_department)
            totalBook = it.findViewById(R.id.lab_total_book)
            totalSlide = it.findViewById(R.id.lab_total_slide)
            totalLecture = it.findViewById(R.id.lab_total_lecture)
            totalSheet = it.findViewById(R.id.lab_total_sheet)
            totalSuggestion = it.findViewById(R.id.lab_total_suggetion)

           recyclerView = it.findViewById(R.id.prog_recycler_view)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)


        recyclerView.adapter = UniversityResultProgramRecyclerViewAdapter(context,programList, clickListener = {
            programClickListener(it)
        })

        totalDepartment.text = programList.size.toString()
    }


    private fun programClickListener(position: String) {
        StudLabAssistant.selectedProgram = position
        startActivity(Intent(appContext,StudLabActivity::class.java))
    }


    private fun getLibraryInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("/Library")

        ref.child("Book").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                totalBook.text = p0.childrenCount.toString()
            }
        })

        ref.child("Slide").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                totalSlide.text = p0.childrenCount.toString()
            }
        })

        ref.child("Sheet").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                totalSheet.text = p0.childrenCount.toString()
            }
        })

        ref.child("Lecture").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                totalLecture.text = p0.childrenCount.toString()
            }
        })

        ref.child("Suggestion").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                totalSuggestion.text = p0.childrenCount.toString()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        getLibraryInfo()
    }

}