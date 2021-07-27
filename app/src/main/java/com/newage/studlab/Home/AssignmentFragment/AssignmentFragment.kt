package com.newage.studlab.Home.AssignmentFragment

import HomeFragment
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.newage.studlab.Adapter.AssignmentRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.AssignmentModel
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.bottom_sheet_create_assignment.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AssignmentFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_assignment, container, false)
    }


    var img1 = ""
    var img2 = ""
    var img3 = ""

    var picker: DatePickerDialog? = null

    lateinit var backButton: ImageView
    private lateinit var createAssignmentButton: ImageView
    lateinit var searchBox:EditText

    //---------------create assignments-----------------
    lateinit var assignmentTitle: TextView
    lateinit var assignmentSubmissionDate: TextView
    lateinit var assignmentTextMessage: TextView

    lateinit var assignmentImageOneContainer: CardView
    lateinit var assignmentImageTwoContainer: CardView
    lateinit var assignmentImageThreeContainer: CardView

    lateinit var assignmentImageOne: ImageView
    lateinit var assignmentImageTwo: ImageView
    lateinit var assignmentImageThree: ImageView

    lateinit var assignmentSaveButton: TextView


    //--------------------------------------------------
    private var assignmentList = ArrayList<AssignmentModel>()
    var filteredList = ArrayList<AssignmentModel>()


    lateinit var recyclerView: RecyclerView
    lateinit var resNotFound: TextView

    lateinit var adapter: AssignmentRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentName = "assignment"
        fragmentPosition = 0.0

        activity?.let {

            backButton = it.findViewById(R.id.fragment_back_button)
            resNotFound = it.findViewById(R.id.fragment_empty)
            createAssignmentButton = it.findViewById(R.id.fragment_create_assignments)

            searchBox = it.findViewById(R.id.search_box)

            assignmentTitle = it.findViewById(R.id.assignment_title)
            assignmentSubmissionDate = it.findViewById(R.id.assignment_submission_date)
            assignmentTextMessage = it.findViewById(R.id.assignment_text)

            assignmentImageOneContainer = it.findViewById(R.id.image_1_container)
            assignmentImageTwoContainer = it.findViewById(R.id.image_2_container)
            assignmentImageThreeContainer = it.findViewById(R.id.image_3_container)

            assignmentImageOne = it.findViewById(R.id.assignment_image_one)
            assignmentImageTwo = it.findViewById(R.id.assignment_image_two)
            assignmentImageThree = it.findViewById(R.id.assignment_image_three)

            assignmentSaveButton = it.findViewById(R.id.assignment_save_button)


            recyclerView = it.findViewById(R.id.fragment_recycler_view)
        }
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadAssignment()

        allButtonClickingEventListeners()
        textChangeEventListeners()
    }

    private fun loadAssignment(){

        val databaseHandler = DatabaseHelper(requireContext())
        assignmentList = databaseHandler.getAssignmentData()

        adapter = if(assignmentList.isNotEmpty()) {
            AssignmentRecyclerViewAdapter(appContext, assignmentList,

                clickEditListener = {




                }, clickSaveListener = {

                when {
                    it.imageOne.isNotEmpty() && it.imageTwo.isNotEmpty() && it.imageThree.isNotEmpty() -> {
                        saveImageToSdCard(it.imageOne,it.assignmentTitle + "_1")
                        saveImageToSdCard(it.imageTwo,it.assignmentTitle + "_2")
                        saveImageToSdCard(it.imageThree,it.assignmentTitle + "_3")
                    }
                    it.imageOne.isNotEmpty() && it.imageTwo.isNotEmpty() -> {
                        saveImageToSdCard(it.imageOne,it.assignmentTitle + "_1")
                        saveImageToSdCard(it.imageTwo,it.assignmentTitle + "_2")

                    }
                    it.imageOne.isNotEmpty() -> {
                        saveImageToSdCard(it.imageOne,it.assignmentTitle + "_1")

                    }
                }

            },clickDeleteListener = {

                if(databaseHandler.deleteSingleAssignmentTitle(it.assignmentTitle)){
                    Toasty.success(appContext, "Done", Toast.LENGTH_SHORT, true).show()
                    loadAssignment()
                }else{
                    Toasty.warning(appContext, "Failed", Toast.LENGTH_SHORT, true).show()
                }

            })
        }else{
            AssignmentRecyclerViewAdapter(appContext, ArrayList(),
                clickEditListener = {




                }, clickSaveListener = {

                when {
                    it.imageOne.isNotEmpty() && it.imageTwo.isNotEmpty() && it.imageThree.isNotEmpty() -> {
                        saveImageToSdCard(it.imageOne,it.assignmentTitle + "_1")
                        saveImageToSdCard(it.imageTwo,it.assignmentTitle + "_2")
                        saveImageToSdCard(it.imageThree,it.assignmentTitle + "_3")
                    }
                    it.imageOne.isNotEmpty() && it.imageTwo.isNotEmpty() -> {
                        saveImageToSdCard(it.imageOne,it.assignmentTitle + "_1")
                        saveImageToSdCard(it.imageTwo,it.assignmentTitle + "_2")

                    }
                    it.imageOne.isNotEmpty() -> {
                        saveImageToSdCard(it.imageOne,it.assignmentTitle + "_1")

                    }
                }

            },clickDeleteListener = {

                if(databaseHandler.deleteSingleAssignmentTitle(it.assignmentTitle)){
                    Toasty.success(appContext, "Done", Toast.LENGTH_SHORT, true).show()
                    loadAssignment()
                }else{
                    Toasty.warning(appContext, "Failed", Toast.LENGTH_SHORT, true).show()
                }

            })
        }
        recyclerView.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun allButtonClickingEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, HomeFragment())
            transaction.commit()
        }

        createAssignmentButton.setOnClickListener {
            val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_layout)

            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }

        }

        assignmentImageOne.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select image"), 100)
        }

        assignmentImageTwo.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select image"), 200)
        }

        assignmentImageThree.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select image"), 300)

        }

        assignmentSubmissionDate.setOnClickListener{
            val calendar: Calendar = Calendar.getInstance()
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            val month: Int = calendar.get(Calendar.MONTH)
            val year: Int = calendar.get(Calendar.YEAR)
            // date picker dialog
            picker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                    assignmentSubmissionDate.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year

                }, year, month, day)
            picker!!.show()
        }

        assignmentSaveButton.setOnClickListener{

            if(assignmentTitle.text.toString().isNotEmpty() && assignmentSubmissionDate.text.toString().isNotEmpty() && assignmentTextMessage.text.toString().isNotEmpty() && img1.isNotEmpty()){
                createAssignments()
            }else if(assignmentTitle.text.toString().isEmpty()){
                Toasty.warning(appContext, "Title is missing", Toast.LENGTH_SHORT, true).show()

            }else if(assignmentSubmissionDate.text.toString().isEmpty()){
                Toasty.warning(appContext, "Submission date is missing", Toast.LENGTH_SHORT, true).show()

            }else if(assignmentTextMessage.text.toString().isEmpty()){
                Toasty.warning(appContext, "Assignment is missing", Toast.LENGTH_SHORT, true).show()

            }else if(img1.isEmpty()){
                Toasty.warning(appContext, "Please attached at last one image resource.", Toast.LENGTH_SHORT, true).show()

            }


        }
    }

    private fun textChangeEventListeners(){
        searchBox.addTextChangedListener(object : TextWatcher { override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })
    }


    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        filteredList = assignmentList.filter { it.assignmentTitle.contains(text) } as ArrayList<AssignmentModel>
        adapter.filterAssignmentList(filteredList)
    }


    private fun createAssignments(){

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance() //or use getDateInstance()
        val formattedDate = formatter.format(date)

        val databaseHandler = DatabaseHelper(requireContext())
        if(databaseHandler.createAssignmentData(AssignmentModel(assignmentTitle.text.toString(),
            assignmentSubmissionDate.text.toString(),
            assignmentTextMessage.text.toString(),
            formattedDate, "false",
            img1,img2,img3)) > 0){

            assignmentTitle.text = null
            assignmentSubmissionDate.text = null
            assignmentTextMessage.text = null

            assignmentImageOne.setImageDrawable(null)
            assignmentImageTwo.setImageDrawable(null)
            assignmentImageThree.setImageDrawable(null)

            assignmentImageTwoContainer.visibility = View.GONE
            assignmentImageThreeContainer.visibility = View.GONE

            BottomSheetBehavior.from(bottom_sheet_layout).state = BottomSheetBehavior.STATE_COLLAPSED
            Toasty.success(appContext, "Assignment created successfully", Toast.LENGTH_SHORT, true).show()
        }else{
            Toasty.error(appContext, "Something wrong..", Toast.LENGTH_SHORT, true).show()
        }

        loadAssignment()
    }


    //-----------------------------Save image to sdCard--------------------------------------

    private fun saveImageToSdCard(Image:String, image_name: String) {

        try {
            val imageBytes = Base64.decode(Image, Base64.DEFAULT)

            val inputStream: InputStream = ByteArrayInputStream(imageBytes)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

            val write: (OutputStream) -> Boolean = {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, image_name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/StudLab/Assignment")
                }

                appContext.contentResolver.let {
                    it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                        it.openOutputStream(uri)?.let(write)
                    }
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Assignment"
                val file = File(imagesDir)
                if (!file.exists()) {
                    file.mkdir()
                }
                val image = File(imagesDir, image_name)
                write(FileOutputStream(image))
            }

            Toasty.success(appContext, "Saved!!", Toast.LENGTH_SHORT, true).show()

        } catch (e: java.lang.Exception) {
            Toasty.error(appContext, "Something wrong: $e", Toast.LENGTH_SHORT, true).show()
            Log.v("SaveFile", "" + e)
        }

    }

    private fun bitmapToBase64(bitmap:Bitmap):String{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream )
        val b = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var bitmap:Bitmap? = null

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            try {

                bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, data!!.data)
                assignmentImageOne.setImageBitmap(bitmap)
                assignmentImageTwoContainer.visibility = View.VISIBLE

                img1 = bitmapToBase64(bitmap!!)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            try {

                bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, data!!.data)
                assignmentImageTwo.setImageBitmap(bitmap)
                assignmentImageThreeContainer.visibility = View.VISIBLE

                img2 = bitmapToBase64(bitmap!!)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }else if (requestCode == 300 && resultCode == Activity.RESULT_OK) {
            try {

                bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, data!!.data)
                assignmentImageThree.setImageBitmap(bitmap)

                img3 = bitmapToBase64(bitmap!!)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}