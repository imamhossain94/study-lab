package com.newage.studlab.Home.AnnexFragment

import AnnexHomeFragment.Companion.Total_Demand
import AnnexHomeFragment.Companion.Total_Due
import AnnexHomeFragment.Companion.Total_Paid
import AnnexHomeFragment.Companion.Total_Waiver
import AnnexHomeFragment.Companion.feesWaiver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.AnnexAdapter.FeesSemesterRecyclerViewAdapter
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.AnnexModel.AnnexFees
import com.newage.studlab.Model.Results
import com.newage.studlab.R

class AnnexFeesSemesterFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_annex_fees_semester, container, false)
    }


    companion object{
        lateinit var thisSemester:AnnexFees
    }

    //lateinit var backButton: ImageView
    lateinit var resNotFound: TextView

    lateinit var total_demand: TextView
    lateinit var total_waiver: TextView
    lateinit var total_paid: TextView
    lateinit var total_deu: TextView



    lateinit var recyclerView: RecyclerView
    var resultList =ArrayList<Results>()

    //val intakeResult = HashMap<String, ArrayList<Results>>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentPosition = 0.0
        fragmentName = "AnnexFeesSemesterFragment"

        activity?.let {

//            backButton = it.findViewById(R.id.back_button)
            resNotFound = it.findViewById(R.id.fragment_empty)

            total_demand = it.findViewById(R.id.total_demand)
            total_waiver = it.findViewById(R.id.total_waiver)
            total_paid = it.findViewById(R.id.total_paid)
            total_deu = it.findViewById(R.id.total_due)


            recyclerView = it.findViewById(R.id.annex_fees_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)




        allbuttonClickingEventListeners()

        if(feesWaiver.size != 0)
        recyclerView.adapter = FeesSemesterRecyclerViewAdapter(context,feesWaiver, clickListener = {
            feesSemesterClickEvent(it)
        })


        total_demand.text = Total_Demand
        total_waiver.text = Total_Waiver
        total_paid.text = Total_Paid
        total_deu.text =  Total_Due
    }

    private fun allbuttonClickingEventListeners(){
       /* backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }*/
    }

    private fun feesSemesterClickEvent(fees: AnnexFees){
        thisSemester = fees

        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)
        transaction.replace(R.id.main_home_fragment_container, AnnexFeesReceiptFragment())
        transaction.commit()

    }


}