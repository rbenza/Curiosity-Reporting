package nl.rvbsoftdev.curiosityreporting.global

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.databinding.CustomViewPhotoOverlayBinding
import nl.rvbsoftdev.curiosityreporting.feature.explore.ExploreViewModel
import nl.rvbsoftdev.curiosityreporting.feature.favorite.FavoritesViewModel

class PhotoOverlay @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomViewPhotoOverlayBinding

    init {
        View.inflate(context, R.layout.custom_view_photo_overlay, this)
        binding = CustomViewPhotoOverlayBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, this, true)
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setupClickListenersAndVm(viewModel: ViewModel, clickBack: (() -> Unit)? = null, clickShare: (() -> Unit)? = null, clickFavorite: (() -> Unit)? = null, clickDelete: (() -> Unit)? = null) {
        with(binding) {

            if (viewModel is ExploreViewModel) {
                exploreViewModel = viewModel
                photoInfoExplore.isVisible = true
                photoInfoFavorite.isGone = true
                setupFavorite(viewModel.selectedPhoto.value)
                favoriteButton.setOnClickListener {
                    isVisible = true
                    deleteButton.isGone = true
                    clickFavorite?.invoke()
                    val msg = if (viewModel.selectedPhoto.value?.isFavorite == true) "Removed from favorites" else "Added to favorites"
                    val icon = if (viewModel.selectedPhoto.value?.isFavorite == true) R.drawable.icon_star else R.drawable.icon_star_selected
                    binding.favoriteButton.setImageResource(icon)
                    (root.context as NavigationActivity).showStyledSnackbarMessage(this@PhotoOverlay, msg, 3000, icon)
                }
            } else {
                if (viewModel is FavoritesViewModel) {
                    favoritesViewModel = viewModel
                    favoriteButton.isGone = true
                    photoInfoExplore.isGone = true
                    photoInfoFavorite.isVisible = true
                    deleteButton.apply {
                        isVisible = true
                        setImageResource(R.drawable.icon_delete)
                        setOnClickListener {
                            clickDelete?.invoke()
                            (root.context as NavigationActivity).showStyledSnackbarMessage(this@PhotoOverlay, "Deleted photo, overlay will close in 3 seconds", 3000, R.drawable.icon_delete)
                        }
                    }
                }
            }
            backButton.setOnClickListener { clickBack?.invoke() }
            shareButton.setOnClickListener { clickShare?.invoke() }
        }
    }

    fun setInfoText(input: String? = "") {
        binding.photoInfoExplore.text = input
        binding.photoInfoFavorite.text = input
    }

    fun setupFavorite(photo: Photo?) {
        val favoriteIcon = if (photo?.isFavorite == true) R.drawable.icon_star_selected else R.drawable.icon_star
        binding.favoriteButton.setImageResource(favoriteIcon)
    }
}