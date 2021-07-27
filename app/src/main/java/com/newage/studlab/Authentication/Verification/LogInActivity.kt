package com.newage.studlab.Authentication.Verification

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.kknirmale.networkhandler.config.NetworkConfig
import com.kknirmale.networkhandler.listener.NetworkStateListener
import com.kknirmale.networkhandler.utils.NetworkConstant
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.encryption
import com.newage.studlab.Application.StudLab.Companion.homeInfo
import com.newage.studlab.Application.StudLab.Companion.userList
import com.newage.studlab.Authentication.Introduction
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Model.AppsInfoModel.HomeInfo
import com.newage.studlab.Model.TokenModel.DeviceToken
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import io.github.tonnyl.light.Light
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : AppCompatActivity(), NetworkStateListener {

    private var networkConfig : NetworkConfig? = null

    lateinit var progressLogin:ProgressDialog

    lateinit var id:String

    lateinit var annexId:EditText
    lateinit var annexPass:EditText
    lateinit var forgetPass:TextView

    lateinit var studLabButtoon:Button
    lateinit var bubtButton: Button
    lateinit var annexButton:Button

    lateinit var logInButton:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)


        //network config instance
        networkConfig = NetworkConfig.getInstance()
        //add connectivity listener
        networkConfig!!.addNetworkConnectivityListener(this)


        progressLogin = ProgressDialog(this@LogInActivity).apply {
            setMessage("Login....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        annexId = findViewById(R.id.log_in_user_id)
        annexPass = findViewById(R.id.log_in__password)
        forgetPass = findViewById(R.id.forget_password_text_view)

        forgetPass = findViewById(R.id.forget_password_text_view)

        studLabButtoon = findViewById(R.id.studlab_button)
        bubtButton = findViewById(R.id.bubt_button)
        annexButton = findViewById(R.id.annex_button)

        logInButton = findViewById(R.id.log_in_button)

        allButtonClickListener()
        textChangeEventListener()
    }

    private fun allButtonClickListener(){
        logInButton.setOnClickListener{
            if(annexId.text.toString().length == 11 && annexPass.text.toString().isNotEmpty()){
                loadRequestedUser(annexId.text.toString(), annexPass.text.toString())
            }else if(annexId.text.toString().length != 11){
                Toasty.warning(this, "Check your annex id", Toast.LENGTH_SHORT, true).show()
            }else if(annexPass.text.toString().isEmpty()){
                Toasty.warning(this, "Check the password", Toast.LENGTH_SHORT, true).show()
            }
        }

        studLabButtoon.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://studlab.herokuapp.com/"))
            startActivity(i)
        }

        bubtButton.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://bubt.edu.bd/"))
            startActivity(i)
        }

        annexButton.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.annex.bubt.edu.bd/"))
            startActivity(i)
        }

        forgetPass.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.annex.bubt.edu.bd/?daikonPage=d808b8bde944a42985023"))
            startActivity(i)
        }
    }

    private fun textChangeEventListener(){
        annexId.addTextChangedListener(object :
            TextWatcher { override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length == 11 ) {
                annexId.onEditorAction(EditorInfo.IME_ACTION_DONE)
            }
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun loadRequestedUser(id:String, pass:String){

        progressLogin.show()

        val ref = FirebaseDatabase.getInstance().getReference("/Users/$id")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    userList.clear()
                    currentUser = dataSnapshot.getValue(Users::class.java)

                    if(encryption.decrypt(currentUser!!.annex_pass) == pass){
                        studLabLogInFromLoginActivity()
                    }else{
                        progressLogin.dismiss()
                        this@LogInActivity.runOnUiThread {
                            Toasty.error(this@LogInActivity, "ID or Password is invalid!", Toast.LENGTH_SHORT, true).show()
                        }
                    }
                }else{
                    progressLogin.dismiss()
                    this@LogInActivity.runOnUiThread {
                        Toasty.error(this@LogInActivity, "No registered user found", Toast.LENGTH_SHORT, true).show()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                progressLogin.dismiss()
                throw databaseError.toException()!!
            }
        })


    }

    private fun studLabLogInFromLoginActivity(){

        FirebaseAuth.getInstance().signInWithEmailAndPassword("studlab@newagebd.com", "~u7t{QD`/[k-2fHR+CbB:rcj;'`Ly`jy")
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                updateTokenLogin()
            }
            .addOnFailureListener {
                progressLogin.dismiss()
                this.runOnUiThread {
                    Toasty.error(this, "Failed", Toast.LENGTH_LONG, true).show()
                }
            }
    }


    @SuppressLint("SetTextI18n")
    private fun updateTokenLogin() {

        val refreshToken = FirebaseInstanceId.getInstance().token

        val ref = FirebaseDatabase.getInstance().getReference("/Tokens/${annexId.text}")
        val token = DeviceToken(annexId.text.toString(),refreshToken!!)
        ref.setValue(token).addOnSuccessListener {

            this.runOnUiThread{
                Toasty.success(this, "Success", Toast.LENGTH_SHORT, true).show()

            }

            appsInfoLogin()
        }
        ref.setValue(token).addOnFailureListener {
            progressLogin.dismiss()
            this.runOnUiThread {
                Toasty.error(this, "Failed", Toast.LENGTH_LONG, true).show()
            }
        }
    }

    private fun uploadUserLogin(){
        val userDatabase = DatabaseHelper(this)
        userDatabase.deleteUserData()
        userDatabase.saveUserData(currentUser)
        val user = userDatabase.getUserData()
        val uid = FirebaseAuth.getInstance().uid
        if(uid != null  && user != null){
            val intent = Intent(this, HomeMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    private fun appsInfoLogin(){

        val ref = FirebaseDatabase.getInstance().getReference("/AppsInfo")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for (productSnapshot in dataSnapshot.children) {
                        homeInfo = productSnapshot.getValue(HomeInfo::class.java)
                    }
                    progressLogin.dismiss()
                    uploadUserLogin()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                progressLogin.dismiss()
                throw databaseError.toException()!!
            }
        })
    }



    override fun onBackPressed() {
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_right_to_left)
        startActivity(Intent(this, Introduction::class.java))
        finish()
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
                        login_layout?.let {
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
                    login_layout?.let {
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
