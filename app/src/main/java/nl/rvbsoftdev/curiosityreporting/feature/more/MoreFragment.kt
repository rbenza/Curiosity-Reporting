package nl.rvbsoftdev.curiosityreporting.feature.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentMoreBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity

/** More Fragment where the user can navigate to the fragments; 'About', 'Settings' and share the app or visit the NASA website.
 **/

class MoreFragment : BaseFragment<FragmentMoreBinding>() {

    override val layout = R.layout.fragment_more
    override val firebaseTag = "More Fragment"
    private val navigationActivity by lazy { (activity as NavigationActivity) }
    private val viewModel: MoreViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMoreItems.adapter = MoreAdapter {
            when (it.id) {
                0 -> visitNasaWebsite()
                1 -> shareTheApp()
                2 -> findNavController().navigate(R.id.action_more_fragment_to_settings_fragment)
                3 -> findNavController().navigate(R.id.action_more_fragment_to_about_fragment)
            }
        }.apply { submitList(viewModel.moreItemslist) }
    }

    private fun visitNasaWebsite() {
        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nasa.gov/mission_pages/msl/index.html")).apply {
            if (this.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(this)
            } else {
                navigationActivity.showStyledToastMessage(getString(R.string.no_internet_app))
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
                navigationActivity.showStyledToastMessage("No app to share found!\n" + "\n" + "Go to ${playStoreLink} to share the app")
            }
        }
    }
}