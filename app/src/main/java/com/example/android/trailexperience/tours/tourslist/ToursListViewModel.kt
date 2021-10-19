package com.example.android.trailexperience.tours.tourslist

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trailexperience.tours.data.local.ToursLocalDataSource
import com.example.android.trailexperience.tours.data.objects.Result
import com.example.android.trailexperience.tours.data.objects.TourDataItem
import com.example.android.trailexperience.tours.data.objects.TourItem
import com.example.android.trailexperience.tours.data.objects.Type
import com.example.android.trailexperience.tours.data.remote.ToursRemoteDataSource
import com.example.android.trailexperience.utils.asDomainModel
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

    // show fab menu
    private var _showFabMenu = MutableLiveData<Boolean>()
    val showFabMenu: LiveData<Boolean>
        get() = _showFabMenu

    // list that holds the tour data to be displayed on the UI
    private var _filteredToursList = MutableLiveData<List<TourItem>>()
    val filteredToursList : LiveData<List<TourItem>>
    get() = _filteredToursList

    private var _typeFilter = MutableLiveData<Type>()
    val typeFilter : LiveData<Type>
    get() = _typeFilter

    val remoteToursList = remoteDataSource.tours
    val remoteUpdateRequired = remoteDataSource.updateRequired
    val localUpdateRequired = localDataSource.updateRequired

    init {
        _showFabMenu.value = false
    }

    /**
     * Get all the reminders from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadTours(type: Type?) {
        _showLoading.value = true
        viewModelScope.launch {
            //interacting with the dataSource has to be through a coroutine
            val result = localDataSource.getTours()
            _showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    val filteredList = mutableListOf<TourDataItem>()

                    (result.data as List<TourDataItem>).forEach { tour ->
                        when (type ?: typeFilter.value ?: Type.All) {
                            Type.All -> filteredList.add(tour)
                            else -> {
                                if (tour.type == type) {
                                    filteredList.add(tour)
                                }
                            }
                        }
                    }

                    val dataList = ArrayList<TourItem>()
                    dataList.addAll(filteredList.map { tour ->
                        //map the reminder data from the DB to the be ready to be displayed on the UI
                        tour.asDomainModel()
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
        viewModelScope.launch {
            localDataSource.saveTours(tours)
        }
    }

    fun pushTour(tour: TourDataItem){
        viewModelScope.launch {
            remoteDataSource.saveTour(tour)
        }
    }

    /**
     * Inform the user that there's not any data if the tours list is empty
     */
    private fun invalidateShowNoData() {
        _showNoData.value = filteredToursList.value == null || filteredToursList.value!!.isEmpty()
    }

    fun toggleFabMenu() {
        _showFabMenu.value = !_showFabMenu.value!!
    }

    fun reset() {
        _showFabMenu.value = false
    }

    fun updateTypeFilter(type: Type) {
        loadTours(type)
    }
}