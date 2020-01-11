package nl.rvbsoftdev.curiosityreporting.viewmodels

import androidx.lifecycle.ViewModel
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.adapters.MoreItems

class MoreViewModel : ViewModel() {

    val moreItemslist = arrayListOf(
            MoreItems(0, R.drawable.icon_mission, "Visit the NASA website", "Get to know more about Mars and the mission of Curiosity"),
            MoreItems(1, R.drawable.icon_share, "Share", "Share this app with friends and family"),
            MoreItems(2, R.drawable.icon_settings, "Settings", "Adjust appearance and settings"),
            MoreItems(3, R.drawable.icon_info, "About this app", "View the Privacy Policy, GitHub repository or contact the developer"))

}