package com.example.android.trailexperience.tours.data.objects

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class TourItem(
    var name: String?,
    var description: String?,
    var difficulty: String?,
    var image: String?,
    var location: LatLng?,
    var type: String?,
    var id: String?
) : Serializable