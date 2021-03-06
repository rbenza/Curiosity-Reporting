package nl.rvbsoftdev.curiosityreporting.data

import kotlinx.serialization.Serializable

/** Data model for the Retrofit network requests. **/

@Serializable
data class NetworkPhotoContainer(val photos: List<NetworkPhoto>? = emptyList(), val latest_photos: List<NetworkPhoto>? = emptyList())

@Serializable
data class NetworkPhoto(
        val id: Int? = null,
        val sol: Int? = null,
        val img_src: String? = null,
        val earth_date: String? = null,
        val camera: NetworkCamera? = null,
        val rover: NetworkRover? = null
)

@Serializable
data class NetworkRover(
        val max_date: String? = null,
        val max_sol: Int? = null,
        val total_photos: Int? = null
)

@Serializable
data class NetworkCamera(
        val full_name: String? = null,
        val name: String? = null
)

/** Kotlin Extension Functions to map 'NetworkPhoto' to 'Photo' **/

fun NetworkPhotoContainer.toListOfPhoto(): List<Photo>? {
    if (photos?.isNullOrEmpty() == false) {
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


