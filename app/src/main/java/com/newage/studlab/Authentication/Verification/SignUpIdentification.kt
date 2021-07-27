package com.newage.studlab.Authentication.Verification

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.annexApis
import com.newage.studlab.Application.StudLab.Companion.userList
import com.newage.studlab.Authentication.Verification.SignUpActivity.Companion.currentStapes
import com.newage.studlab.Authentication.Verification.SignUpActivity.Companion.nextButton
import com.newage.studlab.Authentication.Verification.SignUpActivity.Companion.stepView
import com.newage.studlab.Plugins.StudLabAssistant.Companion.userPostToFaculty
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import io.github.tonnyl.light.Light.make
import kotlinx.android.synthetic.main.fragment_sign_up_identification.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView
import java.io.IOException
import java.util.concurrent.TimeUnit


class SignUpIdentification : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_identification, container, false)
    }

    var type_http = OkHttpClient()

    companion object{
        lateinit var user_type:String

        lateinit var user_id:String
        lateinit var user_name:String
        lateinit var user_gender:String
        lateinit var user_dob:String
        lateinit var user_phone_old: String
        lateinit var user_phone_new: String
        lateinit var user_password: String
        lateinit var user_image:String
        lateinit var user_faculty_name: String
        lateinit var user_prog_or_dept: String
        lateinit var user_shift_or_post:String
        lateinit var user_intake:String
        lateinit var user_section: String
        lateinit var user_address: String
        lateinit var user_status:String
        lateinit var user_blood: String
        lateinit var user_room_no:String

    }

    lateinit var student:Button
    lateinit var faculty:Button
    lateinit var userId:EditText
    lateinit var search: GifImageView
    lateinit var barcode:ImageView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user_type = "Student"
        user_id = "null"
        user_name = "null"
        user_gender = "null"
        user_dob = "null"
        user_phone_old = "null"
        user_phone_new = "null"
        user_password = "null"
        user_image = "null"
        user_faculty_name = "null"
        user_prog_or_dept = "null"
        user_shift_or_post = "null"
        user_intake = "null"
        user_section = "null"
        user_address = "null"
        user_status = "null"
        user_blood = "null"
        user_room_no = "null"

        currentStapes = 1

        activity?.let {
            student = it.findViewById(R.id.student_button)
            faculty = it.findViewById(R.id.teacher_button)
            userId = it.findViewById(R.id.sign_up_identification_user_id)
            search = it.findViewById(R.id.sign_up_identification_search_button)
            barcode = it.findViewById(R.id.sign_up_identification_barcode_button)
        }

        nextButton.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
        nextButton.isEnabled = false

        nextButton.text = "NEXT"

        allButtonClickingEventListeners()
        textChangesEventListners()
    }



    private fun allButtonClickingEventListeners(){
        barcode.setOnClickListener{
            val scanner = IntentIntegrator(requireActivity())
            scanner.setDesiredBarcodeFormats(IntentIntegrator.CODE_39)
            scanner.setBeepEnabled(true)
            scanner.initiateScan()
        }

        student.setOnClickListener{
            user_type = student_text.text.toString()
            //userId.text = null
            userId.hint = "Enter $user_type ID"
            faculty.background = ContextCompat.getDrawable(requireContext(), R.drawable.squar_white_button)
            student.background = ContextCompat.getDrawable(requireContext(), R.drawable.sign_up_user_type_button)
        }

        faculty.setOnClickListener{
            user_type = teacher_text.text.toString()
            //userId.text = null
            userId.hint = "Enter $user_type ID"
            faculty.background = ContextCompat.getDrawable(requireContext(), R.drawable.sign_up_user_type_button)
            student.background = ContextCompat.getDrawable(requireContext(), R.drawable.squar_white_button)
        }

        nextButton.setOnClickListener{
            findUser(userId.text.toString())
        }

    }

    private fun textChangesEventListners(){
        userId.addTextChangedListener(object : TextWatcher { override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 11) {
                    user_id = userId.text.toString()
                    userId.onEditorAction(EditorInfo.IME_ACTION_DONE)
                    nextButton.background = resources.getDrawable(R.drawable.intro_button_green)
                    nextButton.isEnabled = true
                }else{
                    nextButton.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
                    nextButton.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun parseStudentData(url: String){
        var check:Boolean = false

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        type_http = builder.build()

        val request = Request.Builder().url(url).build()

        type_http.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                check = false
                activity!!.runOnUiThread {
                    search.setImageResource(R.drawable.database_error)
                    if(e.message?.contains("Unable")!!){
                        Toasty.error(requireContext(), "No Internet", Toast.LENGTH_SHORT, true).show()
                    }else if(e.message?.contains("failed")!!){
                        Toasty.info(requireContext(), "Time out", Toast.LENGTH_SHORT, true).show()
                    }else{
                        Toasty.error(requireContext(), "An unknown error", Toast.LENGTH_SHORT, true).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {
                                val responseObj = JSONObject(response.body()!!.string())
                                user_id = responseObj.getString("sis_std_id")
                                user_name = responseObj.getString("sis_std_name")
                                user_gender = responseObj.getString("sis_std_gender")
                                //user_dob, user_phone_old, user_phone_new, user_password, user_image
                                user_prog_or_dept = responseObj.getString("sis_std_prgrm_sn")
                                user_faculty_name = userPostToFaculty(user_prog_or_dept)
                                user_shift_or_post
                                user_intake = responseObj.getString("sis_std_intk")
                                //user_section
                                user_address = responseObj.getString("sis_std_Bplace")
                                user_status
                                user_blood = responseObj.getString("sis_std_blood")

                                if(user_gender == "M" || user_gender == "Male"){
                                    user_image = "https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/user%20image%2FAvater%2Fsm0.png?alt=media&token=875f7545-28ed-4c5c-a06f-18379cb2d52b"
                                }else{
                                    user_image = "https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/user%20image%2FAvater%2Fsf0.png?alt=media&token=a96ecff3-50da-4726-811d-d1b127465f56"
                                }


                                check = true

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    search.setImageResource(R.drawable.database_error)
                                    if(e.message?.contains("org")!!){
                                        view?.let {
                                            make(it, "You are not a registered student of BUBT.", R.drawable.database_error_32,R.color.colorRed, R.color.colorWhite,5000,R.drawable.tick_icon_16,R.color.colorWhite)
                                                .setAction("ADMIT NOW") {
                                                    val uri: Uri = Uri.parse("https://admission.bubt.edu.bd/?fbclid=IwAR1NIo5MR0Rz8XKTiYTMCD75996t7U-oSofPRdrS-abMBt_aUUoTweY-jtI")
                                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                                    startActivity(intent)
                                                }
                                                .show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {
                            search.setImageResource(R.drawable.database_success)
                            //Toast.makeText(requireContext(), user_name,Toast.LENGTH_LONG).show()

                            stepView.go(1,true)

                            fragmentManager!!.beginTransaction().setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,0,0)
                                .replace(R.id.sign_up_fragment_container, SignUpActivity.fragments[1])
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
            }
        })
    }


    private fun parseFacultyData(url: String){
        var check:Boolean = false
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        val request = Request.Builder()
            .url(url)
            .build()
        type_http.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                check = false
                activity!!.runOnUiThread {
                    search.setImageResource(R.drawable.database_error)
                    if(e.message?.contains("Unable")!!){
                        Toasty.error(requireContext(), "No Internet", Toast.LENGTH_SHORT, true).show()
                    }else if(e.message?.contains("failed")!!){
                        Toasty.info(requireContext(), "Time out", Toast.LENGTH_SHORT, true).show()
                    }else{
                        Toasty.error(requireContext(), "An unknown error", Toast.LENGTH_SHORT, true).show()
                    }
                }
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {
                                val responseString:String = response.body()!!.string().replace("[","")
                                val responseObj = JSONObject(responseString)

                                user_id = responseObj.getString("EmpId")
                                user_name = responseObj.getString("EmpName")
                                user_gender = responseObj.getString("Gender")
                                user_dob = responseObj.getString("DOB")
                                user_phone_old = responseObj.getString("ECNo")
                                //user_phone_new, user_password, user_image
                                user_prog_or_dept = responseObj.getString("DeptName")
                                user_faculty_name = userPostToFaculty(user_prog_or_dept)
                                user_shift_or_post = responseObj.getString("PosName")
                                //user_intake, user_section
                                user_address = responseObj.getString("PermanentAddress")
                                user_status = responseObj.getString("StatusId")
                                user_blood = responseObj.getString("BloodGroup")


                                if(user_gender == "M" || user_gender == "Male"){
                                    user_image = "https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/user%20image%2FMale.png?alt=media&token=872c423e-af61-4b9f-8caf-11a810489f3b"
                                }else{
                                    user_image = "https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/user%20image%2FFemale.png?alt=media&token=a55cb1ba-5146-4246-b7bb-d9e9f0e3b7b0"
                                }

                                check = true
                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    search.setImageResource(R.drawable.database_error)
                                    if(e.message?.contains("No")!!){
                                        view?.let {
                                            make(it, "You are not a faculty member of BUBT.", R.drawable.database_error_32,R.color.colorRed, R.color.colorWhite,5000,R.drawable.tick_icon_16,R.color.colorWhite)
                                                .setAction("FIND JOB") {
                                                    val uri: Uri = Uri.parse("http://m.bdjobstoday.com/company.php?company=Bangladesh%20University%20of%20Business%20and%20Technology%20-BUBT")
                                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                                    startActivity(intent)
                                                }
                                                .show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                activity!!.runOnUiThread {
                    when {
                        check -> {
                            search.setImageResource(R.drawable.database_success)
                            //Toast.makeText(requireContext(), user_name,Toast.LENGTH_LONG).show()

                            stepView.go(1,true)
                            fragmentManager!!.beginTransaction().setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,0,0)
                                .replace(R.id.sign_up_fragment_container, SignUpActivity.fragments[1])
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
            }
        })
    }

    //StudLab.studentList[i].user_id == id && user_type == "Student" || StudLab.studentList[i].user_id == id && user_type == "Faculty"

    private fun findUser(id:String) {
        search.setImageResource(R.drawable.database_loading)
        var flag:Int = 1

        if(userList.size != 0)
        if(userList.any { it.user_id == user_id }){

            search.setImageResource(R.drawable.database_success)
            view?.let {
                make(it, "You are already a registered user.\nTry Log In.", R.drawable.user_info_32,R.color.colorBlue2, R.color.colorWhite,5000,R.drawable.login_icon_16,R.color.colorWhite)
                    .setAction("LOG IN") {
                        startActivity(Intent(requireContext(),LogInActivity::class.java))
                    }
                    .show()
            }

        }else{
            if(user_type == "Student"){
                parseStudentData("${annexApis!!.annexVerify}$id&type=stdVerify")
            }else{
                parseFacultyData("${annexApis!!.annexVerify}$id&type=empVerify")
            }
        }




        /*for(i in 0 until StudLab.userList.size){
            if(StudLab.userList[i].user_id == user_id){
                search.setImageResource(R.drawable.database_success)
                view?.let {
                    make(it, "You are already a registered user.\nTry Log In.", R.drawable.user_info_32,R.color.colorBlue2, R.color.colorWhite,5000,R.drawable.login_icon_16,R.color.colorWhite)
                        .setAction("LOG IN") {
                            startActivity(Intent(requireContext(),LogInActivity::class.java))
                        }
                        .show()
                }
                flag = 1
                break
            }else{
                flag = 0;
            }
        }

        if(flag == 0 && user_type == "Student" ||  StudLab.userList.size == 0 && user_type == "Student" ){
            parseStudentData("https://www.bubt.edu.bd/global_file/getData.php?id=$id&type=stdVerify")
        }else if(flag == 0 && user_type == "Faculty" || StudLab.userList.size == 0 && user_type == "Faculty"){
            parseFacultyData("https://www.bubt.edu.bd/global_file/getData.php?id=$id&type=empVerify")
        }*/
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toasty.warning(requireContext(), "Operation unsuccessful", Toast.LENGTH_SHORT, true).show()
                } else {
                    userId.setText(result.contents)
                    findUser(result.contents)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}