package nl.rvbsoftdev.curiosityreporting.ui_fragment_destinations

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.MainActivity
import nl.rvbsoftdev.curiosityreporting.databinding.Fragment2MissionDetailBinding
import nl.rvbsoftdev.curiosityreporting.viewmodels.SharedViewModel

class MissionDetailFragment2 : Fragment() {

    private val mViewModel: SharedViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Mission Detail Fragment 2")
        (activity as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        val dataBinding = Fragment2MissionDetailBinding.inflate(inflater)
        dataBinding.sharedViewModel = mViewModel

        return dataBinding.root
    }

    /** Only allow portrait orientation. Content in Mission Detail Fragment not suitable for landscape orientation **/
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}



