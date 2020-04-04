package nl.rvbsoftdev.curiosityreporting.feature.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Broadcast receiver used in conjunction with AlarmManager to display weekly notifications to the user **/

class NotificationsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        AppNotifications.notify(context)
    }
}
