package nl.rvbsoftdev.curiosityreporting.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.MainActivity
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentFactsBinding

/** Facts Fragment with information about Mars**/

class FactsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Facts Fragment")
        (activity as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        val dataBinding = FragmentFactsBinding.inflate(inflater)

        /** simple Onclicklistener with lambda for single event instead of wiring it through a ViewModel with LiveData (overkill here) **/
        dataBinding.spacexImg.setOnClickListener { visitSpaceXWebsite() }

        return dataBinding.root
    }

    private fun visitSpaceXWebsite() {
        val launchBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.spacex.com/mars"))
        if (launchBrowser.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(launchBrowser)
        } else {
            (activity as MainActivity).showStyledToastMessage(getString(R.string.no_internet_app))
        }
    }

    /** Only allow portrait orientation. Content in Facts Fragment not suitable for landscape orientation **/
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}



