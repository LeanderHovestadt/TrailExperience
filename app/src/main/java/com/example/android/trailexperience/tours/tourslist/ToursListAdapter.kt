package com.example.android.trailexperience.tours.tourslist

import com.example.android.trailexperience.R
import com.example.android.trailexperience.tours.base.BaseRecyclerViewAdapter
import com.example.android.trailexperience.tours.data.objects.TourItem

//Use data binding to show the tour on the item
class ToursListAdapter(callBack: (selectedTour: TourItem) -> Unit) :
    BaseRecyclerViewAdapter<TourItem>(callBack) {
    override fun getLayoutRes(viewType: Int) = R.layout.it_tour
}