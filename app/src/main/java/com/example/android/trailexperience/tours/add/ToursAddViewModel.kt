package com.example.android.trailexperience.tours.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.trailexperience.tours.data.objects.TourItem
import com.example.android.trailexperience.tours.data.remote.ToursRemoteDataSource
import com.example.android.trailexperience.utils.asDatabaseModel
import com.example.android.trailexperience.utils.isValid
import kotlinx.coroutines.launch
import timber.log.Timber

class ToursAddViewModel(app: Application, private val remoteDataSource: ToursRemoteDataSource) : AndroidViewModel(app) {

    private var _showMissingDetails = MutableLiveData<Boolean>()
    val showMissingDetails : LiveData<Boolean>
    get() = _showMissingDetails

    var onTourSaved = remoteDataSource.updateRequired

    fun saveTour(tour: TourItem) {
        viewModelScope.launch {
            if (tour.isValid()) {
                Timber.i("tour ${tour.name} saved")
                remoteDataSource.saveTour(tour.asDatabaseModel())
            }
            else {
                Timber.w("could not save tour")
                _showMissingDetails.postValue(true)
            }
        }
    }

    fun onMissingDetailsShown() {
        _showMissingDetails.value = false
    }

}