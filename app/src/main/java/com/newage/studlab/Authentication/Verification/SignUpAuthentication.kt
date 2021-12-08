package com.newage.studlab.Authentication.Verification

import android.util.Log
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import com.google.firebase.iid.FirebaseInstanceId
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.annexApis
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.encryption
import com.newage.studlab.Application.StudLab.Companion.homeInfo
import com.newage.studlab.Authentication.Verification.SignUpActivity.Companion.currentStapes
import com.newage.studlab.Authentication.Verification.SignUpActivity.Companion.nextButton
import com.newage.studlab.Authentication.Verification.SignUpActivity.Companion.stepView
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_address
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_blood
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_faculty_name
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_gender
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_id
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_image
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_intake
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_name
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_phone_old
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_prog_or_dept
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_room_no
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_status
import com.newage.studlab.Authentication.Verification.SignUpIdentification.Companion.user_type
import com.newage.studlab.Authentication.Verification.SignUpInformation.Companion.user_dob
import com.newage.studlab.Authentication.Verification.SignUpInformation.Companion.user_phone_new
import com.newage.studlab.Authentication.Verification.SignUpInformation.Companion.user_section
import com.newage.studlab.Authentication.Verification.SignUpInformation.Companion.user_semester
import com.newage.studlab.Authentication.Verification.SignUpInformation.Companion.user_shift
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Model.AppsInfoModel.HomeInfo
import com.newage.studlab.Model.TokenModel.DeviceToken
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class SignUpAuthentication : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up_authentication, container, false)
    }

    lateinit var progress:ProgressDialog

    companion object{
        lateinit var password:String
    }

    private var typeHttp = OkHttpClient()
    //val loginApi = "https://antheaxs2-temp.herokuapp.com/api/bubt/login?"
    private var loginApi:String = ""

    lateinit var authInfo:TextView
    lateinit var annexId:EditText
    lateinit var annexPass:EditText


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginApi = annexApis!!.annexLogin

        progress = ProgressDialog(context!!).apply {
            setTitle("Loading....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        activity?.let {
            authInfo = it.findViewById(R.id.auth_info)
            annexId = it.findViewById(R.id.annex_id_edit_text)
            annexPass = it.findViewById(R.id.annex_password)

        }

        authInfo.text = "Good Job, $user_name. We are happy to see you in the last step."
        annexId.setText(user_id)

        nextButton.background = resources.getDrawable(R.drawable.intro_button_green_disabled)
        nextButton.isEnabled = false

        annexId.isEnabled = false


        nextButton.text = "Sign Up"
        currentStapes = 3

        allButtonClickingEventListener()
        textChangesEventListeners()
    }


    @SuppressLint("DefaultLocale")
    private fun  allButtonClickingEventListener(){
        nextButton.setOnClickListener{
            annexLogin(user_id, password)
        }
    }

    private fun textChangesEventListeners(){
        annexPass.addTextChangedListener(object :
            TextWatcher { override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length >= 2) {
                password = annexPass.text.toString().trim()
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



    private fun annexLogin(id:String,pass:String){
        progress.show()
        progress.setMessage("Please wait.")

        val url = "${loginApi}id=$id&pass=$pass"
        //val url = "https://bubt.herokuapp.com/bubt/v1/login?id=17181103084&pass=imamagun123"

        var check = false
        var phpSessionId = ""

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(60, TimeUnit.SECONDS)
        builder.readTimeout(60, TimeUnit.SECONDS)
        builder.writeTimeout(60, TimeUnit.SECONDS)

        typeHttp = builder.build()

        val request = Request.Builder().url(url).build()

        typeHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                check = false
                activity!!.runOnUiThread {
                    progress.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            progress.dismiss()
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
                                    progress.dismiss()
                                    Log.d("lal", e.toString())
                                    Toasty.error(requireContext(), "ID or Password is invalid!", Toast.LENGTH_SHORT, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {

                            //Toasty.success(requireContext(), "Annex Log In Success", Toast.LENGTH_SHORT, true).show()
                            studLabLogIn(id, pass, phpSessionId)
                            //downloadSmartRoutine("https://antheaxs2-temp.herokuapp.com/api/bubt/routine?phpsessid=$phpSessionId")
                        }
                    }
                }
            }
        })
    }

    private fun studLabLogIn(id:String, pass:String, sassId:String){

        FirebaseAuth.getInstance().signInWithEmailAndPassword("studlab@newagebd.com", "~u7t{QD`/[k-2fHR+CbB:rcj;'`Ly`jy")
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                uploadUser(id, pass, sassId)
            }
            .addOnFailureListener {
                progress.dismiss()
                activity!!.runOnUiThread {
                    Toasty.error(requireContext(), "Failed", Toast.LENGTH_LONG, true).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun uploadUser(id:String, pass:String, sassId:String){
        progress.setMessage("Finishing.")

        val user = Users(
            user_id,
            user_name,
            user_gender,
            user_dob,
            user_phone_old,
            user_phone_new,
            encryption.encrypt(pass),
            user_type,
            user_image,
            user_faculty_name,
            user_prog_or_dept,
            user_shift,
            user_intake,
            user_section,
            user_semester,
            user_address,
            user_status,
            user_blood,
            user_room_no,
            id,
            encryption.encrypt(pass),
            sassId)

        val userDatabase = DatabaseHelper(requireContext())
        userDatabase.deleteUserData()
        userDatabase.saveUserData(user)

        val ref = FirebaseDatabase.getInstance().getReference("/Users/$id")
        ref.setValue(user).addOnSuccessListener {
            updateToken()
            appsInfoAuth(user)
        }
        ref.setValue(user).addOnFailureListener {
            progress.dismiss()
            activity!!.runOnUiThread {
                Toasty.error(requireContext(), "Failed", Toast.LENGTH_SHORT, true)
                    .show()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateToken() {

        progress.setMessage("Finished.")

        val refreshToken = FirebaseInstanceId.getInstance().token
        val ref = FirebaseDatabase.getInstance().getReference("/Tokens/$user_id")
        val token = DeviceToken(user_id,refreshToken!!)
        ref.setValue(token).addOnSuccessListener { }
        ref.setValue(token).addOnFailureListener {
            progress.dismiss()
            activity!!.runOnUiThread {
                Toasty.error(requireContext(), "Failed", Toast.LENGTH_SHORT, true)
                    .show()
            }
        }
    }


    private fun appsInfoAuth(user:Users){

        val ref = FirebaseDatabase.getInstance().getReference("/AppsInfo")
        ref.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for (productSnapshot in dataSnapshot.children) {
                        homeInfo = productSnapshot.getValue(HomeInfo::class.java)
                    }

                    activity!!.runOnUiThread{
                        nextButton.text = "Finish"

                        stepView.go(3,true)
                        stepView.done(true)

                        currentUser = user

                        activity!!.finish()

                        startActivity(Intent(requireContext(), HomeMainActivity::class.java))

                        Toasty.success(requireContext(), "Success", Toast.LENGTH_SHORT, true).show()


                    }

                    progress.dismiss()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                progress.dismiss()
                throw databaseError.toException()!!
            }
        })
    }


/*    val encrypted = encryption!!.encryptOrNull(secretText)
    val decrypted = encryption!!.decryptOrNull(encrypted)*/

}
