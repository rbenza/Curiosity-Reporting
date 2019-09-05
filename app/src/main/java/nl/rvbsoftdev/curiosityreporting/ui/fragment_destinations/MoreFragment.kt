package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_more.*
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.adapters.ListViewAdapter
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity
import nl.rvbsoftdev.curiosityreporting.viewmodels.MoreViewModel

/** More Fragment where the user can navigate to the fragments; 'About', 'Settings' and share the app or visit the NASA website.
 * Uses Legacy ListView with findViewById since it's a very small list with 4 static items  **/

class MoreFragment : Fragment() {

    private val mViewModel: MoreViewModel by lazy {
        ViewModelProviders.of(this).get(MoreViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "More Fragment")
        (activity as SingleActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = listview_more

        listView.adapter = context?.let { ListViewAdapter(it, mViewModel.moreItemslist) }
        listView.setOnItemClickListener { _, _, position, _ ->
            when (listView.getItemAtPosition(position)) {
                mViewModel.moreItemslist[0] -> visitNasaWebsite()
                mViewModel.moreItemslist[1] -> shareTheApp()
                mViewModel.moreItemslist[2] -> findNavController().navigate(R.id.action_more_fragment_to_settings_fragment)
                mViewModel.moreItemslist[3] -> findNavController().navigate(R.id.action_more_fragment_to_about_fragment)
            }
        }
    }

    private fun visitNasaWebsite() {
        val launchBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nasa.gov/mission_pages/msl/index.html"))
        if (launchBrowser.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(launchBrowser)
        } else {
            (activity as SingleActivity).showStyledToastMessage(getString(R.string.no_internet_app))
        }
    }

    private fun shareTheApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val playStoreLink = "https://play.google.com/store/apps/details?id=nl.rvbsoftdev.curiosityreporting"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this cool app that let's you Explore Mars!\n\nGet it at ${playStoreLink}")
        if (shareIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(shareIntent)
        } else {
            (activity as SingleActivity).showStyledToastMessage("No app to share found!\n" +
                    "\n" +
                    "Go to ${playStoreLink} to share the app")
        }
    }
}

