package com.example.android.trailexperience.tours.tourslist

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trailexperience.tours.data.local.ToursLocalDataSource
import com.example.android.trailexperience.tours.data.objects.Result
import com.example.android.trailexperience.tours.data.objects.TourDataItem
import com.example.android.trailexperience.tours.data.objects.TourItem
import com.example.android.trailexperience.tours.data.remote.ToursRemoteDataSource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import timber.log.Timber

class ToursListViewModel(app: Application, private val localDataSource: ToursLocalDataSource, private val remoteDataSource: ToursRemoteDataSource) : AndroidViewModel(app) {

    // show snackbar
    private var _showSnackbar = MutableLiveData<String>()
    val showSnackBar: LiveData<String>
    get() = _showSnackbar

    // show loading state
    private var _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    // show no data
    private var _showNoData = MutableLiveData<Boolean>()
    val showNoData: LiveData<Boolean>
        get() = _showNoData

    // list that holds the tour data to be displayed on the UI
    private var _filteredToursList = MutableLiveData<List<TourItem>>()
    val filteredToursList : LiveData<List<TourItem>>
    get() = _filteredToursList

    var remoteToursList = remoteDataSource.tours

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadTours() {
        _showLoading.value = true
        viewModelScope.launch {
            //interacting with the dataSource has to be through a coroutine
            val result = localDataSource.getTours()
            _showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<TourItem>()
                    dataList.addAll((result.data as List<TourDataItem>).map { tour ->
                        //map the reminder data from the DB to the be ready to be displayed on the UI
                        TourItem(
                            tour.name,
                            tour.description,
                            tour.difficulty,
                            tour.image,
                            LatLng(tour.latitude ?: 0.0, tour.longitude ?: 0.0),
                            tour.type,
                            tour.id
                        )
                    })
                    _filteredToursList.value = dataList
                }
                is Result.Error ->
                    _showSnackbar.value = result.message!!
            }

            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun fetchTours() {
        viewModelScope.launch {
            //interacting with the dataSource has to be through a coroutine
            Timber.i("fetching Tours")
            remoteDataSource.fetchTours()
        }
    }

    fun saveTours(tours : List<TourDataItem>){
        tours.forEach { unit ->
            viewModelScope.launch {
                localDataSource.saveTour(unit)
            }
        }
    }

    /**
     * Inform the user that there's not any data if the tours list is empty
     */
    private fun invalidateShowNoData() {
        _showNoData.value = filteredToursList.value == null || filteredToursList.value!!.isEmpty()
    }
}