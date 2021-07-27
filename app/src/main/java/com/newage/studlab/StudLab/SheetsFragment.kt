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
import com.newage.studlab.Adapter.LibraryAdapter.SheetRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Model.StudLibrary.Sheet
import com.newage.studlab.Model.StudLibrary.Suggestion
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import kotlin.collections.ArrayList

class SheetsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sheets, container, false)
    }

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: SheetRecyclerViewAdapter

    var sheetList =ArrayList<Sheet>()
    var filteredList = ArrayList<Sheet>()

    @SuppressLint("SimpleDateFormat")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            recyclerView = it.findViewById(R.id.sheets_recycler_view)

        }
        recyclerView.layoutManager = LinearLayoutManager(context)


        if(sheetList.isNotEmpty()){
            filteredList.clear()
            filteredList = sheetList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Sheet>

            adapter = SheetRecyclerViewAdapter(StudLab.appContext, filteredList, downloadClick = {
                downloadSlide(it)
            })
        }else{
            adapter = SheetRecyclerViewAdapter(StudLab.appContext, sheetList, downloadClick = {
                downloadSlide(it)
            })
        }
        recyclerView.adapter = adapter


        loadSheet()
    }


    private fun loadSheet() {
        val ref = FirebaseDatabase.getInstance().getReference("/Library/Sheet")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                sheetList.clear()
                var sheet: Sheet? = null
                for (productSnapshot in dataSnapshot.children) {
                    sheet = productSnapshot.getValue(Sheet::class.java)
                    sheetList.add(sheet!!)
                }

                if(sheetList.isNotEmpty()){
                    filteredList.clear()
                    filteredList = sheetList.filter { it.proCode == StudLabActivity.program && it.semName == StudLabActivity.semesterName && it.courCode == StudLabActivity.coursCode}  as ArrayList<Sheet>
                    adapter.filterSheetList(filteredList)
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

        val data = filteredList.filter { it.sheetUri == url }

        val sheet = Sheet(data[0].proCode,data[0].semName, data[0].courCode,
            data[0].chapter,data[0].sheetTitle,data[0].sheetBy,data[0].sheetIcon,data[0].sheetUri,
            data[0].uploadBy, data[0].uploadDate,data[0].sheetFileSize,(data[0].sheetDownloadTime.toInt() + 1).toString())

        val name = "${data[0].proCode} ${data[0].semName} ${data[0].courCode} ${data[0].sheetTitle.replace("."," ")} ${data[0].chapter} ${data[0].sheetBy} ${data[0].uploadBy}".toLowerCase()

        val ref = FirebaseDatabase.getInstance().getReference("/Library/Sheet/$name")
        ref.setValue(sheet)
            .addOnSuccessListener {
                Toasty.success(StudLab.appContext, "Downloading...", Toast.LENGTH_SHORT, true).show()
            }
            .addOnFailureListener{
                Toasty.error(StudLab.appContext, "Failed", Toast.LENGTH_SHORT, true).show()
            }
    }
}