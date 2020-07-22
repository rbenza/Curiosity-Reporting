package nl.rvbsoftdev.curiosityreporting.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonQualifier
import kotlinx.android.parcel.Parcelize

/** Data model for the Retrofit network requests. '@Parcelize' instructs the Kotlin compiler to generate parcelable methods automatically.**/

@Parcelize
data class NetworkPhotoContainer(val photos: List<NetworkPhoto>? = emptyList(), val latest_photos: List<NetworkPhoto>? = emptyList()) : Parcelable

@Parcelize
data class NetworkPhoto(
        val id: Int? = null,
        val sol: Int? = null,
        val img_src: String? = null,
        val earth_date: String? = null,
        val camera: NetworkCamera? = null,
        val rover: NetworkRover? = null
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

fun NetworkPhotoContainer.toListOfPhoto(): List<Photo>? {
    if (!photos?.isNullOrEmpty()!!) {
        return photos.map {
            Photo(
                    camera = it.camera?.toCamera(),
                    earth_date = it.earth_date,
                    id = it.id,
                    img_src = it.img_src,
                    rover = it.rover?.toRover(),
                    sol = it.sol)

        }
    } else {
        return latest_photos?.map {
            Photo(
                    camera = it.camera?.toCamera(),
                    earth_date = it.earth_date,
                    id = it.id,
                    img_src = it.img_src,
                    rover = it.rover?.toRover(),
                    sol = it.sol)
        }
    }
}

fun NetworkRover.toRover() = Photo.Rover(max_date = this.max_date, max_sol = this.max_sol, total_photos = this.total_photos)

fun NetworkCamera.toCamera() = Photo.Camera(full_name = this.full_name, name = this.name)


