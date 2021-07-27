package com.newage.studlab.StudLab.Tools

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.newage.studlab.Application.StudLab.Companion.courseList
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Model.StudLibrary.Book
import com.newage.studlab.Model.Course
import com.newage.studlab.Model.TokenModel.DeviceToken
import com.newage.studlab.Plugins.StudLabAssistant
import com.newage.studlab.R
import com.newage.studlab.Services.FcmNotificationService.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_studlab_tools_books.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ToolsBook : AppCompatActivity() {

    private lateinit var apiService: APIService

    lateinit var progressBook:ProgressDialog


    lateinit var upload_by:String
    lateinit var program:String
    lateinit var semesterName:String
    lateinit var editionNo:String
    lateinit var coursCode:String

    lateinit var semester:Array<String>
    lateinit var editionArray:Array<String>
    val course = arrayListOf<String>()
    var courList = arrayListOf<Course>()

    val IMAGE : Int = 0
    val PDF : Int = 1
    var bookCoverUri: Uri? = null
    var bookFileUri: Uri? = null

    var bookWriterName: String = ""
    var bookCoverDownloadUrl:String = ""
    var bookPDFDownloadUrl:String = ""
    var bookTitle:String = ""
    var bookSize:Int = 0
    var bookSizeInMb:String = ""

    private var mStorageRef: StorageReference? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_studlab_tools_books)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        apiService = Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        mStorageRef = FirebaseStorage.getInstance().getReference();
        updateSpinner()

        semester = resources.getStringArray(R.array.semesters)
        semesterName = semester[0]

        editionArray = resources.getStringArray(R.array.edition)
        editionNo = editionArray[0]

        program = StudLabAssistant.selectedProgram
        upload_by = currentUser!!.user_id

        upload_program_book.text = "Upload $program Book"

        progressBook = ProgressDialog(this).apply {
            setTitle("Uploading....")
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

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

        val editionSpinnerAdapter = ArrayAdapter.createFromResource(this,R.array.edition,R.layout.spinner_item_tool_upload)
        editionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        edition_spinner.adapter = editionSpinnerAdapter

        edition_spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            @SuppressLint("SetTextI18n")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                editionNo = editionArray[position]

                if(upload_book_title.text.toString().isNotEmpty())
                preview_book_title_edition.text = upload_book_title.text.toString() +" "+ editionNo
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

                val date = Calendar.getInstance().time
                val formatter = SimpleDateFormat.getDateInstance()
                val formatedDate = formatter.format(date)

                preview_book_description.text = "$coursCode \nUploaded by ${currentUser!!.user_id} \non $formatedDate"

            }
        }
    }



    private fun allButtonClickListener(){

        upload_book_cover_img.setOnClickListener{
            selectBookCover()
        }

        browse_book_img.setOnClickListener{
            selectBookPdf()
        }

        upload_book_button.setOnClickListener{
            addBook()
        }

        var rate = 0f

        review_book_minus.setOnClickListener{
            if(upload_book_rating.text.toString().isEmpty()){
                preview_book_rating.rating = rate
                upload_book_rating.text = rate.toString()
            }else if(rate >= .5f){
                rate -=.5f
                preview_book_rating.rating = rate
                upload_book_rating.text = rate.toString()
            }else{
                preview_book_rating.rating = rate
                upload_book_rating.text = rate.toString()
            }
        }

        review_book_plus.setOnClickListener{
            if(upload_book_rating.text.toString().isEmpty()){
                preview_book_rating.rating = rate
                upload_book_rating.text = rate.toString()
            }else if(rate <= 4.5f){
                rate +=.5f
                preview_book_rating.rating = rate
                upload_book_rating.text = rate.toString()
            }else{
                preview_book_rating.rating = rate
                upload_book_rating.text = rate.toString()
            }
        }

        upload_book_info_icon.setOnClickListener{
            Toasty.info(this, "Add a book", Toast.LENGTH_SHORT, true).show()
        }
    }


    private fun allTextChangesEventListeners(){
        upload_book_title.addTextChangedListener(object : TextWatcher { @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            preview_book_title_edition.text = upload_book_title.text.toString() +" "+ editionNo
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        upload_book_writer_name.addTextChangedListener(object : TextWatcher { @SuppressLint("SetTextI18n")
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            preview_book_writer.text = "By "+upload_book_writer_name.text.toString()
        }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }


    private fun selectBookCover(){
        setupPermissions()
        val intent = Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent,IMAGE)
    }

    private fun selectBookPdf(){
        setupPermissions()
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select pdf from here..."
            ),
            PDF
        )
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {

        }
    }


    @SuppressLint("SetTextI18n")
    private fun addBook(){
        bookTitle = upload_book_title.text.toString()
        bookWriterName = upload_book_writer_name.text.toString()
        bookPDFDownloadUrl = url_book_link_input.text.toString()

        if(bookTitle.isNotEmpty() && bookWriterName.isNotEmpty() && bookCoverUri!=null && bookFileUri!=null && upload_book_rating.text.toString().isNotEmpty() || bookTitle.isNotEmpty() && bookWriterName.isNotEmpty() && bookCoverUri!=null && bookPDFDownloadUrl.isNotEmpty()  && upload_book_rating.text.toString().isNotEmpty()){
            uploadCoverImage()

        }else if(bookTitle.isEmpty()){
            Toasty.warning(this, "Book title is empty", Toast.LENGTH_SHORT, true).show()

        }else if(bookWriterName.isEmpty()){
            Toasty.warning(this, "Book writer name is empty", Toast.LENGTH_SHORT, true).show()

        }else if(bookCoverUri == null){
            Toasty.warning(this, "Book cover image is required", Toast.LENGTH_SHORT, true).show()

        }else if(bookFileUri == null){
            Toasty.warning(this, "Please select a book or insert an download url.", Toast.LENGTH_SHORT, true).show()

        }else if(upload_book_rating.text.toString().isEmpty()){
            Toasty.warning(this, "Book rating is empty", Toast.LENGTH_SHORT, true).show()

        }

    }

    private fun uploadCoverImage() {

        progressBook.show()
        progressBook.setMessage("Uploading cover image.")
        val filename = "${upload_book_title.text} image"
        val ref = mStorageRef!!.child("/library/$program/$semesterName/$coursCode/Book/$filename")

        ref.putFile(bookCoverUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                bookCoverDownloadUrl = it.toString()
                uploadBookPdf()
            }
        }.addOnFailureListener { e ->
            progressBook.dismiss()
            Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()

        }.addOnProgressListener { taskSnapshot ->
            val valu = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressBook.setMessage("Complete " + valu.toInt() + "%")
        }

    }


    private fun uploadBookPdf() {

        progressBook.setMessage("Uploading book..")

        val filename = "${upload_book_title.text} Pdf"
        val ref = mStorageRef!!.child("/library/$program/$semesterName/$coursCode/Book/$filename")

        ref.putFile(bookFileUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                bookPDFDownloadUrl = it.toString()
                uploadBook()
            }


        }.addOnFailureListener { e ->
            progressBook.dismiss()
            Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()

        }.addOnProgressListener { taskSnapshot ->
            val value = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressBook.setMessage("Complete " + value.toInt() + "%")
        }
    }


    @SuppressLint("DefaultLocale")
    private fun uploadBook(){
        progressBook.setMessage("Please wait")
        val filename = "$program $semesterName $coursCode ${upload_book_title.text} $editionNo".toLowerCase()
        val ref = FirebaseDatabase.getInstance().getReference("/Library/Book/$filename")

        val book = Book(program,semesterName,coursCode,"$bookTitle $editionNo",
            upload_book_rating.text.toString(),bookWriterName,preview_book_description.text.toString(),
            "0", "0", "",
            bookCoverDownloadUrl,bookPDFDownloadUrl,bookSizeInMb)

        ref.setValue(book)
            .addOnSuccessListener {

                createNotification("Library: ${book.courCode}","${book.bookTitleEdition} by ${book.bookWriterName} was uploaded by ${currentUser!!.user_id}")
                progressBook.dismiss()

                Toasty.success(this, "Upload complete", Toast.LENGTH_SHORT, true).show()
            }
            .addOnFailureListener{
                progressBook.dismiss()
                Toasty.error(this, "Failed", Toast.LENGTH_SHORT, true).show()
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE && resultCode == Activity.RESULT_OK && data!=null){
            bookCoverUri = data.getData()!!

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, bookCoverUri)
            upload_book_cover_img.setImageBitmap(bitmap)
        }else if(requestCode == PDF && resultCode == Activity.RESULT_OK && data!=null){
            bookFileUri = data.getData()!!
            val scheme = bookFileUri!!.getScheme();

            if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                try {
                    val fileInputStream=getApplicationContext().getContentResolver().openInputStream(bookFileUri!!);
                    bookSize = fileInputStream!!.available();
                    val sizeInMbDouble = bookSize/(1000.0*1000)
                    bookSizeInMb = "%.2f".format(sizeInMbDouble)

                    preview_book_size.text = "File selected $bookSizeInMb MB"

                } catch (e:Exception) {
                    e.printStackTrace();
                }
            } else if(scheme.equals(ContentResolver.SCHEME_FILE)) {
                val path = bookFileUri!!.getPath();
                try {
                    val f = File(path);
                    //Toast.makeText(this, "${f.length()}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        }

    }


    //Send notification
    //Send notification ----------------------------------------------------------------------------
    private fun createNotification(title:String, message: String){

        val ref = FirebaseDatabase.getInstance().getReference("/Tokens")
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){

                    var tokens: DeviceToken?
                    for (productSnapshot in dataSnapshot.children) {

                        tokens = productSnapshot.getValue(DeviceToken::class.java)

                        //if(tokens.userId)
                        sendNotifications(tokens!!.tokenId,title,message)

                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw (databaseError.toException() as Throwable?)!!
            }
        })
    }



    private fun sendNotifications(userToken: String, title: String, message: String) {
        val data = Data(title, message)
        val sender = NotificationSender(data, userToken)

        apiService.sendNotifcation(sender).enqueue(object : Callback<MyResponse> {
            override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                if (response.code() == 200) {
                    if (response.body()!!.success != 1) {
                        Toast.makeText(this@ToolsBook, "Failed ", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
            }
        })
    }










}