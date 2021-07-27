package com.newage.studlab.Authentication.Verification


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kknirmale.networkhandler.config.NetworkConfig
import com.kknirmale.networkhandler.listener.NetworkStateListener
import com.kknirmale.networkhandler.utils.NetworkConstant
import com.newage.studlab.Authentication.Introduction
import com.newage.studlab.R
import com.shuhart.stepview.StepView
import io.github.tonnyl.light.Light
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(), NetworkStateListener{


    private var networkConfig : NetworkConfig? = null

    companion object{
        lateinit var stepView:StepView
        var currentStapes:Int = 0
        lateinit var steps:ArrayList<String>
        lateinit var fragments:ArrayList<Fragment>
        lateinit var nextButton:Button
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)


        //network config instance
        networkConfig = NetworkConfig.getInstance()
        //add connectivity listener
        networkConfig!!.addNetworkConnectivityListener(this)


        fragments = arrayListOf(SignUpIdentification(),SignUpInformation(),SignUpAuthentication())
        steps = arrayListOf("1","2","3")

        supportFragmentManager.beginTransaction()
            .replace(R.id.sign_up_fragment_container, fragments[0])
            .addToBackStack(null)
            .commit()

        nextButton = findViewById(R.id.sign_up_next_button)

        stepView = findViewById(R.id.step_view)
        stepView.state
            .animationType(StepView.ANIMATION_ALL)
            .animationDuration(resources.getInteger(android.R.integer.config_shortAnimTime))
            .commit()

        stepView.go(0,true)

    }

    override fun onBackPressed() {
        when (currentStapes) {
            1 -> {
                overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_left_to_right)
                startActivity(Intent(this, Introduction::class.java))
                finish()

            }
            2 -> {
                stepView.go(0,true)

                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right,0,0)
                    .replace(R.id.sign_up_fragment_container, fragments[0])
                    .addToBackStack(null)
                    .commit()
            }
            3 -> {

                stepView.go(1,true)
                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right,0,0)
                    .replace(R.id.sign_up_fragment_container, fragments[1])
                    .addToBackStack(null)
                    .commit()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNetworkSpeedChanged(speedType: Int) {
        when(speedType) {
            NetworkConstant.WIFI_CONNECTED -> { }
            NetworkConstant.FULL_SPEED_CONNECTED -> { }
            NetworkConstant.SLOW_CONNECTED ->  { }
            NetworkConstant.LOW_SPEED_CONNECTED -> { }
        }
    }

    var decision = true

    override fun onNetworkStatusChanged(isConnected: Boolean) {
        when(isConnected) {
            true -> {
                try {
                    if(!decision){
                        signup_layout?.let {
                            Light.make(it, "Connected. You are connected to the internet.",
                                R.drawable.internet_32,
                                R.color.colorGreen,
                                R.color.colorWhite,
                                Toast.LENGTH_SHORT,
                                R.drawable.tick_icon_16,
                                R.color.colorWhite)
                                .setAction("ok") {

                                }.show()
                        }
                        decision = true
                    }
                }catch (e:Exception){ }

            }
            false -> {
                try {
                    signup_layout?.let {
                        Light.make(it, "Connection failed. Please check internet settings.",
                            R.drawable.no_internet_32,
                            R.color.colorRed,
                            R.color.colorWhite,
                            50000,
                            R.drawable.setting_icon_16,
                            R.color.colorWhite)
                            .setAction("Settings") {
                                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                startActivity(intent)
                            }.show()
                    }

                    decision = false
                }catch (e:Exception){ }
            }
        }
    }


}
