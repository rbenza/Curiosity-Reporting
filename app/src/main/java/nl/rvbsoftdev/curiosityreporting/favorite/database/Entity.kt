package nl.rvbsoftdev.curiosityreporting.favorite.database

import androidx.room.*
import nl.rvbsoftdev.curiosityreporting.data.Camera
import nl.rvbsoftdev.curiosityreporting.data.Photo
import nl.rvbsoftdev.curiosityreporting.data.Rover

/** Room database table with only unique items (id property is unique) and ordered by earth_date **/

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)

@Entity(indices = [Index(value = ["id"], unique = true)])
data class FavoriteDatabasePhoto (
        @PrimaryKey
        val id: Int,
        @Embedded val camera: DatabaseCamera,
        val earth_date: String,
        val img_src: String,
        val sol: Int,
        @Embedded val rover: DatabaseRover)

@Entity
data class DatabaseRover (
        @PrimaryKey(autoGenerate = true)
        val max_date: String,
        val max_sol: Int,
        val total_photos: Int)

@Entity
data class DatabaseCamera (
        @PrimaryKey(autoGenerate = true)
        val full_name: String,
        val name: String)

/** Various Kotlin Extension Functions to map a (List of) 'FavoriteDatabasePhoto' to a (List of) 'Photo' for the app **/

fun List<FavoriteDatabasePhoto>.asDataBaseModel(): List<Photo> {
    return map {
        Photo(
                id = it.id,
                camera = it.camera.asAppDataModel(),
                earth_date = it.earth_date,
                img_src = it.img_src,
                sol = it.sol,
                rover = it.rover.asAppDataModel())
    }
}

fun Photo.asDataBaseModel(): FavoriteDatabasePhoto {
    return let {
        FavoriteDatabasePhoto(
                id = it.id,
                camera = it.camera.asDataBaseModel(),
                earth_date = it.earth_date,
                img_src = it.img_src,
                sol = it.sol,
                rover = it.rover.asDataBaseModel())
    }
}

fun FavoriteDatabasePhoto.asAppDataModel(): Photo {
    return let {
        Photo(
                id = it.id,
                camera = it.camera.asAppDataModel(),
                earth_date = it.earth_date,
                img_src = it.img_src,
                sol = it.sol,
                rover = it.rover.asAppDataModel())
    }
}

fun Camera.asDataBaseModel(): DatabaseCamera {
    return let {
        DatabaseCamera(full_name = it.full_name, name = it.name)
    }
}


fun Rover.asDataBaseModel(): DatabaseRover {
    return let {
        DatabaseRover(
                max_date = it.max_date,
                max_sol = it.max_sol,
                total_photos = it.total_photos)
    }
}

fun DatabaseCamera.asAppDataModel(): Camera {
    return let {
        Camera(full_name = it.full_name, name = it.name)
    }
}

fun DatabaseRover.asAppDataModel(): Rover {
    return let {
        Rover(max_date = it.max_date, max_sol = it.max_sol, total_photos = it.total_photos)
    }
}


