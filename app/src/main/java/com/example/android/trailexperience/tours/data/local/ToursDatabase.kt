package com.example.android.trailexperience.tours.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.trailexperience.tours.data.objects.TourDataItem

/**
 * The Room Database that contains the reminders table.
 */
@Database(entities = [TourDataItem::class], version = 1, exportSchema = false)
abstract class ToursDatabase : RoomDatabase() {

    abstract fun toursDao(): ToursDao
}