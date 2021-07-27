import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.AnnexFragment.AnnexFeesSemesterFragment
import com.newage.studlab.Home.AnnexFragment.AnnexResultSemesterFragment
import com.newage.studlab.Home.CourseFragment.AllCourseFragment
import com.newage.studlab.Home.CourseFragment.PresentCourseFragment
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Home.RoutineFragment.SmartRoutineFragment
import com.newage.studlab.Model.AnnexModel.AnnexFees
import com.newage.studlab.Model.AnnexModel.AnnexResults
import com.newage.studlab.Model.AnnexModel.SmartRoutine
import com.newage.studlab.Model.RoutineModel.RoutineConfig
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.Plugins.StudLabAssistant.Companion.textSemesterToOrdinalValue
import com.newage.studlab.Plugins.StudLabGenerate
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialogue_annex_login.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class AnnexHomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_annex_home, container, false)
    }

    companion object{
        //smart routine
        var smartRtn = ArrayList<SmartRoutine>()
        var thisDay:String = ""
        var classesList = ArrayList<SmartRoutine>()


        //fees
        var feesWaiver = ArrayList<AnnexFees>()
        var Total_Demand:String = ""
        var Total_Due:String = ""
        var Total_Paid:String = ""
        var Total_Waiver:String = ""


        //results
        var results = ArrayList<AnnexResults>()
        var cgpa:String = ""
        var sgpa:String = ""
        var semester:String = ""


    }

    //smart routine
    lateinit var routine: JSONArray
    var smartRoutine = ArrayList<SmartRoutine>()

    //fees & waiver
    lateinit var fees: JSONArray

    //fees & waiver
    lateinit var res: JSONArray

    //api links
    var logInApi:String = ""
    var feesApi:String = ""
    var resultApi:String = ""
    var routineApi:String = ""

    lateinit var processAnnexHome: ProgressDialog

    //call api attiributes
    private var typeHttp = OkHttpClient()

    //header info
    lateinit var annexStdImage:ImageView
    lateinit var annexStdName:TextView
    lateinit var annexStdId:TextView
    lateinit var annexStdDept:TextView
    lateinit var annexStdIntake:TextView
    lateinit var annexStdSection:TextView
    lateinit var annexStdShift:TextView
    lateinit var annexSemester:TextView
    lateinit var annexSGpa:TextView
    lateinit var annexCGpa:TextView
    lateinit var annexStdGender:TextView
    lateinit var annexStdBlood:TextView
    lateinit var annexStdDob:TextView

    //annex header button
    lateinit var annexHeaderRefreshButton: ImageView

    //item_button
    lateinit var summaryButton:Button
    lateinit var routineButton:Button
    lateinit var feesWaiverButton:Button
    lateinit var allCourseButton:Button
    lateinit var previousCourseButton:Button
    lateinit var presentCourseButton:Button


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //annex api
        logInApi = StudLab.annexApis!!.annexLogin
        feesApi = StudLab.annexApis!!.annexFees
        resultApi = StudLab.annexApis!!.annexResult
        routineApi = StudLab.annexApis!!.annexRoutine

        processAnnexHome = ProgressDialog(requireContext()).apply {
            setTitle("Connecting....")
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }

        fragmentPosition = 0.0
        fragmentName = "AnnexHomeFragment"

        activity?.let {

            //header info
            annexStdImage = it.findViewById(R.id.annex_std_image)
            annexStdName = it.findViewById(R.id.annex_std_name)
            annexStdId = it.findViewById(R.id.annex_std_id)
            annexStdDept = it.findViewById(R.id.annex_std_prog)
            annexStdIntake = it.findViewById(R.id.annex_std_intake)
            annexStdSection = it.findViewById(R.id.annex_std_section)
            annexStdShift = it.findViewById(R.id.annex_std_shift)

            annexSemester = it.findViewById(R.id.annex_std_semester)
            annexSGpa = it.findViewById(R.id.annex_std_s_gpa)
            annexCGpa = it.findViewById(R.id.annex_std_c_gpa)

            annexStdGender = it.findViewById(R.id.annex_std_gender)
            annexStdBlood = it.findViewById(R.id.annex_std_blood_group)
            annexStdDob = it.findViewById(R.id.annex_std_dob)

            //annex button
            annexHeaderRefreshButton = it.findViewById(R.id.annex_header_refesh)

            //item_button
            summaryButton = it.findViewById(R.id.summary_button)
            routineButton = it.findViewById(R.id.routine_button)
            feesWaiverButton = it.findViewById(R.id.fees_waiver_button)
            allCourseButton = it.findViewById(R.id.all_course_button)
            previousCourseButton = it.findViewById(R.id.previous_cour_button)
            presentCourseButton = it.findViewById(R.id.present_cour_button)

        }



        processLogin()
        loadAnnexHeaderInfo()
        allButtonClickEventListeners()
    }

    private fun loadAnnexHeaderInfo(){

        val routineDatabase = DatabaseHelper(requireContext())
        val user = routineDatabase.getUserData()!!

        if(user.user_image.isNotEmpty()){
            Picasso.get().load(user.user_image).networkPolicy(NetworkPolicy.OFFLINE).into(annexStdImage, object: com.squareup.picasso.Callback{
                override fun onSuccess() { }
                override fun onError(e: Exception?) {
                    Picasso.get().load(user.user_image).into(annexStdImage)
                }
            })
        }

        annexStdName.text = user.user_name
        annexStdId.text = user.user_id
        annexStdDept.text = user.user_prog_or_dept
        annexStdIntake.text = user.user_intake
        annexStdSection.text = user.user_section
        annexStdShift.text = user.user_shift_or_post

        annexSemester.text = textSemesterToOrdinalValue(user.user_semester)

        val userResult = StudLabGenerate().loadMyResult()
        if(userResult.isNotEmpty()){
            if(userResult[userResult.size-1].Student_Cgpa.isNotEmpty()){

                annexSGpa.text = userResult[userResult.size-1].Student_Sgpa
                annexCGpa.text = userResult[userResult.size-1].Student_Cgpa

            }
        }

        annexStdGender.text = user.user_gender

        if(user.user_blood.length > 4){
            annexStdBlood.text = "---"
        }else{
            annexStdBlood.text = user.user_blood
        }

        annexStdDob.text = user.user_dob
    }


    private fun allButtonClickEventListeners(){
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)

        annexHeaderRefreshButton.setOnClickListener{
            if(StudLab.currentUser!!.annex_id != "null" && StudLab.encryption.decryptOrNull(StudLab.currentUser!!.annex_pass) != "null" && StudLab.currentUser!!.annex_session_id != "null"){

                val annexLoginWithSession = "${logInApi}id=${StudLab.currentUser!!.annex_id}&pass=${StudLab.encryption.decryptOrNull(
                    StudLab.currentUser!!.annex_pass)}&phpsessid=${StudLab.currentUser!!.annex_session_id}"
                annexLoginWithSessionId(annexLoginWithSession)

            }else{
                activity!!.showAnnexLoginDialog(StudLab.currentUser!!.user_id)
            }
        }


        summaryButton.setOnClickListener{
            Toasty.info(requireContext(), "Upcoming: annex feature", Toast.LENGTH_SHORT, true).show()
        }

        routineButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, SmartRoutineFragment())
            transaction.commit()
        }

        feesWaiverButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, AnnexFeesSemesterFragment())
            transaction.commit()
        }

        allCourseButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, AllCourseFragment())
            transaction.commit()
        }

        previousCourseButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, AnnexResultSemesterFragment())
            transaction.commit()
        }

        presentCourseButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, PresentCourseFragment())
            transaction.commit()
        }


    }

    private fun processLogin(){

        if(smartRoutine.isEmpty() || feesWaiver.isEmpty() || results.isEmpty()){
            if(StudLab.currentUser!!.annex_id != "null" && StudLab.encryption.decryptOrNull(StudLab.currentUser!!.annex_pass) != "null" && StudLab.currentUser!!.annex_session_id != "null"){

                val annexLoginWithSession = "${logInApi}id=${StudLab.currentUser!!.annex_id}&pass=${StudLab.encryption.decryptOrNull(
                    StudLab.currentUser!!.annex_pass)}&phpsessid=${StudLab.currentUser!!.annex_session_id}"
                annexLoginWithSessionId(annexLoginWithSession)

            }else{
                activity!!.showAnnexLoginDialog(StudLab.currentUser!!.user_id)
            }
        }

    }

    @SuppressLint("InflateParams")
    private fun Activity.showAnnexLoginDialog(id:String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.dialogue_annex_login, null)
        dialogBuilder.setView(dialogView)

        val dialogueView = dialogView.containers

        val annexLogo = dialogView.annex_logo
        val crossButton = dialogView.close_button

        val userId = dialogView.log_in_user_id
        val userPass = dialogView.log_in__password

        val forgetPass = dialogView.annex_forget_pass
        val logIn = dialogView.annex_login

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        dialogueView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.setCancelable(false)
        alertDialog.show()

        //blinkingView(annexLogo,true)

        userId.isEnabled = false

        //load data
        if(StudLab.currentUser!!.annex_id != "null")
            userId.setText(id)

        annexLogo.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.annex.bubt.edu.bd/"))
            startActivity(i)
        }

        crossButton.setOnClickListener{
            alertDialog.dismiss()
        }

        forgetPass.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.annex.bubt.edu.bd/?daikonPage=d808b8bde944a42985023"))
            startActivity(i)
        }

        logIn.setOnClickListener{
            val pass = userPass.text.toString()

            if(id.isNotEmpty() && pass.isNotEmpty()){

                alertDialog.dismiss()
                annexLogin(id,pass)

            }else if(id.isEmpty()){
                userId.error = "??"
            }else if(pass.isEmpty()){
                userPass.error = "??"
            }
        }
    }

    private fun annexLogin(id:String,pass:String){

        processAnnexHome.show()
        processAnnexHome.setMessage("Please wait...")

        val url = "${logInApi}id=$id&pass=$pass"

        var check = false
        var phpSessionId = ""

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        typeHttp = builder.build()

        val request = Request.Builder().url(url).build()

        typeHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                check = false
                activity!!.runOnUiThread {
                    processAnnexHome.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            activity!!.runOnUiThread {
                                processAnnexHome.dismiss()
                                Toasty.error(requireContext(), "Error: Annex login!!", Toast.LENGTH_SHORT, true).show()
                            }
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {
                                check = true
                                val responseObj = JSONObject(response.body()!!.string())
                                phpSessionId = responseObj.getString("PHPSESSID")

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processAnnexHome.dismiss()
                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {

                            Toasty.success(requireContext(), "Connected", Toast.LENGTH_LONG, true).show()
                            val user = Users(
                                StudLab.currentUser!!.user_id,
                                StudLab.currentUser!!.user_name,
                                StudLab.currentUser!!.user_gender,
                                StudLab.currentUser!!.user_dob,
                                StudLab.currentUser!!.user_phone_old,
                                StudLab.currentUser!!.user_phone_new,
                                StudLab.currentUser!!.user_password,
                                StudLab.currentUser!!.user_type,
                                StudLab.currentUser!!.user_image,
                                StudLab.currentUser!!.user_faculty_name,
                                StudLab.currentUser!!.user_prog_or_dept,
                                StudLab.currentUser!!.user_shift_or_post,
                                StudLab.currentUser!!.user_intake,
                                StudLab.currentUser!!.user_section,
                                StudLab.currentUser!!.user_semester,
                                StudLab.currentUser!!.user_address,
                                StudLab.currentUser!!.user_status,
                                StudLab.currentUser!!.user_blood,
                                StudLab.currentUser!!.user_room_no, id,
                                StudLab.encryption.encryptOrNull(pass),phpSessionId)

                            val userDatabase = DatabaseHelper(requireContext())
                            userDatabase.deleteUserData()
                            userDatabase.saveUserData(user)

                            val ref = FirebaseDatabase.getInstance().getReference("/Users/$id")
                            ref.setValue(user).addOnSuccessListener {}
                            ref.setValue(user).addOnFailureListener{}

                            if(smartRoutine.isEmpty())
                                downloadSmartRoutine("${routineApi}phpsessid=$phpSessionId")

                            if (feesWaiver.isEmpty())
                                parseFeesData("${feesApi}phpsessid=$phpSessionId")

                            if(results.isEmpty())
                                parseResultsData("${resultApi}phpsessid=$phpSessionId")

                        }
                    }
                }
            }
        })


    }

    private fun annexLoginWithSessionId(url:String){

        processAnnexHome.show()
        processAnnexHome.setMessage("Please wait...")

        var check = false
        var phpSessionId = ""

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        typeHttp = builder.build()

        val request = Request.Builder().url(url).build()

        typeHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                check = false
                activity!!.runOnUiThread {
                    processAnnexHome.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            activity!!.runOnUiThread {
                                processAnnexHome.dismiss()
                                Toasty.error(requireContext(), "Error: Annex login!!", Toast.LENGTH_SHORT, true).show()
                            }
                            check = false
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {
                                check = true
                                //smartRoutine.clear()
                                val responseObj = JSONObject(response.body()!!.string())
                                phpSessionId = responseObj.getString("PHPSESSID")

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processAnnexHome.dismiss()
                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {
                            Toasty.success(requireContext(), "Connected", Toast.LENGTH_LONG, true).show()

                            if(smartRoutine.isEmpty())
                            downloadSmartRoutine("${routineApi}phpsessid=$phpSessionId")

                            if (feesWaiver.isEmpty())
                            parseFeesData("${feesApi}phpsessid=$phpSessionId")

                            if(results.isEmpty())
                            parseResultsData("${resultApi}phpsessid=$phpSessionId")

                        }
                    }
                }
            }
        })
    }

    //--------------------------------------------------------Load Routine----------------------------------------------------------
    private fun downloadSmartRoutine(url:String){
        processAnnexHome.show()
        processAnnexHome.setTitle("Downloading Routine")

        var check = false

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        typeHttp = builder.build()

        val request = Request.Builder().url(url).build()

        typeHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                check = false
                activity!!.runOnUiThread {
                    processAnnexHome.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            activity!!.runOnUiThread {
                                processAnnexHome.dismiss()
                                Toasty.error(requireContext(), "Error: By Annex!!", Toast.LENGTH_SHORT, true).show()
                            }
                            check = false
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {
                                check = true
                                smartRoutine.clear()
                                val responseObj = JSONObject(response.body()!!.string())
                                routine = responseObj.getJSONArray("data")

                                val body = routine.toString()
                                val gson = GsonBuilder().create()

                                val listDayClass = object : TypeToken<ArrayList<SmartRoutine>>() {}.type
                                smartRoutine  = gson.fromJson(body, listDayClass)

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processAnnexHome.dismiss()
                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {
                            processAnnexHome.dismiss()
                            Toasty.success(requireContext(), "Routine Is Ready", Toast.LENGTH_LONG, true).show()

                            //loadSmartRoutine()
                        }
                    }
                }
            }
        })
    }

    //--------------------------------------------------------Load Fees----------------------------------------------------------
    private fun parseFeesData(url: String){
        processAnnexHome.show()
        processAnnexHome.setTitle("Downloading Fees & Waiver")

        var check = false

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        typeHttp = builder.build()

        val request = Request.Builder().url(url).build()

        typeHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                check = false
                activity!!.runOnUiThread {
                    processAnnexHome.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            activity!!.runOnUiThread {
                                processAnnexHome.dismiss()
                                Toasty.error(requireContext(), "Error: By Annex!!", Toast.LENGTH_SHORT, true).show()
                            }
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {
                                check = true
                                feesWaiver.clear()
                                val responseObj = JSONObject(response.body()!!.string())
                                fees = responseObj.getJSONArray("data")

                                val result = responseObj.getJSONObject("result")
                                Total_Demand = result.getString("Total_Demand")
                                Total_Due = result.getString("Total_Due")
                                Total_Paid = result.getString("Total_Paid")
                                Total_Waiver = result.getString("Total_Waiver")


                                val body = fees.toString()
                                val gson = GsonBuilder().create()

                                val listDayClass = object : TypeToken<ArrayList<AnnexFees>>() {}.type
                                feesWaiver = gson.fromJson(body, listDayClass)


                                activity!!.runOnUiThread{
                                    processAnnexHome.dismiss()
                                }

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processAnnexHome.dismiss()
                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {

                            processAnnexHome.dismiss()
                            Toasty.success(requireContext(), "Fees Is Ready", Toast.LENGTH_LONG, true).show()


                        }
                    }
                }
            }
        })
    }


    private fun parseResultsData(url: String){
        processAnnexHome.show()
        processAnnexHome.setTitle("Downloading Results")

        var check = false

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        typeHttp = builder.build()

        val request = Request.Builder().url(url).build()

        typeHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                check = false
                activity!!.runOnUiThread {
                    processAnnexHome.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            activity!!.runOnUiThread {
                                processAnnexHome.dismiss()
                                Toasty.error(requireContext(), "Error: By Annex!!", Toast.LENGTH_SHORT, true).show()
                            }
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {

                                check = true
                                results.clear()
                                val responseObj = JSONObject(response.body()!!.string())
                                res = responseObj.getJSONArray("data")



                                val body = res.toString()
                                val gson = GsonBuilder().create()

                                val listPrevResult = object : TypeToken<ArrayList<AnnexResults>>() {}.type
                                results = gson.fromJson(body, listPrevResult)


                                activity!!.runOnUiThread{
                                    processAnnexHome.dismiss()
                                }

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processAnnexHome.dismiss()

                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {

                            processAnnexHome.dismiss()
                            Toasty.success(requireContext(), "Previous Course Is Ready", Toast.LENGTH_LONG, true).show()


                        }
                    }
                }
            }
        })
    }



}