package com.newage.studlab.Calculators

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.Sgpa
import com.newage.studlab.R
import kotlinx.android.synthetic.main.sgpa_calculator_main.*

class SgpaCalculator: AppCompatActivity() {

    private val sg = ArrayList<Sgpa>()
    var grade_eqarned: String = ""
    var grade_point: Float = 0.0f
    var credit_hour: Float = 0.0f
    var grade_x_credit: Float = 0.0f
    var count_course: Int = 0
    var total_course: Int = 0
    var total_grade_point: Float = 0.0f
    var total_credit_hour: Float = 0.0f
    var total_grade_x_credit: Float = 0.0f
    var total_Sgpa:Float = 0.0f

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sgpa_calculator_main)
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        setAllInput()
        displayButtonClick()
        gradeButtonClick()
        creditButtonClick()
        grade_credit_button.setOnClickListener{
            Toast.makeText(this,"G: Grade on row\n" +
                    "C: Credit on column",Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayData() {
        val recyclerView = findViewById<RecyclerView>(R.id.calculator_screen_view)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        sg.add(
            Sgpa(
                count_course.toString(),
                grade_eqarned,
                grade_point,
                credit_hour,
                grade_x_credit
            )
        )
        recyclerView.adapter = SgpaAdapter(this, sg, clickListener = {})
        recyclerView.scrollToPosition(count_course-1)
    }

    @SuppressLint("SetTextI18n")
    private fun pushData(g_e: String, g_p: Float, c_h: Float) {
        count_course += 1
        grade_eqarned = g_e
        grade_point = g_p
        credit_hour = c_h
        grade_x_credit = grade_point * credit_hour
        displayData()
        total_course = count_course
        total_grade_point += g_p
        total_credit_hour += c_h
        total_grade_x_credit += grade_x_credit
        total_Sgpa = total_grade_x_credit/total_credit_hour

        cal_total_course.text = "C: $total_course"
        cal_grade_point.text = "%.2f".format(total_grade_point)
        cal_total_credit_hour.text = "%.2f".format(total_credit_hour)
        cal_total_g_x_c.text ="%.2f".format(total_grade_x_credit)
        cal_sgpa_result.text = "SGPA: %.2f".format(total_Sgpa)
    }


    @SuppressLint("SetTextI18n")
    fun displayButtonClick(){
        cal_clear_button.setOnClickListener{
            sg.clear()
            calculator_screen_view?.adapter?.notifyDataSetChanged()
            cal_total_course.text ="C: 0"
            cal_grade_point.text = "0.00"
            cal_total_credit_hour.text = "0.00"
            cal_total_g_x_c.text = "0.00"
            cal_sgpa_result.text = "SGPA: 0.00"
            grade_eqarned = ""
            grade_point = 0.0f
            credit_hour = 0.0f
            grade_x_credit = 0.0f
            count_course = 0
            total_course = 0
            total_grade_point= 0.0f
            total_credit_hour= 0.0f
            total_grade_x_credit= 0.0f
            total_Sgpa = 0.0F
        }

        cal_del_button.setOnClickListener{
            if(total_course>0){
                val gp: Float = sg[total_course-1].gradePoint
                val ch:Float = sg[total_course-1].creditHour
                val gc: Float = sg[total_course-1].gradeXCredit
                //val tc: Int = total_course
                total_grade_point -= gp
                total_credit_hour -= ch
                total_grade_x_credit -= gc
                total_Sgpa = total_grade_x_credit/total_credit_hour
                sg.removeAt(total_course-1)
                count_course-=1
                total_course-=1
                calculator_screen_view?.adapter?.notifyDataSetChanged()
                cal_total_course.text = "C: $count_course"
                cal_grade_point.text = "%.2f".format(total_grade_point)
                cal_total_credit_hour.text = "%.2f".format(total_credit_hour)
                cal_total_g_x_c.text ="%.2f".format(total_grade_x_credit)
                cal_sgpa_result.text = "SGPA: %.2f".format(total_Sgpa)
            }
        }
    }
    private fun creditButtonClick() {
        C_four_button.setOnClickListener{
            Toast.makeText(this,"Credit: 4",Toast.LENGTH_SHORT).show()
        }
        C_three_button.setOnClickListener{
            Toast.makeText(this,"Credit: 3",Toast.LENGTH_SHORT).show()
        }
        C_two_button.setOnClickListener{
            Toast.makeText(this,"Credit: 2",Toast.LENGTH_SHORT).show()
        }
        C_onePointFive_button.setOnClickListener{
            Toast.makeText(this,"Credit: 1.75",Toast.LENGTH_SHORT).show()
        }
        C_pointSevenFive_button.setOnClickListener{
            Toast.makeText(this,"Credit: 0.75",Toast.LENGTH_SHORT).show()
        }
    }
    fun gradeButtonClick(){
        A_plus_button.setOnClickListener{
            Toast.makeText(this,"Grade: A+\n +" +
                    "Number: 80-100",Toast.LENGTH_SHORT).show()
        }
        A_button.setOnClickListener{
            Toast.makeText(this,"Grade: A\n" +
                    "Number: 75-79",Toast.LENGTH_SHORT).show()
        }
        A_minus_button.setOnClickListener{
            Toast.makeText(this,"Grade: A-\n" +
                    "Number: 70-74",Toast.LENGTH_SHORT).show()
        }
        B_plus_button.setOnClickListener{
            Toast.makeText(this,"Grade: B+\n" +
                    "Number: 65-69",Toast.LENGTH_SHORT).show()
        }
        B_button.setOnClickListener{
            Toast.makeText(this,"Grade: B\n" +
                    "Number: 60-64",Toast.LENGTH_SHORT).show()
        }
        B_minus_button.setOnClickListener{
            Toast.makeText(this,"Grade: B-\n" +
                    "Number: 55-59",Toast.LENGTH_SHORT).show()
        }
        C_plus_button.setOnClickListener{
            Toast.makeText(this,"Grade: C+\n" +
                    "Number: 50-54",Toast.LENGTH_SHORT).show()
        }
        C_button.setOnClickListener{
            Toast.makeText(this,"Grade: C\n" +
                    "Number: 45-49",Toast.LENGTH_SHORT).show()
        }
        D_button.setOnClickListener{
            Toast.makeText(this,"Grade: D\n" +
                    "Number: 40-44",Toast.LENGTH_SHORT).show()
        }
        F_button.setOnClickListener{
            Toast.makeText(this,"Grade: F\n" +
                    "Number: 0-39",Toast.LENGTH_SHORT).show()
        }

    }

    private fun setAllInput() {
        //----------------(A+)--------------------
        C4_A_plus.setOnClickListener {
            pushData("A+", 4.00F, 4.0F)
        }
        C3_A_plus.setOnClickListener {
            pushData("A+", 4.00F, 3.0F)
        }

        C2_A_plus.setOnClickListener {
            pushData("A+", 4.00F, 2.0F)
        }
        C15_A_plus.setOnClickListener {
            pushData("A+", 4.00F, 1.5F)
        }
        C75_A_plus.setOnClickListener {
            pushData("A+", 4.00F, 0.75F)
        }
        //-------------------(A)------------------
        C4_A.setOnClickListener {
            pushData("A", 3.75F, 4.0F)
        }
        C3_A.setOnClickListener {
            pushData("A", 3.75F, 3.0F)
        }
        C2_A.setOnClickListener {
            pushData("A", 3.75F, 2.0F)
        }
        C15_A.setOnClickListener {
            pushData("A", 3.75F, 1.5F)
        }
        C75_A.setOnClickListener {
            pushData("A", 3.75F, 0.75F)
        }
        //------------------(A-)------------------
        C4_A_minus.setOnClickListener {
            pushData("A-", 3.50F, 4.0F)
        }
        C3_A_minus.setOnClickListener {
            pushData("A-", 3.50F, 3.0F)
        }
        C2_A_minus.setOnClickListener {
            pushData("A-", 3.50F, 2.0F)
        }
        C15_A_minus.setOnClickListener {
            pushData("A-", 3.50F, 1.5F)
        }
        C75_A_minus.setOnClickListener {
            pushData("A-", 3.50F, 0.75F)
        }
        //-------------------------(B+)---------------------
        C4_B_Plus.setOnClickListener {
            pushData("B+", 3.25F, 4.0F)
        }
        C3_B_Plus.setOnClickListener {
            pushData("B+", 3.25F, 3.0F)
        }
        C2_B_Plus.setOnClickListener {
            pushData("B+", 3.25F, 2.0F)
        }
        C15_B_Plus.setOnClickListener {
            pushData("B+", 3.25F, 1.5F)
        }
        C75_B_Plus.setOnClickListener {
            pushData("B+", 3.25F, 0.75F)
        }
        //---------------------(B)---------------------
        C4_B.setOnClickListener {
            pushData("B", 3.00F, 4.0F)
        }
        C3_B.setOnClickListener {
            pushData("B", 3.00F, 3.0F)
        }
        C2_B.setOnClickListener {
            pushData("B", 3.00F, 2.0F)
        }
        C15_B.setOnClickListener {
            pushData("B", 3.00F, 1.5F)
        }
        C75_B.setOnClickListener {
            pushData("B", 3.00F, 0.75F)
        }
        //----------------(B-)---------------
        C4_B_Minus.setOnClickListener {
            pushData("B-", 2.75F, 4.0F)
        }
        C3_B_Minus.setOnClickListener {
            pushData("B-", 2.75F, 3.0F)
        }
        C2_B_Minus.setOnClickListener {
            pushData("B-", 2.75F, 2.0F)
        }
        C15_B_Minus.setOnClickListener {
            pushData("B-", 2.75F, 1.5F)
        }
        C75_B_Minus.setOnClickListener {
            pushData("B-", 2.75F, 0.75F)
        }
        //------------------(C+)-------------------
        C4_C_Plus.setOnClickListener {
            pushData("C+", 2.50F, 4.0F)
        }
        C3_C_Plus.setOnClickListener {
            pushData("C+", 2.50F, 3.0F)
        }
        C2_C_Plus.setOnClickListener {
            pushData("C+", 2.50F, 2.0F)
        }
        C15_C_Plus.setOnClickListener {
            pushData("C+", 2.50F, 1.5F)
        }
        C75_C_Plus.setOnClickListener {
            pushData("C+", 2.50F, 0.75F)
        }
        //------------------(C)----------------------
        C4_C.setOnClickListener {
            pushData("C", 2.25F, 4.0F)
        }
        C3_C.setOnClickListener {
            pushData("C", 2.25F, 3.0F)
        }
        C2_C.setOnClickListener {
            pushData("C", 2.25F, 2.0F)
        }
        C15_C.setOnClickListener {
            pushData("C", 2.25F, 1.5F)
        }
        C75_C.setOnClickListener {
            pushData("C", 2.25F, 0.75F)
        }
        //-------------------(D)-----------------
        C4_D.setOnClickListener {
            pushData("D", 2.00F, 4.0F)
        }
        C3_D.setOnClickListener {
            pushData("D", 2.00F, 3.0F)
        }
        C2_D.setOnClickListener {
            pushData("D", 2.00F, 2.0F)
        }
        C15_D.setOnClickListener {
            pushData("D", 2.00F, 1.5F)
        }
        C75_D.setOnClickListener {
            pushData("D", 2.00F, 0.75F)
        }
        //----------------(F)--------------------
        C4_F.setOnClickListener {
            pushData("F", 0.00F, 4.0F)
        }
        C3_F.setOnClickListener {
            pushData("F", 0.00F, 3.0F)
        }
        C2_F.setOnClickListener {
            pushData("F", 0.00F, 2.0F)
        }
        C15_F.setOnClickListener {
            pushData("F", 0.00F, 1.5F)
        }
        C75_F.setOnClickListener {
            pushData("F", 0.00F, 0.75F)
        }
    }

   /* override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //This is used to hide/show 'Status Bar' & 'System Bar'. Swip bar to get it as visible.
        val decorView = window.decorView
        if (hasFocus) {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }*/
}

