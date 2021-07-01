package com.example.android.trailexperience.tours.data.local

import android.content.Context
import androidx.room.Room

/**
 * Singleton class that is used to create a tour db
 */
object LocalDB {

    /**
     * static method that creates a tour class and returns the DAO of the tour
     */
    fun createToursDao(context: Context): ToursDao {
        return Room.databaseBuilder(
            context.applicationContext,
            ToursDatabase::class.java, "tours.db"
        ).build().toursDao()
    }

}