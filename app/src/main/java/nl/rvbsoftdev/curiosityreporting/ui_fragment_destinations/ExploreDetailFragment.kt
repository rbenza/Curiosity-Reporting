package nl.rvbsoftdev.curiosityreporting.ui_fragment_destinations

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.MainActivity
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.viewmodels.SharedViewModel
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentExploreDetailBinding
import nl.rvbsoftdev.curiosityreporting.viewmodels.ExploreDetailViewModel
import nl.rvbsoftdev.curiosityreporting.viewmodels.ExploreDetailViewModelFactory

/** Explore Detail Fragment that lets the user zoom in on the selected photo. The photo can also be shared or added/removed from the Room local database **/

lateinit var viewModelFactory: ExploreDetailViewModelFactory

const val REQUEST_CODE = 1

class ExploreDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Explore Detail Fragment")
        (activity as MainActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        val application = requireNotNull(activity).application
        val dataBinding = FragmentExploreDetailBinding.inflate(inflater)
        dataBinding.lifecycleOwner = this
        val photo = ExploreDetailFragmentArgs.fromBundle(arguments!!).selectedPhoto
        viewModelFactory = ExploreDetailViewModelFactory(photo, application)
        val viewModel = ViewModelProviders.of(
                this, viewModelFactory).get(ExploreDetailViewModel::class.java)

        dataBinding.exploreDetailViewModel = viewModel

        /** a few simple Onclicklisteners with lambdas for single events instead of wiring it through the ViewModel with LiveData **/

        dataBinding.shareButton.setOnClickListener {
            if (requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requireActivity().requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
            } else sharePhoto()
        }
        dataBinding.backButton.setOnClickListener { (activity as MainActivity).onSupportNavigateUp() }
        dataBinding.favoriteButton.setOnClickListener {
            viewModel.addPhotoToFavorites(viewModel.selectedPhoto.value!!)
            (activity as MainActivity).showStyledSnackbarMessage(requireView(), getString(R.string.photo_add_to_fav), null, 2500, R.drawable.icon_star_selected, null)
            it.visibility = View.GONE
            dataBinding.favoriteButtonSelected.visibility = View.VISIBLE

        }

        dataBinding.favoriteButtonSelected.setOnClickListener {
            viewModel.removePhotoFromFavorites(viewModel.selectedPhoto.value!!)
            (activity as MainActivity).showStyledSnackbarMessage(requireView(), getString(R.string.photo_removed_from_fav), null, 2500, R.drawable.icon_star, null)
            it.visibility = View.GONE
            dataBinding.favoriteButton.visibility = View.VISIBLE
        }

        return dataBinding.root
    }


    private fun sharePhoto() {
        try {
            val viewModel = ViewModelProviders.of(
                    this, viewModelFactory).get(ExploreDetailViewModel::class.java)

            if (!viewModel.selectedPhoto.value?.img_src.isNullOrEmpty()) {
                Glide.with(requireContext())
                        .asBitmap()
                        .load(viewModel.selectedPhoto.value?.img_src)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                try {

                                val imagePath = MediaStore.Images.Media.insertImage(requireActivity().contentResolver, resource, "Curiosity Mars Image " + viewModel.selectedPhoto.value?.earth_date, null)
                                val uri = Uri.parse(imagePath)

                                val shareIntent = Intent(Intent.ACTION_SEND)
                                shareIntent.type = "image/*"
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing photo NASA's Mars Rover Curiosity captured on ${SharedViewModel.DateFormatter.formatDate(viewModel.selectedPhoto.value?.earth_date)}!")
                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                                if (shareIntent.resolveActivity(requireActivity().packageManager) != null) {
                                    startActivity(shareIntent)
                                } else {
                                    (activity as MainActivity).showStyledToastMessage("No app installed to share this photo!")
                                }} catch (e: Exception) { }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
            }
        } catch (e: Exception) {
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sharePhoto()
            }
            else -> (activity as MainActivity).showStyledToastMessage("Access to storage is required to share this photo")
        }
    }
}


