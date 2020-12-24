package nl.rvbsoftdev.curiosityreporting.feature.favorite

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.stfalcon.imageviewer.StfalconImageViewer
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.databinding.FragmentFavoritesBinding
import nl.rvbsoftdev.curiosityreporting.global.BaseFragment
import nl.rvbsoftdev.curiosityreporting.global.NavigationActivity
import nl.rvbsoftdev.curiosityreporting.global.PhotoOverlay
import nl.rvbsoftdev.curiosityreporting.global.SharedViewModel
import timber.log.Timber

/** Favorites Fragment that provides a unique List of Photos sorted by most recent earth date contained in the local room database **/

class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>() {

    override val layout = R.layout.fragment_favorites
    private val REQUEST_CODE: Int = 1
    override val firebaseTag = "Favorite Fragment"
    private val navigationActivity by lazy { activity as NavigationActivity }
    private val viewModel: FavoritesViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var builder: StfalconImageViewer<Photo>
    private lateinit var photoOverlay: PhotoOverlay

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favoritesViewModel = viewModel

        val favoritePhotoAdapter = FavoritePhotoAdapter(viewLifecycleOwner, viewModel) { photo, position ->
            viewModel.selectedFavoritePhoto.value = photo
            photoOverlay = PhotoOverlay(requireContext()).apply {
                setupClickListenersAndVm(viewModel, { builder.close() }, { sharePhoto() }, clickDelete = {
                    viewModel.removePhotoFromFavorites(photo)
                    sharedViewModel.deletedFavoritePhoto.value = photo
                    binding.root.postDelayed({ builder.close() }, 3000) })
            }
            viewModel.favoritePhotos.value?.let { setupSwipeImageViewer(it, position) }
        }

        viewModel.favoritePhotos.observe(viewLifecycleOwner) { listOfPhotos ->
            binding.recyclerviewPhotoFavorites.adapter = favoritePhotoAdapter.apply { submitList(listOfPhotos) }
            setHasOptionsMenu(!listOfPhotos.isNullOrEmpty())
            if (listOfPhotos.isNullOrEmpty()) {
                sharedViewModel.deletedAllFavorites.value = true
            }
        }

        /** Lets the user select a list or grid as preference **/
        var listOrGrid = 1
        if (PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("favorites_photo_layout", "List") == "Grid") {
            listOrGrid = 3
        }
        binding.recyclerviewPhotoFavorites.layoutManager = GridLayoutManager(requireContext(), listOrGrid)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_favorites_menu, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                activity?.let {
                    AlertDialog.Builder(it).apply {
                        setView(android.R.layout.select_dialog_item)
                        setTitle("Delete all photos from favorites?")
                        setPositiveButton("OK") { _, _ ->
                            viewModel.removeAllPhotoFromFavorites()
                            sharedViewModel.deletedAllFavorites.value = true
                            (it as NavigationActivity).showStyledSnackbarMessage(requireView(),
                                    text = "Favorite photo(s) deleted!",
                                    durationMs = 3000,
                                    icon = R.drawable.icon_delete_all)
                        }
                        setNegativeButton("Cancel") { _, _ -> }
                        show()
                    }
                }
            }
        }
        return true
    }

    private fun setupSwipeImageViewer(list: List<Photo>, position: Int) {
        builder = StfalconImageViewer.Builder(requireContext(), list) { view, img ->
            Glide.with(requireContext()).load(img.img_src).into(view)
        }
                .withImageChangeListener {
                    viewModel.selectedFavoritePhoto.value = viewModel.favoritePhotos.value?.get(it)
                    photoOverlay.setInfoText(getString(
                            R.string.explore_detail_photo_taken_on,
                            viewModel.selectedFavoritePhoto.value?.camera?.full_name,
                            viewModel.formatStringDate(viewModel.selectedFavoritePhoto.value?.earth_date ?: ""),
                            viewModel.selectedFavoritePhoto.value?.sol))
                }
                .withStartPosition(position)
                .withOverlayView(photoOverlay)
                .show()
    }

    /** Uses Glide to convert the selected photo to a bitmap and share.
     * Invokes requestPermission function to request runtime permission for storage access **/

    private fun sharePhoto() {
        try {
            if (!viewModel.selectedFavoritePhoto.value?.img_src.isNullOrEmpty()) {
                Glide.with(requireContext())
                        .asBitmap()
                        .load(viewModel.selectedFavoritePhoto.value?.img_src)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                val imagePath = MediaStore.Images.Media.insertImage(
                                        requireActivity().contentResolver, resource, "Curiosity Mars Image " + viewModel.selectedFavoritePhoto.value?.earth_date, null)
                                val uri = Uri.parse(imagePath)
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "image/*"
                                    putExtra(Intent.EXTRA_TEXT,
                                            "Check out this amazing photo NASA's Mars Rover Curiosity captured on ${viewModel.selectedFavoritePhoto.value?.earth_date}!")
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                }
                                if (shareIntent.resolveActivity(requireActivity().packageManager) != null) {
                                    startActivity(shareIntent)
                                } else {
                                    navigationActivity.showStyledToastMessage("No app installed to share this photo!")
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sharePhoto()
            }
            else -> navigationActivity.showStyledToastMessage(getString(R.string.disk_access_required))
        }
    }
}

