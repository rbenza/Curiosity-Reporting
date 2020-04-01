package nl.rvbsoftdev.curiosityreporting.mission

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.fragment1_mission_detail.*
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.Fragment1MissionDetailBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity
import nl.rvbsoftdev.curiosityreporting.global.SharedViewModel

class MissionDetailFragment1 : BaseFragment<Fragment1MissionDetailBinding>() {

    override val layout = R.layout.fragment1_mission_detail
    override val firebaseTag = "Mission Detail Fragment 1"
    private val viewModel: SharedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sharedViewModel = viewModel
        binding.missionPhoto2.setOnClickListener { watchCuriosityLandingOnYouTube() }
    }

    private fun watchCuriosityLandingOnYouTube() {
        try {
            val launchBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=Esj5juUzhpU"))
            if (launchBrowser.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(launchBrowser)
            } else {
                (activity as NavigationActivity).showStyledToastMessage(getString(R.string.no_internet_app2))
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

