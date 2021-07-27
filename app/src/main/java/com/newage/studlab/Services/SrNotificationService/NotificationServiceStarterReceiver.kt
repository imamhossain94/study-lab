package com.newage.studlab.Services.SrNotificationService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.newage.studlab.Services.SrNotificationService.NotificationEventReceiver.Companion.setupAlarm

/**
 * Created by klogi
 *
 * Broadcast receiver for: BOOT_COMPLETED, TIMEZONE_CHANGED, and TIME_SET events. Sets Alarm Manager for notification;
 */
class NotificationServiceStarterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        setupAlarm(context)
    }
}