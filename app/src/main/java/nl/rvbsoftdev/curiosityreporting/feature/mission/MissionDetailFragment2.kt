package nl.rvbsoftdev.curiosityreporting.feature.mission

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.Fragment2MissionDetailBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.SharedViewModel

class MissionDetailFragment2 : BaseFragment<Fragment2MissionDetailBinding>() {

    override val layout = R.layout.fragment2_mission_detail
    override val firebaseTag = "Mission Detail Fragment 2"
    private val viewModel: SharedViewModel by lazy { ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sharedViewModel = viewModel
    }

    /** Only allow portrait orientation. Content in Mission Detail Fragment 2 not suitable for landscape orientation **/
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}