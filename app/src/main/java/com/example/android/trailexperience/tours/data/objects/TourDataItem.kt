package com.example.android.trailexperience.tours.data.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*


/**
 * Immutable model class for a Reminder. In order to compile with Room
 *
 * @param name          name of the tour
 * @param type          type of the tour, mtb or hike
 * @param description   description of the tour
 * @param latitude      latitude of the tour
 * @param longitude     longitude of the tour
 * @param difficulty    difficulty of the tour, easy, medium or hard
 * @param image         path to an image of the tour
 * @param location      location of the tour
 * @param entry_id      id of the tour
 */

@Entity(tableName = "tours")
data class TourDataItem(
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "type") var type: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "difficulty") var difficulty: String?,
    @ColumnInfo(name = "image") var image: String?,
    @ColumnInfo(name = "latitude") var latitude: Double?,
    @ColumnInfo(name = "longitude") var longitude: Double?,
    @PrimaryKey @ColumnInfo(name = "entry_id") val id: String = UUID.randomUUID().toString()
)