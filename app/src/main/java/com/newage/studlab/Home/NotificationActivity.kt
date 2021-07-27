package com.newage.studlab.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.studlab.Adapter.NotificationRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Model.NotificationModel
import com.newage.studlab.Model.RoutineModel.RoutineConfig
import com.newage.studlab.Model.StudLibrary.Lecture
import com.newage.studlab.Plugins.StudLabGenerate
import com.newage.studlab.R
import es.dmoral.toasty.Toasty

class NotificationActivity : AppCompatActivity() {


    lateinit var all:TextView
    lateinit var blood:TextView
    lateinit var library:TextView
    lateinit var recyclerView:RecyclerView

    lateinit var adapter:NotificationRecyclerViewAdapter

    var notification = ArrayList<NotificationModel>()
    var filteredList = ArrayList<NotificationModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)

        all = findViewById(R.id.notify_all)
        blood = findViewById(R.id.notify_blood)
        library = findViewById(R.id.notify_library)
        recyclerView = findViewById(R.id.notification_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)




        uiChangesOnButtonClick()
        loadNotification()
    }

    private fun loadNotification(){
        val databaseHandler = DatabaseHelper(this)
        notification = databaseHandler.getNotificationData()

        adapter = if(notification.isNotEmpty()) {
            NotificationRecyclerViewAdapter(this, notification, clickDeleteListener = {

                if(databaseHandler.deleteSingleNotificationRow(it.message)){
                    Toasty.success(StudLab.appContext, "Done", Toast.LENGTH_SHORT, true).show()
                    loadNotification()
                }else{
                    Toasty.warning(StudLab.appContext, "Failed", Toast.LENGTH_SHORT, true).show()
                }

            })
        }else{
            NotificationRecyclerViewAdapter(this, ArrayList(), clickDeleteListener = {

            })
        }
        recyclerView.adapter = adapter
    }

    private fun uiChangesOnButtonClick(){
        all.setOnClickListener{

            filteredList = notification
            adapter.filterNotificationList(filteredList)

            all.setBackgroundResource(R.drawable.square_round_white_blue_strok)
            blood.setBackgroundResource(R.drawable.square_round_white_blue_disabled)
            library.setBackgroundResource(R.drawable.square_round_white_blue_disabled)

            all.setTextColor(resources.getColor(R.color.colorBlue2))
            blood.setTextColor(resources.getColor(R.color.colorBlueDisabled))
            library.setTextColor(resources.getColor(R.color.colorBlueDisabled))
        }

        blood.setOnClickListener{
            filteredList = notification.filter { it.notificationType == "blood" } as ArrayList<NotificationModel>
            adapter.filterNotificationList(filteredList)

            all.setBackgroundResource(R.drawable.square_round_white_blue_disabled)
            blood.setBackgroundResource(R.drawable.square_round_white_blue_strok)
            library.setBackgroundResource(R.drawable.square_round_white_blue_disabled)

            all.setTextColor(resources.getColor(R.color.colorBlueDisabled))
            blood.setTextColor(resources.getColor(R.color.colorBlue2))
            library.setTextColor(resources.getColor(R.color.colorBlueDisabled))
        }

        library.setOnClickListener{
            filteredList = notification.filter { it.notificationType == "library" } as ArrayList<NotificationModel>
            adapter.filterNotificationList(filteredList)

            all.setBackgroundResource(R.drawable.square_round_white_blue_disabled)
            blood.setBackgroundResource(R.drawable.square_round_white_blue_disabled)
            library.setBackgroundResource(R.drawable.square_round_white_blue_strok)

            all.setTextColor(resources.getColor(R.color.colorBlueDisabled))
            blood.setTextColor(resources.getColor(R.color.colorBlueDisabled))
            library.setTextColor(resources.getColor(R.color.colorBlue2))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
        this.finish()
    }

}
