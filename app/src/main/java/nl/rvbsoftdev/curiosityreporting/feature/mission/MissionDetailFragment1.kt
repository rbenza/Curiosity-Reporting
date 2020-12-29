package nl.rvbsoftdev.curiosityreporting.feature.mission

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.Fragment1MissionDetailBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity
import nl.rvbsoftdev.curiosityreporting.global.SharedViewModel

class MissionDetailFragment1 : BaseFragment<Fragment1MissionDetailBinding>() {

    override val layout = R.layout.fragment1_mission_detail
    override val firebaseTag = "Mission Detail Fragment 1"
    private val viewModel: SharedViewModel by activityViewModels()

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
}

