package com.newage.studlab.Application

//import com.newage.studlab.Services.SampleBootReceiver

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kknirmale.networkhandler.config.NetworkConfig
import com.newage.studlab.BuildConfig.APPLICATION_ID
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Model.Api.AnnexApis
import com.newage.studlab.Model.AppsInfoModel.HomeInfo
import com.newage.studlab.Model.Course
import com.newage.studlab.Model.Intake
import com.newage.studlab.Model.Program
import com.newage.studlab.Model.Results
import com.newage.studlab.Model.UserModel.Teacher
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.Plugins.getJsonDataFromAsset
import com.newage.studlab.Services.AlarmBroadcastReceiver
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import se.simbio.encryption.Encryption


class StudLab: Application() {

    companion object{
        lateinit  var appContext: Context
        lateinit var encryption: Encryption

        var annexApis:AnnexApis? = null

        var homeInfo:HomeInfo? = null

        var currentUser:Users? = null
        //var currentSemester:String = ""

        val userList = arrayListOf<Users>()
        val facultyMemberList = arrayListOf<Teacher>()

        val programList = arrayListOf<Program>()
        var newProgramList = arrayListOf<Program>()
        val courseList = arrayListOf<Course>()
        val intakeResult = HashMap<String, ArrayList<Results>>()
        var intakeSemesterResult = ArrayList<Intake>()
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        encryption = Encryption.getDefault(
            "comNewAgeStudLab", "anUnofficialApplication", ByteArray(
                16
            )
        )

        //for network status
        NetworkConfig.initNetworkConfig(this)


        FirebaseApp.initializeApp(this)
        val isMain = isMainProcess(this)
        if (!isMain) {
            // other things
            AlarmBroadcastReceiver().setAlarm(this)
            return
        }

        //FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val scoresRef = FirebaseDatabase.getInstance().getReference("studlab")
        scoresRef.keepSynced(true)

        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        val built = builder.build()
        built.setIndicatorsEnabled(false)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)

        //
        loadCurrentUser()
        val uid = FirebaseAuth.getInstance().uid
        if(uid != null){

            loadAnnexApis()

            appsInfo()
            loadUser()
            loadFacultyMember()

            loadProgram()
            loadCourse()
        }else{
            studLabLogIn()
        }


//        val receiver = ComponentName(applicationContext, SampleBootReceiver::class.java)
//
//        applicationContext.packageManager.setComponentEnabledSetting(
//            receiver,
//            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//            PackageManager.DONT_KILL_APP
//        )

/*        val ref = FirebaseDatabase.getInstance().getReference("/AnnexApis/Api")
        ref.setValue(AnnexApis())
            .addOnSuccessListener {
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            }*/




/*        FirebaseAuth.getInstance().createUserWithEmailAndPassword("studlab@newagebd.com", "~u7t{QD`/[k-2fHR+CbB:rcj;'`Ly`jy")
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                //uploadImageToDatabase()
                // else if successful
                // Log.d("Main", "Successfully created user with uid: ${it.result?.user?.uid}")
                Toast.makeText(this, "Successfully created user with uid: ${it.result?.user?.uid}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Log.d("Main", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }*/

        //addResult()

/*        val ref = FirebaseDatabase.getInstance().getReference("/AppsInfo/home")
        ref.setValue(HomeInfo("6824","235","5","17"))*/

/*        val ref = FirebaseDatabase.getInstance().getReference("/AppsInfo/Blood")
        ref.setValue(BloodRequest("0"))*/

/*
        val ref = FirebaseDatabase.getInstance().getReference("/Avatar/sm0")
        ref.setValue(ProfileImage("https://firebasestorage.googleapis.com/v0/b/studlab.appspot.com/o/user%20image%2FAvater%2Fsm0.png?alt=media&token=875f7545-28ed-4c5c-a06f-18379cb2d52b"))
*/

    }


    private fun isMainProcess(context: Context?): Boolean {
        if (null == context) {
            return true
        }
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val pid = Process.myPid()
        for (processInfo in manager.runningAppProcesses) {
            if (APPLICATION_ID == processInfo.processName && pid == processInfo.pid) {
                return true
            }
        }
        return false
    }


    private fun addResult(){

        val js = ""

        //val jsonMap = Gson().fromJson<Any>(js, object : TypeToken<HashMap<String, Any>>() {}.type)

        //val ref = FirebaseDatabase.getInstance().getReference("/Results")
        // ref.setValue(jsonMap)


        val jsonFileString = getJsonDataFromAsset(this, "llb_intake_36_4th_semester.json")
        Toast.makeText(this, jsonFileString, Toast.LENGTH_LONG).show()
        // Log.i("data", jsonFileString!!)

        val gson = Gson()
        val listPersonType = object : TypeToken<List<Results>>() {}.type

        val res: List<Results> = gson.fromJson(jsonFileString, listPersonType)


        res.forEachIndexed { idx, res ->
            //Log.i("data", "> Item $idx:\n$res")
        }

        val ref = FirebaseDatabase.getInstance().getReference("/Results/LLB/Intake 36/Fourth Semester")
        ref.setValue(res)


    }

    private fun studLabLogIn(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            "studlab@newagebd.com",
            "~u7t{QD`/[k-2fHR+CbB:rcj;'`Ly`jy"
        )
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                loadAnnexApis()

                appsInfo()
                loadUser()
                loadFacultyMember()

                loadProgram()
                loadCourse()
                loadCurrentUser()

            }
            .addOnFailureListener {

            }
    }


    private fun loadAnnexApis(){

        val ref = FirebaseDatabase.getInstance().getReference("/AnnexApis")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (productSnapshot in dataSnapshot.children) {
                        annexApis = productSnapshot.getValue(AnnexApis::class.java)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()!!
            }
        })
    }


    private fun appsInfo(){

        val ref = FirebaseDatabase.getInstance().getReference("/AppsInfo")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (productSnapshot in dataSnapshot.children) {
                        homeInfo = productSnapshot.getValue(HomeInfo::class.java)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()!!
            }
        })
    }

    private fun loadUser(){
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
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()!!
            }
        })
    }

    private fun loadFacultyMember(){

        val ref = FirebaseDatabase.getInstance().getReference("/People/Faculty")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    facultyMemberList.clear()
                    var mem: Teacher?
                    for (productSnapshot in dataSnapshot.children) {

                        mem = productSnapshot.getValue(Teacher::class.java)
                        facultyMemberList.add(mem!!)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()!!
            }
        })
    }

    private fun loadProgram(){
        val ref = FirebaseDatabase.getInstance().getReference("/department")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                programList.clear()
                var program: Program? = null
                for (productSnapshot in dataSnapshot.children) {
                    program = productSnapshot.getValue(Program::class.java)
                    programList.add(program!!)
                    newProgramList.add(program)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })
    }

    private fun loadCourse(){
        val ref = FirebaseDatabase.getInstance().getReference("/Course")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                courseList.clear()
                var course: Course? = null
                for (productSnapshot in dataSnapshot.children) {

                    course = productSnapshot.getValue(Course::class.java)
                    courseList.add(course!!)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })
    }

    private fun loadCurrentUser(){
        val databaseHandler = DatabaseHelper(this)
        val user = databaseHandler.getUserData()
        if (user != null) {
            currentUser = user
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        //Remove all listeners while on low memory
        NetworkConfig.getInstance().removeAllNetworkConnectivityListener()
    }
}







