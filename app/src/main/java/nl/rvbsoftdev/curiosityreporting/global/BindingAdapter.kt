package nl.rvbsoftdev.curiosityreporting.global

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import nl.rvbsoftdev.curiosityreporting.R

/** Custom databinding adapters to bind data to different views.
 * By setting the views as lifecycle owner the data is then observed and the UI updates automatically when the data changes (see BaseFragment in UI folder) **/


@BindingAdapter("imageUrl")
fun ImageView.loadImageUrl(imgUrl: String?) {

    val loadingSpinner = CircularProgressDrawable(context).apply {
        strokeWidth = 4f
        centerRadius = 18f
        setColorSchemeColors(context.getColor(R.color.DeepOrange))
        start()
    }

    val pictureQualitySetting = when (PreferenceManager.getDefaultSharedPreferences(context).getString("picture_quality", "High")) {
        "High" -> 100
        "Normal" -> 75
        else -> 25
    }

    Glide.with(context)
            .load(imgUrl)
            .encodeQuality(pictureQualitySetting)
            .apply(RequestOptions()
                    .placeholder(loadingSpinner)
                    .error(R.drawable.icon_broken_image))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            /** no Room database needed for NetworkPhotos, very small chance users loads same photos twice (360k photos in NASA db).
             * Glide cache impl works when user selects same date twice with datepicker. Max cache size 250MB **/
            .into(this)
}

@BindingAdapter("setImageResource")
fun ImageView.setImageResource(resource: Int) = setImageResource(resource)

@BindingAdapter("isVisible")
fun View.setVisibility(value: Boolean) {
    isVisible = value
}

