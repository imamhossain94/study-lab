package com.newage.studlab.Authentication.Verification

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Authentication.Verification.SignUpActivity.Companion.currentStapes
import com.newage.studlab.Authentication.Verification.SignUpActivity.Companion.nextButton
import com.newage.studlab.Authentication.Verification.SignUpActivity.Companion.stepView
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_name
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import java.util.*

class SignUpInformation : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_information, container, false)
    }

    companion object{
        lateinit var user_semester:String
        lateinit var user_section:String
        lateinit var user_shift:String
        lateinit var user_dob:String
        lateinit var user_phone_new:String
    }


    lateinit var userInformation: TextView

    lateinit var userSemesterText: TextView
    lateinit var userSectionText: TextView
    lateinit var userShiftText: TextView
    lateinit var userDobText: TextView

    lateinit var userContactNo:EditText

    lateinit var userSemesterSpinner: Spinner
    lateinit var userShiftSpinner: Spinner
    lateinit var userSectionSpinner: Spinner

    lateinit var progress: ProgressDialog
    lateinit var loadingDialog: AlertDialog
    lateinit var  mBuilder:AlertDialog.Builder


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {

            userInformation = it.findViewById(R.id.user_information)

            userSemesterText = it.findViewById(R.id.user_semester_textview)
            userSectionText = it.findViewById(R.id.user_section_text_view)
            userShiftText = it.findViewById(R.id.user_shift_textview)

            userDobText = it.findViewById(R.id.user_dob_text_view)

            userContactNo = it.findViewById(R.id.user_contact_edit_text)

            userSemesterSpinner = it.findViewById(R.id.user_semester_spinner)
            userShiftSpinner = it.findViewById(R.id.user_shift_spinner)
            userSectionSpinner = it.findViewById(R.id.user_section_spinner)

        }



        progress = ProgressDialog(requireContext()).apply {
            setTitle("Please Wait....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.loading_dialog, null)
        mBuilder = AlertDialog.Builder(requireContext()).setView(mDialogView).setCancelable(false)

        userInformation.text = "Hi, $user_name."

        nextButton.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
        nextButton.isEnabled = false

        currentStapes = 2
        nextButton.text = "NEXT"

        allButtonClickingEventListener()
        textChangesEventListeners()
        handleSpinners()

    }

    private fun handleSpinners(){
        val semester = resources.getStringArray(R.array.semesters)
        val section = resources.getStringArray(R.array.section)
        val shift = resources.getStringArray(R.array.shift_v2)

        val semesterSpinner = ArrayAdapter.createFromResource(appContext,R.array.semesters,R.layout.spinner_item_signup)
        semesterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userSemesterSpinner.adapter = semesterSpinner
        userSemesterSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //val order = sortBy[position]
                userSemesterText.text = semester[position]
                user_semester = semester[position]
            }
        }


        val sectionSpinner = ArrayAdapter.createFromResource(appContext,R.array.section,R.layout.spinner_item_signup)
        sectionSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userSectionSpinner.adapter = sectionSpinner
        userSectionSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                userSectionText.text = section[position]
                user_section = section[position]
            }
        }

        val shiftSpinner = ArrayAdapter.createFromResource(appContext,R.array.shift_v2,R.layout.spinner_item_signup)
        shiftSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userShiftSpinner.adapter = shiftSpinner
        userShiftSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                userShiftText.text = shift[position]
                user_shift = shift[position]
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun  allButtonClickingEventListener(){

        userDobText.setOnClickListener{
            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            // date picker dialog
            val picker = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, y, monthOfYear, dayOfMonth ->

                    userDobText.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + y
                    user_dob = userDobText.text.toString()
                    activity!!.runOnUiThread{
                        if (userContactNo.text.toString().length == 11 && userDobText.text.isNotEmpty()) {
                            userContactNo.onEditorAction(EditorInfo.IME_ACTION_DONE)
                            nextButton.background = resources.getDrawable(R.drawable.intro_button_green)
                            nextButton.isEnabled = true
                        }else{
                            nextButton.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
                            nextButton.isEnabled = false
                        }
                        textChangesEventListeners()
                    }
                }, year, month, day)
            picker.show()
        }

        nextButton.setOnClickListener{

            stepView.go(2,true)
            fragmentManager!!.beginTransaction().setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,0,0)
                .replace(R.id.sign_up_fragment_container, SignUpActivity.fragments[2])
                .addToBackStack(null)
                .commit()

        }
    }

    private fun textChangesEventListeners(){

        userContactNo.addTextChangedListener(object : TextWatcher { override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length == 11 && userDobText.text.isNotEmpty()) {
                user_phone_new = userContactNo.text.toString()
                userContactNo.onEditorAction(EditorInfo.IME_ACTION_DONE)
                nextButton.background = resources.getDrawable(R.drawable.intro_button_green)
                nextButton.isEnabled = true
            }else if(s.length == 11){
                user_phone_new = userContactNo.text.toString()
                userContactNo.onEditorAction(EditorInfo.IME_ACTION_DONE)
            } else{
                nextButton.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
                nextButton.isEnabled = false
            }
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }


}
