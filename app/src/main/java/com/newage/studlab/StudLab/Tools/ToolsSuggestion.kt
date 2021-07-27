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
import com.newage.studlab.Model.StudLibrary.Suggestion
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.R
import com.newage.studlab.StudLab.StudLabActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_studlab_tools_suggestion.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ToolsSuggestion : AppCompatActivity() {

    lateinit var progressSuggest:ProgressDialog
    var picker: DatePickerDialog? = null

    lateinit var upload_by:String
    lateinit var program:String
    lateinit var semesterName:String
    lateinit var coursCode:String
    lateinit var termsName:String

    lateinit var semester:Array<String>
    lateinit var terms:Array<String>

    val course = arrayListOf<String>()
    var courList = arrayListOf<Course>()

    val PDF : Int = 0
    var suggestionFileUri: Uri? = null

    var suggestionFileIcon:String = ""
    var suggestionDownloadUrl:String = ""

    var suggestionTitle:String = ""
    var suggestionGivenBy:String = ""
    var suggestionGivenDate:String = ""

    var suggestionSize:Int = 0
    var suggestionSizeInMb:String = ""
    private var mStorageRef: StorageReference? = null

    var fileExtn:String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_studlab_tools_suggestion)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        progressSuggest = ProgressDialog(this).apply {
            setTitle("Uploading....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        upload_by = currentUser!!.user_id


        mStorageRef = FirebaseStorage.getInstance().getReference();
        updateSpinner()

        semester = resources.getStringArray(R.array.semesters)
        terms = resources.getStringArray(R.array.terms)

        semesterName = semester[0]
        program = StudLabAssistant.selectedProgram

        upload_prog_suggestion_message.text = "Upload $program Suggestion"

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance()
        val formatedDate = formatter.format(date)

        suggestion_upload_by.text = "Upload by ${StudLab.currentUser!!.user_id} on $formatedDate"

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

    private fun loadCourse(semester:String){

        course.clear()
        courList.clear()

        if(StudLab.courseList.size != 0){
            courList = StudLab.courseList.filter { it-> it.programCode == program && it.semesterTitle == semester } as ArrayList<Course>

            if(courList.size != 0){
                courList.forEach{
                    course.add(it.courseCode)
                }
            }
        }

        if(course.size == 0){
            course.add("No Course")
            Toasty.info(this, "No course found", Toast.LENGTH_SHORT, true).show()
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
                    val chapterSpinnerAdapter = ArrayAdapter.createFromResource(this@ToolsSuggestion,R.array.terms,R.layout.spinner_item_tool_upload)
                    chapterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                    terms_spinner.adapter = chapterSpinnerAdapter

                    terms_spinner.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                        @SuppressLint("SetTextI18n")
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            termsName = terms[position]

                            suggestion_title_preview.text = "$coursCode ($termsName) Suggestion"
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun allButtonClickListener(){

        browse_suggestion_img.setOnClickListener{
            selectSuggestionDoc()
        }


        add_suggestion_button.setOnClickListener{
            addSuggestion()
        }

        suggestion_info_icon.setOnClickListener{
            Toasty.info(this, "Add a suggestion. pdf, doc file allowed.", Toast.LENGTH_SHORT, true).show()

        }

        suggestion_date.setOnClickListener{
            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            // date picker dialog
            picker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    suggestion_date.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                    suggestion_by_preview.text = "By ${suggestion_by.text} on ${suggestion_date.text}"
                }, year, month, day)
            picker!!.show()
        }
    }


    private fun allTextChangesEventListeners(){
        suggestion_by.addTextChangedListener(object : TextWatcher { @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            suggestion_by_preview.text = "By ${suggestion_by.text} on ${suggestion_date.text}"

        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

    }




    private fun selectSuggestionDoc(){
        setupPermissions()
        val intent =  Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        val mimeTypes = arrayOf("application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/pdf")
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


    private fun addSuggestion(){

        suggestionGivenBy = suggestion_by.text.toString()
        suggestionGivenDate = suggestion_date.text.toString()
        suggestionDownloadUrl = suggestion_link.text.toString()

        if(suggestionGivenBy.isNotEmpty() && suggestionGivenDate.isNotEmpty() && suggestionFileUri!=null ||suggestionGivenBy.isNotEmpty() && suggestionGivenDate.isNotEmpty() && suggestionDownloadUrl.isNotEmpty()){
            uploadLecturefile()

        }else if(suggestionGivenBy.isEmpty()){
            Toasty.warning(this, "Suggestion by is empty", Toast.LENGTH_SHORT, true).show()
        }
        else if(suggestionGivenDate.isEmpty()){
            Toasty.warning(this, "Suggestion date is empty", Toast.LENGTH_SHORT, true).show()

        }else if(suggestionFileUri == null){

            Toasty.warning(this, "No pdf or doc file selected.", Toast.LENGTH_SHORT, true).show()
        }
    }



    @SuppressLint("DefaultLocale")
    private fun uploadLecturefile() {
        progressSuggest.show()

        val name = "${suggestion_by.text} ${suggestion_title_preview.text} $coursCode".toLowerCase()
        val ref = mStorageRef!!.child("/library/$program/$semesterName/$coursCode/Suggestion/$name")


        ref.putFile(suggestionFileUri!!).addOnSuccessListener {

            ref.downloadUrl.addOnSuccessListener {
                suggestionDownloadUrl = it.toString()
                uploadLecture()
            }

        }.addOnFailureListener { e ->
            progressSuggest.dismiss()
            Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()

        }.addOnProgressListener { taskSnapshot ->
            val value = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressSuggest.setMessage("Complete " + value.toInt() + "%")
        }
    }


    @SuppressLint("DefaultLocale")
    private fun uploadLecture(){
        progressSuggest.setMessage("Finished")

        val name = "$program $semesterName $coursCode ${suggestion_by.text} $termsName".toLowerCase()

        val icon = suggestionFileIcon
        val ref = FirebaseDatabase.getInstance().getReference("/Library/Suggestion/$name")

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)

        val suggest = Suggestion(program, semesterName, coursCode,
            suggestion_by.text.toString(), termsName, suggestionGivenDate,
            icon, suggestionDownloadUrl, upload_by, formatedDate,
            suggestionSizeInMb, "0")

        ref.setValue(suggest)
            .addOnSuccessListener {
                progressSuggest.dismiss()
                Toasty.success(this, "Success", Toast.LENGTH_SHORT, true).show()
            }
            .addOnFailureListener{
                progressSuggest.dismiss()
                Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()
            }
    }



    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PDF && resultCode == Activity.RESULT_OK && data!=null){

            suggestionFileUri = data.getData()!!
            val scheme = suggestionFileUri!!.getScheme();

            if (data.getData().toString().contains(".pdf")) {
                fileExtn = "pdf"
                suggestionFileIcon = "PDF"
                suggestion_icon.setImageResource(R.drawable.pdf_icon_svg)
            } else if (data.getData().toString().contains(".doc")) {
                fileExtn = "doc"
                suggestionFileIcon = "DOC"
                suggestion_icon.setImageResource(R.drawable.doc_icon_svg)
            }else if (suggestionFileUri.toString().contains(".docx")){
                fileExtn = "docx"
                suggestionFileIcon = "DOCX"
                suggestion_icon.setImageResource(R.drawable.doc_icon_svg)
            }

            if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                try {
                    val fileInputStream=getApplicationContext().getContentResolver().openInputStream(suggestionFileUri!!);
                    suggestionSize = fileInputStream!!.available();
                    val sizeInMbDouble = suggestionSize/(1000.0*1000)
                    suggestionSizeInMb = "%.2f".format(sizeInMbDouble)
                    suggestion_file_size.text = "$suggestionSizeInMb MB"

                } catch (e:Exception) {
                    e.printStackTrace();
                }
            }else if(scheme.equals(ContentResolver.SCHEME_FILE)) {
                val path = suggestionFileUri!!.getPath();
                try {
                    val f = File(path!!)
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        }
    }

}