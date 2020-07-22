package nl.rvbsoftdev.curiosityreporting.feature.mission

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.navigation.fragment.findNavController
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentMissionBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment

/** 'MissionFragment' first screen the user sees. Provides navigation to MissionDetailFragments 1, 2 and 3 and a Twitter WebView. **/

class MissionFragment : BaseFragment<FragmentMissionBinding>() {

    override val layout = R.layout.fragment_mission
    override val firebaseTag = "Mission Fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            journeyCardview.setOnClickListener { findNavController().navigate(R.id.action_mission_fragment_to_mission_detail_fragment1) }
            marsCardview.setOnClickListener { findNavController().navigate(R.id.action_mission_fragment_to_mission_detail_fragment2) }
            equipmentCardview.setOnClickListener { findNavController().navigate(R.id.action_mission_fragment_to_mission_detail_fragment3) }
            cardviewTwitter.setOnClickListener {  WebView(requireContext()).apply {
                    webChromeClient = WebChromeClient()
                    loadUrl("https://twitter.com/marscuriosity?lang=browser#night")
                }
            }
        }
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