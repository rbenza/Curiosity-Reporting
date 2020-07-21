package nl.rvbsoftdev.curiosityreporting.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/** Data model for the Retrofit network requests. '@Parcelize' instructs the Kotlin compiler to generate parcelable methods automatically.**/

@Parcelize
data class NetworkPhotoContainer(val photos: List<NetworkPhoto>) : Parcelable

@Parcelize
data class NetworkPhoto(
        val camera: NetworkCamera? = null,
        val earth_date: String? = null,
        val id: Int? = null,
        val img_src: String? = null,
        val rover: NetworkRover? = null,
        val sol: Int? = null
) : Parcelable

@Parcelize
data class NetworkRover(
        val max_date: String? = null,
        val max_sol: Int? = null,
        val total_photos: Int? = null
) : Parcelable

@Parcelize
data class NetworkCamera(
        val full_name: String? = null,
        val name: String? = null
) : Parcelable

/** Kotlin Extension Functions to map 'NetworkPhoto' to 'Photo' **/

fun NetworkPhotoContainer.toListOfPhoto(): List<Photo> {
    return photos.map {
        Photo(
                camera = it.camera?.toCamera(),
                earth_date = it.earth_date,
                id = it.id,
                img_src = it.img_src,
                rover = it.rover?.toRover(),
                sol = it.sol)

    }
}

fun NetworkRover.toRover(): Photo.Rover {
    return let {
        Photo.Rover(max_date = it.max_date, max_sol = it.max_sol, total_photos = it.total_photos)
    }
}

fun NetworkCamera.toCamera(): Photo.Camera {
    return let {
        Photo.Camera(full_name = it.full_name, name = it.name)
    }
}
