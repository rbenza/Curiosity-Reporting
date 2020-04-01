package nl.rvbsoftdev.curiosityreporting.network

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import nl.rvbsoftdev.curiosityreporting.domain.Camera
import nl.rvbsoftdev.curiosityreporting.domain.Photo
import nl.rvbsoftdev.curiosityreporting.domain.Rover

/** Data model for the Retrofit network requests. '@Parcelize' instructs the Kotlin compiler to generate parcelable methods automatically.**/

@Parcelize
data class NetworkPhotoContainer(val photos: List<NetworkPhoto>) : Parcelable

@Parcelize
data class NetworkPhoto(
        val camera: NetworkCamera,
        val earth_date: String,
        val id: Int,
        val img_src: String,
        val rover: NetworkRover,
        val sol: Int
) : Parcelable

@Parcelize
data class NetworkRover(
        val max_date: String,
        val max_sol: Int,
        val total_photos: Int
) : Parcelable

@Parcelize
data class NetworkCamera(
        val full_name: String,
        val name: String
) : Parcelable

/** Various Kotlin Extension Functions to map a (List of) 'NetworkPhoto' to a (List of) 'Photo' **/

fun NetworkPhotoContainer.asAppDataModel(): List<Photo> {
    return photos.map {
        Photo(
                camera = it.camera.asAppDataModel(),
                earth_date = it.earth_date,
                id = it.id,
                img_src = it.img_src,
                rover = it.rover.asAppDataModel(),
                sol = it.sol)

    }
}

fun NetworkRover.asAppDataModel(): Rover {
    return let {
        Rover(max_date = it.max_date, max_sol = it.max_sol, total_photos = it.total_photos)
    }
}

fun NetworkCamera.asAppDataModel(): Camera {
    return let {
        Camera(full_name = it.full_name, name = it.name)
    }
}
