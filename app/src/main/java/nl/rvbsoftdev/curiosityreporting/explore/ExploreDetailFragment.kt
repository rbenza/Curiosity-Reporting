package nl.rvbsoftdev.curiosityreporting.explore

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentExploreDetailBinding
import nl.rvbsoftdev.curiosityreporting.explore.ExploreDetailFragmentArgs
import nl.rvbsoftdev.curiosityreporting.global.*

/** Explore Detail Fragment that lets the user zoom in on the selected photo. The photo can also be shared or added/removed from the Room local database **/

class ExploreDetailFragment : BaseFragment<FragmentExploreDetailBinding>() {

    override val layout = R.layout.fragment_explore_detail
    override val firebaseTag = "Explore Detail Fragment"
    private val singleActivity by lazy { (activity as NavigationActivity) }
    private lateinit var viewModelFactory: ExploreDetailViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val application = requireNotNull(activity).application
        val photo = ExploreDetailFragmentArgs.fromBundle(arguments!!).selectedPhoto
        viewModelFactory = ExploreDetailViewModelFactory(photo, application)
        val viewModel = ViewModelProviders.of(
                this, viewModelFactory).get(ExploreDetailViewModel::class.java)

        binding.exploreDetailViewModel = viewModel
        /** a few simple Onclicklisteners with lambdas for single events instead of wiring it through the ViewModel with LiveData **/
        binding.backButton.setOnClickListener { singleActivity.onSupportNavigateUp() }
        binding.shareButton.setOnClickListener {
            if (requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), Companion.REQUEST_CODE)
            } else sharePhoto()
        }
        binding.favoriteButton.setOnClickListener {
            viewModel.addPhotoToFavorites(viewModel.selectedPhoto.value!!)
            singleActivity.showStyledSnackbarMessage(requireView(),
                    text = getString(R.string.photo_add_to_fav),
                    durationMs = 2500,
                    icon = R.drawable.icon_star_selected)
            showFavButton(true)
        }
        binding.favoriteButtonSelected.setOnClickListener {
            viewModel.removePhotoFromFavorites(viewModel.selectedPhoto.value!!)
            singleActivity.showStyledSnackbarMessage(requireView(),
                    text = getString(R.string.photo_removed_from_fav),
                    durationMs = 2500,
                    icon = R.drawable.icon_star)
            showFavButton(false)
        }
        viewModel.selectedPhoto.observe(
                viewLifecycleOwner,
                Observer {
                    if (viewModel.selectedPhoto.value?.id == viewModel.searchForPhotoInFavoritesDatabase(it)?.value?.id) {
                        showFavButton(true)
                    } else {
                        showFavButton(false)
                    }
                }
        )
    }

    /** function to reduce code duplication **/
    private fun showFavButton(show: Boolean) {
        when (show) {
            true -> {
                binding.favoriteButton.setInvisible()
                binding.favoriteButtonSelected.setVisible()
            }
            false -> {
                binding.favoriteButton.setVisible()
                binding.favoriteButtonSelected.setInvisible()
            }
        }
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

                                    val imagePath = MediaStore.Images.Media.insertImage(singleActivity.contentResolver,
                                            resource, "Curiosity Mars Image " + viewModel.selectedPhoto.value?.earth_date, null)
                                    val uri = Uri.parse(imagePath)

                                    val shareIntent = Intent(Intent.ACTION_SEND)
                                    shareIntent.type = "image/*"
                                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                                            "Check out this amazing photo NASA's Mars Rover Curiosity captured on ${SharedViewModel.DateFormatter.formatDate(viewModel.selectedPhoto.value?.earth_date)}!")
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                                    if (shareIntent.resolveActivity(singleActivity.packageManager) != null) {
                                        startActivity(shareIntent)
                                    } else {
                                        singleActivity.showStyledToastMessage("No app installed to share this photo!")
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
            Companion.REQUEST_CODE -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sharePhoto()
            }
            else -> singleActivity.showStyledToastMessage("Access to storage is required to share this photo")
        }
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}