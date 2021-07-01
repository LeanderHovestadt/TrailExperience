package com.example.android.trailexperience.tours.detail

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
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import timber.log.Timber

class TourDetailViewModel(app : Application, private val localDataSource: ToursLocalDataSource) : AndroidViewModel(app) {

    private var _item = MutableLiveData<TourItem>()
    val item: LiveData<TourItem>
        get() = _item



    fun setDetailTourFromId(tourId: String?) {
        viewModelScope.launch {
            if (tourId == null){
                Timber.e("Invalid Id. Can't set tour.")
                return@launch
            }

            when (val result = localDataSource.getTour(tourId)) {
                is Result.Success<*> -> {
                    val tour = result.data as TourDataItem

                    _item.postValue(tour.asDomainModel())
                }
                // TODO snackbar on error
                //is Result.Error ->
                    //_showSnackbar.value = result.message!!
            }
        }
    }

}