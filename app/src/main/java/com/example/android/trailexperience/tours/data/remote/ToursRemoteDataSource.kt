package com.example.android.trailexperience.tours.data.remote

import androidx.lifecycle.LiveData
import com.example.android.trailexperience.tours.data.objects.Result
import com.example.android.trailexperience.tours.data.objects.TourDataItem

interface ToursRemoteDataSource {
    val tours : LiveData<List<TourDataItem>>
    val updateRequired : LiveData<Boolean>
    suspend fun fetchTours()
    suspend fun saveTour(tour: TourDataItem)
}