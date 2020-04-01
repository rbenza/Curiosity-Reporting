package nl.rvbsoftdev.curiosityreporting.global

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.domain.Photo
import nl.rvbsoftdev.curiosityreporting.explore.ExplorePhotoAdapter
import nl.rvbsoftdev.curiosityreporting.favorite.FavoritesPhotoAdapter

/** Custom databinding adapters to bind data to different views.
 * By setting the fragment in which the views are present as lifecycle owner the data is then observed
 * and the UI updates automatically when the data changes (see BaseFragment in UI folder) **/

@BindingAdapter("explorePhotos")
fun RecyclerView.explorePhotos(data: List<Photo>?) = (adapter as ExplorePhotoAdapter).apply { submitList(data) }

@BindingAdapter("favoritesPhotos")
fun RecyclerView.favoritesPhotos(data: List<Photo>?) = (adapter as FavoritesPhotoAdapter).apply { submitList(data) }


@BindingAdapter("imageUrl")
fun ImageView.imageUrl(imgUrl: String?) {

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

@BindingAdapter("imageConnectionStatus")
fun ImageView.imageConnectionStatus(connectionStatus: NasaApiConnectionStatus?) {
    when (connectionStatus) {
        NasaApiConnectionStatus.LOADING,
        NasaApiConnectionStatus.DONE -> {
            setGone()
        }
        NasaApiConnectionStatus.ERROR -> {
            apply {
                setVisible()
                setImageResource(R.drawable.icon_connection_error)
            }
        }
        NasaApiConnectionStatus.NODATA -> {
            apply {
                setVisible()
                setImageResource(R.drawable.icon_database_no_data)
            }
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


/** Some convenient extension functions on the View class **/

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun View.viewVisibleOrGone(show: Boolean) = if (show) setVisible() else setGone()

fun setViewsGone(vararg views: View) = views.forEach { it.setGone() }

fun setViewsVisible(vararg views: View) = views.forEach { it.setVisible() }

fun setViewsInVisible(vararg views: View) = views.forEach { it.setInvisible() }



