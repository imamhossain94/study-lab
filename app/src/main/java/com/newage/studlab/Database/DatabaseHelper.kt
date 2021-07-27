package com.newage.studlab.Database
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.newage.studlab.Model.AnnexModel.SmartRoutine
import com.newage.studlab.Model.AssignmentModel
import com.newage.studlab.Model.BloodModel.Blood
import com.newage.studlab.Model.NotificationModel
import com.newage.studlab.Model.RoutineModel.RoutineConfig
import com.newage.studlab.Model.RoutineModel.Routines
import com.newage.studlab.Model.UserModel.Users


class DatabaseHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {


    companion object {

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "StudLab"

        //all table name
        private const val TABLE_USER_DATA = "user_data"
        private const val TABLE_CLASS_ROUTINE = "routines_class"
        private const val TABLE_EXAM_ROUTINE = "routine_exam"
        private const val TABLE_SMART_ROUTINE = "routine_smart"
        private const val TABLE_BLOOD_DATA = "blood_data"
        private const val TABLE_SMART_ROUTINE_UPDATE = "routine_smart_update"
        private const val TABLE_NOTIFICATION = "notification"
        private const val TABLE_ASSIGNMENT = "assignment"

        //User data table column name
        private const val user_id:String = "user_id"
        private const val user_name:String = "user_name"
        private const val user_gender:String = "user_gender"
        private const val user_dob:String = "user_dob"
        private const val user_phone_old: String = "user_phone_old"
        private const val user_phone_new: String = "user_phone_new"
        private const val user_password: String = "user_password"
        private const val user_type:String = "user_type"
        private const val user_image:String = "user_image"
        private const val user_faculty_name: String = "user_faculty_name"
        private const val user_prog_or_dept: String = "user_prog_or_dept"
        private const val user_shift_or_post:String = "user_shift_or_post"
        private const val user_intake:String = "user_intake"
        private const val user_section: String = "user_section"
        private const val user_semester: String = "user_semester"
        private const val user_address: String = "user_address"
        private const val user_status:String = "user_status"
        private const val user_blood: String = "user_blood"
        private const val user_room_no:String = "user_room_no"
        private const val annex_id:String = "annex_id"
        private const val annex_pass: String = "annex_pass"
        private const val annex_session_id:String = "annex_session_id"

        //Create user data table
        private const val CREATE_USER_DATA_TABLE:String = "CREATE TABLE " +
                "$TABLE_USER_DATA($user_id TEXT, $user_name TEXT,$user_gender TEXT, $user_dob TEXT,$user_phone_old TEXT, $user_phone_new TEXT,$user_password TEXT, $user_type TEXT, $user_image TEXT," +
                "$user_faculty_name TEXT, $user_prog_or_dept TEXT,$user_shift_or_post TEXT, $user_intake TEXT,$user_section TEXT,$user_semester TEXT, $user_address TEXT,$user_status TEXT, $user_blood TEXT," +
                "$user_room_no TEXT, $annex_id TEXT,$annex_pass TEXT, $annex_session_id TEXT)"

        //Routine table column name
        private const val programCode:String = "programCode"
        private const val intakeNo: String = "intakeNo"
        private const val sectionNo: String = "sectionNo"
        private const val semesterName: String = "semesterName"
        private const val sessionName:String = "sessionName"
        private const val routineType: String = "routineType"
        private const val routineImage: String = "routineImage"
        private const val uploadDate:String = "uploadDate"
        private const val uploadBy:String = "uploadBy"

        //Smart routine table column
        private const val Building: String = "Building"
        private const val Day: String = "Day"
        private const val Intake: String = "Intake"
        private const val Room_No:String = "Room_No"
        private const val Schedule: String = "Schedule"
        private const val Section: String = "Section"
        private const val Subject_Code:String = "Subject_Code"
        private const val Teacher_Code:String = "Teacher_Code"

        //Smart routine update table column
        private const val Last_Update: String = "Last_Update"

        //Create routine table
        private const val CREATE_CLASS_ROUTINE_TABLE:String = "CREATE TABLE $TABLE_CLASS_ROUTINE($programCode TEXT, $intakeNo TEXT,$sectionNo TEXT, $semesterName TEXT,$sessionName TEXT, $routineType TEXT,$routineImage TEXT, $uploadDate TEXT, $uploadBy TEXT)"
        private const val CREATE_EXAM_ROUTINE_TABLE:String = "CREATE TABLE $TABLE_EXAM_ROUTINE($programCode TEXT, $intakeNo TEXT,$sectionNo TEXT, $semesterName TEXT,$sessionName TEXT, $routineType TEXT,$routineImage TEXT, $uploadDate TEXT, $uploadBy TEXT)"
        private const val CREATE_SMART_ROUTINE_TABLE:String = "CREATE TABLE $TABLE_SMART_ROUTINE($Building TEXT, $Day TEXT,$Intake TEXT, $Room_No TEXT,$Schedule TEXT, $Section TEXT,$Subject_Code TEXT, $Teacher_Code TEXT)"
        private const val CREATE_SMART_ROUTINE_UPDATE:String = "CREATE TABLE $TABLE_SMART_ROUTINE_UPDATE($Last_Update TEXT)"

        //Blood table column name
        private const val bloodName:String = "bloodName"
        private const val bloodId: String = "bloodId"
        private const val bloodGroup: String = "bloodGroup"
        private const val bloodTotalDonate: String = "bloodTotalDonate"
        private const val bloodLastDonateDate:String = "bloodLastDonateDate"

        //create blood data table
        private const val CREATE_BLOOD_DATA_TABLE:String = "CREATE TABLE $TABLE_BLOOD_DATA($bloodName TEXT, $bloodId TEXT,$bloodGroup TEXT, $bloodTotalDonate TEXT,$bloodLastDonateDate TEXT)"

        //notification table column
        private const val title:String = "title"
        private const val message:String = "message"
        private const val arrivalTime:String = "arrivalTime"
        private const val isChecked:String = "isChecked"
        private const val notificationType:String = "notificationType"

        //create notification data table
        private const val CREATE_NOTIFICATION_DATA_TABLE:String = "CREATE TABLE $TABLE_NOTIFICATION($title TEXT, $message TEXT,$arrivalTime TEXT, $isChecked TEXT,$notificationType TEXT)"

        //assignment table column
        private const val assignmentTitle:String = "assignmentTitle"
        private const val submissionDate:String = "submissionDate"
        private const val assignmentText:String = "assignmentText"
        private const val createdDate:String = "createdDate"
        private const val isSubmitted:String = "isSubmitted"
        private const val imageOne:String = "imageOne"
        private const val imageTwo:String = "imageTwo"
        private const val imageThree:String = "imageThree"

        //create assignment table
        private const val CREATE_TABLE_ASSIGNMENT:String = "CREATE TABLE $TABLE_ASSIGNMENT($assignmentTitle TEXT, $submissionDate TEXT,$assignmentText TEXT, $createdDate TEXT,$isSubmitted TEXT, $imageOne TEXT,$imageTwo TEXT, $imageThree TEXT)"


    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_USER_DATA_TABLE)
        db?.execSQL(CREATE_CLASS_ROUTINE_TABLE)
        db?.execSQL(CREATE_EXAM_ROUTINE_TABLE)
        db?.execSQL(CREATE_SMART_ROUTINE_TABLE)
        db?.execSQL(CREATE_SMART_ROUTINE_UPDATE)
        db?.execSQL(CREATE_BLOOD_DATA_TABLE)
        db?.execSQL(CREATE_NOTIFICATION_DATA_TABLE)
        db?.execSQL(CREATE_TABLE_ASSIGNMENT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER_DATA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLASS_ROUTINE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXAM_ROUTINE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SMART_ROUTINE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SMART_ROUTINE_UPDATE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BLOOD_DATA")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATION")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ASSIGNMENT")
        onCreate(db)
    }

    //User data ------------------------------------------------------------------------------------
    fun saveUserData(user: Users?):Long{
        val db = this.writableDatabase
        val userData = ContentValues()

        if (user != null) {
            userData.put(user_id,user.user_id)
            userData.put(user_name,user.user_name)
            userData.put(user_gender,user.user_gender)
            userData.put(user_dob,user.user_dob)
            userData.put(user_phone_old,user.user_phone_old)
            userData.put(user_phone_new,user.user_phone_new)
            userData.put(user_password,user.user_password)
            userData.put(user_type,user.user_type)
            userData.put(user_image,user.user_image)
            userData.put(user_faculty_name,user.user_faculty_name)
            userData.put(user_prog_or_dept,user.user_prog_or_dept)
            userData.put(user_shift_or_post,user.user_shift_or_post)
            userData.put(user_intake,user.user_intake)
            userData.put(user_section,user.user_section)
            userData.put(user_semester,user.user_semester)
            userData.put(user_address,user.user_address)
            userData.put(user_status,user.user_status)
            userData.put(user_blood,user.user_blood)
            userData.put(user_room_no,user.user_room_no)
            userData.put(annex_id,user.annex_id)
            userData.put(annex_pass,user.annex_pass)
            userData.put(annex_session_id,user.annex_session_id)
        }
        val success = db.insert(TABLE_USER_DATA, null, userData)
        db.close()
        return success
    }

    fun getUserData():Users?{
        val selectQuery = "SELECT  * FROM $TABLE_USER_DATA"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        var user: Users? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val user_id  = cursor.getString(cursor.getColumnIndex("user_id"))
            val user_name  = cursor.getString(cursor.getColumnIndex("user_name"))
            val user_gender = cursor.getString(cursor.getColumnIndex("user_gender"))
            val user_dob  = cursor.getString(cursor.getColumnIndex("user_dob"))
            val user_phone_old = cursor.getString(cursor.getColumnIndex("user_phone_old"))
            val user_phone_new  = cursor.getString(cursor.getColumnIndex("user_phone_new"))
            val user_password = cursor.getString(cursor.getColumnIndex("user_password"))
            val user_type  = cursor.getString(cursor.getColumnIndex("user_type"))
            val user_image = cursor.getString(cursor.getColumnIndex("user_image"))
            val user_faculty_name  = cursor.getString(cursor.getColumnIndex("user_faculty_name"))
            val user_prog_or_dept  = cursor.getString(cursor.getColumnIndex("user_prog_or_dept"))
            val user_shift_or_post = cursor.getString(cursor.getColumnIndex("user_shift_or_post"))
            val user_intake  = cursor.getString(cursor.getColumnIndex("user_intake"))
            val user_section = cursor.getString(cursor.getColumnIndex("user_section"))
            val user_semester = cursor.getString(cursor.getColumnIndex("user_semester"))
            val user_address  = cursor.getString(cursor.getColumnIndex("user_address"))
            val user_status = cursor.getString(cursor.getColumnIndex("user_status"))
            val user_blood  = cursor.getString(cursor.getColumnIndex("user_blood"))
            val user_room_no = cursor.getString(cursor.getColumnIndex("user_room_no"))
            val annex_id = cursor.getString(cursor.getColumnIndex("annex_id"))
            val annex_pass  = cursor.getString(cursor.getColumnIndex("annex_pass"))
            val annex_session_id = cursor.getString(cursor.getColumnIndex("annex_session_id"))

            user= Users(user_id, user_name, user_gender, user_dob, user_phone_old, user_phone_new,
                user_password, user_type, user_image, user_faculty_name, user_prog_or_dept, user_shift_or_post,
                user_intake, user_section,user_semester, user_address, user_status, user_blood, user_room_no,
                annex_id, annex_pass, annex_session_id)
            cursor.close()
        }
        db.close()
        return user
    }

    fun deleteUserData(){
        val db = this.writableDatabase
        db.delete(TABLE_USER_DATA, null, null);
        db.close()
    }

    //Class routine --------------------------------------------------------------------------------
    fun saveClassRoutine(routine: Routines?):Long{
        val db = this.writableDatabase
        val values = ContentValues()

        //Putting data.....
        if (routine != null) {
            values.put(programCode,routine.programCode)
            values.put(intakeNo,routine.intakeNo)
            values.put(sectionNo,routine.sectionNo)
            values.put(semesterName,routine.semesterName)
            values.put(sessionName,routine.sessionName)
            values.put(routineType,routine.routineType)
            values.put(routineImage,routine.routineImage)
            values.put(uploadDate,routine.uploadDate)
            values.put(uploadBy,routine.uploadBy)
        }
        val success = db.insert(TABLE_CLASS_ROUTINE, null, values)
        db.close()
        return success
    }

    fun getClassRoutine():Routines?{
        val selectQuery = "SELECT  * FROM $TABLE_CLASS_ROUTINE"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        var routine: Routines? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val programCode  = cursor.getString(cursor.getColumnIndex("programCode"))
            val intakeNo  = cursor.getString(cursor.getColumnIndex("intakeNo"))
            val sectionNo = cursor.getString(cursor.getColumnIndex("sectionNo"))
            val semesterName  = cursor.getString(cursor.getColumnIndex("semesterName"))
            val sessionName = cursor.getString(cursor.getColumnIndex("sessionName"))
            val routineType  = cursor.getString(cursor.getColumnIndex("routineType"))
            val routineImage = cursor.getString(cursor.getColumnIndex("routineImage"))
            val uploadDate  = cursor.getString(cursor.getColumnIndex("uploadDate"))
            val uploadBy = cursor.getString(cursor.getColumnIndex("uploadBy"))

            routine= Routines(programCode,intakeNo,sectionNo,semesterName,sessionName,
                routineType,routineImage,uploadDate,uploadBy)
            cursor.close()
        }
        db.close()
        return routine
    }

    fun deleteClassRoutine(){
        val db = this.writableDatabase
        db.delete(TABLE_CLASS_ROUTINE, null, null);
        db.close()
    }

    //Table exam routine ---------------------------------------------------------------------------
    fun saveExamRoutine(routine: Routines?):Long{
        val db = this.writableDatabase
        val values = ContentValues()

        //Putting data.....
        if (routine != null) {
            values.put(programCode,routine.programCode)
            values.put(intakeNo,routine.intakeNo)
            values.put(sectionNo,routine.sectionNo)
            values.put(semesterName,routine.semesterName)
            values.put(sessionName,routine.sessionName)
            values.put(routineType,routine.routineType)
            values.put(routineImage,routine.routineImage)
            values.put(uploadDate,routine.uploadDate)
            values.put(uploadBy,routine.uploadBy)
        }
        val success = db.insert(TABLE_EXAM_ROUTINE, null, values)
        db.close()
        return success
    }

    fun getExamRoutine():Routines?{
        val selectQuery = "SELECT  * FROM $TABLE_EXAM_ROUTINE"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        var routine: Routines? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val programCode  = cursor.getString(cursor.getColumnIndex("programCode"))
            val intakeNo  = cursor.getString(cursor.getColumnIndex("intakeNo"))
            val sectionNo = cursor.getString(cursor.getColumnIndex("sectionNo"))
            val semesterName  = cursor.getString(cursor.getColumnIndex("semesterName"))
            val sessionName = cursor.getString(cursor.getColumnIndex("sessionName"))
            val routineType  = cursor.getString(cursor.getColumnIndex("routineType"))
            val routineImage = cursor.getString(cursor.getColumnIndex("routineImage"))
            val uploadDate  = cursor.getString(cursor.getColumnIndex("uploadDate"))
            val uploadBy = cursor.getString(cursor.getColumnIndex("uploadBy"))

            routine= Routines(programCode,intakeNo,sectionNo,semesterName,sessionName,
                routineType,routineImage,uploadDate,uploadBy)
            cursor.close()
        }
        db.close()
        return routine
    }

    fun deleteExamRoutine(){
        val db = this.writableDatabase
        db.delete(TABLE_EXAM_ROUTINE, null, null);
        db.close()
    }

    //Table smart routine --------------------------------------------------------------------------
    fun saveSmartRoutine(routine: SmartRoutine?):Long{
        val db = this.writableDatabase
        val values = ContentValues()

        //Putting data.....
        if (routine != null) {
            values.put(Building,routine.Building)
            values.put(Day,routine.Day)
            values.put(Intake,routine.Intake)
            values.put(Room_No,routine.Room_No)
            values.put(Schedule,routine.Schedule)
            values.put(Section,routine.Section)
            values.put(Subject_Code,routine.Subject_Code)
            values.put(Teacher_Code,routine.Teacher_Code)
        }
        val success = db.insert(TABLE_SMART_ROUTINE, null, values)
        db.close()
        return success
    }

    @SuppressLint("Recycle")
    fun getSmartRoutine():ArrayList<SmartRoutine>{

        val routineList:ArrayList<SmartRoutine> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_SMART_ROUTINE"
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var Building:String
        var Day:String
        var Intake:String
        var Room_No:String
        var Schedule:String
        var Section:String
        var Subject_Code:String
        var Teacher_Code:String

        if (cursor.moveToFirst()) {
            do {
                Building  = cursor.getString(cursor.getColumnIndex("Building"))
                Day  = cursor.getString(cursor.getColumnIndex("Day"))
                Intake = cursor.getString(cursor.getColumnIndex("Intake"))
                Room_No  = cursor.getString(cursor.getColumnIndex("Room_No"))
                Schedule = cursor.getString(cursor.getColumnIndex("Schedule"))
                Section  = cursor.getString(cursor.getColumnIndex("Section"))
                Subject_Code = cursor.getString(cursor.getColumnIndex("Subject_Code"))
                Teacher_Code  = cursor.getString(cursor.getColumnIndex("Teacher_Code"))

                val routine= SmartRoutine(Building, Day, Intake, Room_No, Schedule, Section, Subject_Code, Teacher_Code)

                routineList.add(routine)
            } while (cursor.moveToNext())
        }
        return routineList
    }

    fun deleteSmartRoutine(){
        val db = this.writableDatabase
        db.delete(TABLE_SMART_ROUTINE, null, null);
        db.close()
    }

    //Table smart routine update--------------------------
    fun saveSmartRoutineUpdateDate(lastUpdate: RoutineConfig?):Long{
        val db = this.writableDatabase
        val values = ContentValues()

        //Putting data.....
        if (lastUpdate != null) {
            values.put(Last_Update,lastUpdate.LastUpdate)
        }
        val success = db.insert(TABLE_SMART_ROUTINE_UPDATE, null, values)
        db.close()
        return success
    }

    fun getSmartRoutineUpdateDate(): RoutineConfig?{
        val selectQuery = "SELECT  * FROM $TABLE_SMART_ROUTINE_UPDATE"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        var updateDate: RoutineConfig? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val udate  = cursor.getString(cursor.getColumnIndex("Last_Update"))

            updateDate= RoutineConfig(udate)
            cursor.close()
        }
        db.close()
        return updateDate
    }

    fun deleteSmartRoutineUpdateDate(){
        val db = this.writableDatabase
        db.delete(TABLE_SMART_ROUTINE_UPDATE, null, null);
        db.close()
    }


    //Table blood data---------------------------------------------------------
    fun saveBloodData(blood: Blood?):Long{
        val db = this.writableDatabase
        val values = ContentValues()

        //Putting data.....
        if (blood != null) {
            values.put(bloodName,blood.bloodName)
            values.put(bloodId,blood.bloodId)
            values.put(bloodGroup,blood.bloodGroup)
            values.put(bloodTotalDonate,blood.bloodTotalDonate)
            values.put(bloodLastDonateDate,blood.bloodLastDonateDate)
        }
        val success = db.insert(TABLE_BLOOD_DATA, null, values)
        db.close()
        return success
    }

    fun getBloodData():Blood?{
        val selectQuery = "SELECT  * FROM $TABLE_BLOOD_DATA"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        var blood: Blood? = null

        if(cursor.moveToFirst()){
            cursor.moveToFirst()

            val bloodName  = cursor.getString(cursor.getColumnIndex("bloodName"))
            val bloodId  = cursor.getString(cursor.getColumnIndex("bloodId"))
            val bloodGroup = cursor.getString(cursor.getColumnIndex("bloodGroup"))
            val bloodTotalDonate  = cursor.getString(cursor.getColumnIndex("bloodTotalDonate"))
            val bloodLastDonateDate = cursor.getString(cursor.getColumnIndex("bloodLastDonateDate"))

            blood= Blood(bloodName,bloodId,bloodGroup,bloodTotalDonate,bloodLastDonateDate)
            cursor.close()
        }
        db.close()
        return blood
    }

    fun deleteBloodData(){
        val db = this.writableDatabase
        db.delete(TABLE_BLOOD_DATA, null, null);
        db.close()
    }

    //Table notification data---------------------------------------------------------
    fun saveNotificationData(notification: NotificationModel?):Long{
        val db = this.writableDatabase
        val values = ContentValues()

        //Putting data.....
        if (notification != null) {
            values.put(title,notification.title)
            values.put(message,notification.message)
            values.put(arrivalTime,notification.arrivalTime)
            values.put(isChecked,notification.isChecked)
            values.put(notificationType,notification.notificationType)
        }
        val success = db.insert(TABLE_NOTIFICATION, null, values)
        db.close()
        return success
    }

    @SuppressLint("Recycle")
    fun getNotificationData():ArrayList<NotificationModel>{

        val notificationList:ArrayList<NotificationModel> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_NOTIFICATION"
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var title:String
        var message:String
        var arrivalTime:String
        var isChecked:String
        var notificationType:String


        if (cursor.moveToFirst()) {
            do {

                title  = cursor.getString(cursor.getColumnIndex("title"))
                message  = cursor.getString(cursor.getColumnIndex("message"))
                arrivalTime = cursor.getString(cursor.getColumnIndex("arrivalTime"))
                isChecked  = cursor.getString(cursor.getColumnIndex("isChecked"))
                notificationType = cursor.getString(cursor.getColumnIndex("notificationType"))

                val notification= NotificationModel(title,message,arrivalTime,isChecked,notificationType)

                notificationList.add(notification)
            } while (cursor.moveToNext())
        }
        return notificationList

    }

    fun deleteNotificationData(){
        val db = this.writableDatabase
        db.delete(TABLE_NOTIFICATION, null, null);
        db.close()
    }

    fun deleteSingleNotificationRow(value: String): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_NOTIFICATION, "$message=?", arrayOf(value)) > 0
    }

    //Table assignment---------------------------------------------------------
    fun createAssignmentData(assignment: AssignmentModel?):Long{
        val db = this.writableDatabase
        val values = ContentValues()

        //Putting data.....
        if (assignment != null) {
            values.put(assignmentTitle,assignment.assignmentTitle)
            values.put(submissionDate,assignment.submissionDate)
            values.put(assignmentText,assignment.assignmentText)
            values.put(createdDate,assignment.createdDate)
            values.put(isSubmitted,assignment.isSubmitted)
            values.put(imageOne,assignment.imageOne)
            values.put(imageTwo,assignment.imageTwo)
            values.put(imageThree,assignment.imageThree)
        }
        val success = db.insert(TABLE_ASSIGNMENT, null, values)
        db.close()
        return success
    }

    @SuppressLint("Recycle")
    fun getAssignmentData():ArrayList<AssignmentModel>{

        val assignmentList:ArrayList<AssignmentModel> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_ASSIGNMENT"
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var assignmentTitle:String
        var submissionDate:String
        var assignmentText:String
        var createdDate:String
        var isSubmitted:String
        var imageOne:String
        var imageTwo:String
        var imageThree:String

        if (cursor.moveToFirst()) {
            do {

                assignmentTitle  = cursor.getString(cursor.getColumnIndex("assignmentTitle"))
                submissionDate  = cursor.getString(cursor.getColumnIndex("submissionDate"))
                assignmentText = cursor.getString(cursor.getColumnIndex("assignmentText"))
                createdDate  = cursor.getString(cursor.getColumnIndex("createdDate"))
                isSubmitted = cursor.getString(cursor.getColumnIndex("isSubmitted"))
                imageOne = cursor.getString(cursor.getColumnIndex("imageOne"))
                imageTwo  = cursor.getString(cursor.getColumnIndex("imageTwo"))
                imageThree = cursor.getString(cursor.getColumnIndex("imageThree"))

                val assignment = AssignmentModel(assignmentTitle,submissionDate,assignmentText,createdDate,isSubmitted,imageOne,imageTwo,imageThree)

                assignmentList.add(assignment)
            } while (cursor.moveToNext())
        }
        return assignmentList

    }

    fun deleteAssignmentData(){
        val db = this.writableDatabase
        db.delete(TABLE_ASSIGNMENT, null, null);
        db.close()
    }

    fun deleteSingleAssignmentTitle(value: String): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_ASSIGNMENT, "$assignmentTitle=?", arrayOf(value)) > 0
    }





}


