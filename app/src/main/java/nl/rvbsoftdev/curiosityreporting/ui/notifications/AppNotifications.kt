package nl.rvbsoftdev.curiosityreporting.ui.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import nl.rvbsoftdev.curiosityreporting.R

/** Weekly notifications for the user. Can be disabled in settings **/

object AppNotifications {

    private val NOTIFICATION_TAG = "NewPhotos"
    val NOTIFICATION_CHANNEL = "Alert channel"

    fun notify(context: Context) {
        val res = context.resources
        val picture = BitmapFactory.decodeResource(res, R.drawable.image_launch_screen)
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)

                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.icon_photo_library)
                .setContentTitle("Meanwhile on Mars...")
                .setContentText("See what Curiosity has been up to this week!")
                .setColorized(true)
                .setColor(res.getColor(R.color.DeepOrange, null))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                .setContentIntent(
                        NavDeepLinkBuilder(context)
                                .setGraph(R.navigation.navigation_graph)
                                .setDestination(R.id.explore_fragment)
                                .createPendingIntent())
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText("See what Curiosity has been up to this week!"))
                .setAutoCancel(true)

        notify(context, builder.build())
    }

    private fun notify(context: Context, notification: Notification) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(NOTIFICATION_TAG, 0, notification)
    }
}
