package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.content.pm.ActivityInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.viewmodels.SharedViewModel
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentMissionBinding
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity

/** 'MissionFragment' first screen the user sees. Provides navigation to MissionDetailFragments 1, 2 and 3 and a Twitter webview. **/

class MissionFragment : Fragment() {

    private val mViewModel: SharedViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Mission Fragment")
        (activity as SingleActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        val dataBinding = FragmentMissionBinding.inflate(inflater)
        dataBinding.sharedViewModel = mViewModel

        /** A few simple Onclicklisteners with lambda for single event instead of wiring it through a ViewModel with LiveData **/

        dataBinding.cardviewTwitter.setOnClickListener {
            val webview = WebView(requireContext())
            webview.webChromeClient = WebChromeClient()
            webview.loadUrl("https://twitter.com/marscuriosity?lang=browser#night")
        }

        dataBinding.journeyCardview.setOnClickListener { findNavController().navigate(R.id.action_mission_fragment_to_mission_detail_fragment1) }
        dataBinding.marsCardview.setOnClickListener { findNavController().navigate(R.id.action_mission_fragment_to_mission_detail_fragment2) }
        dataBinding.equipmentCardview.setOnClickListener { findNavController().navigate(R.id.action_mission_fragment_to_mission_detail_fragment3) }

        return dataBinding.root
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










