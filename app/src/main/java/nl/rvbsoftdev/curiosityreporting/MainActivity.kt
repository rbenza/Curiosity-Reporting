package nl.rvbsoftdev.curiosityreporting

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
import androidx.lifecycle.ViewModelProviders
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
import kotlinx.android.synthetic.main.activity_main.*
import nl.rvbsoftdev.curiosityreporting.ui.NotificationsBroadcastReceiver
import nl.rvbsoftdev.curiosityreporting.ui.AppNotifications
import nl.rvbsoftdev.curiosityreporting.viewmodels.SharedViewModel

/** Single Activity for the whole app, sets up all the UI elements (Toolbar, Bottom Nav, Side Nav, Notification Channel, Broadcast Receiver)
 *  and contains a Navigation Host Fragment which hosts all the Fragment destinations (see navigation_graph.xml for full overview). **/

class MainActivity : AppCompatActivity() {

    /** Firebase setup to monitor app performance and usage **/
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var mViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        setAndReturnUserTheme()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(global_toolbar)
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
        bottom_nav?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupSideNavigationMenu(navController: NavController) {
        side_nav?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun visibilityNavElements(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mission_fragment -> {
                    bottom_nav?.visibility = View.VISIBLE
                    global_toolbar?.visibility = View.GONE
                }
                R.id.mission_detail_fragment1 -> {
                    bottom_nav?.visibility = View.GONE
                    global_toolbar?.visibility = View.VISIBLE
                }
                R.id.mission_detail_fragment2 -> {
                    bottom_nav?.visibility = View.GONE
                    global_toolbar?.visibility = View.VISIBLE
                }
                R.id.mission_detail_fragment3 -> {
                    bottom_nav?.visibility = View.GONE
                    global_toolbar?.visibility = View.VISIBLE
                }
                R.id.about_fragment -> bottom_nav?.visibility = View.GONE
                R.id.settings_fragment -> bottom_nav?.visibility = View.GONE
                R.id.fragment_privacy_policy -> bottom_nav?.visibility = View.GONE
                R.id.explore_detail_fragment -> {
                    bottom_nav?.visibility = View.GONE
                    global_toolbar?.visibility = View.GONE
                }
                R.id.favorites_detail_fragment -> {
                    bottom_nav?.visibility = View.GONE
                    global_toolbar?.visibility = View.GONE
                }
                else -> {
                    bottom_nav?.visibility = View.VISIBLE
                    global_toolbar?.visibility = View.VISIBLE
                }

            }
        }
    }

    /** Check if nothing expensive runs on UI thread **/
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
            NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), setTopLevelDestinations)


    fun setAndReturnUserTheme(): String? {
        val userTheme = PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "Dark")
        if (userTheme == "Dark") setTheme(R.style.AppThemeDark)
        else setTheme(R.style.AppThemeLight)

        return userTheme
    }

    fun showStyledToastMessage(toastMessage: String) {
        if (setAndReturnUserTheme() == "Dark") {
            StyleableToast.makeText(this, toastMessage, Toast.LENGTH_LONG, R.style.ToastStyleDark).show()
        } else StyleableToast.makeText(this, toastMessage, Toast.LENGTH_LONG, R.style.ToastStyleLight).show()
    }

    fun showStyledSnackbarMessage(view: View, msg: String, msgAction: String?, durationMs: Int,
                                  icon: Int, action: (() -> Unit)?) {
        when (setAndReturnUserTheme()) {
            "Dark" -> {
                ChocoBar.builder()
                        .setView(view)
                        .setBackgroundColor(this.getColor(R.color.NearerBlack))
                        .setTextSize(15F)
                        .setTextColor(this.getColor(R.color.YellowSand))
                        .setTextTypefaceStyle(Typeface.BOLD_ITALIC)
                        .setText(msg)
                        .setMaxLines(8)
                        .setActionText(msgAction)
                        .setActionTextColor(this.getColor(R.color.DeepOrange))
                        .setActionTextSize(18F)
                        .setActionTextTypefaceStyle(Typeface.BOLD)
                        .setIcon(icon)
                        .setDuration(durationMs)
                        .setActionClickListener { action!!() }
                        .build()
                        .show()
            }
            "Light" -> {

                ChocoBar.builder()
                        .setView(view)
                        .setBackgroundColor(this.getColor(R.color.YellowLightSand))
                        .setTextSize(15F)
                        .setTextColor(this.getColor(R.color.DarkerGrey))
                        .setTextTypefaceStyle(Typeface.BOLD_ITALIC)
                        .setText(msg)
                        .setMaxLines(8)
                        .setActionText(msgAction)
                        .setActionTextColor(this.getColor(R.color.DarkBrown))
                        .setActionTextSize(18F)
                        .setActionTextTypefaceStyle(Typeface.BOLD)
                        .setIcon(icon)
                        .setDuration(durationMs)
                        .setActionClickListener { action!!() }
                        .build()
                        .show()
            }

        }
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
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_DAY * 7,
                    AlarmManager.INTERVAL_HALF_DAY * 7,
                    pendingIntent)
        }
    }

    fun updateUI() {
        this.recreate()
    }
}





















