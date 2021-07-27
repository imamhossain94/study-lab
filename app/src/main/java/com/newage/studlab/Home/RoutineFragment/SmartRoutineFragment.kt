package com.newage.studlab.Home.RoutineFragment

import HomeFragment
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.newage.studlab.Adapter.AnnexAdapter.RoutineDayRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.annexApis
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.encryption
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.AnnexModel.RoutineDay
import com.newage.studlab.Model.AnnexModel.SmartRoutine
import com.newage.studlab.Model.RoutineModel.RoutineConfig
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.Plugins.StudLabGenerate
import com.newage.studlab.R
import com.newage.studlab.Services.AlarmBroadcastReceiver
import com.newage.studlab.Services.SrNotificationService.NotificationEventReceiver


import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialogue_annex_login.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext


class SmartRoutineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_smart_routine, container, false)
    }

    //apis -------------------------


    var logInApi:String = ""
    var routineApi:String = ""


    lateinit var progressSmartRoutine:ProgressDialog

    private var typeHttp = OkHttpClient()
    lateinit var routine: JSONArray
    var smartRoutine = ArrayList<SmartRoutine>()

    companion object{
        var smartRtn = ArrayList<SmartRoutine>()
        var thisDay:String = ""
        var classesList = ArrayList<SmartRoutine>()
    }

    lateinit var backButton: ImageView
    private lateinit var loadingClass: ProgressBar

    lateinit var gotoLink:ImageView

    lateinit var lastUpdate:TextView
    lateinit var refreshButton: ImageView
    lateinit var recyclerView: RecyclerView

    lateinit var adapter: RoutineDayRecyclerViewAdapter

    override fun onStart() {
        super.onStart()
        //AlarmBroadcastReceiver().setAlarm(appContext)
    }


    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //NotificationEventReceiver.setupAlarm(appContext)
        AlarmBroadcastReceiver().setAlarm(appContext)

        logInApi = annexApis!!.annexLogin
        routineApi = annexApis!!.annexRoutine



        progressSmartRoutine = ProgressDialog(requireContext()).apply {
            setTitle("Connecting....")
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }

        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            loadingClass = it.findViewById(R.id.loading_class)

            gotoLink = it.findViewById(R.id.fragment_link_button)

            lastUpdate = it.findViewById(R.id.smart_routine_last_update)
            refreshButton = it.findViewById(R.id.smart_routine_refresh_button)

            recyclerView = it.findViewById(R.id.routine_recycler_view)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RoutineDayRecyclerViewAdapter(context, ArrayList(), clickListener = {
            dayClickListener(it)
        })

        recyclerView.adapter = adapter

        fragmentName = "smartRoutine"
        fragmentPosition = 0.0

        loadSmartRoutine()
        allButtonClickingEventListeners()

        //load some data----------------
        val databaseHandler = DatabaseHelper(requireContext())
        val lUpdate:RoutineConfig? = databaseHandler.getSmartRoutineUpdateDate()

        if(lUpdate != null) {
            lastUpdate.text = "Update: ${lUpdate.LastUpdate}"
        }
    }

    @SuppressLint("DefaultLocale")
    private fun loadSmartRoutine(){
        val databaseHandler = DatabaseHelper(requireContext())
        smartRtn = databaseHandler.getSmartRoutine()
        if(smartRtn.size != 0){
            adapter.filterDayList(StudLabGenerate().createSmartRoutineService())

            val calendar = Calendar.getInstance()

            /*when (val day = calendar.time.toString().substring(0, 3).toLowerCase()) {
                "sat" -> {
                    NotificationService().runNotification(smartRtn.filter { s ->
                        s.Day.toLowerCase().contains(
                            day
                        ) && s.Subject_Code != ""
                    } as ArrayList<SmartRoutine>, day)
                }
                "sun" -> {
                    NotificationService().runNotification(smartRtn.filter { s ->
                        s.Day.toLowerCase().contains(
                            day
                        ) && s.Subject_Code != ""
                    } as ArrayList<SmartRoutine>, day)
                }
                "mon" -> {
                    NotificationService().runNotification(smartRtn.filter { s ->
                        s.Day.toLowerCase().contains(
                            day
                        ) && s.Subject_Code != ""
                    } as ArrayList<SmartRoutine>, day)
                }
                "tue" -> {
                    NotificationService().runNotification(smartRtn.filter { s ->
                        s.Day.toLowerCase().contains(
                            day
                        ) && s.Subject_Code != ""
                    } as ArrayList<SmartRoutine>, day)
                }
                "wed" -> {
                    NotificationService().runNotification(smartRtn.filter { s ->
                        s.Day.toLowerCase().contains(
                            day
                        ) && s.Subject_Code != ""
                    } as ArrayList<SmartRoutine>, day)
                }
                "thu" -> {
                    NotificationService().runNotification(smartRtn.filter { s ->
                        s.Day.toLowerCase().contains(
                            day
                        ) && s.Subject_Code != ""
                    } as ArrayList<SmartRoutine>, day)
                }
                "fri" -> {
                    NotificationService().runNotification(smartRtn.filter { s ->
                        s.Day.toLowerCase().contains(
                            day
                        ) && s.Subject_Code != ""
                    } as ArrayList<SmartRoutine>, day)
                }
            }*/
        }else{
            adapter.filterDayList(ArrayList())

            if(currentUser!!.annex_id != "null" && encryption.decryptOrNull(currentUser!!.annex_pass) != "null" && currentUser!!.annex_session_id != "null"){

                val annexLoginWithSession = "${logInApi}id=${currentUser!!.annex_id}&pass=${encryption.decryptOrNull(
                    currentUser!!.annex_pass
                )}&phpsessid=${currentUser!!.annex_session_id}"
                annexLoginWithSessionId(annexLoginWithSession)

            }else{
                requireActivity().showAnnexLoginDialog(currentUser!!.user_id)
            }

        }


    }

    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = requireFragmentManager().beginTransaction()
            transaction.setCustomAnimations(0, 0, 0, 0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }

        refreshButton.setOnClickListener{
            if(currentUser!!.annex_id != "null" && encryption.decryptOrNull(currentUser!!.annex_pass) != "null" && currentUser!!.annex_session_id != "null"){

                val annexLoginWithSession = "${logInApi}id=${currentUser!!.annex_id}&pass=${encryption.decryptOrNull(
                    currentUser!!.annex_pass
                )}&phpsessid=${currentUser!!.annex_session_id}"
                annexLoginWithSessionId(annexLoginWithSession)

            }else{
                requireActivity().showAnnexLoginDialog(currentUser!!.user_id)
            }
        }

        gotoLink.setOnClickListener{
            Toasty.info(
                appContext,
                "A beautiful view of smart routine will be found here after upcoming update.",
                Toast.LENGTH_LONG,
                true
            ).show()
        }
    }


    private fun dayClickListener(day: RoutineDay){

        thisDay = day.routine_day

        when (thisDay) {
            "Saturday" -> {
                classesList =
                    smartRtn.filter { s -> s.Day == thisDay && s.Subject_Code != "" } as ArrayList<SmartRoutine>
            }
            "Sunday" -> {
                classesList =
                    smartRtn.filter { s -> s.Day == thisDay && s.Subject_Code != "" } as ArrayList<SmartRoutine>
            }
            "Monday" -> {
                classesList =
                    smartRtn.filter { s -> s.Day == thisDay && s.Subject_Code != "" } as ArrayList<SmartRoutine>
            }
            "Tuesday" -> {
                classesList =
                    smartRtn.filter { s -> s.Day == thisDay && s.Subject_Code != "" } as ArrayList<SmartRoutine>
            }
            "Wednesday" -> {
                classesList =
                    smartRtn.filter { s -> s.Day == thisDay && s.Subject_Code != "" } as ArrayList<SmartRoutine>
            }
            "Thursday" -> {
                classesList =
                    smartRtn.filter { s -> s.Day == thisDay && s.Subject_Code != "" } as ArrayList<SmartRoutine>
            }
            "Friday" -> {
                classesList =
                    smartRtn.filter { s -> s.Day == thisDay && s.Subject_Code != "" } as ArrayList<SmartRoutine>
            }
        }

        val transaction = requireFragmentManager().beginTransaction()
        transaction.setCustomAnimations(0, 0, 0, 0)
        transaction.replace(R.id.main_home_fragment_container, SmartRoutineClassFragment())
        transaction.commit()
    }

    @SuppressLint("InflateParams")
    private fun Activity.showAnnexLoginDialog(id: String) {

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
        if(currentUser!!.annex_id != "null")
        userId.setText(id)

        annexLogo.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.annex.bubt.edu.bd/"))
            startActivity(i)
        }

        crossButton.setOnClickListener{
            alertDialog.dismiss()
        }

        forgetPass.setOnClickListener{
            val i = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.annex.bubt.edu.bd/?daikonPage=d808b8bde944a42985023")
            )
            startActivity(i)
        }

        logIn.setOnClickListener{
            val pass = userPass.text.toString()

            if(id.isNotEmpty() && pass.isNotEmpty()){

                alertDialog.dismiss()
                annexLogin(id, pass)

            }else if(id.isEmpty()){
                userId.error = "??"
            }else if(pass.isEmpty()){
                userPass.error = "??"
            }
        }
    }

    //currentUser
    //https://antheaxs2-temp.herokuapp.com/api/bubt/routine?phpsessid=62f923d8cc529eff1fcf117c8262986

    private fun annexLogin(id: String, pass: String){

        progressSmartRoutine.show()
        progressSmartRoutine.setMessage("Please wait...")

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
                requireActivity().runOnUiThread {
                    progressSmartRoutine.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            requireActivity().runOnUiThread {
                                progressSmartRoutine.dismiss()
                                Toasty.error(
                                    requireContext(),
                                    "Error: Annex login!!",
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
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
                                requireActivity().runOnUiThread {
                                    progressSmartRoutine.dismiss()
                                    Toasty.error(
                                        requireContext(),
                                        e.message!!,
                                        Toast.LENGTH_LONG,
                                        true
                                    ).show()
                                }
                            }
                        }
                    }
                }

                requireActivity().runOnUiThread {
                    when {
                        check -> {

                            Toasty.success(requireContext(), "Connected", Toast.LENGTH_LONG, true)
                                .show()
                            val user = Users(
                                currentUser!!.user_id,
                                currentUser!!.user_name,
                                currentUser!!.user_gender,
                                currentUser!!.user_dob,
                                currentUser!!.user_phone_old,
                                currentUser!!.user_phone_new,
                                currentUser!!.user_password,
                                currentUser!!.user_type,
                                currentUser!!.user_image,
                                currentUser!!.user_faculty_name,
                                currentUser!!.user_prog_or_dept,
                                currentUser!!.user_shift_or_post,
                                currentUser!!.user_intake,
                                currentUser!!.user_section,
                                currentUser!!.user_semester,
                                currentUser!!.user_address,
                                currentUser!!.user_status,
                                currentUser!!.user_blood,
                                currentUser!!.user_room_no,
                                id,
                                encryption.encryptOrNull(pass),
                                phpSessionId
                            )

                            val userDatabase = DatabaseHelper(requireContext())
                            userDatabase.deleteUserData()
                            userDatabase.saveUserData(user)

                            val ref = FirebaseDatabase.getInstance().getReference("/Users/$id")
                            ref.setValue(user).addOnSuccessListener {}
                            ref.setValue(user).addOnFailureListener {}

                            downloadSmartRoutine("${routineApi}phpsessid=$phpSessionId")
                        }
                    }
                }
            }
        })


    }

    private fun annexLoginWithSessionId(url: String){

        progressSmartRoutine.show()
        progressSmartRoutine.setMessage("Please wait...")

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
                requireActivity().runOnUiThread {
                    progressSmartRoutine.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            requireActivity().runOnUiThread {
                                progressSmartRoutine.dismiss()
                                Toasty.error(
                                    requireContext(),
                                    "Error: Annex login!!",
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
                            }
                            check = false
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {
                                check = true
                                smartRoutine.clear()
                                val responseObj = JSONObject(response.body()!!.string())
                                phpSessionId = responseObj.getString("PHPSESSID")

                            } catch (e: JSONException) {
                                check = false
                                requireActivity().runOnUiThread {
                                    progressSmartRoutine.dismiss()
                                    Toasty.error(
                                        requireContext(),
                                        e.message!!,
                                        Toast.LENGTH_LONG,
                                        true
                                    ).show()
                                }
                            }
                        }
                    }
                }

                requireActivity().runOnUiThread {
                    when {
                        check -> {
                            Toasty.success(requireContext(), "Connected", Toast.LENGTH_LONG, true)
                                .show()
                            downloadSmartRoutine("${routineApi}phpsessid=$phpSessionId")
                        }
                    }
                }
            }
        })
    }


    private fun downloadSmartRoutine(url: String){

        progressSmartRoutine.setTitle("Downloading")

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
                requireActivity().runOnUiThread {
                    progressSmartRoutine.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            requireActivity().runOnUiThread {
                                progressSmartRoutine.dismiss()
                                Toasty.error(
                                    requireContext(),
                                    "Error: Annex routine!!",
                                    Toast.LENGTH_SHORT,
                                    true
                                ).show()
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

                                val listDayClass =
                                    object : TypeToken<ArrayList<SmartRoutine>>() {}.type
                                smartRoutine = gson.fromJson(body, listDayClass)

                            } catch (e: JSONException) {
                                check = false
                                requireActivity().runOnUiThread {
                                    progressSmartRoutine.dismiss()
                                    Toasty.error(
                                        requireContext(),
                                        e.message!!,
                                        Toast.LENGTH_LONG,
                                        true
                                    ).show()
                                }
                            }
                        }
                    }
                }

                requireActivity().runOnUiThread {
                    when {
                        check -> {
                            progressSmartRoutine.dismiss()
                            Toasty.success(
                                requireContext(),
                                "Routine Is Ready",
                                Toast.LENGTH_LONG,
                                true
                            ).show()
                            val routineDatabase = DatabaseHelper(requireContext())
                            routineDatabase.deleteSmartRoutine()
                            routineDatabase.deleteSmartRoutineUpdateDate()


                            for (i in 0 until smartRoutine.size) {
                                //saveSmartRoutine
                                routineDatabase.saveSmartRoutine(smartRoutine[i])
                            }


                            val date = Calendar.getInstance().time
                            val formatter =
                                SimpleDateFormat.getDateInstance() //or use getDateInstance()
                            val formattedDate = formatter.format(date)

                            routineDatabase.saveSmartRoutineUpdateDate(RoutineConfig(formattedDate))
                            loadSmartRoutine()
                        }
                    }
                }
            }
        })
    }






    /*    val encrypted = encryption!!.encryptOrNull(secretText)
    val decrypted = encryption!!.decryptOrNull(encrypted)*/
}