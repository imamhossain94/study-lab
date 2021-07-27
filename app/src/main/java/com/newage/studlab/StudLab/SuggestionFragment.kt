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
import com.newage.studlab.Adapter.LibraryAdapter.SuggestionRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Model.StudLibrary.Suggestion
import com.newage.studlab.R
import es.dmoral.toasty.Toasty


class SuggestionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_suggestion, container, false)
    }

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: SuggestionRecyclerViewAdapter

    var suggestList =ArrayList<Suggestion>()
    var filteredList = ArrayList<Suggestion>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            recyclerView = it.findViewById(R.id.suggestion_recycler_view)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)

        if(suggestList.isNotEmpty()){
            filteredList.clear()
            filteredList = suggestList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Suggestion>

            adapter = SuggestionRecyclerViewAdapter(appContext, filteredList, downloadClick = {
                downloadSlide(it)
            })
        }else{
            adapter = SuggestionRecyclerViewAdapter(appContext, suggestList, downloadClick = {
                downloadSlide(it)
            })
        }
        recyclerView.adapter = adapter

        loadSheet()
    }

    private fun loadSheet() {
        val ref = FirebaseDatabase.getInstance().getReference("/Library/Suggestion")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                suggestList.clear()
                var suggest: Suggestion? = null
                for (productSnapshot in dataSnapshot.children) {
                    suggest = productSnapshot.getValue(Suggestion::class.java)
                    suggestList.add(suggest!!)
                }

                if(suggestList.isNotEmpty()){
                    filteredList.clear()
                    filteredList = suggestList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Suggestion>
                    adapter.filterSuggestList(filteredList)
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

        val data = filteredList.filter { it.fileUri == url }

        val suggest = Suggestion(data[0].proCode,data[0].semName, data[0].courCode,
            data[0].suggestionBy, data[0].suggestionFor, data[0].suggestionDate,
            data[0].fileIcon, data[0].fileUri, data[0].uploadBy, data[0].uploadDate,
            data[0].fileSize, (data[0].fileDownloadTime.toInt() + 1).toString() )

        val name = "${data[0].proCode} ${data[0].semName} ${data[0].courCode} ${data[0].suggestionBy} ${data[0].suggestionFor}".toLowerCase()

        val ref = FirebaseDatabase.getInstance().getReference("/Library/Suggestion/$name")
        ref.setValue(suggest)
            .addOnSuccessListener {
                Toasty.success(appContext, "Downloading...", Toast.LENGTH_SHORT, true).show()
            }
            .addOnFailureListener{
                Toasty.error(appContext, "Failed", Toast.LENGTH_SHORT, true).show()
            }
    }



}