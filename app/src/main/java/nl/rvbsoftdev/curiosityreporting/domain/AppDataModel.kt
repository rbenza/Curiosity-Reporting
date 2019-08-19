package nl.rvbsoftdev.curiosityreporting.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/** Data model for the app, the app does not interact with 'NetworkPhoto' or 'FavoriteDataBasePhoto', these types are abstracted away by the repository API (MVVM architecture, separation of concerns).
 * Makes adjusting, adding or removing data sources in the future easy. **/

@Parcelize
data class Photo(
        val camera: Camera,
        val earth_date: String,
        val id: Int,
        val img_src: String,
        val rover: Rover,
        val sol: Int) : Parcelable

@Parcelize
data class Rover(
        val max_date: String,
        val max_sol: Int,
        val total_photos: Int) : Parcelable

@Parcelize
data class Camera(
        val full_name: String,
        val name: String) : Parcelable
