package nl.rvbsoftdev.curiosityreporting.feature.mission

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentMissionBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.SharedViewModel

/** 'MissionFragment' first screen the user sees. Provides navigation to MissionDetailFragments 1, 2 and 3 and a Twitter webview. **/

class MissionFragment : BaseFragment<FragmentMissionBinding>() {

    override val layout = R.layout.fragment_mission
    override val firebaseTag = "Mission Fragment"
    private val viewModel: SharedViewModel by lazy { ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sharedViewModel = viewModel

        /** A few simple Onclicklisteners with lambda for single event instead of wiring it through a ViewModel with LiveData **/
        binding.cardviewTwitter.setOnClickListener {
            WebView(requireContext()).apply {
                webChromeClient = WebChromeClient()
                loadUrl("https://twitter.com/marscuriosity?lang=browser#night")
            }
        }
        binding.journeyCardview.setOnClickListener { findNavController().navigate(R.id.action_mission_fragment_to_mission_detail_fragment1) }
        binding.marsCardview.setOnClickListener { findNavController().navigate(R.id.action_mission_fragment_to_mission_detail_fragment2) }
        binding.equipmentCardview.setOnClickListener { findNavController().navigate(R.id.action_mission_fragment_to_mission_detail_fragment3) }

    }

    /** Only allow portrait orientation. Content in Mission Fragment not suitable for landscape orientation **/
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}