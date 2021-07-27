package com.newage.studlab.Home

import AnnexHomeFragment
import HomeFragment
import MoreOptionFragment
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.db.williamchart.ExperimentalFeature
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.kknirmale.networkhandler.config.NetworkConfig
import com.kknirmale.networkhandler.listener.NetworkStateListener
import com.kknirmale.networkhandler.utils.NetworkConstant
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.intakeResult
import com.newage.studlab.Application.StudLab.Companion.intakeSemesterResult
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.AnnexFragment.AnnexFeesSemesterFragment
import com.newage.studlab.Home.AnnexFragment.AnnexResultSemesterFragment
import com.newage.studlab.Home.BloodFragment.BloodHomeFragment
import com.newage.studlab.Home.BubtFragment.EventsFragment
import com.newage.studlab.Home.BubtFragment.NoticeBoardFragment
import com.newage.studlab.Home.ResultFragment.IntakeResultSemesterFragment
import com.newage.studlab.Home.ResultFragment.UniversityIntakeFragment
import com.newage.studlab.Home.ResultFragment.UniversityResultProgramFragment
import com.newage.studlab.Home.RoutineFragment.SmartRoutineFragment
import com.newage.studlab.Home.UserFragment.StudentIntakeFragment
import com.newage.studlab.Home.UserFragment.StudentProgramFragment
import com.newage.studlab.Home.UserFragment.TeacherProgramFragment
import com.newage.studlab.Model.Intake
import com.newage.studlab.Model.Results
import com.newage.studlab.Model.SatusModel.Status
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.Plugins.StudLabAssistant.Companion.textSemesterToNumber
import com.newage.studlab.Plugins.StudLabGenerate
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home_main.*
import java.text.SimpleDateFormat
import java.util.*


@ExperimentalFeature
class HomeMainActivity : AppCompatActivity(), NetworkStateListener {

    private var networkConfig : NetworkConfig? = null

    companion object{
        var position = 0
        var fragmentName = ""
        var fragmentPosition = 0.0
        //var
    }

    //--------------------perissions-----------------------
    var permissions: Array<String> = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.VIBRATE,
        Manifest.permission.RECORD_AUDIO
    )



    private lateinit var bottomNav:ChipNavigationBar
    private var previousPosition = 0

    private fun switchContent(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        if(position != previousPosition){

            if(previousPosition > position){
                transaction.setCustomAnimations(
                    R.anim.enter_left_to_right,
                    R.anim.exit_left_to_right,
                    0,
                    0
                )
                transaction.replace(R.id.main_home_fragment_container, fragment)
            }else{
                transaction.setCustomAnimations(
                    R.anim.enter_right_to_left,
                    R.anim.exit_right_to_left,
                    0,
                    0
                )
                transaction.replace(R.id.main_home_fragment_container, fragment)
            }

            transaction.commit()
        }

        previousPosition = position
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)

        //NotificationEventReceiver.setupAlarm(this)


        //network config instance
        networkConfig = NetworkConfig.getInstance()
        //add connectivity listener
        networkConfig!!.addNetworkConnectivityListener(this)


        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setItemSelected(R.id.nav_home)

        bottomNav.setOnItemSelectedListener(object : ChipNavigationBar.OnItemSelectedListener {
            override fun onItemSelected(id: Int) {

                when (id) {
                    R.id.nav_home -> {
                        position = 0
                        switchContent(HomeFragment())
                    }
                    R.id.nav_blood -> {
                        position = 1
                        switchContent(BloodHomeFragment())
                    }
                    R.id.nav_profile -> {
                        position = 2
                        switchContent(ProfileFragment())
                    }
                    R.id.nav_lab -> {
                        position = 3
                        switchContent(StudLabFragment())
                    }
                    R.id.nav_more -> {
                        position = 4
                        switchContent(MoreOptionFragment())
                    }
                    else -> {
                        position = 0
                        switchContent(HomeFragment())
                    }
                }


            }
        })

        if (savedInstanceState == null) {
            val fragment = HomeFragment()
            supportFragmentManager.beginTransaction().replace(
                R.id.main_home_fragment_container,
                fragment
            )
                .commit()
        }



        //Bangladesh University of Business &amp; Technology

        //Picasso.get().load("https://scontent-sin6-2.xx.fbcdn.net/v/t1.0-9/p720x720/91621175_222883658958574_213959203214065664_o.jpg?_nc_cat=105&_nc_sid=85a577&_nc_eui2=AeHtG7D6wKuBTOSuZYrmNRJ0N3p9HevzAAs3en0d6_MAC0Nx2tYcIua-n66w90h8Yd829pSs7eg_Z39BsxXsNnFz&_nc_ohc=i4qEkUO-trgAX9_Onnb&_nc_ht=scontent-sin6-2.xx&_nc_tp=6&oh=9da49929681a167ce5d12da91dc895ef&oe=5EBE4823").into(drawer_header_cover_image)
        //https://www.wallpaperup.com/uploads/wallpapers/2014/07/21/401537/d51bb22ad6b2149b5c2354f7a5a166ec-700.jpg


        val img = "https://weirdnewsfiles.com/wp-content/uploads/2018/01/Technology.jpg"
        Picasso.get().load(img).into(drawer_header_cover_image)

        /*Picasso.get().load(img).networkPolicy(NetworkPolicy.OFFLINE).into(drawer_header_cover_image, object: com.squareup.picasso.Callback{
            override fun onSuccess() {

            }
            override fun onError(e: Exception?) {
                Picasso.get().load(currentUser!!.user_image).into(drawer_header_cover_image)
            }
        })*/

        checkPermissions()

        loadUserData()
        loadIntakeResult()
        allButtonClickingEventListener()
    }

    //-------------------------------checking android permissions---------------------------
    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                100
            )
            return false
        }
        return true
    }


    @SuppressLint("WrongConstant")
    private fun checkDrawerBehaviour(){

        val navDrawer: DrawerLayout = findViewById(R.id.drawer_layout)
        navDrawer.closeDrawer(Gravity.START)

    }




    private fun allButtonClickingEventListener(){


        drawer_header_refresh_button.setOnClickListener{

            loadUserData()
            loadIntakeResult()
            allButtonClickingEventListener()
            checkDrawerBehaviour()

        }




        drawer_button_signout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            val databaseHandler = DatabaseHelper(this)
            databaseHandler.deleteUserData()
            databaseHandler.deleteSmartRoutine()
            databaseHandler.deleteBloodData()
            databaseHandler.deleteSmartRoutine()
            databaseHandler.deleteExamRoutine()

            this.finish()
        }

        drawer_button_home.setOnClickListener{
            checkDrawerBehaviour()

            Handler().postDelayed({
                position = 0
                bottomNav.setItemSelected(R.id.nav_home)
                switchContent(HomeFragment())
            }, 350)


        }

        drawer_button_profile.setOnClickListener{
            checkDrawerBehaviour()

            Handler().postDelayed({
                position = 2
                bottomNav.setItemSelected(R.id.nav_profile)
                switchContent(ProfileFragment())
            }, 350)


        }

        drawer_button_my_classes.setOnClickListener{

            Toasty.info(
                this,
                "Upcoming: The concept of google classroom will be implemented here.",
                Toast.LENGTH_SHORT,
                true
            ).show()

        }

        drawer_button_settings.setOnClickListener{

            Toasty.info(
                this,
                "Time: Due to time shortage setting section has remained untouched.",
                Toast.LENGTH_SHORT,
                true
            ).show()

        }

        drawer_button_notification.setOnClickListener{
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        drawer_button_report.setOnClickListener{

            Toasty.info(this, "This is dynamic.", Toast.LENGTH_SHORT, true).show()

        }

        drawer_button_about.setOnClickListener{
            startActivity(Intent(this, AboutActivity::class.java))
        }

    }


    override fun onBackPressed() {

        if(position == 0){

            if(fragmentName == "homeFragment"){
                finish()
            }else if(fragmentName == "classRoutine"){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "examRoutine"){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "smartRoutine" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "smartRoutine" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    SmartRoutineFragment()
                )
                    .commit()
            }else if (fragmentName == "previousCourse"){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if (fragmentName == "presentCourse"){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if (fragmentName == "allCourse"){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "students" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "students" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    StudentProgramFragment()
                )
                    .commit()
            }else if(fragmentName == "students" && fragmentPosition == 0.2){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    StudentIntakeFragment()
                )
                    .commit()
            }else if(fragmentName == "faculty" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "faculty" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    TeacherProgramFragment()
                )
                    .commit()
            }

            // For stuff

            else if(fragmentName == "myResult"){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "intakeResult" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    IntakeResultSemesterFragment()
                )
                    .commit()
            }else if(fragmentName == "intakeResult" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "universityResult" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "universityResult" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    UniversityResultProgramFragment()
                )
                    .commit()
            }else if(fragmentName == "universityResult" && fragmentPosition == 0.2){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    UniversityIntakeFragment()
                )
                    .commit()
            }else if(fragmentName == "assignment" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }else if(fragmentName == "compareProfile" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    HomeFragment()
                )
                    .commit()
            }



        }else if(position == 1 || position == 2 || position == 3){

            finish()

        }else if(position == 4){

            if(fragmentName == "MoreOptionFragment"){
                finish()
            }

            //annex home-----------------
            else if(fragmentName == "AnnexHomeFragment" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    MoreOptionFragment()
                )
                    .commit()
            }

            else if(fragmentName == "AnnexFeesSemesterFragment" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    AnnexHomeFragment()
                )
                    .commit()
            } else if(fragmentName == "AnnexFeesReceiptFragment" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    AnnexFeesSemesterFragment()
                )
                    .commit()
            }

            else if(fragmentName == "AnnexResultSemesterFragment" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    AnnexHomeFragment()
                )
                    .commit()
            } else if(fragmentName == "AnnexSemesterCourseResultFragment" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    AnnexResultSemesterFragment()
                )
                    .commit()
            }


            else if(fragmentName == "smartRoutine" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    AnnexHomeFragment()
                )
                    .commit()
            }else if(fragmentName == "smartRoutine" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    SmartRoutineFragment()
                )
                    .commit()
            }

            else if (fragmentName == "presentCourse"){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    AnnexHomeFragment()
                )
                    .commit()
            }else if (fragmentName == "allCourse"){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    AnnexHomeFragment()
                )
                    .commit()
            }


            else if(fragmentName == "NoticeBoardFragment" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    MoreOptionFragment()
                )
                    .commit()
            }else if(fragmentName == "NoticeDetailsFragment" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    NoticeBoardFragment()
                )
                    .commit()
            }else if(fragmentName == "EventsFragment" && fragmentPosition == 0.0){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    MoreOptionFragment()
                )
                    .commit()
            }else if(fragmentName == "EventDetailsFragment" && fragmentPosition == 0.1){
                supportFragmentManager.beginTransaction().replace(
                    R.id.main_home_fragment_container,
                    EventsFragment()
                )
                    .commit()
            }





        }else{
            val fragment = HomeFragment()
            supportFragmentManager.beginTransaction().replace(
                R.id.main_home_fragment_container,
                fragment
            )
                .commit()
            bottomNav.setItemSelected(R.id.nav_home)
        }


    }

    private fun loadIntakeResult() {
        val intake = "Intake ${currentUser!!.user_intake}"
        val ref = FirebaseDatabase.getInstance().getReference(
            "/Results/${
                StudLabAssistant.titleToDeptCode(
                    currentUser!!.user_prog_or_dept
                )
            }/${intake}"
        )
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {

            val to: GenericTypeIndicator<ArrayList<Results>> = object :
                GenericTypeIndicator<ArrayList<Results>>() {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        intakeResult[ds.key!!] = ds.getValue<ArrayList<Results>>(to)!!
                    }
                    intakeInfo(intake)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun intakeInfo(intake: String){
        intakeSemesterResult.clear()

        for(key in intakeResult.keys){
            println("Element at key $key : ${intakeResult[key]}")

            val totalStudent = intakeResult.getValue(key).size
            var pass = 0

            for (i in 0 until intakeResult.getValue(key).size-1){
                if(intakeResult.getValue(key)[i].Student_Cgpa >= "2.00"){
                    pass++
                }
            }
            val fail = totalStudent - pass
            intakeSemesterResult.add(
                Intake(
                    key,
                    intake,
                    totalStudent.toString(),
                    pass.toString(),
                    fail.toString()
                )
            )
        }

        val newSemList = ArrayList<Intake>(12)
        for(i in 0 until intakeSemesterResult.size){
            val intk = Intake(
                StudLabAssistant.textSemesterToOrdinal(intakeSemesterResult[i].semesterName),
                intakeSemesterResult[i].intake_Title,
                intakeSemesterResult[i].totalStudents,
                intakeSemesterResult[i].totalPass,
                intakeSemesterResult[i].totalFail
            )
            newSemList.add(intk)
        }

        newSemList.sortBy { it.semesterName }

        intakeSemesterResult.clear()
        intakeSemesterResult = newSemList
        //currentSemester = (intakeSemesterResult.size + 1).toString()

        val userResult = StudLabGenerate().loadMyResult()
        drawer_user_cgpa!!.text = userResult[userResult.size - 1].Student_Cgpa


    }



    @SuppressLint("SetTextI18n")
    private fun loadUserData(){
        drawer_user_image_progress_bar!!.visibility = View.VISIBLE

        if(currentUser!!.user_image != "")
            Picasso.get().load(currentUser!!.user_image).networkPolicy(NetworkPolicy.OFFLINE).into(
                drawer_user_image,
                object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        drawer_user_image_progress_bar!!.visibility = View.INVISIBLE
                    }

                    override fun onError(e: Exception?) {
                        drawer_user_image_progress_bar!!.visibility = View.INVISIBLE
                        Picasso.get().load(currentUser!!.user_image).into(drawer_user_image)
                    }
                })
        drawer_user_name_semester!!.text = "${currentUser!!.user_name} (Lv-${textSemesterToNumber(
            currentUser!!.user_semester
        )})"
        drawer_user_id_intake_section!!.text = "${currentUser!!.user_id} (${currentUser!!.user_intake}-${currentUser!!.user_section.replace(
            "Section ",
            "0"
        )})"
        drawer_user_dept_post!!.text = currentUser!!.user_prog_or_dept


    }


    override fun onResume() {
        super.onResume()
        onlineStatus("online")
    }

    override fun onPause() {
        super.onPause()
        onlineStatus("offline")
    }

    @SuppressLint("SimpleDateFormat", "DefaultLocale")
    private fun onlineStatus(state: String){
        val ref = FirebaseDatabase.getInstance().getReference("/Status/${currentUser!!.user_id}")
        val calendar = Calendar.getInstance().time.toString()
        val lastSeen = "${SimpleDateFormat("hh:mm a").format(Date()).toLowerCase()}-${calendar.substring(
            4,
            10
        )}, ${calendar.substring(30, 34)}"
        val status = Status(currentUser!!.user_id, state, lastSeen)
        ref.setValue(status)
        //Fri Jun 19 14:52:02 GMT+06:00 2020
    }

    override fun onNetworkSpeedChanged(speedType: Int) {
        when(speedType) {
            NetworkConstant.WIFI_CONNECTED -> {
            }
            NetworkConstant.FULL_SPEED_CONNECTED -> {
            }
            NetworkConstant.SLOW_CONNECTED -> {
            }
            NetworkConstant.LOW_SPEED_CONNECTED -> {
            }
        }
    }

    private var decision = true

    @SuppressLint("SetTextI18n")
    override fun onNetworkStatusChanged(isConnected: Boolean) {

        when(isConnected) {
            true -> {
                try {
                    if (!decision) {
                        status_text.text = "Online!"
                        internet_status.setBackgroundColor(getColor(R.color.colorGreen))
                        Handler().postDelayed({
                            progress_bar.visibility = View.GONE
                            status_text.visibility = View.GONE
                        }, 2500)
                        decision = true
                    }
                } catch (e: Exception) {
                }

            }
            false -> {
                try {
                    if (decision) {
                        internet_status.setBackgroundColor(getColor(R.color.colorRed))
                        status_text.text = "Offline!"
                        progress_bar.visibility = View.VISIBLE
                        status_text.visibility = View.VISIBLE
                        decision = false
                    }
                } catch (e: Exception) {
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return
            }
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
    }
}
