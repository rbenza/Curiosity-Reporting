package nl.rvbsoftdev.curiosityreporting.feature.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import nl.rvbsoftdev.curiosityreporting.feature.more.SettingsFragment

/** Broadcast receiver used in conjunction with AlarmManager to display weekly notifications to the user **/

class NotificationsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)

        // only show weekly notification if user settings allow
        if (sp.getBoolean("notifications", true)){
            AppNotifications.notify(context)
        }
    }
}
