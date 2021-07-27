package com.newage.studlab.Authentication

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver.handleIntent
import androidx.viewpager.widget.ViewPager
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kknirmale.networkhandler.config.NetworkConfig
import com.kknirmale.networkhandler.listener.NetworkStateListener
import com.kknirmale.networkhandler.utils.NetworkConstant
import com.newage.studlab.Adapter.IntroductionViewPagerAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.userList
import com.newage.studlab.Authentication.Verification.LogInActivity
import com.newage.studlab.Authentication.Verification.SignUpActivity
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Model.Api.AnnexApis
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.Plugins.StudLabAssistant.Companion.oldState
import com.newage.studlab.Plugins.StudLabAssistant.Companion.setIntroClickedState
import com.newage.studlab.R
import com.newage.studlab.Services.SampleBootReceiver
import com.newage.studlab.Services.SrNotificationService.NotificationEventReceiver

import io.github.tonnyl.light.Light
import kotlinx.android.synthetic.main.activity_introduction.*
import java.util.*
import java.util.concurrent.TimeUnit


class Introduction : AppCompatActivity(), NetworkStateListener {

    private var networkConfig : NetworkConfig? = null

    var currentPage = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        checkPermissions()

        //network config instance
        networkConfig = NetworkConfig.getInstance()
        //add connectivity listener
        networkConfig!!.addNetworkConnectivityListener(this)



        if(oldState == "Log") overridePendingTransition(
            R.anim.enter_right_to_left,
            R.anim.exit_right_to_left
        )
        else if(oldState == "Sign") overridePendingTransition(
            R.anim.enter_left_to_right,
            R.anim.exit_left_to_right
        )

        val viewPagerAdapter = IntroductionViewPagerAdapter(supportFragmentManager)
        intro_viewpager.adapter = viewPagerAdapter
        dots_indicator.setViewPager(intro_viewpager)

        autoViewpagerChange()

        initialization()

        allButtonClickingEventListeners()
        viewPagerPageChangeEventListeners()

        NotificationEventReceiver.setupAlarm(applicationContext)  //------------------------------------------------------------




    }

    private fun allButtonClickingEventListeners(){
        intro_login_button.setOnClickListener{
            setIntroClickedState("Log")
            overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_left_to_right)
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }

        intro_signup_button.setOnClickListener{
            setIntroClickedState("Sign")
            overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_right_to_left)
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun viewPagerPageChangeEventListeners(){
        intro_viewpager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentPage = position
            }

        })
    }

    private fun autoViewpagerChange(){

        val numberOfPage = 8
        var timer: Timer? = null
        val delayMs: Long = 100

        val periodMs: Long = 1500

        val handler = Handler()
        val update = Runnable {
            if (currentPage == numberOfPage - 1) {
                currentPage = 0
            }
            intro_viewpager.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, delayMs, periodMs)
    }





    private fun initialization(){
        val uid = FirebaseAuth.getInstance().uid
        val databaseHandler = DatabaseHelper(this)
        val user = databaseHandler.getUserData()

        if(uid != null && user != null){

            intro_login_button.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
            intro_login_button.isEnabled = false
            intro_signup_button.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
            intro_signup_button.isEnabled = false
            intro_loading.visibility = View.VISIBLE

            Handler().postDelayed({
                intro_loading.visibility = View.INVISIBLE
                val intent = Intent(this, HomeMainActivity::class.java)
                startActivity(intent)

                finish()
            }, 700)



        }else if(uid != null && user == null){
            intro_login_button.background = resources.getDrawable(R.drawable.intro_button_blue)
            intro_login_button.isEnabled = true
            intro_signup_button.background = resources.getDrawable(R.drawable.intro_button_green)
            intro_signup_button.isEnabled = true
            intro_loading.visibility = View.INVISIBLE

        }else{
            intro_login_button.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
            intro_login_button.isEnabled = false
            intro_signup_button.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
            intro_signup_button.isEnabled = false
            intro_loading.visibility = View.VISIBLE
            studLabLogInIntro()
        }
    }

    private fun studLabLogInIntro(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            "studlab@newagebd.com",
            "~u7t{QD`/[k-2fHR+CbB:rcj;'`Ly`jy"
        )
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                loadAnnexApis()

            }
            .addOnFailureListener {
                this.runOnUiThread {
                    onFailed()
                    intro_loading.visibility = View.INVISIBLE
                }
            }
    }


    //getting apis--------------------
    private fun loadAnnexApis(){

        val ref = FirebaseDatabase.getInstance().getReference("/AnnexApis")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (productSnapshot in dataSnapshot.children) {
                        StudLab.annexApis = productSnapshot.getValue(AnnexApis::class.java)
                    }

                    loadUserIntro()

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()!!
            }
        })
    }



    private fun loadUserIntro(){
        val ref = FirebaseDatabase.getInstance().getReference("/Users")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userList.clear()
                    var usr: Users?
                    for (productSnapshot in dataSnapshot.children) {

                        usr = productSnapshot.getValue(Users::class.java)
                        userList.add(usr!!)
                    }

                    this@Introduction.runOnUiThread {
                        intro_login_button.background =
                            resources.getDrawable(R.drawable.intro_button_blue)
                        intro_login_button.isEnabled = true
                        intro_signup_button.background =
                            resources.getDrawable(R.drawable.intro_button_green)
                        intro_signup_button.isEnabled = true
                        intro_loading.visibility = View.INVISIBLE
                    }
                } else {
                    this@Introduction.runOnUiThread {
                        intro_loading.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                this@Introduction.runOnUiThread {
                    onFailed()
                    intro_loading.visibility = View.INVISIBLE
                }
                throw databaseError.toException()!!
            }
        })
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

    var decision = true

    override fun onNetworkStatusChanged(isConnected: Boolean) {

        when(isConnected) {
            true -> {
                try {
                    if (!decision) {
                        intro_layout?.let {
                            Light.make(
                                it, "Connected. You are connected to the internet.",
                                R.drawable.internet_32,
                                R.color.colorGreen,
                                R.color.colorWhite,
                                Toast.LENGTH_SHORT,
                                R.drawable.tick_icon_16,
                                R.color.colorWhite
                            )
                                .setAction("ok") {

                                }.show()
                        }
                        decision = true
                    }
                } catch (e: Exception) {
                }

            }
            false -> {
                try {
                    intro_layout?.let {
                        Light.make(
                            it, "Connection failed. Please check internet settings.",
                            R.drawable.no_internet_32,
                            R.color.colorRed,
                            R.color.colorWhite,
                            50000,
                            R.drawable.setting_icon_16,
                            R.color.colorWhite
                        )
                            .setAction("Settings") {
                                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                startActivity(intent)
                            }.show()
                    }

                    decision = false
                } catch (e: Exception) {
                }
            }
        }
    }


    private fun onFailed(){
        intro_layout?.let {
            Light.make(
                it, "Ops! failed to load. Please try again.",
                R.drawable.error_icon_32,
                R.color.colorRed,
                R.color.colorWhite,
                10000,
                R.drawable.rotate_icon_svg_16dp,
                R.color.colorWhite
            )
                .setAction("Retry") {
                    intro_login_button.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
                    intro_login_button.isEnabled = false
                    intro_signup_button.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
                    intro_signup_button.isEnabled = false
                    intro_loading.visibility = View.VISIBLE
                    studLabLogInIntro()
                }.show()
        }

    }


    var permissions: Array<String> = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.VIBRATE,
        Manifest.permission.RECORD_AUDIO
    )

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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
         //handleIntent();
    }

}
