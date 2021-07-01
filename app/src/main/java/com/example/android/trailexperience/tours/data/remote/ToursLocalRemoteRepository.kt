package com.example.android.trailexperience.tours.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.trailexperience.tours.data.local.ToursLocalDataSource
import com.example.android.trailexperience.tours.data.objects.TourDataItem
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

const val COLLECTION_NAME = "Touren"

const val KEY_NAME : String = "name"
const val KEY_DESCRIPTION = "description"
const val KEY_DIFFICULTY = "difficulty"
const val KEY_IMAGE = "image"
const val KEY_LOCATION = "location"
const val KEY_TYPE = "type"


class ToursRemoteService(private val localDataSource: ToursLocalDataSource) : ToursRemoteDataSource {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private var _tours = MutableLiveData<List<TourDataItem>>()
    override val tours : LiveData<List<TourDataItem>>
        get() = _tours

    override suspend fun fetchTours() {
        Timber.d("fetchTours called")
        withContext(ioDispatcher) {
            val db = Firebase.firestore
            db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener { result ->
                    val tourItems = mutableListOf<TourDataItem>()
                    for (document in result) {
                        Timber.d("${document.id} => ${document.data}")

                        val name = document.data.getValue(KEY_NAME)
                        val description = document.data.getValue(KEY_DESCRIPTION)
                        val difficulty = document.data.getValue(KEY_DIFFICULTY)
                        val image = document.data.getValue(KEY_IMAGE)
                        val location = document.data.getValue(KEY_LOCATION)
                        val type = document.data.getValue(KEY_TYPE)

                        if (name == null || description == null || difficulty == null || image == null || location == null || type == null){
                            Timber.e("Could not parse document with id ${document.id}")
                            continue
                        }

                        val geoPoint = location as GeoPoint

                        val tourItem = TourDataItem(name as String, type as String, description as String, difficulty as String, image as String, geoPoint.latitude, geoPoint.longitude,  document.id)

                        tourItems.add(tourItem)
                    }

                    Timber.i("fetched ${tourItems.size} items.")
                    _tours.postValue(tourItems)
                }
                .addOnFailureListener { exception ->
                    Timber.w("Error getting documents. $exception")
                }
        }
    }
}