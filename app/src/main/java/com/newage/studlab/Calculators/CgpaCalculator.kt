package com.newage.studlab.Calculators

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Model.Cgpa
import com.newage.studlab.R
import kotlinx.android.synthetic.main.cgpa_calculator_main.*
import www.sanju.motiontoast.MotionToast


class CgpaCalculator: AppCompatActivity() {

    private val cg = ArrayList<Cgpa>()
    var bool:Boolean = false

    var grade_earned: String = ""
    var grade_point: Float = 0.0f
    var credit_hour: Float = 0.0f
    var grade_x_credit: Float = 0.0f
    var sgpa: Float = 0.0f
    var cgpa: Float = 0.0f

    var total_semester: Int = 0

    var count_course: Int = 0
    var total_course: Int = 0
    var total_grade_point: Float = 0.0f
    var total_credit_hour: Float = 0.0f
    var total_grade_x_credit: Float = 0.0f
    var total_Sgpa:Float = 0.0f

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cgpa_calculator_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;


        displayButtonClick()

        gradeButtonClick()
        creditButtonClick()
        setAllInput()

        cgpa_cal_info.setOnClickListener{
            MotionToast.darkColorToast(this,"CGPA Calculator",
                MotionToast.TOAST_INFO,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this,R.font.helvetica_regular))
        }


        grade_credit_button.setOnClickListener{
            Toast.makeText(this,"G: Grade on row\n" +
                    "C: Credit on column",Toast.LENGTH_LONG).show()
        }
    }

    private fun displayData() {
        val recyclerView = findViewById<RecyclerView>(R.id.calculator_screen_view)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cg.add(
            Cgpa(
                count_course.toString(),
                grade_earned,
                grade_point,
                credit_hour,
                grade_x_credit,
                sgpa
            )
        )
        recyclerView.adapter = CgpaAdapter(this, cg, clickListener = {})
        recyclerView.scrollToPosition(count_course-1)
    }

    @SuppressLint("SetTextI18n")
    private fun pushData(g_e: String, g_p: Float, c_h: Float) {
        count_course += 1
        grade_earned = g_e
        grade_point = g_p
        credit_hour = c_h
        grade_x_credit = grade_point * credit_hour

        displayData()

        total_course = count_course
        total_grade_point += g_p
        total_credit_hour += c_h
        total_grade_x_credit += grade_x_credit
        sgpa = total_grade_x_credit/total_credit_hour
        total_Sgpa += sgpa
        cgpa = total_Sgpa/total_semester

        cgpa_total_course.text = "C: $total_course"
        cgpa_total_grade_point.text = "%.2f".format(total_grade_point)
        cgpa_total_credit_hour.text = "%.2f".format(total_credit_hour)
        cgpa_total_g_x_c.text ="%.2f".format(total_grade_x_credit)
        cgpa_total_sgpa.text = "%.2f".format(total_Sgpa)
        cal_cgpa_result.text = "%.2f".format(cgpa)

    }


    fun displayButtonClick(){
        cal_clear_button.setOnClickListener{
            cg.clear()
            bool= false
            grade_earned= ""
            grade_point= 0.0f
            credit_hour = 0.0f
            grade_x_credit= 0.0f
            cgpa= 0.0f
            total_semester= 0
            count_course= 0
            total_course= 0
            total_grade_point = 0.0f
            total_credit_hour= 0.0f
            total_grade_x_credit = 0.0f
            total_Sgpa= 0.0f
            calculator_screen_view?.adapter?.notifyDataSetChanged()
            cgpa_total_course.text ="C: 0"
            cgpa_total_grade_point.text = "0.00"
            cgpa_total_credit_hour.text = "0.00"
            cgpa_total_g_x_c.text = "0.00"
            cal_cgpa_result.text = "CGPA: 0.00"
            s1.background = ContextCompat.getDrawable(this, R.drawable.squar_round_gradiant)
            s2.background = ContextCompat.getDrawable(this, R.drawable.squar_round_gradiant)

        }

        cal_del_button.setOnClickListener{
            if(total_course>0){
                var gp: Float = cg[total_course-1].gradePoint
                var ch:Float = cg[total_course-1].creditHour
                var gc: Float = cg[total_course-1].gradeXCredit
                var sc: Float = cg[total_course-1].sGpa

                total_grade_point -= gp
                total_credit_hour -= ch
                total_grade_x_credit -= gc
                total_Sgpa -=sc
                cg.removeAt(total_course-1)
                count_course-=1
                total_course-=1
                cgpa_total_course.text = "C: $total_course"
                cgpa_total_grade_point.text = "%.2f".format(total_grade_point)
                cgpa_total_credit_hour.text = "%.2f".format(total_credit_hour)
                cgpa_total_g_x_c.text ="%.2f".format(total_grade_x_credit)
                cgpa_total_sgpa.text = "%.2f".format(total_Sgpa)
                cal_cgpa_result.text = "CGPA: %.2f".format(cgpa)
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
            if(bool)
            pushData("A+", 4.00F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_A_plus.setOnClickListener {
            if(bool)
            pushData("A+", 4.00F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }

        C2_A_plus.setOnClickListener {
            if(bool)
            pushData("A+", 4.00F, 2.0F)
            else
                MotionToast.darkColorToast(this,"Please select a semester first.",
                    MotionToast.TOAST_INFO,
                    MotionToast.GRAVITY_CENTER,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this,R.font.helvetica_regular))

            //Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_A_plus.setOnClickListener {
            if(bool)
            pushData("A+", 4.00F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_A_plus.setOnClickListener {
            if(bool)
            pushData("A+", 4.00F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        //-------------------(A)------------------
        C4_A.setOnClickListener {
            if(bool)
            pushData("A", 3.75F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_A.setOnClickListener {
            if(bool)
            pushData("A", 3.75F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C2_A.setOnClickListener {
            if(bool)
            pushData("A", 3.75F, 2.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_A.setOnClickListener {
            if(bool)
            pushData("A", 3.75F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_A.setOnClickListener {
            if(bool)
            pushData("A", 3.75F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        //------------------(A-)------------------
        C4_A_minus.setOnClickListener {
            if(bool)
            pushData("A-", 3.50F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_A_minus.setOnClickListener {
            if(bool)
            pushData("A-", 3.50F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C2_A_minus.setOnClickListener {
            if(bool)
            pushData("A-", 3.50F, 2.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_A_minus.setOnClickListener {
            if(bool)
            pushData("A-", 3.50F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_A_minus.setOnClickListener {
            if(bool)
            pushData("A-", 3.50F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        //-------------------------(B+)---------------------
        C4_B_Plus.setOnClickListener {
            if(bool)
            pushData("B+", 3.25F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_B_Plus.setOnClickListener {
            if(bool)
            pushData("B+", 3.25F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C2_B_Plus.setOnClickListener {
            if(bool)
            pushData("B+", 3.25F, 2.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_B_Plus.setOnClickListener {
            if(bool)
            pushData("B+", 3.25F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_B_Plus.setOnClickListener {
            if(bool)
            pushData("B+", 3.25F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        //---------------------(B)---------------------
        C4_B.setOnClickListener {
            if(bool)
            pushData("B", 3.00F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_B.setOnClickListener {
            if(bool)
            pushData("B", 3.00F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C2_B.setOnClickListener {
            if(bool)
            pushData("B", 3.00F, 2.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_B.setOnClickListener {
            if(bool)
            pushData("B", 3.00F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_B.setOnClickListener {
            if(bool)
            pushData("B", 3.00F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        //----------------(B-)---------------
        C4_B_Minus.setOnClickListener {
            if(bool)
            pushData("B-", 2.75F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_B_Minus.setOnClickListener {
            if(bool)
            pushData("B-", 2.75F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C2_B_Minus.setOnClickListener {
            if(bool)
            pushData("B-", 2.75F, 2.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_B_Minus.setOnClickListener {
            if(bool)
            pushData("B-", 2.75F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_B_Minus.setOnClickListener {
            if(bool)
            pushData("B-", 2.75F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        //------------------(C+)-------------------
        C4_C_Plus.setOnClickListener {
            if(bool)
            pushData("C+", 2.50F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_C_Plus.setOnClickListener {
            if(bool)
            pushData("C+", 2.50F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C2_C_Plus.setOnClickListener {
            if(bool)
            pushData("C+", 2.50F, 2.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_C_Plus.setOnClickListener {
            if(bool)
            pushData("C+", 2.50F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_C_Plus.setOnClickListener {
            if(bool)
            pushData("C+", 2.50F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        //------------------(C)----------------------
        C4_C.setOnClickListener {
            if(bool)
            pushData("C", 2.25F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_C.setOnClickListener {
            if(bool)
            pushData("C", 2.25F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C2_C.setOnClickListener {
            if(bool)
            pushData("C", 2.25F, 2.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_C.setOnClickListener {
            if(bool)
            pushData("C", 2.25F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_C.setOnClickListener {
            if(bool)
            pushData("C", 2.25F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        //-------------------(D)-----------------
        C4_D.setOnClickListener {
            if(bool)
            pushData("D", 2.00F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_D.setOnClickListener {
            if(bool)
            pushData("D", 2.00F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C2_D.setOnClickListener {
            if(bool)
            pushData("D", 2.00F, 2.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_D.setOnClickListener {
            if(bool)
            pushData("D", 2.00F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_D.setOnClickListener {
            if(bool)
            pushData("D", 2.00F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        //----------------(F)--------------------
        C4_F.setOnClickListener {
            if(bool)
            pushData("F", 0.00F, 4.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C3_F.setOnClickListener {
            if(bool)
            pushData("F", 0.00F, 3.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C2_F.setOnClickListener {
            if(bool)
            pushData("F", 0.00F, 2.0F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C15_F.setOnClickListener {
            if(bool)
            pushData("F", 0.00F, 1.5F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
        C75_F.setOnClickListener {
            if(bool)
            pushData("F", 0.00F, 0.75F)
            else
                Toast.makeText(this,"Please select a semester first.",Toast.LENGTH_LONG).show()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
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
    }
}

