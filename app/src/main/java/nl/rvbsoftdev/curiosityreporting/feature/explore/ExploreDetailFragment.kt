package nl.rvbsoftdev.curiosityreporting.feature.explore

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentExploreDetailBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity
import nl.rvbsoftdev.curiosityreporting.global.formatDate

/** Explore Detail Fragment that lets the user zoom in on the selected photo. The photo can also be shared or added/removed from the Room local database **/

class ExploreDetailFragment : BaseFragment<FragmentExploreDetailBinding>() {

    override val layout = R.layout.fragment_explore_detail
    override val firebaseTag = "Explore Detail Fragment"
    private val navigationActivity by lazy { activity as NavigationActivity }
    private val safeArgs by navArgs<ExploreDetailFragmentArgs>()
    private val viewModel: ExploreDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedPhoto.value = safeArgs.selectedPhoto
        binding.apply {

            exploreDetailViewModel = viewModel
            backButton.setOnClickListener { findNavController().navigateUp() }
            shareButton.setOnClickListener {
                if (requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
                } else sharePhoto()
            }

            favoriteButton.setOnClickListener {
                if (viewModel.selectedPhoto.value!!.isFavorite) {
                    viewModel.selectedPhoto.value!!.isFavorite = false
                    viewModel.removePhotoFromFavorites(viewModel.selectedPhoto.value!!)
                    navigationActivity.showStyledSnackbarMessage(requireView(),
                            text = getString(R.string.photo_removed_from_fav),
                            durationMs = 2500,
                            icon = R.drawable.icon_star)

                } else {
                    viewModel.selectedPhoto.value!!.isFavorite = true
                    viewModel.addPhotoToFavorites(viewModel.selectedPhoto.value!!)
                    navigationActivity.showStyledSnackbarMessage(requireView(),
                            text = getString(R.string.photo_add_to_fav),
                            durationMs = 2500,
                            icon = R.drawable.icon_star_selected)
                }
            }
        }
    }

    /** Uses Glide to convert the selected photo to a bitmap and share. Invokes requestPermission function to request runtime permission for storage access **/
    private fun sharePhoto() {
        try {

            if (!viewModel.selectedPhoto.value?.img_src.isNullOrEmpty()) {
                Glide.with(requireContext())
                        .asBitmap()
                        .load(viewModel.selectedPhoto.value?.img_src)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                try {
                                    val imagePath = MediaStore.Images.Media.insertImage(navigationActivity.contentResolver,
                                            resource, "Curiosity Mars Image " + viewModel.selectedPhoto.value?.earth_date, null)
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "image/*"
                                        putExtra(Intent.EXTRA_TEXT,"Check out this amazing photo NASA's Mars Rover Curiosity captured on ${formatDate(viewModel.selectedPhoto.value?.earth_date)}!")
                                        putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath)) }

                                    if (shareIntent.resolveActivity(navigationActivity.packageManager) != null) {
                                        startActivity(shareIntent)
                                    } else {
                                        navigationActivity.showStyledToastMessage(getString(R.string.no_app_to_share_photo))
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
        if (requestCode == REQUEST_CODE && (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            sharePhoto()
        } else {
            navigationActivity.showStyledToastMessage(getString(R.string.storage_access_required))
        }
    }

    companion object {
        const val REQUEST_CODE = 1
    }
}