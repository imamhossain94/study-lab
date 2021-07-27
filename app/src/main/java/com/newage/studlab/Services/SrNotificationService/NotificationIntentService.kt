package com.newage.studlab.Services.SrNotificationService

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.legacy.content.WakefulBroadcastReceiver
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Model.AnnexModel.SmartRoutine
import com.newage.studlab.Services.NotificationService
import java.util.*

/*
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.newage.studlab.Database.DatabaseHelper;
import com.newage.studlab.Home.HomeMainActivity;
import com.newage.studlab.Home.RoutineFragment.SmartRoutineFragment;
import com.newage.studlab.Model.AnnexModel.SmartRoutine;
import com.newage.studlab.R;
import com.newage.studlab.Services.NotificationService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

*/
/**
 * Created by klogi
 *
 *
 */
/*

public class NotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

*/
/*        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Scheduled Notification")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText("This notification has been triggered by Notification Service")
                .setSmallIcon(R.drawable.app_icon);

        Intent mainIntent = new Intent(this, HomeMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());*/
/*


*/
/*        SmartRoutineFragment smartRoutineFragment = new SmartRoutineFragment();
        smartRoutineFragment.performNotification(getApplicationContext());*/
/*


        notifyData();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    void notifyData()  {


        DatabaseHelper databaseHandler = new DatabaseHelper(getApplicationContext());
        ArrayList<SmartRoutine> smartRtn = databaseHandler.getSmartRoutine();

        if(smartRtn.size() != 0){

            Calendar calendar = Calendar.getInstance();

            String day = calendar.getTime().toString().substring(0,3).toLowerCase();

            switch (day){
                case  "sat":{

                    ArrayList<SmartRoutine> filteredList = new ArrayList<>();

                    for (SmartRoutine s : smartRtn) {
                        if (s.getDay().toLowerCase().contains(day)&& !s.getSubject_Code().isEmpty()) {
                            filteredList.add(s);
                        }
                    }

                    new NotificationService().runNotification(filteredList,day);
                }
                case  "sun":{

                    ArrayList<SmartRoutine> filteredList = new ArrayList<>();

                    for (SmartRoutine s : smartRtn) {
                        if (s.getDay().toLowerCase().contains(day)&& !s.getSubject_Code().isEmpty()) {
                            filteredList.add(s);
                        }
                    }

                    new NotificationService().runNotification(filteredList,day);
                }
                case  "mon":{

                    ArrayList<SmartRoutine> filteredList = new ArrayList<>();

                    for (SmartRoutine s : smartRtn) {
                        if (s.getDay().toLowerCase().contains(day)&& !s.getSubject_Code().isEmpty()) {
                            filteredList.add(s);
                        }
                    }

                    new NotificationService().runNotification(filteredList,day);
                }
                case  "tue":{

                    ArrayList<SmartRoutine> filteredList = new ArrayList<>();

                    for (SmartRoutine s : smartRtn) {
                        if (s.getDay().toLowerCase().contains(day)&& !s.getSubject_Code().isEmpty()) {
                            filteredList.add(s);
                        }
                    }

                    new NotificationService().runNotification(filteredList,day);
                }
                case  "wed":{

                    ArrayList<SmartRoutine> filteredList = new ArrayList<>();

                    for (SmartRoutine s : smartRtn) {
                        if (s.getDay().toLowerCase().contains(day)&& !s.getSubject_Code().isEmpty()) {
                            filteredList.add(s);
                        }
                    }

                    new NotificationService().runNotification(filteredList,day);
                }
                case  "thu":{

                    ArrayList<SmartRoutine> filteredList = new ArrayList<>();

                    for (SmartRoutine s : smartRtn) {
                        if (s.getDay().toLowerCase().contains(day)&& !s.getSubject_Code().isEmpty()) {
                            filteredList.add(s);
                        }
                    }

                    new NotificationService().runNotification(filteredList,day);
                }
                case  "fri":{

                    ArrayList<SmartRoutine> filteredList = new ArrayList<>();

                    for (SmartRoutine s : smartRtn) {
                        if (s.getDay().toLowerCase().contains(day)&& !s.getSubject_Code().isEmpty()) {
                            filteredList.add(s);
                        }
                    }

                    new NotificationService().runNotification(filteredList,day);
                }
            }

        }
    }

}
*/ /**
 * Created by klogi
 *
 *
 */
class NotificationIntentService : IntentService(NotificationIntentService::class.java.simpleName) {
    override fun onHandleIntent(intent: Intent?) {
        Log.d(javaClass.simpleName, "onHandleIntent, started handling a notification event")
        try {
            val action = intent!!.action
            if (ACTION_START == action) {
                processStartNotification()
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent)
        }
    }

    private fun processDeleteNotification(intent: Intent) {
        // Log something?
    }

    private fun processStartNotification() {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

/*        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("lal notification")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText("This lal notification has been triggered by studlab")
                .setSmallIcon(R.drawable.ic_notification_svg_dark_blue);

        Intent mainIntent = new Intent(this, NotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());*/
        notifyData()
    }

    fun notifyData() {

        val databaseHandler = DatabaseHelper(applicationContext)
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

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val ACTION_START = "ACTION_START"
        private const val ACTION_DELETE = "ACTION_DELETE"
        fun createIntentStartNotificationService(context: Context?): Intent {
            val intent = Intent(context, NotificationIntentService::class.java)
            intent.action = ACTION_START
            return intent
        }

        fun createIntentDeleteNotification(context: Context?): Intent {
            val intent = Intent(context, NotificationIntentService::class.java)
            intent.action = ACTION_DELETE
            return intent
        }
    }
}