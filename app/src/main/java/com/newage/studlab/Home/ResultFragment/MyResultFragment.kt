package com.newage.studlab.Home.ResultFragment

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
import com.newage.studlab.Adapter.ResultAdapter.MyResultRecyclerViewAdapter
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Model.Results
import com.newage.studlab.Plugins.StudLabAssistant.Companion.ordinalSemesterToText
import com.newage.studlab.Plugins.StudLabGenerate
import com.newage.studlab.R
import es.dmoral.toasty.Toasty


class MyResultFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_result, container, false)
    }

    var programCode:String = ""
    var intakeNo:String = ""

    lateinit var backButton: ImageView
    lateinit var downButton:ImageView
    lateinit var sortBySpinner:Spinner
    lateinit var searchBox:EditText

    lateinit var resNotFound: TextView

    lateinit var recyclerView: RecyclerView
    lateinit var adapter:MyResultRecyclerViewAdapter


    var resList =ArrayList<Results>()
    lateinit var sortBy:Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            downButton = it.findViewById(R.id.fragment_sort_by_button)
            sortBySpinner = it.findViewById(R.id.sort_by_spinner)
            searchBox = it.findViewById(R.id.search_box)
            resNotFound = it.findViewById(R.id.fragment_empty)

            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        sortBy = resources.getStringArray(R.array.sort_by_0)

        fragmentName = "myResult"

        allbuttonClickingEventListeners()

        resList = StudLabGenerate().loadMyResult()

        if(resList.size != 0){
            adapter = MyResultRecyclerViewAdapter(context, resList, clickListener = {
                myResultClickListener(it)
            })
        }else{
            adapter = MyResultRecyclerViewAdapter(context, resList, clickListener = {
                myResultClickListener(it)
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

        Handler().postDelayed({
            handleSpinner()
        },600)
    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        val filteredList: ArrayList<Results> = ArrayList()
        for (item in resList) {
            if (ordinalSemesterToText(item.Semester_Title).toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        adapter.filterMyResultList(filteredList)
    }

    private fun handleSpinner(){
        val bloodSpinnerAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.sort_by_0,R.layout.spinner_item_transparent)
        bloodSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sortBySpinner.adapter = bloodSpinnerAdapter

        sortBySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val order = sortBy[position]

                if(order == "Sort by semester"){
                    resList.sortBy { it.Semester_Title }
                }else if(order == "Sort by cgpa"){
                    resList.sortBy { it.Student_Cgpa}
                }else if(order == "Sort by sgpa"){
                    resList.sortBy { it.Student_Sgpa }
                }else if(order == "Sort by name"){
                    resList.sortBy { it.Student_Name }
                }else if(order == "Sort by id"){
                    resList.sortBy { it.Student_ID }
                }else if(order == "Sort by rank"){
                    resList.sortBy { it.program_Code }
                }
                adapter.filterMyResultList(resList)
            }
        }
    }

    private fun allbuttonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }
    }



    private fun myResultClickListener(position:Int){


        Toasty.info(requireContext(), "Sorry: detailed result will available in future update.", Toast.LENGTH_LONG, true).show()


    }

}