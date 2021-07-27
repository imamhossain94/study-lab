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
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Model.Course
import com.newage.studlab.Model.StudLibrary.Sheet
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_studlab_tools_sheets.*
import kotlinx.android.synthetic.main.fragment_studlab_tools_sheets.chapter_no
import kotlinx.android.synthetic.main.fragment_studlab_tools_sheets.course_spinner
import kotlinx.android.synthetic.main.fragment_studlab_tools_sheets.semester_spinner
import kotlinx.android.synthetic.main.fragment_studlab_tools_sheets.sheet_tools_info_icon
import java.text.SimpleDateFormat
import java.util.*

class ToolsSheet : AppCompatActivity() {

    lateinit var progressSheets:ProgressDialog

    lateinit var upload_by:String
    lateinit var program:String
    lateinit var semesterName:String
    lateinit var coursCode:String
    lateinit var chapterNo:String

    lateinit var semester:Array<String>
    lateinit var chapter:Array<String>
    val course = arrayListOf<String>()
    var courList = arrayListOf<Course>()

    val DOC : Int = 0

    var sheetFileUri: Uri? = null

    var sheetFileIcon:String = ""
    var sheetDownloadUrl:String = ""

    var sheetTitle:String = ""
    var sheetGivenBy:String = ""

    var sheetSize:Int = 0
    var sheetSizeInMb:String = ""
    private var mStorageRef: StorageReference? = null

    var fileExtn:String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_studlab_tools_sheets)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        progressSheets = ProgressDialog(this).apply {
            setTitle("Uploading....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        upload_by = currentUser!!.user_id
        //Toast.makeText(this,upload_by,Toast.LENGTH_SHORT).show()

        mStorageRef = FirebaseStorage.getInstance().getReference()
        updateSpinner()

        semester = resources.getStringArray(R.array.semesters)
        chapter = resources.getStringArray(R.array.chapter)
        semesterName = semester[0]
        program = StudLabAssistant.selectedProgram

        upload_prog_sheet_msg.text = "Upload $program Sheets"


        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)
        sheets_upload_by_date.text = "Upload By $upload_by  [$formatedDate]"

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
                    val chapterSpinnerAdapter = ArrayAdapter.createFromResource(this@ToolsSheet,R.array.chapter,R.layout.spinner_item_tool_upload)
                    chapterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    chapter_no.adapter = chapterSpinnerAdapter

                    chapter_no.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                        @SuppressLint("SetTextI18n")
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            chapterNo = chapter[position]
                            sheets_course_code.text = "$coursCode (${chapterNo})"

                        }
                    }
                }

            }
        }
    }

    private fun allButtonClickListener(){
        browse_sheet_img.setOnClickListener{
            selectSheetDoc()
        }
        add_sheet_button.setOnClickListener{
            addSheet()
        }
        sheet_tools_info_icon.setOnClickListener{
            Toasty.info(this, "Add sheet. pdf or doc.", Toast.LENGTH_SHORT, true).show()
        }
    }


    private fun allTextChangesEventListeners(){
        sheet_title_input.addTextChangedListener(object : TextWatcher { @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            sheet_by_title.text = "${sheet_title_input.text} by ${sheet_given_input.text}"
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        sheet_given_input.addTextChangedListener(object : TextWatcher { @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            sheet_by_title.text = "${sheet_title_input.text} by ${sheet_given_input.text}"
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun selectSheetDoc(){
        setupPermissions()
        val intent =  Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        val mimeTypes = arrayOf("application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/pdf")
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, DOC);
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {

        }
    }

    private fun addSheet(){
        sheetTitle = sheet_title_input.text.toString()
        sheetGivenBy = sheet_given_input.text.toString()
        sheetDownloadUrl = url_sheet_link_input.text.toString()

        if(sheetTitle.isNotEmpty() && sheetGivenBy.isNotEmpty() && sheetFileUri!=null || sheetTitle.isNotEmpty() && sheetGivenBy.isNotEmpty() && sheetDownloadUrl.isNotEmpty()){
            uploadSheetfile()
        }else if(sheetTitle.isEmpty()){
            Toasty.info(this, "Title is empty", Toast.LENGTH_SHORT, true).show()
        }else if(sheetGivenBy.isEmpty()){
            Toasty.info(this, "Given by is empty.", Toast.LENGTH_SHORT, true).show()
        }else if(sheetFileUri == null){
            Toasty.info(this, "No doc or pdf is selected.", Toast.LENGTH_SHORT, true).show()
        }
    }



    @SuppressLint("DefaultLocale")
    private fun uploadSheetfile() {

        progressSheets.show()

        val name = "$program $semesterName $coursCode $sheetTitle $fileExtn $chapterNo $sheetGivenBy $upload_by".toLowerCase()
        val ref = mStorageRef!!.child("/library/$program/$semesterName/$coursCode/Sheet/$name")

        ref.putFile(sheetFileUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                sheetDownloadUrl = it.toString()
                uploadSheet()
            }


        }.addOnFailureListener { e ->
            progressSheets.dismiss()
            Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()

        }.addOnProgressListener { taskSnapshot ->
            val value = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressSheets.setMessage("Complete " + value.toInt() + "%")
        }
    }


    @SuppressLint("DefaultLocale")
    private fun uploadSheet(){
        val name = "$program $semesterName $coursCode $sheetTitle $fileExtn $chapterNo $sheetGivenBy $upload_by".toLowerCase()
        val ref = FirebaseDatabase.getInstance().getReference("/Library/Sheet/$name")

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)

        val sheet = Sheet(
            program,
            semesterName,
            coursCode,
            chapterNo,
            "$sheetTitle.$fileExtn",
            sheetGivenBy,
            sheetFileIcon,
            sheetDownloadUrl,
            upload_by,
            formatedDate,
            sheetSizeInMb,
            "0")

        ref.setValue(sheet)
            .addOnSuccessListener {
                progressSheets.dismiss()

                Toasty.success(this, "Complete", Toast.LENGTH_SHORT, true).show()


            }
            .addOnFailureListener{
                progressSheets.dismiss()

                Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()

            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == DOC && resultCode == Activity.RESULT_OK && data!=null){
            sheetFileUri = data.getData()!!
            val scheme = sheetFileUri!!.getScheme();

            if (data.getData().toString().contains(".pdf")) {
                fileExtn = "pdf"
                sheetFileIcon = "PDF"

                sheets_file_icon.setImageResource(R.drawable.pdf_icon_svg)

            } else if (data.getData().toString().contains(".doc")) {
                fileExtn = "doc"
                sheetFileIcon = "DOC"

                sheets_file_icon.setImageResource(R.drawable.doc_icon_svg)

            }else if (sheetFileUri.toString().contains(".docx")){
                fileExtn = "docx"
                sheetFileIcon = "DOCX"

                sheets_file_icon.setImageResource(R.drawable.doc_icon_svg)

            }

            if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                try {
                    val fileInputStream=getApplicationContext().getContentResolver().openInputStream(sheetFileUri!!);
                    sheetSize = fileInputStream!!.available();
                    val sizeInMbDouble = sheetSize/(1000.0*1000)
                    sheetSizeInMb = "%.2f".format(sizeInMbDouble)
                    //Toast.makeText(this, bookSizeInMb,Toast.LENGTH_SHORT).show()
                } catch (e:Exception) {
                    e.printStackTrace();
                }
            }
        }
    }
}