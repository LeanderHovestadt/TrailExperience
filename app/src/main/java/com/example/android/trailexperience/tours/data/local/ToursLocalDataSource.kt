package com.example.android.trailexperience.tours.data.local

import com.example.android.trailexperience.tours.data.objects.TourDataItem
import com.example.android.trailexperience.tours.data.objects.Result

/**
 * Main entry point for accessing tour data.
 */
interface ToursLocalDataSource {
    suspend fun getTours(): Result<List<TourDataItem>>
    suspend fun saveTour(tour: TourDataItem)
    suspend fun getTour(id: String): Result<TourDataItem>
    suspend fun deleteAllTours()
}