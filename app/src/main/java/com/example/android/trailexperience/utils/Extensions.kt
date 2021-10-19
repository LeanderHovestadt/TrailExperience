package com.example.android.trailexperience.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trailexperience.tours.base.BaseRecyclerViewAdapter
import com.example.android.trailexperience.tours.data.objects.TourDataItem
import com.example.android.trailexperience.tours.data.objects.TourItem
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

/**
 * Extension function to setup the RecyclerView
 */
fun <T> RecyclerView.setup(
    adapter: BaseRecyclerViewAdapter<T>
) {
    this.apply {
        layoutManager = LinearLayoutManager(this.context)
        this.adapter = adapter
    }
}

fun Fragment.setTitle(title: String) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }
}

fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            bool
        )
    }
}

//animate changing the view visibility
fun View.fadeIn() {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeIn.alpha = 1f
        }
    })
}

//animate changing the view visibility
fun View.fadeOut() {
    this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeOut.alpha = 1f
            this@fadeOut.visibility = View.GONE
        }
    })
}

fun TourDataItem.asDomainModel() : TourItem {
    if (isValid()){
        return TourItem(name, description, difficulty, image, LatLng(latitude!!, longitude!!), type, hm, km, id)
    }

    Timber.e("Can't convert TourDataItem to TourItem")
    return TourItem(null, null, null, null, null, null, null, null, null)
}

fun TourDataItem.isValid() : Boolean{
    return (name != null && description != null && difficulty != null && image != null && latitude != null && longitude != null && type != null && hm != null && km != null)
}

fun TourItem.asDatabaseModel() : TourDataItem {
    if (isValid()){
        return TourDataItem(name, type, description, difficulty, image, location?.latitude, location?.longitude, hm, km)
    }

    Timber.e("Can't convert TourItem to TourDataItem")
    return TourDataItem(null, null, null, null, null, null, null, null, null)
}

fun TourItem.isValid() : Boolean {
    Timber.i("Is Tour Valid: $name, $type, $description, $difficulty, $image, $location, $hm, $km, $id")
    return (name != null && type != null && description != null && difficulty != null && image != null && location != null && hm != null && km != null)
}