package com.newage.studlab.Home.UserFragment

import HomeFragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.UserAdapter.TeacherUserProgramRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.newProgramList
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.Program
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.R

class TeacherProgramFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_faculty_program, container, false)
    }

    companion object{
        var programCode:String = ""
    }

    lateinit var backButton: ImageView
    lateinit var resNotFound: TextView
    lateinit var downButton:ImageView
    lateinit var sortBySpinner: Spinner
    lateinit var searchBox: EditText
    lateinit var adapter: TeacherUserProgramRecyclerViewAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var sortBy:Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            downButton = it.findViewById(R.id.fragment_sort_by_button)
            sortBySpinner = it.findViewById(R.id.sort_by_spinner)
            backButton = it.findViewById(R.id.fragment_back_button)
            resNotFound = it.findViewById(R.id.fragment_empty)
            searchBox = it.findViewById(R.id.search_box)
            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        sortBy = resources.getStringArray(R.array.sort_by_3_1)

        fragmentName = "faculty"
        fragmentPosition = 0.0

        if(!StudLabAssistant.isAdded){
            for(i in newProgramList.size-1 downTo  0){
                if(newProgramList[i].programCode == "BBA" || newProgramList[i].programCode == "CSE_E" ||
                    newProgramList[i].programCode == "EEE_E" || newProgramList[i].programCode == "TEX_E"){
                    newProgramList.remove(newProgramList[i])
                }
            }
            newProgramList.addAll(StudLabAssistant().bbaProgram())
            StudLabAssistant.isAdded = true
        }

        adapter = TeacherUserProgramRecyclerViewAdapter(context, newProgramList, clickListener = {
            userProgramClickListener(it)
        })
        recyclerView.adapter = adapter

        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int
            ) {}

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })

        allButtonClickingEventListeners()

        if(newProgramList.size != 0)
            Handler().postDelayed({
                handleSpinner()
            },600)
    }

    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        val filteredList: ArrayList<Program> = ArrayList()
        for (item in newProgramList) {
            if (item.programCode.toLowerCase().contains(text.toLowerCase())
                || item.progFaculty.toLowerCase().contains(text.toLowerCase())||
                item.programTitle.contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter.filterMyResultList(filteredList)
    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(appContext,R.array.sort_by_3_1,R.layout.spinner_item_transparent)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sortBySpinner.adapter = bloodSpinnerAdapter

        sortBySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val order = sortBy[position]

                if(order == "Sort by program"){
                    newProgramList.sortBy { it.programCode }
                }else if(order == "Sort by faculty"){
                    newProgramList.sortByDescending { it.facultyNumber.toInt()}
                }
                adapter.filterMyResultList(newProgramList)
            }
        }
    }

    private fun userProgramClickListener(position:String){
        programCode = position
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)
        transaction.replace(R.id.main_home_fragment_container, TeacherMemberFragment())
        transaction.commit()
    }

}