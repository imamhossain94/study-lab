import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.db.williamchart.ExperimentalFeature
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.homeInfo
import com.newage.studlab.Home.AssignmentFragment.AssignmentFragment
import com.newage.studlab.Home.CompareFragment.CompareProfileFragment
import com.newage.studlab.Home.CourseFragment.AllCourseFragment
import com.newage.studlab.Home.CourseFragment.PresentCourseFragment
import com.newage.studlab.Home.CourseFragment.PreviousCourseFragment
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.position
import com.newage.studlab.Home.ResultFragment.IntakeResultSemesterFragment
import com.newage.studlab.Home.ResultFragment.MyResultFragment
import com.newage.studlab.Home.ResultFragment.UniversityResultProgramFragment
import com.newage.studlab.Home.RoutineFragment.ClassRoutineFragment
import com.newage.studlab.Home.RoutineFragment.ExamRoutineFragment
import com.newage.studlab.Home.RoutineFragment.SmartRoutineFragment
import com.newage.studlab.Home.UserFragment.TeacherProgramFragment
import com.newage.studlab.Home.UserFragment.StudentProgramFragment
import com.newage.studlab.Home.UserFragment.StuffFragment
import com.newage.studlab.Model.SatusModel.Status
import com.newage.studlab.Model.UserModel.Users
import com.newage.studlab.R
import es.dmoral.toasty.Toasty


@ExperimentalFeature
class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    lateinit var bubtLogo:ImageView

    lateinit var appUser:TextView
    lateinit var onlineUser:TextView
    lateinit var student:TextView
    lateinit var teaacher:TextView
    lateinit var faculty:TextView
    lateinit var program:TextView

    lateinit var classRoutine:Button
    lateinit var examRoutine:Button
    lateinit var smartRoutine:Button

    lateinit var previousCourse:Button
    lateinit var presentCourse:Button
    lateinit var futureCourse:Button

    lateinit var studentButton:Button
    lateinit var teacherButton:Button
    lateinit var stuffButton:Button

    lateinit var myResultButton:Button
    lateinit var intakeResultButton:Button
    lateinit var universityResultButton:Button

    lateinit var compareButton:Button
    lateinit var assignmentButton:Button
    lateinit var myClassButton:Button



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        activity?.let {

           // gridView = it.findViewById(R.id.grid_layout)
            bubtLogo = it.findViewById(R.id.home_header_varsity_logo_image)

            appUser = it.findViewById(R.id.home_app_User)
            onlineUser = it.findViewById(R.id.home_online_noe)
            student = it.findViewById(R.id.home_total_student)
            teaacher = it.findViewById(R.id.home_total_teacher)
            faculty = it.findViewById(R.id.home_total_faculty)
            program = it.findViewById(R.id.home_total_program)

            classRoutine = it.findViewById(R.id.class_routine_buttlon)
            examRoutine = it.findViewById(R.id.exam_routine_button)
            smartRoutine = it.findViewById(R.id.smart_routine_button)

            previousCourse = it.findViewById(R.id.course_previous_buttlon)
            presentCourse = it.findViewById(R.id.course_present_button)
            futureCourse = it.findViewById(R.id.course_all_button)

            studentButton = it.findViewById(R.id.student_button)
            teacherButton = it.findViewById(R.id.teacher_button)
            stuffButton = it.findViewById(R.id.stuff_button)

            myResultButton = it.findViewById(R.id.my_result_buttlon)
            intakeResultButton = it.findViewById(R.id.intake_result_button)
            universityResultButton = it.findViewById(R.id.university_result_button)

            compareButton = it.findViewById(R.id.compare_button)
            assignmentButton = it.findViewById(R.id.assignment_button)
            myClassButton = it.findViewById(R.id.classroom_button)

        }
        position  = 0
        fragmentName = "homeFragment"

        appUser()
        onlineUser()
        loadHeaderInformation()
        allButtonClickEventListeners()
    }


    private fun loadHeaderInformation(){
        appUser.text = (StudLab.userList.size).toString()

        faculty.text = homeInfo!!.totalFaculty
        program.text = homeInfo!!.totalProgram
        student.text = homeInfo!!.totalStudent
        teaacher.text = homeInfo!!.totalTeacher

    }

    private fun allButtonClickEventListeners(){

        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)

        classRoutine.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, ClassRoutineFragment())
            transaction.commit()
        }

        examRoutine.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, ExamRoutineFragment())
            transaction.commit()
        }

        smartRoutine.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, SmartRoutineFragment())
            transaction.commit()
        }

        // Course button
        previousCourse.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, PreviousCourseFragment())
            transaction.commit()
        }

        presentCourse.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, PresentCourseFragment())
            transaction.commit()
        }

        futureCourse.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, AllCourseFragment())
            transaction.commit()
        }

        // User button
        studentButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, StudentProgramFragment())
            transaction.commit()
        }

        teacherButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, TeacherProgramFragment())
            transaction.commit()
        }

        stuffButton.setOnClickListener{
           /* transaction.replace(R.id.main_home_fragment_container, StuffFragment())
            transaction.commit()*/
            Toasty.info(requireContext(), "Upcoming: with a new experiences.", Toast.LENGTH_SHORT, true).show()
        }

        // Result button
        myResultButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, MyResultFragment())
            transaction.commit()
        }
        intakeResultButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, IntakeResultSemesterFragment())
            transaction.commit()
        }

        universityResultButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, UniversityResultProgramFragment())
            transaction.commit()
        }

        compareButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, CompareProfileFragment())
            transaction.commit()
        }

        assignmentButton.setOnClickListener{
            transaction.replace(R.id.main_home_fragment_container, AssignmentFragment())
            transaction.commit()
        }


        myClassButton.setOnClickListener{
            Toasty.info(requireContext(), "Upcoming: The concept of google classroom will be implemented here.", Toast.LENGTH_SHORT, true).show()
        }
    }

    var onlineUserList = ArrayList<Status>()
    var appUserList = ArrayList<Users>()

    private fun appUser(){

        val ref = FirebaseDatabase.getInstance().getReference("/Users")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    appUserList.clear()
                    var usr: Users?
                    for (productSnapshot in dataSnapshot.children) {

                        usr = productSnapshot.getValue(Users::class.java)
                        appUserList.add(usr!!)
                    }

                    appUser.text = appUserList.size.toString()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()!!
            }
        })
    }

    private fun onlineUser(){
        val ref = FirebaseDatabase.getInstance().getReference("/Status")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    onlineUserList.clear()
                    var status: Status?
                    for (productSnapshot in dataSnapshot.children) {

                        status = productSnapshot.getValue(Status::class.java)
                        onlineUserList.add(status!!)

                    }

                    onlineUser .text = "${ onlineUserList.filter { it -> it.statusId == "online"}.size}"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw (databaseError.toException() as Throwable?)!!
            }
        })
    }



}