package nl.rvbsoftdev.curiosityreporting.ui.fragment_destinations

import android.content.Context
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
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentFavoritesDetailBinding
import nl.rvbsoftdev.curiosityreporting.ui.single_activity.SingleActivity
import nl.rvbsoftdev.curiosityreporting.viewmodels.FavoritesDetailViewModel
import nl.rvbsoftdev.curiosityreporting.viewmodels.FavoritesDetailViewModelFactory
import nl.rvbsoftdev.curiosityreporting.viewmodels.SharedViewModel

/** Favorites Detail Fragment that lets the user zoom in on the selected photo. The photo can also be shared or removed from favorites**/

class FavoritesDetailFragment : Fragment() {

    val REQUEST_CODE: Int = 1

    lateinit var mViewModelFactory: FavoritesDetailViewModelFactory

    private val singleActivity by lazy { activity as SingleActivity }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Favorites Detail Fragment")
        singleActivity.firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application
        val dataBinding = FragmentFavoritesDetailBinding.inflate(inflater)
        dataBinding.lifecycleOwner = this
        val favoritePhoto = FavoritesDetailFragmentArgs.fromBundle(arguments!!).selectedPhoto
        mViewModelFactory = FavoritesDetailViewModelFactory(favoritePhoto, application)
        val viewModel = ViewModelProviders.of(
                this, mViewModelFactory).get(FavoritesDetailViewModel::class.java)

        dataBinding.favoritesDetailViewModel = viewModel

        /** some simple Onclicklisteners with lambda for single events instead of wiring it through a ViewModel with LiveData **/
        dataBinding.shareButton.setOnClickListener {
            if (requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
            } else sharePhoto()
        }
        dataBinding.backButton.setOnClickListener { singleActivity.onSupportNavigateUp() }
        dataBinding.deleteFavoritePhoto.setOnClickListener {
            viewModel.removePhotoFromFavorites(viewModel.selectedPhoto.value!!)
            singleActivity.showStyledSnackbarMessage(requireView(),
                    text = getString(R.string.photo_removed_from_fav),
                    durationMs = 2500,
                    icon = R.drawable.icon_delete)
        }
        return dataBinding.root
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

