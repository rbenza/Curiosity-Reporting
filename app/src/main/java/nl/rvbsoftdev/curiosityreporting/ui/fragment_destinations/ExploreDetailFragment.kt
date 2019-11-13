package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.analytics.FirebaseAnalytics
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.adapters.viewInvisible
import nl.rvbsoftdev.curiosityreporting.adapters.viewVisible
import nl.rvbsoftdev.curiosityreporting.viewmodels.SharedViewModel
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentExploreDetailBinding
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity
import nl.rvbsoftdev.curiosityreporting.viewmodels.ExploreDetailViewModel
import nl.rvbsoftdev.curiosityreporting.viewmodels.ExploreDetailViewModelFactory

/** Explore Detail Fragment that lets the user zoom in on the selected photo. The photo can also be shared or added/removed from the Room local database **/

const val REQUEST_CODE = 1

class ExploreDetailFragment : Fragment() {

    lateinit var viewModelFactory: ExploreDetailViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Explore Detail Fragment")
        (activity as SingleActivity).firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

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
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
            } else sharePhoto()
        }
        dataBinding.backButton.setOnClickListener { (activity as SingleActivity).onSupportNavigateUp() }
        dataBinding.favoriteButton.setOnClickListener {
            viewModel.addPhotoToFavorites(viewModel.selectedPhoto.value!!)
            (activity as SingleActivity).showStyledSnackbarMessage(requireView(), getString(R.string.photo_add_to_fav), null, 2500, R.drawable.icon_star_selected, null)
            it.viewInvisible()
            dataBinding.favoriteButtonSelected.viewVisible()
        }

        dataBinding.favoriteButtonSelected.setOnClickListener {
            viewModel.removePhotoFromFavorites(viewModel.selectedPhoto.value!!)
            (activity as SingleActivity).showStyledSnackbarMessage(requireView(), getString(R.string.photo_removed_from_fav), null, 2500, R.drawable.icon_star, null)
            it.visibility = View.INVISIBLE
            dataBinding.favoriteButton.viewVisible()
        }

        viewModel.selectedPhoto.observe(this, Observer {
            val validatePhoto = viewModel.searchForPhotoInFavoritesDatabase(it)
            if (viewModel.selectedPhoto.value?.id == validatePhoto?.value?.id) {
                dataBinding.favoriteButtonSelected.viewVisible()
                dataBinding.favoriteButton.viewInvisible()
            } else {
                dataBinding.favoriteButtonSelected.viewInvisible()
                dataBinding.favoriteButton.viewVisible()
            }
        })

        return dataBinding.root
    }
    /** Uses Glide to convert the selected photo to a bitmap and share. Invokes requestPermission function to request runtime permission for storage access **/
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
                                        (activity as SingleActivity).showStyledToastMessage("No app installed to share this photo!")
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sharePhoto()
            }
            else -> (activity as SingleActivity).showStyledToastMessage("Access to storage is required to share this photo")
        }
    }
}


