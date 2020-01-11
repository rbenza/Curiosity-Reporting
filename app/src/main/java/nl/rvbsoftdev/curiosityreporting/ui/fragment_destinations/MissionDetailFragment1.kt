package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.Fragment1MissionDetailBinding
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity
import nl.rvbsoftdev.curiosityreporting.viewmodels.SharedViewModel

class MissionDetailFragment1 : BaseFragment<Fragment1MissionDetailBinding>() {

    override val layout = R.layout.fragment1_mission_detail
    override val firebaseTag = "Mission Detail Fragment 1"
    private val mViewModel: SharedViewModel by lazy { ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sharedViewModel = mViewModel
        binding.missionPhoto2.setOnClickListener { watchCuriosityLandingOnYouTube() }
    }

    private fun watchCuriosityLandingOnYouTube() {
        try {
            val launchBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Esj5juUzhpU"))
            if (launchBrowser.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(launchBrowser)
            } else {
                (activity as SingleActivity).showStyledToastMessage(getString(R.string.no_internet_app2))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Only allow portrait orientation. Content in Mission Detail Fragment 1 not suitable for landscape orientation **/
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}

