package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentAboutBinding
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity

/** About Fragment that provides information about the app and the developer. **/

class AboutFragment : Fragment() {

    private val singleActivity by lazy { (activity as SingleActivity) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "About Fragment")
        singleActivity.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        val dataBinding = FragmentAboutBinding.inflate(inflater)
        dataBinding.lifecycleOwner = this

        /** simple Onclicklisteners with lambda for single events instead of wiring it through a ViewModel with LiveData **/
        dataBinding.sendEmailButton.setOnClickListener { sendEmail() }
        dataBinding.githubImg.setOnClickListener { visitGitHubRepository() }
        dataBinding.privacyImg.setOnClickListener { findNavController().navigate(AboutFragmentDirections.actionAboutFragmentToPrivacyPolicyFragment()) }

        return dataBinding.root
    }

    private fun sendEmail() {
        val startEmailApp = Intent(Intent.ACTION_SEND)
        startEmailApp.type = "message/rfc2822"
        startEmailApp.putExtra(Intent.EXTRA_EMAIL, Array(1) { "rvbsoftdev@gmail.com" })
        startEmailApp.putExtra(Intent.EXTRA_SUBJECT, "The 'Curiosity Reporting' Android app")
        if (startEmailApp.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(startEmailApp)
        } else {
            singleActivity.showStyledSnackbarMessage(requireView(),
                    text = getString(R.string.email_toast1),
                    durationMs = 8000,
                    icon = R.drawable.icon_email)
        }
    }

    private fun visitGitHubRepository() {
        val launchBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rbenza/Curiosity-Reporting/blob/master/README.md"))
        if (launchBrowser.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(launchBrowser)
        } else {
            singleActivity.showStyledToastMessage("No internet app found!\n\nGo to: github.com/rbenza/Curiosity-Reporting")
        }
    }

    /** Only allow portrait orientation. Content in About Fragment not suitable for landscape orientation **/
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}






