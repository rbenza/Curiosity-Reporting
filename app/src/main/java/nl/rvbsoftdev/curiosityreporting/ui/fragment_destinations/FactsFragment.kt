package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentFactsBinding
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity

/** Facts Fragment with information about Mars**/

class FactsFragment : BaseFragment<FragmentFactsBinding>() {

    override val layout = R.layout.fragment_facts
    override val firebaseTag = "Facts Fragment"
    private val singleActivity by lazy { activity as SingleActivity }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** simple Onclicklistener with lambda for single event instead of wiring it through a ViewModel with LiveData (overkill here) **/
        binding.spacexImg.setOnClickListener { visitSpaceXWebsite() }
    }

    private fun visitSpaceXWebsite() {
        val launchBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.spacex.com/mars"))
        if (launchBrowser.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(launchBrowser)
        } else {
            singleActivity.showStyledToastMessage(getString(R.string.no_internet_app))
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