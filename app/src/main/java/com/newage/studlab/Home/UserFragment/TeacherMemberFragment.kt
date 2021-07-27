package com.newage.studlab.Home.UserFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import com.newage.studlab.Adapter.TeacherMemberRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.facultyMemberList
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Home.UserFragment.TeacherProgramFragment.Companion.programCode
import com.newage.studlab.Model.UserModel.Teacher
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.Plugins.StudLabAssistant.Companion.pCodeToHint
import com.newage.studlab.R


class TeacherMemberFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_faculty_member, container, false)
    }

    lateinit var backButton: ImageView
    lateinit var resNotFound: TextView
    lateinit var downButton:ImageView
    lateinit var sortBySpinner: Spinner
    lateinit var searchBox: EditText
    lateinit var adapter: TeacherMemberRecyclerViewAdapter

    lateinit var recyclerView: RecyclerView
    lateinit var sortBy:Array<String>
    private var newMemberList = arrayListOf<Teacher>()
    private var defaultMemberList = arrayListOf<Teacher>()

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
        sortBy = resources.getStringArray(R.array.sort_by_7)

        fragmentName = "faculty"
        fragmentPosition = 0.0

        if (facultyMemberList.isNotEmpty()){
            for(i in 0 until  facultyMemberList.size){
                if(facultyMemberList[i].facultyDept.contains(pCodeToHint(programCode))||
                    facultyMemberList[i].facultyName.contains(pCodeToHint(programCode))){
                    newMemberList.add(facultyMemberList[i])
                    defaultMemberList.add(facultyMemberList[i])
                }
            }

            adapter = TeacherMemberRecyclerViewAdapter(context, newMemberList,clickListenerProfile= {
                memberProfileClickListener(it)
            }, clickListenerEmail = {
                memberEmailClickListener(it)
            })
        }else{
            adapter = TeacherMemberRecyclerViewAdapter(context, newMemberList,clickListenerProfile= {
                memberProfileClickListener(it)
            }, clickListenerEmail = {
                memberEmailClickListener(it)
            })
        }

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

        if(StudLab.programList.size != 0)
            Handler().postDelayed({
                handleSpinner()
            },600)
    }

    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, TeacherProgramFragment())
            transaction.commit()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        val filteredList: ArrayList<Teacher> = ArrayList()
        for (item in newMemberList) {
            if (item.facultyEmp.toLowerCase().contains(text.toLowerCase())
                || item.facultyRoomNo.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter.filterMyResultList(filteredList)
    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.sort_by_7,R.layout.spinner_item_transparent)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sortBySpinner.adapter = bloodSpinnerAdapter

        sortBySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val order = sortBy[position]

                if(order == "Sort by designation"){
                    adapter.filterMyResultList(defaultMemberList)
                }else if(order == "Sort by name"){
                    newMemberList.sortBy { it.facultyEmp }
                    adapter.filterMyResultList(newMemberList)
                }else if(order == "Sort by code"){
                    newMemberList.sortBy { it.facultyFacultyCode }
                    adapter.filterMyResultList(newMemberList)
                }else if(order == "Sort by room no"){
                    newMemberList.sortByDescending { StudLabAssistant().emptyStringToInt(it.facultyRoomNo) }
                    adapter.filterMyResultList(newMemberList)
                }

            }
        }
    }

    private fun memberProfileClickListener(member:Teacher){
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(member.facultyProfileLink))
        startActivity(i)
    }

    private fun memberEmailClickListener(member:Teacher){
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + member.facultyEmail))
        startActivity(i)
    }
}