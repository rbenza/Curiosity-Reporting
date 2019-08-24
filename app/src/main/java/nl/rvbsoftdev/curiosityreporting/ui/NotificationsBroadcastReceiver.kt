package nl.rvbsoftdev.curiosityreporting.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

 /** Broadcast receiver used in conjunction with AlarmManager to display weekly Notifications to the user **/

class NotificationsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        AppNotifications.notify(context)
    }
}
