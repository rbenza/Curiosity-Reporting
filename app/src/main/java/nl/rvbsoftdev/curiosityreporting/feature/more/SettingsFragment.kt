package nl.rvbsoftdev.curiosityreporting.feature.more

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity

/** Settings Fragment where the user can adjust the theme and app settings. Uses OnSharedPreferenceChangeListener to react to preferences changing. **/

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val navigationActivity by lazy { (activity as NavigationActivity) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, "Settings Fragment")
            navigationActivity.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, this)
        }

        if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("nasa_key", null).isNullOrEmpty()) {
            navigationActivity.showStyledSnackbarMessage(view,
                    text = getString(R.string.nasa_key_warning),
                    textAction = "GET KEY",
                    durationMs = 5000,
                    icon = R.drawable.icon_key,
                    action = { getNasaKeyAtWebsite() })
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        when (key) {
            "notifications" -> when (sharedPreferences.getBoolean("notifications", true)) {
                true -> navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.noti_on),
                        durationMs = 4000, icon = R.drawable.icon_notifications)
                false -> navigationActivity.showStyledSnackbarMessage(requireView(), getString(R.string.noti_off),
                        durationMs = 3000, icon = R.drawable.icon_notifications_off)
            }

            "picture_quality" -> when (sharedPreferences.getString("picture_quality", "High")) {
                "High" -> {
                    navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.high_pic_qlty),
                            durationMs = 3000, icon = R.drawable.icon_image)
                }
                "Normal" -> {
                    navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.normal_pic_qlty),
                            durationMs = 3000, icon = R.drawable.icon_image)
                }
                "Low" -> {
                    navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.low_pic_qlty),
                            durationMs = 3000, icon = R.drawable.icon_image)
                }
            }

            "explore_photo_layout" -> when (sharedPreferences.getString("explore_photo_layout", "Grid")) {
                "List" -> {
                    navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.photo_expl_list_msg),
                            durationMs = 3000, icon = R.drawable.icon_list)
                }
                "Grid" -> {
                    navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.photo_expl_grid_msg),
                            durationMs = 3000, icon = R.drawable.icon_grid)
                }
            }

            "favorites_photo_layout" -> when (sharedPreferences.getString("favorites_photo_layout", "List")) {
                "List" -> {
                    navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.photo_fav_list_msg),
                            durationMs = 3000, icon = R.drawable.icon_list)
                }
                "Grid" -> {
                    navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.photo_fav_grid_msg),
                            durationMs = 3000, icon = R.drawable.icon_grid)
                }
            }

            "nasa_key" -> when (sharedPreferences.getString("nasa_key", null)) {
                "" -> {
                    navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.def_key),
                            durationMs = 4000, icon = R.drawable.icon_key)
                }
                else -> {
                    navigationActivity.showStyledSnackbarMessage(requireView(), text = getString(R.string.personal_key_applied),
                            durationMs = 5000, icon = R.drawable.icon_key)
                }
            }
        }
    }

    private fun getNasaKeyAtWebsite() {
        try {
            val launchBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.nasa.gov/index.html#apply-for-an-api-key"))
            if (launchBrowser.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(launchBrowser)
            } else {
                navigationActivity.showStyledToastMessage(getString(R.string.no_internet_app))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Prevent memory leak, unregister OnSharedPreferenceChangeListener when fragment looses focus for user.
     * Secondly locks orientaton to portrait (SettingsFragment does not inherit from BaseFragment) **/
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}