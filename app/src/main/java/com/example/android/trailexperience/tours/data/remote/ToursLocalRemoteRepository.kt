package com.example.android.trailexperience.tours.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.trailexperience.tours.data.local.ToursLocalDataSource
import com.example.android.trailexperience.tours.data.objects.TourDataItem
import com.example.android.trailexperience.tours.data.objects.Type
import com.example.android.trailexperience.utils.isValid
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
const val KEY_HM = "hm"
const val KEY_KM = "km"


class ToursRemoteService() : ToursRemoteDataSource {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private var _tours = MutableLiveData<List<TourDataItem>>()
    override val tours : LiveData<List<TourDataItem>>
        get() = _tours

    private var _updateRequired = MutableLiveData<Boolean>()
    override val updateRequired : LiveData<Boolean>
    get() = _updateRequired

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
                        val typeAsStr = document.data.getValue(KEY_TYPE)
                        val hm = document.data.getValue(KEY_HM)
                        val km = document.data.getValue(KEY_KM)

                        if (name == null || description == null || difficulty == null || image == null || location == null || typeAsStr == null || hm == null || km == null){
                            Timber.e("Could not parse document with id ${document.id}")
                            continue
                        }

                        val geoPoint = location as GeoPoint

                        val type = when(typeAsStr as String) {
                            "mtb" -> Type.Mountainbike
                            "hike" -> Type.Hike
                            "climb" -> Type.Climb
                            else -> Type.Hike
                        }
                        val tourItem = TourDataItem(name as String, type, description as String, difficulty as String, image as String, geoPoint.latitude, geoPoint.longitude, hm as Long, km as Long, document.id)

                        tourItems.add(tourItem)
                    }

                    Timber.i("fetched ${tourItems.size} items.")
                    _tours.postValue(tourItems)
                    _updateRequired.postValue(false)
                }
                .addOnFailureListener { exception ->
                    Timber.w("Error getting documents. $exception")
                }
        }
    }

    override suspend fun saveTour(tour: TourDataItem) {
        Timber.d("saveTour is called for tour ${tour.name}")
        withContext(ioDispatcher){

            if (!tour.isValid()){
                Timber.e("Tour with id ${tour.id} is invalid")
                return@withContext
            }

            val typeAsStr = when(tour.type){
                Type.Mountainbike -> "mtb"
                Type.Hike -> "hike"
                Type.Climb -> "climb"
                else -> "hike"
            }

            // Create a new user with a first and last name
            val tourAsHashMap = hashMapOf(
                KEY_NAME to tour.name,
                KEY_DESCRIPTION to tour.description,
                KEY_DIFFICULTY to tour.difficulty,
                KEY_IMAGE to tour.image,
                KEY_LOCATION to GeoPoint(tour.latitude!!, tour.longitude!!),
                KEY_TYPE to typeAsStr,
                KEY_HM to tour.hm,
                KEY_KM to tour.km
            )

            // Add a new document with a generated ID
            val db = Firebase.firestore
            db.collection(COLLECTION_NAME)
                .add(tourAsHashMap)
                .addOnSuccessListener { id ->
                    Timber.i( "successfully added tour with id $id")
                    _updateRequired.postValue(true)
                }
                .addOnFailureListener { e ->
                    Timber.w( "Error adding document: ${e.message}")
                }
        }
    }
}