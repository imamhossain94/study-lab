package com.newage.studlab.Services

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Model.AnnexModel.SmartRoutine
import com.newage.studlab.R
import com.newage.studlab.Services.SrNotificationService.NotificationEventReceiver
import java.text.SimpleDateFormat
import java.util.*

class SampleBootReceiver : BroadcastReceiver() {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent



    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE)

            if (pendingIntent != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent)
            }

            alarmMgr?.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_HOUR,
                AlarmManager.INTERVAL_HALF_HOUR,
                alarmIntent
            )

            val action = intent.action
            if ("ACTION_START" == action) {
                notifyData(context)
            }
        }
    }

    fun notifyData(context:Context) {

        val databaseHandler = DatabaseHelper(context)
        val smartRtn = databaseHandler.getSmartRoutine()
        if(smartRtn.size != 0){

            val calendar = Calendar.getInstance()

            when (val day = calendar.time.toString().substring(0,3).toLowerCase()) {

                "sat" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase().contains(day)&& s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "sun" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase().contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "mon" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase().contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "tue" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase().contains(day)&& s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "wed" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase().contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "thu" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase().contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "fri" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase().contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
            }

        }
    }
}

class NotificationService {

    private var contentView: RemoteViews? = null
    private var notification: Notification? = null
    private var notificationManager: NotificationManager? = null
    private val NotificationID = 1005
    private var mBuilder: NotificationCompat.Builder? = null

    @SuppressLint("SimpleDateFormat")
    fun runNotification(data: ArrayList<SmartRoutine>, day: String) {

        notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mBuilder = NotificationCompat.Builder(appContext, "notify_001")
        contentView = RemoteViews("com.newage.studlab", R.layout.notification_smart_routine)
        contentView!!.setImageViewResource(R.id.image, R.mipmap.ic_launcher)

        val intent = Intent(appContext, HomeMainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        contentView!!.setOnClickPendingIntent(R.id.notification_button, pendingIntent)

        contentView!!.setTextViewText(
            R.id.last_updated_text, "Last Updated: ${
                SimpleDateFormat("hh:mm a").format(
                    Date()
                )
            }"
        )



        val time = SimpleDateFormat("hhmm")
        val currentTime = time.format(Date()).toInt()


        if(data.size != 0){

            when (data.size) {
                1 -> {
                    contentView!!.setTextViewText(R.id.c_code1, data[0].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room1,
                        "${data[0].Building} R-${data[0].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time1, data[0].Schedule.substring(0, 8))


                    contentView!!.setViewVisibility(R.id.block1, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block2, View.GONE)
                    contentView!!.setViewVisibility(R.id.block3, View.GONE)
                    contentView!!.setViewVisibility(R.id.block4, View.GONE)
                    contentView!!.setViewVisibility(R.id.block5, View.GONE)


                    val scheduleList = data[0].Schedule.split("to")
                    val startTime = scheduleList[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime = scheduleList[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime..endTime) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }


                    builtin()
                    //contentView!!.setViewVisibility(R)
                }
                2 -> {
                    contentView!!.setTextViewText(R.id.c_code1, data[0].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room1,
                        "${data[0].Building} R-${data[0].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time1, data[0].Schedule.substring(0, 8))


                    contentView!!.setTextViewText(R.id.c_code2, data[1].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room2,
                        "${data[1].Building} R-${data[1].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time2, data[1].Schedule.substring(0, 8))



                    contentView!!.setViewVisibility(R.id.block1, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block2, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block3, View.GONE)
                    contentView!!.setViewVisibility(R.id.block4, View.GONE)
                    contentView!!.setViewVisibility(R.id.block5, View.GONE)


                    val scheduleList0 = data[0].Schedule.split("to")
                    val startTime0 = scheduleList0[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime0 = scheduleList0[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime0..endTime0) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList1 = data[1].Schedule.split("to")
                    val startTime1 = scheduleList1[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime1 = scheduleList1[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime1..endTime1) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }
                    builtin()
                }
                3 -> {
                    contentView!!.setTextViewText(R.id.c_code1, data[0].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room1,
                        "${data[0].Building} R-${data[0].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time1, data[0].Schedule.substring(0, 8))

                    contentView!!.setTextViewText(R.id.c_code2, data[1].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room2,
                        "${data[1].Building} R-${data[1].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time2, data[1].Schedule.substring(0, 8))

                    contentView!!.setTextViewText(R.id.c_code3, data[2].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room3,
                        "${data[2].Building} R-${data[2].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time3, data[2].Schedule.substring(0, 8))

                    contentView!!.setViewVisibility(R.id.block1, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block2, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block3, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block4, View.GONE)
                    contentView!!.setViewVisibility(R.id.block5, View.GONE)


                    val scheduleList0 = data[0].Schedule.split("to")
                    val startTime0 = scheduleList0[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime0 = scheduleList0[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime0..endTime0) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList1 = data[1].Schedule.split("to")
                    val startTime1 = scheduleList1[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime1 = scheduleList1[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime1..endTime1) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList3 = data[2].Schedule.split("to")
                    val startTime3 = scheduleList3[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime3 = scheduleList3[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime3..endTime3) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    builtin()
                }
                4 -> {
                    contentView!!.setTextViewText(R.id.c_code1, data[0].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room1,
                        "${data[0].Building} R-${data[0].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time1, data[0].Schedule.substring(0, 8))

                    contentView!!.setTextViewText(R.id.c_code2, data[1].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room2,
                        "${data[1].Building} R-${data[1].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time2, data[1].Schedule.substring(0, 8))

                    contentView!!.setTextViewText(R.id.c_code3, data[2].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room3,
                        "${data[2].Building} R-${data[2].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time3, data[2].Schedule.substring(0, 8))

                    contentView!!.setTextViewText(R.id.c_code4, data[3].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room4,
                        "${data[3].Building} R-${data[3].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time4, data[3].Schedule.substring(0, 8))


                    contentView!!.setViewVisibility(R.id.block1, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block2, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block3, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block4, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block5, View.GONE)


                    val scheduleList0 = data[0].Schedule.split("to")
                    val startTime0 = scheduleList0[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime0 = scheduleList0[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime0..endTime0) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList1 = data[1].Schedule.split("to")
                    val startTime1 = scheduleList1[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime1 = scheduleList1[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime1..endTime1) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList3 = data[2].Schedule.split("to")
                    val startTime3 = scheduleList3[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime3 = scheduleList3[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime3..endTime3) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList4 = data[3].Schedule.split("to")
                    val startTime4 = scheduleList4[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime4 = scheduleList4[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime4..endTime4) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    builtin()
                }
                5 -> {
                    contentView!!.setTextViewText(R.id.c_code1, data[0].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room1,
                        "${data[0].Building} R-${data[0].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time1, data[0].Schedule.substring(0, 8))

                    contentView!!.setTextViewText(R.id.c_code2, data[1].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room2,
                        "${data[1].Building} R-${data[1].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time2, data[1].Schedule.substring(0, 8))

                    contentView!!.setTextViewText(R.id.c_code3, data[2].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room3,
                        "${data[2].Building} R-${data[2].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time3, data[2].Schedule.substring(0, 8))

                    contentView!!.setTextViewText(R.id.c_code4, data[3].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room4,
                        "${data[3].Building} R-${data[3].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time4, data[3].Schedule.substring(0, 8))

                    contentView!!.setTextViewText(R.id.c_code5, data[4].Subject_Code)
                    contentView!!.setTextViewText(
                        R.id.c_room5,
                        "${data[4].Building} R-${data[4].Room_No}"
                    )
                    contentView!!.setTextViewText(R.id.c_time5, data[4].Schedule.substring(0, 8))

                    contentView!!.setViewVisibility(R.id.block1, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block2, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block3, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block4, View.VISIBLE)
                    contentView!!.setViewVisibility(R.id.block5, View.VISIBLE)


                    val scheduleList0 = data[0].Schedule.split("to")
                    val startTime0 = scheduleList0[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime0 = scheduleList0[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime0..endTime0) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList1 = data[1].Schedule.split("to")
                    val startTime1 = scheduleList1[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime1 = scheduleList1[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime1..endTime1) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList3 = data[2].Schedule.split("to")
                    val startTime3 = scheduleList3[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime3 = scheduleList3[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime3..endTime3) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList4 = data[3].Schedule.split("to")
                    val startTime4 = scheduleList4[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime4 = scheduleList4[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime4..endTime4) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.VISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.INVISIBLE)
                    }

                    val scheduleList5 = data[4].Schedule.split("to")
                    val startTime5 = scheduleList5[0].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()
                    val endTime5 = scheduleList5[1].replace(":", "").replace("AM", "").replace(
                        "PM",
                        ""
                    ).replace(" ", "").toInt()

                    if (currentTime in startTime5..endTime5) {
                        contentView!!.setViewVisibility(R.id.c_ind1, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind2, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind3, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind4, View.INVISIBLE)
                        contentView!!.setViewVisibility(R.id.c_ind5, View.VISIBLE)
                    }

                    builtin()
                }
            }
            builtin()
        }


    }

    private fun builtin(){
        mBuilder!!.setSmallIcon(R.drawable.ic_notification_svg)
        mBuilder!!.setAutoCancel(false)
        mBuilder!!.setOngoing(true)
        mBuilder!!.priority = Notification.PRIORITY_LOW
        mBuilder!!.setOnlyAlertOnce(true)
        mBuilder!!.build().flags = Notification.FLAG_NO_CLEAR or Notification.PRIORITY_LOW
        mBuilder!!.setContent(contentView)

        mBuilder!!.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(appContext))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channel = NotificationChannel(channelId, "smart routine", NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(false)
            channel.vibrationPattern = longArrayOf(400)
            notificationManager!!.createNotificationChannel(channel)
            mBuilder!!.setChannelId(channelId)
        }
        notification = mBuilder!!.build()
        notificationManager!!.notify(NotificationID, notification)

    }

}