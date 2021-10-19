package com.example.android.trailexperience.tours.data.local

import androidx.lifecycle.LiveData
import com.example.android.trailexperience.tours.data.objects.TourDataItem
import com.example.android.trailexperience.tours.data.objects.Result

/**
 * Main entry point for accessing tour data.
 */
interface ToursLocalDataSource {
    val updateRequired : LiveData<Boolean>
    suspend fun getTours(): Result<List<TourDataItem>>
    suspend fun saveTours(tour: List<TourDataItem>)
    suspend fun getTour(id: String): Result<TourDataItem>
    suspend fun deleteAllTours()
}