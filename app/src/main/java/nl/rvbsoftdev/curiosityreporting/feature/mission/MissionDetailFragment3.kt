package nl.rvbsoftdev.curiosityreporting.feature.mission

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.Fragment3MissionDetailBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.SharedViewModel

class MissionDetailFragment3 : BaseFragment<Fragment3MissionDetailBinding>() {

    override val layout = R.layout.fragment3_mission_detail
    override val firebaseTag = "Mission Detail Fragment 3"
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sharedViewModel = viewModel
    }
}