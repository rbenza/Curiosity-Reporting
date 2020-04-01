package nl.rvbsoftdev.curiosityreporting.favorite

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentFavoritesDetailBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity
import nl.rvbsoftdev.curiosityreporting.global.SharedViewModel
import nl.rvbsoftdev.curiosityreporting.favorite.FavoritesDetailFragmentArgs

/** Favorites Detail Fragment that lets the user zoom in on the selected photo. The photo can also be shared or removed from favorites**/

class FavoritesDetailFragment : BaseFragment<FragmentFavoritesDetailBinding>() {
    
    override val layout = R.layout.fragment_favorites_detail
    private val REQUEST_CODE: Int = 1
    override val firebaseTag = "Favorites Detail Fragment"
    private lateinit var mViewModelFactory: FavoritesDetailViewModelFactory
    private val singleActivity by lazy { activity as NavigationActivity }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val application = requireNotNull(activity).application
        val favoritePhoto = FavoritesDetailFragmentArgs.fromBundle(arguments!!).selectedPhoto
        mViewModelFactory = FavoritesDetailViewModelFactory(favoritePhoto, application)
        val viewModel = ViewModelProviders.of(
                this, mViewModelFactory).get(FavoritesDetailViewModel::class.java)

        binding.favoritesDetailViewModel = viewModel

        /** some simple Onclicklisteners with lambda for single events instead of wiring it through a ViewModel with LiveData **/
        binding.shareButton.setOnClickListener {
            if (requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
            } else sharePhoto()
        }
        binding.backButton.setOnClickListener { singleActivity.onSupportNavigateUp() }
        binding.deleteFavoritePhoto.setOnClickListener {
            viewModel.removePhotoFromFavorites(viewModel.selectedPhoto.value!!)
            singleActivity.showStyledSnackbarMessage(requireView(),
                    text = getString(R.string.photo_removed_from_fav),
                    durationMs = 2500,
                    icon = R.drawable.icon_delete)
        }
    }

    /** Uses Glide to convert the selected photo to a bitmap and share.
     * Invokes requestPermission function to request runtime permission for storage access **/

    private fun sharePhoto() {
        try {
            val viewModel = ViewModelProviders.of(
                    this, mViewModelFactory).get(FavoritesDetailViewModel::class.java)

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
                                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                                            "Check out this amazing photo NASA's Mars Rover Curiosity captured on ${SharedViewModel.DateFormatter.formatDate(viewModel.selectedPhoto.value?.earth_date)}!")
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                                    if (shareIntent.resolveActivity(requireActivity().packageManager) != null) {
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
            REQUEST_CODE -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sharePhoto()
            }
            else -> singleActivity.showStyledToastMessage(getString(R.string.disk_access_required))
        }
    }
}