package com.newage.studlab.Home.RoutineFragment

import HomeFragment
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.jsibbold.zoomage.ZoomageView
import com.newage.studlab.Adapter.RoutineAdapter.ExamRoutinesRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Model.RoutineModel.Routines
import com.newage.studlab.Plugins.SnapHelper
import com.newage.studlab.Plugins.StudLabAssistant.Companion.titleToDeptCode
import com.newage.studlab.Plugins.showInfoDialog
import com.newage.studlab.R
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.dialogue_delete.view.*
import kotlinx.android.synthetic.main.dialogue_routine_setting.view.*
import kotlinx.android.synthetic.main.dialogue_upload_routine.view.*
import kotlinx.android.synthetic.main.dialogue_upload_routine.view.close_button
import kotlinx.android.synthetic.main.dialogue_upload_routine.view.containers
import www.sanju.motiontoast.MotionToast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ExamRoutineFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exam_routine, container, false)
    }

    private var user_intake = ""

    private val examRoutinesList = ArrayList<Routines>()
    private val examRoutine = ArrayList<Routines>()

    private var routineUri: Uri? = null
    private var routineDownloadUri = ""
    private var proCode = ""
    private var intakeNo = ""
    private var secNo = ""
    private var sesCode = ""
    private var sesYear = ""
    private var semName = ""
    private var routineType = ""
    private var sessionName = ""

    private var mStorageRef: StorageReference? = null

    private lateinit var backButton: ImageView
    private lateinit var linkButton: ImageView
    private lateinit var uploadButton: ImageView
    private lateinit var settingsButton: ImageView
    private lateinit var showHideButton: ImageView

    private lateinit var routineImage: ZoomageView

    private lateinit var programTag:TextView
    private lateinit var intakeTag: TextView
    private lateinit var sectionTag:TextView
    private lateinit var sessionTag:TextView
    private lateinit var semesterTag:TextView
    private lateinit var examUploadTag:TextView
    private lateinit var examType:TextView

    private lateinit var recyclerViewTitle:TextView

    private lateinit var examRecyclerView: RecyclerView

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {

            backButton = it.findViewById(R.id.fragment_back_button)
            linkButton = it.findViewById(R.id.fragment_link_button)
            uploadButton = it.findViewById(R.id.exam_recycler_upload_image)
            settingsButton = it.findViewById(R.id.exam_recycler_settings)
            showHideButton = it.findViewById(R.id.exam_recycler_hide_show)

            routineImage = it.findViewById(R.id.exam_routine_image)

            programTag = it.findViewById(R.id.exam_program_tage)
            intakeTag = it.findViewById(R.id.exam_intake_tag)
            sectionTag = it.findViewById(R.id.exam_section_tag)
            sessionTag = it.findViewById(R.id.exam_session_tag)
            semesterTag = it.findViewById(R.id.exam_semester_tag)
            examUploadTag = it.findViewById(R.id.exam_date_tag)
            examType = it.findViewById(R.id.exam_type_tag)

            recyclerViewTitle = it.findViewById(R.id.exam_recycler_view_title)

            examRecyclerView = it.findViewById(R.id.exam_routine_recycler_view)
        }

        mStorageRef = FirebaseStorage.getInstance().reference

        user_intake = "Intake ${currentUser!!.user_intake}"
        recyclerViewTitle.text = "${titleToDeptCode(currentUser!!.user_prog_or_dept)} - ${user_intake.toUpperCase()}"

        //Current Fragment
        fragmentName = "examRoutine"


        loadExamRoutines(user_intake)
        loadLocalExamRoutine()
        allButtonClickingEventListeners()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        examRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL ,false)
        val linearSnapHelper: LinearSnapHelper = SnapHelper()
        linearSnapHelper.attachToRecyclerView(examRecyclerView)
    }

    private fun loadExamRoutines(intake:String){
        val ref = FirebaseDatabase.getInstance().getReference("/Routines/${titleToDeptCode(currentUser!!.user_prog_or_dept)}/$intake")
        //ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    examRoutinesList.clear()
                    var routine:Routines? = null
                    for (productSnapshot in dataSnapshot.children) {
                        routine = productSnapshot.getValue(Routines::class.java)
                        examRoutinesList.add(routine!!)
                    }
                    loadAllExamRoutine()
                }else{
                    examRoutinesList.clear()
                    loadAllExamRoutine()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })
    }


    private fun loadAllExamRoutine(){
        if(examRoutinesList.size !=0){
            examRoutine.clear()
            for(i in 0 until examRoutinesList.size){
                if(examRoutinesList[i].routineType.contains("Exam")){
                    examRoutine.add(examRoutinesList[i])
                }
            }
            if(examRoutine.size != 0){
                examRecyclerView.adapter = ExamRoutinesRecyclerViewAdapter(appContext,examRoutine,
                    clickListenerInfo = {
                        examRoutineInfoClickListener(it)
                    },
                    clickListenerDownload = {
                        examRoutineDownloadClickListener(it)
                    },
                    clickListenerPin = {
                        examRoutinePinClickListener(it)
                    },
                    clickListenerDelete = {
                        examRoutineDeleteClickListener(it)
                    })
            }else{
                examRecyclerView.adapter = ExamRoutinesRecyclerViewAdapter(appContext,examRoutine,
                    clickListenerInfo = {
                        examRoutineInfoClickListener(it)
                    },
                    clickListenerDownload = {
                        examRoutineDownloadClickListener(it)
                    },
                    clickListenerPin = {
                        examRoutinePinClickListener(it)
                    },
                    clickListenerDelete = {
                        examRoutineDeleteClickListener(it)
                    })
            }
        }else{
            examRecyclerView.adapter = ExamRoutinesRecyclerViewAdapter(appContext,examRoutinesList,
                clickListenerInfo = {
                    examRoutineInfoClickListener(it)
                },
                clickListenerDownload = {
                    examRoutineDownloadClickListener(it)
                },
                clickListenerPin = {
                    examRoutinePinClickListener(it)
                },
                clickListenerDelete = {
                    examRoutineDeleteClickListener(it)
                })
        }
    }


    private fun examRoutineInfoClickListener(routine:Routines){
        activity!!.showInfoDialog(routine.programCode,routine.intakeNo,routine.sectionNo,routine.sessionName,routine.semesterName,routine.uploadBy,routine.uploadDate,routine.routineType)
    }
    private fun examRoutineDownloadClickListener(routine:Routines){
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(routine.routineImage))
        startActivity(i)
    }
    private fun examRoutinePinClickListener(routine:Routines){
        addExamRoutine(routine)
        loadLocalExamRoutine()
        Toasty.success(requireContext(), "Pinned: ${routine.intakeNo}, ${routine.semesterName}, ${routine.sectionNo} - Class Routine", Toast.LENGTH_SHORT, true).show()
    }
    private fun examRoutineDeleteClickListener(routine:Routines){

        if(routine.uploadBy == currentUser!!.user_id){
            activity!!.showExamDeleteDialog(routine)
        }else{
            Toasty.error(requireContext(), "Permission denied: Only ${routine.uploadBy} can delete this post.", Toast.LENGTH_SHORT, true).show()
        }

    }

    private fun deleteExamRoutine(routine: Routines){

        val ref = FirebaseDatabase.getInstance().reference.child("/Routines/${titleToDeptCode(currentUser!!.user_prog_or_dept)}/$user_intake").orderByChild("routineImage").equalTo(routine.routineImage)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    for (productSnapshot in dataSnapshot.children) {
                        productSnapshot.ref.removeValue()
                    }
                    activity!!.runOnUiThread(){
                        Toasty.success(requireContext(), "Deleted", Toast.LENGTH_SHORT, true).show()
                        loadExamRoutines(user_intake)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                activity!!.runOnUiThread(){
                    Toasty.error(requireContext(), "Failed", Toast.LENGTH_SHORT, true).show()
                }
                throw databaseError.toException()
            }
        })
    }


    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }

        linkButton.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bubt.edu.bd/home/page_details/Routine"))
            startActivity(i)
        }

        uploadButton.setOnClickListener{
            activity!!.showUploadExamDialog()
        }

        settingsButton.setOnClickListener{
            activity!!.showExamSettingDialog(titleToDeptCode(currentUser!!.user_prog_or_dept))
        }

        showHideButton.setOnClickListener{
            if (examRecyclerView.visibility == View.VISIBLE) {
                examRecyclerView.visibility = View.GONE
            } else {
                examRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun loadLocalExamRoutine(){
        val databaseHandler = DatabaseHelper(context!!)
        val routine = databaseHandler.getExamRoutine()

        if(routine != null && routine.routineType.contains("Exam")){
            programTag.text = routine.programCode
            intakeTag.text = routine.intakeNo
            sectionTag.text = routine.sectionNo
            sessionTag.text = routine.sessionName
            semesterTag.text = routine.semesterName
            examUploadTag.text = routine.uploadDate
            examType.text = routine.routineType

            if(routine.routineImage != "")
                Picasso.get().load(routine.routineImage).networkPolicy(NetworkPolicy.OFFLINE).into(routineImage, object: com.squareup.picasso.Callback{
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        Picasso.get().load(routine.routineImage).into(routineImage)
                    }
                })
        }else{
            programTag.text = ""
            intakeTag.text = ""
            sectionTag.text = ""
            sessionTag.text = ""
            semesterTag.text = ""
            examUploadTag.text = ""
            examType.text = ""

            Picasso.get().load("https://748073e22e8db794416a-cc51ef6b37841580002827d4d94d19b6.ssl.cf3.rackcdn.com/not-found.png").networkPolicy(NetworkPolicy.OFFLINE).into(routineImage, object: com.squareup.picasso.Callback{
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    Picasso.get().load("https://748073e22e8db794416a-cc51ef6b37841580002827d4d94d19b6.ssl.cf3.rackcdn.com/not-found.png").into(routineImage)
                }
            })
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun Activity.showUploadExamDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.dialogue_upload_routine, null)
        dialogBuilder.setView(dialogView)

        val routineView = dialogView.containers
        val crossButton = dialogView.close_button

        val routineProgram = dialogView.routine_program
        val routineIntake = dialogView.routine_intake

        val sectionSpinner = dialogView.section_spinner
        val sessionSpinner = dialogView.session_spinner
        val sessionYearSpinner = dialogView.session_year
        val semesterSpinner = dialogView.semester_spinner
        val routineTypeSpinner = dialogView.routine_type_spinner

        val browseButton = dialogView.browse_image_button
        val uploadButton = dialogView.upload_routine_button

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        routineView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

        //load some value
        routineProgram.text = titleToDeptCode(currentUser!!.user_prog_or_dept)
        routineIntake.text = user_intake

        //local variable
        proCode = routineProgram.text.toString()
        intakeNo = routineIntake.text.toString()

        val sectionArray:Array<String> = resources.getStringArray(R.array.section)
        val sessionArray:Array<String> = resources.getStringArray(R.array.session)
        val sessionYearArray:Array<String> = resources.getStringArray(R.array.session_year)
        val sessionYearArrayTwo:Array<String> = resources.getStringArray(R.array.session_year_two)
        val semesterArray:Array<String> = resources.getStringArray(R.array.semesters)
        val routineTypeArray:Array<String> = resources.getStringArray(R.array.routine_type)

        val sectionAdapter = ArrayAdapter.createFromResource(this,R.array.section,R.layout.spinner_item_smoke_blue)
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        sectionSpinner.adapter = sectionAdapter
        sectionSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                secNo = sectionArray[position]
            }
        }

        val sessionAdapter = ArrayAdapter.createFromResource(this,R.array.session,R.layout.spinner_item_smoke_blue)
        sessionAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        sessionSpinner.adapter = sessionAdapter
        sessionSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sesCode = sessionArray[position]

                if(sesCode == "Fall"){
                    val sessionYearAdapterTwo = ArrayAdapter.createFromResource(context!!,R.array.session_year_two,R.layout.spinner_item_smoke_blue)
                    sessionYearAdapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    sessionYearSpinner.adapter = sessionYearAdapterTwo
                    sessionYearSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            sesYear = sessionYearArrayTwo[position]
                        }
                    }
                }else{
                    val sessionYearAdapter = ArrayAdapter.createFromResource(context!!,R.array.session_year,R.layout.spinner_item_smoke_blue)
                    sessionYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    sessionYearSpinner.adapter = sessionYearAdapter
                    sessionYearSpinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            sesYear = sessionYearArray[position]
                        }
                    }
                }
            }
        }

        val semesterAdapter = ArrayAdapter.createFromResource(this,R.array.semesters,R.layout.spinner_item_smoke_blue)
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        semesterSpinner.adapter = semesterAdapter
        semesterSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                semName = semesterArray[position]
            }
        }

        val routineTypeAdapter = ArrayAdapter.createFromResource(this,R.array.routine_type,R.layout.spinner_item_smoke_blue)
        routineTypeAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        routineTypeSpinner.adapter = routineTypeAdapter
        routineTypeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                routineType = routineTypeArray[position]
            }
        }

        crossButton.setOnClickListener{
            alertDialog.dismiss()
        }

        browseButton.setOnClickListener{
            selectRoutine()
        }

        uploadButton.setOnClickListener{

            if(sesYear.isNotEmpty() && routineUri != null){
                sessionName = "$sesCode, $sesYear"
                uploadExamCoverImage(semName)
                alertDialog.dismiss()
            }else if(routineUri == null){
                Toasty.warning(requireContext(), "please select an image.", Toast.LENGTH_SHORT, true).show()
            }
        }
    }


    private fun uploadExamCoverImage(semester:String) {
        val filename = "$proCode$intakeNo$secNo$semName$routineType"   //UUID.randomUUID().toString()

        val ref = mStorageRef!!.child("/routines/${titleToDeptCode(currentUser!!.user_prog_or_dept)}/$user_intake/$semester/$filename")

        val progress = ProgressDialog(context).apply {
            setTitle("Uploading....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }

        ref.putFile(routineUri!!).addOnSuccessListener {
            progress.dismiss()

            ref.downloadUrl.addOnSuccessListener {
                routineDownloadUri = it.toString()
                uploadExamRoutine()
            }
        }.addOnFailureListener { e ->
            progress.dismiss()
            MotionToast.darkColorToast(requireActivity(),"Failed!",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(context!!, R.font.helvetica_regular))

        }.addOnProgressListener { taskSnapshot ->
            val valu = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progress.setMessage("Uploaded " + valu.toInt() + "%")
        }

    }

    private fun uploadExamRoutine(){
        val filename = "$proCode$intakeNo$secNo$semName$routineType"   //UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Routines/${titleToDeptCode(currentUser!!.user_prog_or_dept)}/$user_intake/$filename")

        //get current time
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance()
        val formattedDate = formatter.format(date)

        val routine = Routines(proCode,intakeNo,secNo,
            semName,sessionName,routineType,
            routineDownloadUri,formattedDate,currentUser!!.user_id)

        val progress = ProgressDialog(context!!).apply {
            setTitle("Final Touch....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }

        ref.setValue(routine)
            .addOnSuccessListener {
                addExamRoutine(routine)
                progress.dismiss()

                activity!!.runOnUiThread(){
                    loadExamRoutines(user_intake)
                    Toasty.success(requireContext(), "Uploaded", Toast.LENGTH_SHORT, true).show()
                }
            }
            .addOnFailureListener{
                progress.dismiss()
                activity!!.runOnUiThread(){
                    Toasty.success(requireContext(), "Failed", Toast.LENGTH_SHORT, true).show()
                }
            }
    }

    private fun addExamRoutine(routine:Routines){
        val routineDatabase = DatabaseHelper(requireContext())
        routineDatabase.deleteExamRoutine()
        val status:Long = routineDatabase.saveExamRoutine(routine)
    }

    private fun selectRoutine(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent,10)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 10 && resultCode == Activity.RESULT_OK && data!=null){
            routineUri = data.data!!
        }
    }


    //Setting dialogue
    @SuppressLint("InflateParams", "SetTextI18n", "DefaultLocale")
    private fun Activity.showExamSettingDialog(proCode:String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialogue_routine_setting, null)
        dialogBuilder.setView(dialogView)

        val routineView = dialogView.containers
        val crossButton = dialogView.close_button
        val doneButton = dialogView.done_button
        val minusButton = dialogView.setting_minus_button
        val plusButton = dialogView.setting_plus_button

        val settingProgram = dialogView.setting_program
        val settingIntake = dialogView.setting_intake

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        routineView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

        //load some value
        settingProgram.text = proCode
        settingIntake.setText(currentUser!!.user_intake)

        crossButton.setOnClickListener{
            alertDialog.dismiss()
        }

        var iNo = settingIntake.text.toString()

        minusButton.setOnClickListener{
            iNo = (iNo.toInt() - 1).toString()
            settingIntake.setText(iNo)
        }

        plusButton.setOnClickListener{
            iNo = (iNo.toInt() + 1).toString()
            settingIntake.setText(iNo)
        }

        doneButton.setOnClickListener{
            val intakeNo = settingIntake.text.toString()
            if(intakeNo.isNotEmpty()){
                val intake = "Intake $intakeNo"
                loadExamRoutines(intake)
                alertDialog.dismiss()
                recyclerViewTitle.text = "${proCode} - ${intake.toUpperCase()}"
            }else{
                settingIntake.error = "!!"
            }
        }
    }

    //delete dialogue
    @SuppressLint("InflateParams")
    private fun Activity.showExamDeleteDialog(routine:Routines) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView = inflater.inflate(R.layout.dialogue_delete, null)
        dialogBuilder.setView(dialogView)

        val dialogueView = dialogView.containers

        val yesButton = dialogView.yes_button
        val noButton = dialogView.no_button

        val alertDialog = dialogBuilder.create()
        val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
        dialogueView.startAnimation(animPopUp)

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()

        yesButton.setOnClickListener{
            deleteExamRoutine(routine)
            alertDialog.dismiss()
        }

        noButton.setOnClickListener{
            alertDialog.dismiss()
        }
    }
}