package nl.rvbsoftdev.curiosityreporting.global

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.databinding.CustomViewPhotoOverlayBinding
import nl.rvbsoftdev.curiosityreporting.feature.explore.ExploreViewModel

class PhotoOverlay @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: CustomViewPhotoOverlayBinding

    init {
        View.inflate(context, R.layout.custom_view_photo_overlay, this)
        binding = CustomViewPhotoOverlayBinding.inflate(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, this, true)
        setBackgroundColor(Color.TRANSPARENT)
        val favoriteIcon = if(true) R.drawable.icon_star_selected else R.drawable.icon_star
        binding.favoriteButton.setImageResource(favoriteIcon)
    }


    fun setupClickListenersAndVm(vw: ExploreViewModel, clickBack: (() -> Unit)? = null, clickShare: (() -> Unit)? = null, clickFavorite: (() -> Unit)? = null) {
        with(binding) {
            exploreViewModel = vw
            backButton.setOnClickListener { clickBack?.invoke() }
            shareButton.setOnClickListener { clickShare?.invoke() }
            favoriteButton.setOnClickListener { clickFavorite?.invoke()

                (this.root.context as NavigationActivity).showStyledSnackbarMessage(this@PhotoOverlay, "Added to favorites", 3000, R.drawable.icon_star)
            }
        }
    }

    fun setupFavorite(photo: Photo) {
            val favoriteIcon = if(photo.isFavorite) R.drawable.icon_star_selected else R.drawable.icon_star
            binding.favoriteButton.setImageResource(favoriteIcon)
        }
    }