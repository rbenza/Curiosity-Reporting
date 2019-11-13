package nl.rvbsoftdev.curiosityreporting.adapters

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
import com.google.android.material.snackbar.Snackbar
import nl.rvbsoftdev.curiosityreporting.R
import nl.rvbsoftdev.curiosityreporting.domain.Photo
import nl.rvbsoftdev.curiosityreporting.repository.NasaApiConnectionStatus

/** Custom databinding adapters to bind data to different views.
 * By setting the fragment in which the views are present as lifecycle owner the data is then observed
 * and the UI updates automatically when the data changes (see androidx.databinding.ViewDataBinding.setLifecycleOwner) **/

@BindingAdapter("explorePhotos")
fun bindRecyclerViewExplore(recyclerView: RecyclerView, data: List<Photo>?) {
    val adapter = recyclerView.adapter as ExplorePhotoAdapter
    adapter.submitList(data)
}

@BindingAdapter("favoritesPhotos")
fun bindRecyclerViewFavorites(recyclerView: RecyclerView, data: List<Photo>?) {
    val adapter = recyclerView.adapter as FavoritesPhotoAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {

    val loadingSpinner = CircularProgressDrawable(imgView.context)
    loadingSpinner.strokeWidth = 4f
    loadingSpinner.centerRadius = 20f
    if (PreferenceManager.getDefaultSharedPreferences(imgView.context).getString("theme", "Dark") == "Dark") {
        loadingSpinner.setColorSchemeColors(imgView.context.getColor(R.color.DeepOrange))
    } else {
        loadingSpinner.setColorSchemeColors(imgView.context.getColor(R.color.DarkBrown))
    }
    loadingSpinner.start()

    var pictureQualitySetting = 100
    when (PreferenceManager.getDefaultSharedPreferences(imgView.context).getString("picture_quality", "High")) {
        "High" -> pictureQualitySetting = 100
        "Normal" -> pictureQualitySetting = 75
        "Low" -> pictureQualitySetting = 25
    }
    Glide.with(imgView.context)
            .load(imgUrl)
            .encodeQuality(pictureQualitySetting)
            .apply(RequestOptions()
                    .placeholder(loadingSpinner)
                    .error(R.drawable.icon_broken_image))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            /** no Room database needed for NetworkPhotos, very small chance users loads same photos twice (360k photos in NASA db).
             * Glide cache impl works when user selects same date twice with Datepicker fragment. Max cache size 250mb **/
            .into(imgView)
}

@BindingAdapter("imageConnectionStatus")
fun imageConnectionStatus(imageConnectionStatus: ImageView, connectionStatus: NasaApiConnectionStatus?) {
    when (connectionStatus) {
        NasaApiConnectionStatus.LOADING,
        NasaApiConnectionStatus.DONE -> {
            imageConnectionStatus.viewGone()
        }
        NasaApiConnectionStatus.ERROR -> {
            imageConnectionStatus.apply {
                viewVisible()
                setImageResource(R.drawable.icon_connection_error)
            }
        }
        NasaApiConnectionStatus.NODATA -> {
            imageConnectionStatus.apply {
                viewVisible()
                setImageResource(R.drawable.icon_database_no_data)
            }
        }
    }
}

@BindingAdapter("textConnectionStatus")
fun textConnectionStatus(textConnectionStatus: TextView, connectionStatus: NasaApiConnectionStatus?) {
    when (connectionStatus) {
        NasaApiConnectionStatus.LOADING -> {
            textConnectionStatus.apply {
                viewVisible()
                text = context.getText(R.string.connecting_nasa_db)
            }
        }
        NasaApiConnectionStatus.ERROR -> {
            textConnectionStatus.apply {
                viewVisible()
                text = context.getText(R.string.no_conn_nasa_db)
            }
        }
        NasaApiConnectionStatus.NODATA -> {
            textConnectionStatus.apply {
                viewVisible()
                text = context.getText(R.string.no_photos_in_nasa_db)
            }
        }
        NasaApiConnectionStatus.DONE -> {
            textConnectionStatus.viewGone()
        }
    }
}

@BindingAdapter("favoriteText")
fun favoriteText(text: TextView, photo: List<Photo>?) = text.viewVisibleOrGone(photo.isNullOrEmpty())


@BindingAdapter("favoriteImg")
fun favoriteImg(img: ImageView, photo: List<Photo>?) = img.viewVisibleOrGone(photo.isNullOrEmpty())


/** Some convenient extension functions on the View class **/

fun View.viewVisible() {
    visibility = View.VISIBLE
}

fun View.viewInvisible() {
    visibility = View.INVISIBLE
}

fun View.viewGone() {
    visibility = View.GONE
}

fun View.viewVisibleOrGone(show: Boolean) = if (show) viewVisible() else viewGone()



