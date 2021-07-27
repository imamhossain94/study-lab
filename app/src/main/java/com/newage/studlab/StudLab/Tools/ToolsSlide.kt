package com.newage.studlab.StudLab.Tools

import android.annotation.SuppressLint
import android.app.Activity
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
import com.newage.studlab.Application.StudLab.Companion.courseList
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Model.Course
import com.newage.studlab.Model.StudLibrary.Slide
import com.newage.studlab.Plugins.FileHelper.Companion.fileTypre
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_studlab_tools_slides.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ToolsSlide : AppCompatActivity() {


    lateinit var progressSlide:ProgressDialog

    lateinit var upload_by:String
    lateinit var program:String
    lateinit var semesterName:String
    lateinit var coursCode:String
    lateinit var chapterNo:String

    lateinit var semester:Array<String>
    lateinit var chapter:Array<String>

    val course = arrayListOf<String>()
    var courList = arrayListOf<Course>()

    val PPT : Int = 0
    val PDF : Int = 1
    var slideFileUri: Uri? = null

    var slideFileIcon:String = ""
    var slidePptDownloadUrl:String = ""

    var slideTitle:String = ""

    var slideSize:Int = 0
    var slideSizeInMb:String = ""
    private var mStorageRef: StorageReference? = null


    var fileExtn:String = ""



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_studlab_tools_slides)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        progressSlide = ProgressDialog(this).apply {
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
        semesterName = semester[0]
        program = StudLabAssistant.selectedProgram

        upload_slide_title.text = "Upload $program Slide"


        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance()
        val formatedDate = formatter.format(date)

        slide_upload_by_date.text = "Upload by ${currentUser!!.user_id} on $formatedDate"

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

        if(courseList.size != 0){
            courList = courseList.filter { it-> it.programCode == program && it.semesterTitle == semseter } as ArrayList<Course>

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
                    val chapterSpinnerAdapter = ArrayAdapter.createFromResource(this@ToolsSlide,R.array.chapter,R.layout.spinner_item_tool_upload)
                    chapterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    chapter_no.adapter = chapterSpinnerAdapter

                    chapter_no.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                        @SuppressLint("SetTextI18n")
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            chapterNo = chapter[position]
                            slide_course_code.text = "$coursCode (${chapterNo})"

                        }
                    }
                }

            }
        }
    }


    private fun allButtonClickListener(){

        browse_slide_img.setOnClickListener{
            selectSlidePPT()
        }

        add_slide_button.setOnClickListener{
            addSlide()
        }

        sheet_tools_info_icon.setOnClickListener{
            Toasty.info(this, "Add slide. pdf or ppt.", Toast.LENGTH_SHORT, true).show()
        }
    }

    private fun allTextChangesEventListeners(){
        slide_title_inout.addTextChangedListener(object : TextWatcher { @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            slide_file_title.text = slide_title_inout.text.toString()
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun selectSlidePPT(){
        setupPermissions()
        val intent =  Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        val mimeTypes = arrayOf("application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation","application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/pdf")
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PPT);
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {

        }
    }


    private fun addSlide(){
        slideTitle = slide_title_inout.text.toString()
        slidePptDownloadUrl = url_slide_link_input.text.toString()

        if(slideTitle.isNotEmpty() && slideFileUri!=null || slideTitle.isNotEmpty() && slidePptDownloadUrl.isNotEmpty()){
            uploadSlideFile()

        }else if(slideTitle.isEmpty()){
            Toasty.error(this, "Title is empty", Toast.LENGTH_SHORT, true).show()

        }else if(slideFileUri == null){
            Toasty.error(this, "No file selected", Toast.LENGTH_SHORT, true).show()

        }

    }

    @SuppressLint("DefaultLocale")
    private fun uploadSlideFile() {
        progressSlide.show()

        val name = "$program $semesterName $coursCode $slideTitle $fileExtn $chapterNo".toLowerCase()
        val ref = mStorageRef!!.child("/library/$program/$semesterName/$coursCode/Slide/$name")


        ref.putFile(slideFileUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                slidePptDownloadUrl = it.toString()
                uploadSlide()
            }

        }.addOnFailureListener { e ->
            progressSlide.dismiss()
            Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()


        }.addOnProgressListener { taskSnapshot ->
            val value = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressSlide.setMessage("Complete " + value.toInt() + "%")
        }
    }


    @SuppressLint("DefaultLocale")
    private fun uploadSlide(){
        val name = "$program $semesterName $coursCode $slideTitle $fileExtn $chapterNo".toLowerCase()

        val ref = FirebaseDatabase.getInstance().getReference("/Library/Slide/$name")

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)

        val slide = Slide(
            program,
            semesterName,
            coursCode,
            chapterNo,
            "$slideTitle.$fileExtn",
            slidePptDownloadUrl,
            slideFileIcon,
            upload_by,
            formatedDate,
            slideSizeInMb,
            "0"
        )

        progressSlide.setMessage("Finished")

        ref.setValue(slide)
            .addOnSuccessListener {
                progressSlide.dismiss()

                Toasty.success(this, "Success", Toast.LENGTH_SHORT, true).show()

            }
            .addOnFailureListener{
                progressSlide.dismiss()

                Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()

            }
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PPT && resultCode == Activity.RESULT_OK && data!=null){
            slideFileUri = data.getData()!!
            val scheme = slideFileUri!!.getScheme();

            if (data.getData().toString().contains(".pdf")) {
                fileExtn = "pdf"
                slideFileIcon = "PDF"


                slid_file_icon.setImageResource(R.drawable.pdf)
                slide_file_title.text = "${slide_file_title.text}.$fileExtn"

            } else if (data.getData().toString().contains(".ppt")) {
                fileExtn = "ppt"
                slideFileIcon = "PPT"

                slid_file_icon.setImageResource(R.drawable.ppt)
                slide_file_title.text = "${slide_file_title.text}.$fileExtn"

            }else if (slideFileUri.toString().contains(".pptx")){
                fileExtn = "pptx"
                slideFileIcon = "PPTX"

                slid_file_icon.setImageResource(R.drawable.ppt)
                slide_file_title.text = "${slide_file_title.text}.$fileExtn"
            }


            if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                try {
                    val fileInputStream= applicationContext.contentResolver.openInputStream(slideFileUri!!);
                    slideSize = fileInputStream!!.available();
                    val sizeInMbDouble = slideSize/(1000.0*1000)
                    slideSizeInMb = "%.2f".format(sizeInMbDouble)

                    slide_file_size.text = "$slideSizeInMb MB"
                } catch (e:Exception) {
                    e.printStackTrace();
                }
            } else if(scheme.equals(ContentResolver.SCHEME_FILE)) {
                val path = slideFileUri!!.getPath();
                try {
                    val f = File(path);
                    //openFile(f)
                    //Toast.makeText(this, "${f.length()}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        }
    }
}