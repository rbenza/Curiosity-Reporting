package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.Fragment3MissionDetailBinding
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity
import nl.rvbsoftdev.curiosityreporting.viewmodels.SharedViewModel

class MissionDetailFragment3 : BaseFragment<Fragment3MissionDetailBinding>() {

    override val layout = R.layout.fragment3_mission_detail
    override val firebaseTag = "Mission Detail Fragment 3"
    private val mViewModel: SharedViewModel by lazy { ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sharedViewModel = mViewModel
    }

    /** Only allow portrait orientation. Content in Mission Detail Fragment 3 not suitable for landscape orientation **/
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }


}