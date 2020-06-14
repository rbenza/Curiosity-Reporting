package nl.rvbsoftdev.curiosityreporting.global

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.data.NasaApiConnectionStatus
import nl.rvbsoftdev.curiosityreporting.data.Photo

/** Custom databinding adapters to bind data to different views.
 * By setting the views as lifecycle owner the data is then observed and the UI updates automatically when the data changes (see BaseFragment in UI folder) **/


@BindingAdapter("imageUrl")
fun ImageView.loadImageUrl(imgUrl: String?) {

    val loadingSpinner = CircularProgressDrawable(context).apply {
        strokeWidth = 4f
        centerRadius = 20f
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("theme", "Dark") == "Dark") {
            setColorSchemeColors(context.getColor(R.color.DeepOrange))
        } else {
            setColorSchemeColors(context.getColor(R.color.DarkBrown))
        }
        start()
    }

    var pictureQualitySetting = 100
    when (PreferenceManager.getDefaultSharedPreferences(context).getString("picture_quality", "High")) {
        "High" -> pictureQualitySetting = 100
        "Normal" -> pictureQualitySetting = 75
        "Low" -> pictureQualitySetting = 25
    }
    Glide.with(context)
            .load(imgUrl)
            .encodeQuality(pictureQualitySetting)
            .apply(RequestOptions()
                    .placeholder(loadingSpinner)
                    .error(R.drawable.icon_broken_image))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            /** no Room database needed for NetworkPhotos, very small chance users loads same photos twice (360k photos in NASA db).
             * Glide cache impl works when user selects same date twice with Datepicker fragment. Max cache size 250mb **/
            .into(this)
}

@BindingAdapter("setImageResource")
fun ImageView.setImageResource(resource: Int) = setImageResource(resource)

@BindingAdapter("isVisible")
fun View.setVisibility(value: Boolean) {
    isVisible = value
}

@BindingAdapter("imageConnectionStatus")
fun ImageView.imageConnectionStatus(connectionStatus: NasaApiConnectionStatus?) {
    when (connectionStatus) {
        NasaApiConnectionStatus.DONE, NasaApiConnectionStatus.LOADING -> setGone()
        NasaApiConnectionStatus.ERROR -> {
            setVisible()
            setImageResource(R.drawable.icon_connection_error)
        }
        NasaApiConnectionStatus.NODATA -> {
            setVisible()
            setImageResource(R.drawable.icon_database_no_data)
        }
    }
}

@BindingAdapter("textConnectionStatus")
fun TextView.textConnectionStatus(connectionStatus: NasaApiConnectionStatus?) {
    when (connectionStatus) {
        NasaApiConnectionStatus.LOADING -> {
            setVisible()
            text = context.getText(R.string.connecting_nasa_db)
        }
        NasaApiConnectionStatus.ERROR -> {
            setVisible()
            text = context.getText(R.string.no_conn_nasa_db)
        }
        NasaApiConnectionStatus.NODATA -> {
            setVisible()
            text = context.getText(R.string.no_photos_in_nasa_db)
        }
        NasaApiConnectionStatus.DONE -> {
            setGone()
        }
    }
}

@BindingAdapter("favoriteText")
fun TextView.favoriteText(photo: List<Photo>?) = viewVisibleOrGone(photo.isNullOrEmpty())


@BindingAdapter("favoriteImg")
fun ImageView.favoriteImg(photo: List<Photo>?) = viewVisibleOrGone(photo.isNullOrEmpty())





