package com.newage.studlab.Services.FcmNotificationService;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.newage.studlab.Application.StudLab;
import com.newage.studlab.Database.DatabaseHelper;
import com.newage.studlab.Home.HomeMainActivity;
import com.newage.studlab.Model.NotificationModel;
import com.newage.studlab.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title,message;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);
            title=remoteMessage.getData().get("Title");
            message=remoteMessage.getData().get("Message");

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, HomeMainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("Blood Bank", "studlab", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "Blood Bank")
                    .setSmallIcon(R.drawable.ic_notification_svg)
                    .setContentIntent(pendingIntent)
                    .setSound(alarmSound)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setContentText(message);

            assert manager != null;
            manager.createNotificationChannel(notificationChannel);
            manager.notify((int)(System.currentTimeMillis()/1000), mBuilder.build());

        }else{

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_notification_svg)
                            .setContentTitle(title)
                            .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                            .setContentText(message)
                            .setSound(alarmSound);
            assert manager != null;
            manager.notify(0, builder.build());
        }

        saveNotification(title, message);

    }

    private void saveNotification(String title, String message){

        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = SimpleDateFormat.getDateInstance();
        String arrivalTime = formatter.format(date);

        String notificationType = "all";

        if(title.toLowerCase().contains("blood")){
            notificationType = "blood";
        }else if(title.toLowerCase().contains("library")){
            notificationType = "library";
        }


        DatabaseHelper routineDatabase = new DatabaseHelper(this);

        NotificationModel notification = new NotificationModel(title,message,arrivalTime,"false",notificationType);
        routineDatabase.saveNotificationData(notification);
    }



}
