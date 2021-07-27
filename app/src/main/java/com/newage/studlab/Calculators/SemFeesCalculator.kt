package com.newage.studlab.Calculators

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.newage.studlab.Authentication.Verification.SignUpActivity
import com.newage.studlab.Authentication.Verification.SignUpIdentification
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_sem_fees_calculator.*
import java.lang.Exception

class SemFeesCalculator : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sem_fees_calculator)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val feesImage = "https://www.bubt.edu.bd/assets/frontend/media/1578234443Final_Cost_of_Spring_2020-1.png"
        Picasso.get().load(feesImage).networkPolicy(NetworkPolicy.OFFLINE).into(fees_image, object: com.squareup.picasso.Callback{
            override fun onSuccess() { }
            override fun onError(e: Exception?) {
                Picasso.get().load(feesImage).into(fees_image)
            }
        })

        allButtonClickEvent()
        textChangeEventListeners()
    }

    private fun allButtonClickEvent(){

        fragment_back_button.setOnClickListener{
            this.finish()
        }

        fragment_link_button.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bubt.edu.bd/home/page_details/Routine"))
            startActivity(i)
        }

        lculate_button.setOnClickListener{

            val tc = total_credit.text.toString()
            val cf = per_credit_fee.text.toString()
            val rf = reg_fee.text.toString()
            val tw = total_waiver.text.toString()

            if(tc.isNotEmpty() && cf.isNotEmpty() && rf.isNotEmpty() && tw.isNotEmpty()){

                val twN = tw.replace("%","")

                val totalFees = (tc.toFloat() * cf.toFloat()) + rf.toFloat()
                val totalWaiver = totalFees * (twN.toFloat() / 100)
                val feeAfterWaiver = totalFees - totalWaiver

                res_total_fees.text = Html.fromHtml("<b>Total fee::</b> $totalFees")
                res_total_waiver.text =  Html.fromHtml("<b>Total waiver::</b> $totalWaiver")
                res_after_waiver.text = Html.fromHtml("<b>Fees after waiver::</b> $feeAfterWaiver")

                calculate_result_title.visibility = View.VISIBLE
                result_container.visibility = View.VISIBLE

            }else if(tc.isNotEmpty()){

                Toasty.warning(this, "Total credit is empty", Toast.LENGTH_SHORT, true).show()

            }else if(cf.isNotEmpty()){

                Toasty.warning(this, "Per credit fee is empty", Toast.LENGTH_SHORT, true).show()

            }else if(rf.isNotEmpty()){

                Toasty.warning(this, "Registration frr is empty", Toast.LENGTH_SHORT, true).show()

            }else if(tw.isNotEmpty()){

                Toasty.warning(this, "Waiver should be 0% or more", Toast.LENGTH_SHORT, true).show()

            }


        }

    }

    private fun textChangeEventListeners(){

        total_waiver.addTextChangedListener(object :
            TextWatcher { override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //total_waiver.setText()
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

    }


}