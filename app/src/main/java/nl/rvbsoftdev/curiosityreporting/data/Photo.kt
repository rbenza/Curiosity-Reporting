package nl.rvbsoftdev.curiosityreporting.data

import kotlinx.serialization.Serializable

/** Data model for the app, the app does not interact with 'NetworkPhoto' or 'FavoriteDataBasePhoto',
 * these types are abstracted away by the repository API (MVVM architecture, separation of concerns).
 * Makes adjusting, adding or removing data sources in the future easy. **/

@Serializable
data class Photo(
        var isFavorite: Boolean = false,
        val camera: Camera? = null,
        val earth_date: String? = null,
        val id: Int? = null,
        val img_src: String? = null,
        val rover: Rover? = null,
        val sol: Int? = null)  {

    @Serializable
    data class Rover(
            val max_date: String? = null,
            val max_sol: Int? = null,
            val total_photos: Int? = null)

    @Serializable
    data class Camera(
            val full_name: String? = null,
            val name: String? = null)
}
