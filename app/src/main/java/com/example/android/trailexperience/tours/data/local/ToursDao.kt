package com.example.android.trailexperience.tours.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.trailexperience.tours.data.objects.TourDataItem

/**
 * Data Access Object for the tours table.
 */
@Dao
interface ToursDao {
    /**
     * @return all tours.
     */
    @Query("SELECT * FROM tours")
    suspend fun getTours(): List<TourDataItem>

    /**
     * @param tourId the id of the tour
     * @return the tour object with the tourId
     */
    @Query("SELECT * FROM tours where entry_id = :tourId")
    suspend fun getTourById(tourId: String): TourDataItem?

    /**
     * Insert a tour in the database. If the tour already exists, ignore it.
     *
     * @param tour the tour to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveTour(tour: TourDataItem)

    /**
     * Delete all tours.
     */
    @Query("DELETE FROM tours")
    suspend fun deleteAllTours()

}