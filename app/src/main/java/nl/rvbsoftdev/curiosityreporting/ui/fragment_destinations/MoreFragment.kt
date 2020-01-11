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
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentMoreBinding
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentMoreBindingImpl
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity
import nl.rvbsoftdev.curiosityreporting.viewmodels.MoreViewModel

/** More Fragment where the user can navigate to the fragments; 'About', 'Settings' and share the app or visit the NASA website.
 * Uses Legacy ListView with findViewById since it's a very small list with 4 static items  **/

class MoreFragment : BaseFragment<FragmentMoreBinding>() {

    override val layout = R.layout.fragment_more
    override val firebaseTag = "More Fragment"
    private val singleActivity by lazy { (activity as SingleActivity) }
    private val mViewModel: MoreViewModel by lazy { ViewModelProviders.of(this).get(MoreViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listviewMore.apply {
            adapter = ListViewAdapter(requireContext(), mViewModel.moreItemslist)
            setOnItemClickListener { _, _, position, _ ->
                when (this.getItemAtPosition(position)) {
                    mViewModel.moreItemslist[0] -> visitNasaWebsite()
                    mViewModel.moreItemslist[1] -> shareTheApp()
                    mViewModel.moreItemslist[2] -> findNavController().navigate(R.id.action_more_fragment_to_settings_fragment)
                    mViewModel.moreItemslist[3] -> findNavController().navigate(R.id.action_more_fragment_to_about_fragment)
                }
            }
        }
    }

    private fun visitNasaWebsite() {
        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nasa.gov/mission_pages/msl/index.html")).apply {
            if (this.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(this)
            } else {
                singleActivity.showStyledToastMessage(getString(R.string.no_internet_app))
            }
        }
    }

    private fun shareTheApp() {
        val playStoreLink = "https://play.google.com/store/apps/details?id=nl.rvbsoftdev.curiosityreporting"
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this cool app that let's you Explore Mars!\n\nGet it at ${playStoreLink}")
            if (this.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(this)
            } else {
                singleActivity.showStyledToastMessage("No app to share found!\n" + "\n" + "Go to ${playStoreLink} to share the app")
            }
        }
    }


}