package com.newage.studlab.Home.AnnexFragment

import AnnexHomeFragment
import AnnexHomeFragment.Companion.feesWaiver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.AnnexAdapter.FeesSemesterReceiptRecyclerViewAdapter
import com.newage.studlab.Home.AnnexFragment.AnnexFeesSemesterFragment.Companion.thisSemester
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.R

class AnnexFeesReceiptFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_annex_fees_receipt, container, false)
    }

    lateinit var resNotFound: TextView
    lateinit var backButton:ImageView


    lateinit var receiptSemester:TextView
    lateinit var receiptDemand:TextView
    lateinit var receiptWaiver:TextView
    lateinit var receiptPaid:TextView
    lateinit var receiptDue:TextView

    lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentPosition = 0.1
        fragmentName = "AnnexFeesReceiptFragment"

        activity?.let {

            backButton = it.findViewById(R.id.fragment_back_button)

            //header--------
            receiptSemester = it.findViewById(R.id.annex_fees_receipt_title)
            receiptDemand = it.findViewById(R.id.total_demand)
            receiptWaiver = it.findViewById(R.id.total_waiver)
            receiptPaid = it.findViewById(R.id.total_paid)
            receiptDue = it.findViewById(R.id.total_due)


            resNotFound = it.findViewById(R.id.fragment_empty)
            recyclerView = it.findViewById(R.id.annex_fees_receipt_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)


        loadSemesterInfo()
        allbuttonClickingEventListeners()

        if(feesWaiver.size != 0){
            for(i in 0 until feesWaiver.size){
                if(feesWaiver[i].Semester == thisSemester.Semester){
                    recyclerView.adapter = FeesSemesterReceiptRecyclerViewAdapter(context,feesWaiver[i].payments, clickListener = {

                    })
                    break
                }
            }
        }

    }


    private fun loadSemesterInfo(){
        receiptSemester.text = thisSemester.Semester
        receiptDemand.text = thisSemester.Demand
        receiptWaiver.text = thisSemester.Waiver
        receiptPaid.text = thisSemester.Paid
        receiptDue.text = thisSemester.Due
    }


    private fun allbuttonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, AnnexHomeFragment())
            transaction.commit()
        }
    }

}