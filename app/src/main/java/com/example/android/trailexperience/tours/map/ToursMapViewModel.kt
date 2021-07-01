package com.example.android.trailexperience.tours.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.trailexperience.tours.data.local.ToursLocalDataSource
import com.example.android.trailexperience.tours.data.objects.Result
import com.example.android.trailexperience.tours.data.objects.TourDataItem
import com.example.android.trailexperience.tours.data.objects.TourItem
import com.example.android.trailexperience.utils.asDomainModel
import kotlinx.coroutines.launch
import timber.log.Timber

class ToursMapViewModel(app : Application, private val localDataSource: ToursLocalDataSource) : AndroidViewModel(app) {

    private var _filteredTours = MutableLiveData<List<TourItem>>()
    val filteredTours : LiveData<List<TourItem>>
    get() = _filteredTours

    fun setFilteredTours(tourIds: Array<String>) {
        viewModelScope.launch {
            val toursList = mutableListOf<TourItem>()
            Timber.i("requesting filtered tours from localDataSource")
            tourIds.forEach { id ->
                when (val result = localDataSource.getTour(id)) {
                    is Result.Success<*> -> {
                        val tour = result.data as TourDataItem

                        toursList.add(tour.asDomainModel())
                    }
                    is Result.Error -> {
                        Timber.e("Could not find tour with id $id")
                    }
                }
            }

            Timber.i("requested ${toursList.size} tours from localDataSource")
            _filteredTours.postValue(toursList)
        }
    }


}