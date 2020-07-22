package nl.rvbsoftdev.curiosityreporting.global

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.databinding.CustomViewPhotoOverlayBinding
import nl.rvbsoftdev.curiosityreporting.feature.explore.ExploreViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class PhotoOverlay @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomViewPhotoOverlayBinding

    init {
        View.inflate(context, R.layout.custom_view_photo_overlay, this)
        binding = CustomViewPhotoOverlayBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, this, true)
        setBackgroundColor(Color.TRANSPARENT)
    }


    fun setupClickListenersAndVm(viewModel: ExploreViewModel, clickBack: (() -> Unit)? = null, clickShare: (() -> Unit)? = null, clickFavorite: (() -> Unit)? = null) {
        with(binding) {
            exploreViewModel = viewModel
            setupFavorite(viewModel.selectedPhoto.value)
            backButton.setOnClickListener { clickBack?.invoke() }
            shareButton.setOnClickListener { clickShare?.invoke() }
            favoriteButton.setOnClickListener {
                clickFavorite?.invoke()
                val msg = if (viewModel.selectedPhoto.value?.isFavorite == true) "Removed from favorites" else "Added to favorites"
                val icon = if (viewModel.selectedPhoto.value?.isFavorite == true) R.drawable.icon_star else R.drawable.icon_star_selected
                binding.favoriteButton.setImageResource(icon)
                (this.root.context as NavigationActivity).showStyledSnackbarMessage(this@PhotoOverlay, msg, 3000, icon)
            }
        }
    }

    fun setInfoText(input: String? = "") {
        binding.photoInfo.text = input
    }

    fun setupFavorite(photo: Photo?) {
        val favoriteIcon = if (photo?.isFavorite == true) R.drawable.icon_star_selected else R.drawable.icon_star
        binding.favoriteButton.setImageResource(favoriteIcon)
    }
}