package nl.rvbsoftdev.curiosityreporting.feature.more

import androidx.lifecycle.ViewModel
import nl.rvbsoftdev.curiosityreporting.R

class MoreViewModel : ViewModel() {

    val moreItemslist = listOf(
            MoreItem(0, R.drawable.icon_mission, "Visit the NASA website", "Get to know more about Mars and the mission of Curiosity"),
            MoreItem(1, R.drawable.icon_share, "Share", "Share this app with friends and family"),
            MoreItem(2, R.drawable.icon_settings, "Settings", "Adjust appearance and settings"),
            MoreItem(3, R.drawable.icon_info, "About this app", "View the Privacy Policy, GitHub repository or contact the developer"))
}