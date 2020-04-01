package nl.rvbsoftdev.curiosityreporting.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentMoreBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity

/** More Fragment where the user can navigate to the fragments; 'About', 'Settings' and share the app or visit the NASA website.
 * Uses Legacy ListView with findViewById since it's a very small list with 4 static items  **/

class MoreFragment : BaseFragment<FragmentMoreBinding>() {

    override val layout = R.layout.fragment_more
    override val firebaseTag = "More Fragment"
    private val singleActivity by lazy { (activity as NavigationActivity) }
    private val viewModel: MoreViewModel by lazy { ViewModelProviders.of(this).get(MoreViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listviewMore.apply {
            adapter = ListViewAdapter(requireContext(), viewModel.moreItemslist)
            setOnItemClickListener { _, _, position, _ ->
                when (this.getItemAtPosition(position)) {
                    viewModel.moreItemslist[0] -> visitNasaWebsite()
                    viewModel.moreItemslist[1] -> shareTheApp()
                    viewModel.moreItemslist[2] -> findNavController().navigate(R.id.action_more_fragment_to_settings_fragment)
                    viewModel.moreItemslist[3] -> findNavController().navigate(R.id.action_more_fragment_to_about_fragment)
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