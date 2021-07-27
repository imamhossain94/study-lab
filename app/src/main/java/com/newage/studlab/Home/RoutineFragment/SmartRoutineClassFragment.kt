package com.newage.studlab.Home.RoutineFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.AnnexAdapter.RoutineClassesRecyclerViewAdapter
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Home.RoutineFragment.SmartRoutineFragment.Companion.classesList
import com.newage.studlab.Home.RoutineFragment.SmartRoutineFragment.Companion.thisDay
import com.newage.studlab.Model.AnnexModel.RoutineDay
import com.newage.studlab.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SmartRoutineClassFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_smart_routine_classes, container, false)
    }


    lateinit var backButton: ImageView
    lateinit var dayClassDescription:TextView
    lateinit var resNotFound: TextView

    lateinit var recyclerView: RecyclerView

    val dayList =ArrayList<RoutineDay>()



    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {

            backButton = it.findViewById(R.id.fragment_back_button)
            dayClassDescription = it.findViewById(R.id.day_class_despription)
            resNotFound = it.findViewById(R.id.class_not_found)
            recyclerView = it.findViewById(R.id.routine_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        //OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        fragmentName = "smartRoutine"
        fragmentPosition = 0.1


        val time = SimpleDateFormat("hh:mm a")
        val currentTime = time.format(Date())


        if(classesList.size != 0){
            dayClassDescription.text = "$thisDay - $currentTime - ${classesList.size} Classes"
            resNotFound.visibility = View.INVISIBLE
            recyclerView.adapter = RoutineClassesRecyclerViewAdapter(context,classesList, clickListener = {

            })
        }else{
            dayClassDescription.text = "$thisDay - $currentTime - NO CLASS"
            resNotFound.text = "No class for $thisDay"
            resNotFound.visibility = View.VISIBLE
        }


        allButtonClickingEventListeners()

    }

    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, SmartRoutineFragment())
            transaction.commit()
        }
    }





}