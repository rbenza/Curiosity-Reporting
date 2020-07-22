package nl.rvbsoftdev.curiosityreporting.global

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.muddzdev.styleabletoast.StyleableToast
import com.pd.chocobar.ChocoBar
import kotlinx.android.synthetic.main.activity_navigation.*
import nl.rvbsoftdev.curiosityreporting.BuildConfig
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.ActivityNavigationBinding
import nl.rvbsoftdev.curiosityreporting.feature.notification.AppNotifications
import nl.rvbsoftdev.curiosityreporting.feature.notification.NotificationsBroadcastReceiver

/** Single Activity for the whole app, sets up all the UI elements (Toolbar, Bottom Nav, Side Nav,
 * Snackbar/Toast messages, user Theme and Notification Channel).
 * Also contains a Navigation Host Fragment which hosts all the Fragment destinations (see res/navigation/navigation_graph.xml for full overview).
 *
 * For the benefits of a single activity design pattern see: https://www.youtube.com/watch?v=2k8x8V77CrU and
 * https://www.reddit.com/r/androiddev/comments/9yf21b/single_activity_architecture_why/  **/

class NavigationActivity : AppCompatActivity() {

    /** Firebase setup to monitor app performance and usage **/
    lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme) // Sets the normal theme, after the 'splashscreen theme' was set at startup
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_navigation)
        setSupportActionBar(binding.globalToolbar)
        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, setTopLevelDestinations)
        setupBottomNavigation(navController)
        setupSideNavigationMenu(navController)
        visibilityNavElements(navController)
        setupNotificationChannel()
        setupWeeklyNotifications()
        enableStrictMode()
    }

    private val setTopLevelDestinations by lazy {
        AppBarConfiguration(setOf(R.id.mission_fragment, R.id.explore_fragment,
                R.id.facts_fragment, R.id.favorites_fragment, R.id.more_fragment), drawer_layout)
    }

    private fun setupBottomNavigation(navController: NavController) {
        binding.bottomNav?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupSideNavigationMenu(navController: NavController) {
        binding.sideNav?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun visibilityNavElements(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mission_fragment -> {
                    binding.bottomNav?.setVisible()
                    binding.globalToolbar.setGone()
                }
                R.id.mission_detail_fragment1,
                R.id.mission_detail_fragment2,
                R.id.mission_detail_fragment3 -> {
                    binding.bottomNav?.setGone()
                    binding.globalToolbar.setVisible()
                }
                R.id.about_fragment,
                R.id.settings_fragment,
                R.id.fragment_privacy_policy -> binding.bottomNav?.setGone()

                R.id.favorites_detail_fragment -> {
                    binding.bottomNav?.setGone()
                    binding.globalToolbar.setGone()
                }
                else -> {
                    binding.bottomNav?.setVisible()
                    binding.globalToolbar.setVisible()
                }
            }
        }
    }

    /** Monitor if nothing expensive runs on UI thread **/
    private fun enableStrictMode() {
        if (BuildConfig.DEBUG) {
            val policy = StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()

            StrictMode.setThreadPolicy(policy)
        }
    }

    override fun onSupportNavigateUp() =
            NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment),
                    setTopLevelDestinations) || super.onSupportNavigateUp()

    fun showStyledToastMessage(toastMessage: String) = StyleableToast.makeText(this, toastMessage, Toast.LENGTH_LONG, R.style.ToastStyleDark).show()

    fun showStyledSnackbarMessage(view: View, text: String, durationMs: Int,
                                  icon: Int, textAction: String = "", action: (() -> Unit)? = null) {
                ChocoBar.builder()
                        .setView(view)
                        .setBackgroundColor(getColor(R.color.NearBlack))
                        .setTextSize(15F)
                        .setTextColor(getColor(R.color.YellowSand))
                        .setTextTypefaceStyle(Typeface.BOLD_ITALIC)
                        .setText(text)
                        .setMaxLines(8)
                        .setActionText(textAction)
                        .setActionTextColor(getColor(R.color.DeepOrange))
                        .setActionTextSize(18F)
                        .setActionTextTypefaceStyle(Typeface.BOLD)
                        .setIcon(icon)
                        .setDuration(durationMs)
                        .setActionClickListener { action?.invoke()  }
                        .build()
                        .show()
    }

    /** Setup Notification channel for Android 8.0 or higher devices **/
    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(AppNotifications.NOTIFICATION_CHANNEL, "AppNotifications", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Get a weekly notification about Curiosity at Mars."
            nm.createNotificationChannel(channel)
        }
    }

    private fun setupWeeklyNotifications() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notifications", true)) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, NotificationsBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY * 7,
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent)
        }
    }
}