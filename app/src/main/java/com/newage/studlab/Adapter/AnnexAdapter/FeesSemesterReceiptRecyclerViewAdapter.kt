package com.newage.studlab.Adapter.AnnexAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.AnnexModel.Payments
import com.newage.studlab.R
import kotlinx.android.synthetic.main.item_fees_receipt.view.*

class FeesSemesterReceiptRecyclerViewAdapter(val context: Context?, val receiptList: ArrayList<Payments>, val clickListener: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fees_receipt, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bindItems(receiptList[position])


/*        holder.clickableView2.setOnClickListener {
            clickListener(position)
        }
        holder.clickableView3.setOnClickListener {
            clickListener(position)
        }
        holder.clickableView4.setOnClickListener {
            clickListener(position)
        }*/
    }

    override fun getItemCount(): Int {
        return receiptList.size
    }

    class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

/*        val clickableView2 = itemView.detail_button
        val clickableView3 = itemView.detail_button
        val clickableView4 = itemView.detail_button*/


        fun bindItems(recpt: Payments) {

            val paymentNo = itemView.payemnt_no
            val paymentAmmount = itemView.payment_ammount
            val accountCode = itemView.account_code
            val receiptNo = itemView.receipt_no
            val waiver = itemView.total_waiver

            paymentNo.text = recpt.Payment_No
            paymentAmmount.text = recpt.Payment_Amount
            accountCode.text = recpt.Account_Code
            receiptNo.text = recpt.Reciept_No
            waiver.text = recpt.Waiver

        }
    }

}


