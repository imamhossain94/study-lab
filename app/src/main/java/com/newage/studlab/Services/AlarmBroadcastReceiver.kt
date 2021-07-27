package com.newage.studlab.Services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.widget.Toast
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Model.AnnexModel.SmartRoutine
import java.util.*


class AlarmBroadcastReceiver : BroadcastReceiver() {


    init {
        notifyData(appContext)
    }

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            this.setAlarm(context)
        }

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PeriSecure:MyWakeLock")
        wakeLock.acquire(10*60*1000L /*10 minutes*/)

        // Put here YOUR code.
        notifyData(appContext)
        Toast.makeText(context, "Alarm Received!!", Toast.LENGTH_SHORT).show()

        wakeLock.release()
    }

    fun setAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), (1000 * 60 * 1).toLong(), pendingIntent)

        Toast.makeText(context, "Alarm Service Started!!", Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
        Toast.makeText(context, "Alarm Service Canceled!!", Toast.LENGTH_SHORT).show()
    }


    private fun notifyData(context: Context) {

        val databaseHandler = DatabaseHelper(context)
        val smartRtn = databaseHandler.getSmartRoutine()
        if(smartRtn.size != 0){

            val calendar = Calendar.getInstance()

            when (val day = calendar.time.toString().substring(0,3).toLowerCase(Locale.getDefault())
            ) {

                "sat" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase(
                        //Locale.ROOT
                    ).contains(day)&& s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "sun" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase(
                        //Locale.ROOT
                    ).contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "mon" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase(
                        //Locale.ROOT
                    ).contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "tue" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase(
                        //Locale.ROOT
                    ).contains(day)&& s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "wed" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase(
                        //Locale.ROOT
                    ).contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "thu" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase(
                        //Locale.ROOT
                    ).contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
                "fri" -> {
                    NotificationService().runNotification(smartRtn.filter { s -> s.Day.toLowerCase(
                        //Locale.ROOT
                    ).contains(day) && s.Subject_Code != "" } as ArrayList<SmartRoutine>,day)
                }
            }

        }
    }

}