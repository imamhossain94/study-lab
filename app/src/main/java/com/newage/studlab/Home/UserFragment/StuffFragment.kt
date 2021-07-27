package com.newage.studlab.Home.UserFragment

import HomeFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.ResultAdapter.MyResultRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.intakeResult
import com.newage.studlab.Model.Results
import com.newage.studlab.R

class StuffFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stuff, container, false)
    }


    lateinit var backButton: ImageView
    lateinit var resNotFound: TextView

    lateinit var recyclerView: RecyclerView
    val resultList =ArrayList<Results>()

    //val intakeResult = HashMap<String, ArrayList<Results>>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        activity?.let {

            backButton = it.findViewById(R.id.fragment_back_button)
            resNotFound = it.findViewById(R.id.fragment_empty)


            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)




        allbuttonClickingEventListeners()
        //loadIntakeResult()

/*        if(intakeResult.containsKey("First Semester"))
        recyclerView.adapter = MyResultRecyclerViewAdapter(context,intakeResult.getValue("First Semester")!!, clickListener = {
            //resultClickListner(it)
        })*/



    }

    private fun allbuttonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }
    }

    fun position(i: Int): String? {
        val sufixes =
            arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
        return when (i % 100) {
            11, 12, 13 -> i.toString() + "th"
            else -> i.toString() + sufixes[i % 10]
        }
    }


    private fun calculatePosition(resultList:ArrayList<Results>):Results{

        val newResultList =ArrayList<Results>()
        resultList.sortByDescending { it.Student_Cgpa }

        var height = resultList[0].Student_Cgpa
        var n = 1
        for (i in 0 until resultList.size-1){
            if(resultList[i].Student_Cgpa == height){
                val results = Results(resultList[i].Student_ID,resultList[i].Student_Name,
                    resultList[i].Student_Cgpa, resultList[i].Student_Sgpa,position(n)!!,
                    resultList[i].Intake_No,resultList[i].Semester_Title)

                newResultList.add(results)
            }else if(resultList[i].Student_Cgpa < height){
                n++
                height = resultList[i].Student_Cgpa
                val results = Results(resultList[i].Student_ID,resultList[i].Student_Name,
                    resultList[i].Student_Cgpa, resultList[i].Student_Sgpa,
                    position(i)!!,resultList[i].Intake_No,resultList[i].Semester_Title)

                newResultList.add(results)
            }
        }

        var obj = Results()
        newResultList.find { it.Student_ID == currentUser!!.user_id}?.let {
            obj = it
        }
        return obj
    }


    private fun loadIntakeResult() {
        if(intakeResult.containsKey("First Semester")){
            resultList.add(calculatePosition(intakeResult.getValue("First Semester")))
            if(intakeResult.containsKey("Second Semester")){
                resultList.add(calculatePosition(intakeResult.getValue("Second Semester")))
                if(intakeResult.containsKey("Third Semester")){
                    resultList.add( calculatePosition(intakeResult.getValue("Third Semester")))
                    if(intakeResult.containsKey("Fourth Semester")){
                        resultList.add(calculatePosition(intakeResult.getValue("Fourth Semester")))
                        if(intakeResult.containsKey("Fifth Semester")){
                            resultList.add(calculatePosition(intakeResult.getValue("Fifth Semester")))
                            if(intakeResult.containsKey("Sixth Semester")){
                                resultList.add( calculatePosition(intakeResult.getValue("Sixth Semester")) )
                                if(intakeResult.containsKey("Seventh Semester")){
                                    resultList.add(calculatePosition(intakeResult.getValue("Seventh Semester")))
                                }
                            }
                        }
                    }
                }
            }
        }
        recyclerView.adapter =
            MyResultRecyclerViewAdapter(
                context,
                resultList,
                clickListener = {
                    //resultClickListner(it)
                })

        /*val ref = FirebaseDatabase.getInstance().getReference("/Results/$programCode/$intakeNo")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    val to: GenericTypeIndicator<ArrayList<Results>> = object :
                            GenericTypeIndicator<ArrayList<Results>>() {}

                    val map:ArrayList<Results> = ds.getValue<ArrayList<Results>>(to)!!

                    for (snapshotNode in ds.children) {
                        intakeResult.put(snapshotNode.key!!, ds.getValue<ArrayList<Results>>(to)!!)
                    }
                    recyclerView.adapter = MyResultRecyclerViewAdapter(context,map, clickListener = {
                        //resultClickListner(it)
                    })
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })*/


    }


    private fun refreshRecyclerView(){


    }



}