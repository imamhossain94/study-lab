package com.newage.studlab.StudLab.Tools

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Model.Course
import com.newage.studlab.Model.StudLibrary.Lecture
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_studlab_tools_lectures.*
import kotlinx.android.synthetic.main.fragment_studlab_tools_lectures.chapter_no
import kotlinx.android.synthetic.main.fragment_studlab_tools_lectures.course_spinner
import kotlinx.android.synthetic.main.fragment_studlab_tools_lectures.semester_spinner
import kotlinx.android.synthetic.main.fragment_studlab_tools_sheets.*
import java.text.SimpleDateFormat
import java.util.*

class ToolsLecture : AppCompatActivity() {

    lateinit var progressLecture:ProgressDialog
    var picker: DatePickerDialog? = null

    lateinit var upload_by:String
    lateinit var program:String
    lateinit var semesterName:String
    lateinit var coursCode:String
    lateinit var chapterNo:String
    lateinit var lectureNo:String

    lateinit var semester:Array<String>
    lateinit var chapter:Array<String>
    lateinit var lecture:Array<String>
    val course = arrayListOf<String>()
    var courList = arrayListOf<Course>()

    val PDF : Int = 0
    var lectureFileUri: Uri? = null

    var lectureFileIcon:String = ""
    var lectureDownloadUrl:String = ""

    var lectureTitle:String = ""
    var lectureGivenBy:String = ""
    var lectureGivenDate:String = ""

    var lectureSize:Int = 0
    var lectureSizeInMb:String = ""
    private var mStorageRef: StorageReference? = null

    var fileExtn:String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_studlab_tools_lectures)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        progressLecture = ProgressDialog(this).apply {
            setTitle("Uploading....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        upload_by = currentUser!!.user_id
        //Toast.makeText(this,upload_by,Toast.LENGTH_SHORT).show()

        mStorageRef = FirebaseStorage.getInstance().getReference();
        updateSpinner()

        semester = resources.getStringArray(R.array.semesters)
        chapter = resources.getStringArray(R.array.chapter)
        lecture = resources.getStringArray(R.array.lecture)
        semesterName = semester[0]
        program = StudLabAssistant.selectedProgram

        upload_prog_lecture.text = "Upload $program Lecture"



        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)
        lecture_upload_by_date.text = "Upload By $upload_by  [$formatedDate]"

        allButtonClickListener()
        allTextChangesEventListeners()
    }


    private fun updateSpinner(){
        val semesterSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.semesters,R.layout.spinner_item_tool_upload)
        semesterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        semester_spinner.adapter = semesterSpinnerAdapter

        semester_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                semesterName = semester[position]
                loadCourse(semesterName)
            }
        }

    }

    private fun loadCourse(semseter:String){

        course.clear()
        courList.clear()

        if(StudLab.courseList.size != 0){
            courList = StudLab.courseList.filter { it-> it.programCode == program && it.semesterTitle == semseter } as ArrayList<Course>

            if(courList.size != 0){
                courList.forEach{
                    course.add(it.courseCode)
                }
            }
        }

        if(course.size == 0){
            course.add("No Course")
            //Toasty.info(this, "No course found", Toast.LENGTH_SHORT, true).show()
        }

        val courseAdapterSpinner: ArrayAdapter<String> = ArrayAdapter(this, R.layout.spinner_item_tool_upload, course)
        courseAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_item)
        course_spinner.adapter = courseAdapterSpinner

        course_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                coursCode = course[pos]

                if(course[pos] != "No Course"){
                    val chapterSpinnerAdapter = ArrayAdapter.createFromResource(this@ToolsLecture,R.array.chapter,R.layout.spinner_item_tool_upload)
                    chapterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    chapter_no.adapter = chapterSpinnerAdapter

                    chapter_no.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                        @SuppressLint("SetTextI18n")
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            chapterNo = chapter[position]
                            //lecture_course_code.text = "$coursCode ($chapterNo, $lectureNo)"

                            val lectureSpinnerAdapter = ArrayAdapter.createFromResource(this@ToolsLecture,R.array.lecture,R.layout.spinner_item_tool_upload)
                            lectureSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                            lecture_no.adapter = lectureSpinnerAdapter

                            lecture_no.onItemSelectedListener = object :
                                AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(parent: AdapterView<*>?) {}
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    lectureNo = lecture[position]
                                    lecture_course_code.text = "$coursCode ($chapterNo, $lectureNo)"
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun allButtonClickListener(){

        browse_lecture_img.setOnClickListener{
            selectLectureDoc()
        }

        add_lecture_button.setOnClickListener{
            addLecture()
        }

        lecture_tools_info_icon.setOnClickListener{
            Toasty.info(this, "Add Lecture. pdf , doc or zip.", Toast.LENGTH_SHORT, true).show()
        }

        lecture_date.setOnClickListener{

            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            // date picker dialog
            picker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    lecture_date.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    lecture_by_date_title.text = "${lecture_by_input.text} by ${lecture_title_input.text} on ${lecture_date.text}"

                }, year, month, day)
            picker!!.show()

        }



    }


    private fun allTextChangesEventListeners(){
        lecture_title_input.addTextChangedListener(object : TextWatcher { @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            lecture_by_date_title.text = "${lecture_by_input.text} by ${lecture_title_input.text} on ${lecture_date.text}"
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        lecture_by_input.addTextChangedListener(object : TextWatcher { @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            lecture_by_date_title.text = "${lecture_by_input.text} by ${lecture_title_input.text} on ${lecture_date.text}"
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }





    private fun selectLectureDoc(){
        setupPermissions()
        val intent =  Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        val mimeTypes = arrayOf("application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/pdf","application/zip")
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PDF);
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {

        }
    }


    private fun addLecture(){
        lectureTitle = lecture_title_input.text.toString()
        lectureGivenBy = lecture_by_input.text.toString()
        lectureGivenDate = lecture_date.text.toString()
        lectureDownloadUrl = url_lecture_link_input.text.toString()

        if(lectureTitle.isNotEmpty() && lectureGivenBy.isNotEmpty() && lectureGivenDate.isNotEmpty() && lectureFileUri!=null || lectureTitle.isNotEmpty() && lectureGivenBy.isNotEmpty() && lectureGivenDate.isNotEmpty() && lectureDownloadUrl.isNotEmpty()){
            uploadLecturefile()

        }else if(lectureTitle.isEmpty()){
            Toasty.warning(this, "Title is empty.", Toast.LENGTH_SHORT, true).show()
        }else if(lectureGivenBy.isEmpty()){
            Toasty.warning(this, "Lecture by is empty.", Toast.LENGTH_SHORT, true).show()
        }
        else if(lectureGivenDate.isEmpty()){
            Toasty.warning(this, "lecture given date is null.", Toast.LENGTH_SHORT, true).show()

        }else if(lectureFileUri == null){
            Toasty.warning(this, "No pdf, doc or zip file is selected..", Toast.LENGTH_SHORT, true).show()
        }
    }



    @SuppressLint("DefaultLocale")
    private fun uploadLecturefile() {
        progressLecture.show()

        val name = "$program $semesterName $coursCode $lectureTitle $fileExtn $chapterNo $lectureGivenBy $upload_by".toLowerCase()
        val ref = mStorageRef!!.child("/library/$program/$semesterName/$coursCode/Lecture/$name")

        ref.putFile(lectureFileUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                lectureDownloadUrl = it.toString()
                uploadLecture()
            }


        }.addOnFailureListener { e ->
            progressLecture.dismiss()
            Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()
        }.addOnProgressListener { taskSnapshot ->
            val value = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressLecture.setMessage("Complete " + value.toInt() + "%")
        }
    }


    @SuppressLint("DefaultLocale")
    private fun uploadLecture(){
        val name = "$program $semesterName $coursCode $lectureTitle $fileExtn $chapterNo $lectureGivenBy $upload_by".toLowerCase()

        val ref = FirebaseDatabase.getInstance().getReference("/Library/Lecture/$name")

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)

        val lecture = Lecture(program,
            semesterName,
            coursCode,
            lectureNo,
            chapterNo,
            "$lectureTitle.$fileExtn",
            lectureGivenBy,
            lectureFileIcon,
            lectureDownloadUrl,
            upload_by,
            formatedDate,
            lectureGivenDate,
            lectureSizeInMb,
            "0")

        ref.setValue(lecture)
            .addOnSuccessListener {
                progressLecture.dismiss()

                Toasty.success(this, "Success", Toast.LENGTH_SHORT, true).show()

            }
            .addOnFailureListener{
                progressLecture.dismiss()

                Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()
            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PDF && resultCode == Activity.RESULT_OK && data!=null){
            lectureFileUri = data.data!!
            val scheme = lectureFileUri!!.getScheme();

            if (data.data.toString().contains(".pdf")) {
                fileExtn = "pdf"
                lectureFileIcon = "PDF"

                lecture_file_icon.setImageResource(R.drawable.pdf_icon_svg)

            } else if (data.data.toString().contains(".doc")) {
                fileExtn = "doc"
                lectureFileIcon = "DOC"

                lecture_file_icon.setImageResource(R.drawable.doc_icon_svg)

            }else if (data.data.toString().contains(".docx")){
                fileExtn = "docx"
                lectureFileIcon = "DOCX"

                lecture_file_icon.setImageResource(R.drawable.doc_icon_svg)

            }else if (data.data.toString().contains(".zip") || data.data.toString().contains(".ZIP")){
                fileExtn = "zip"
                lectureFileIcon = "ZIP"

                lecture_file_icon.setImageResource(R.drawable.zip_icon_svg)

            }

            if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                try {
                    val fileInputStream=getApplicationContext().getContentResolver().openInputStream(lectureFileUri!!);
                    lectureSize = fileInputStream!!.available();
                    val sizeInMbDouble = lectureSize/(1000.0*1000)
                    lectureSizeInMb = "%.2f".format(sizeInMbDouble)
                    //Toast.makeText(this, bookSizeInMb,Toast.LENGTH_SHORT).show()
                } catch (e:Exception) {
                    e.printStackTrace();
                }
            }
        }
    }
}