package com.example.android.trailexperience.utils

import android.provider.Settings.Global.getString
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.trailexperience.R
import com.example.android.trailexperience.tours.base.BaseRecyclerViewAdapter
import com.example.android.trailexperience.tours.data.objects.TourItem
import com.example.android.trailexperience.tours.data.objects.Type
import com.google.android.material.chip.ChipGroup
import timber.log.Timber


object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:liveData")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
            }
        }
    }

    @BindingAdapter("android:difficulty")
    @JvmStatic
    fun setDifficulty(imageView: ImageView, item: TourItem?) {
        Timber.d("setDifficulty called")
        item?.let {
            item.difficulty?.let {
                Timber.d("difficulty is not null")
                when (item.difficulty) {
                    "easy" -> {
                        Timber.d("difficulty is easy")
                        imageView.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                imageView.resources,
                                R.drawable.ic_trail_diff_green_24,
                                null
                            )
                        )
                        imageView.contentDescription = "Easy difficulty"
                    }
                    "medium" -> {
                        Timber.d("difficulty is medium")
                        imageView.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                imageView.resources,
                                R.drawable.ic_trail_diff_blue_24,
                                null
                            )
                        )
                        imageView.contentDescription = "Medium difficulty"
                    }
                    "hard" -> {
                        Timber.d("difficulty is hard")
                        imageView.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                imageView.resources,
                                R.drawable.ic_trail_diff_blackdiamond_24,
                                null
                            )
                        )
                        imageView.contentDescription = "Hard difficulty"
                    }
                }
            }
        }
    }

    @BindingAdapter("android:type")
    @JvmStatic
    fun setType(imageView: ImageView, item: TourItem?) {
        item?.let {
            item.type?.let {
                when (item.type) {
                    Type.Mountainbike -> {
                        imageView.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                imageView.resources,
                                R.drawable.ic_mountainbike_24,
                                null
                            )
                        )
                        imageView.contentDescription = "Mountainbike tour"
                    }
                    Type.Hike -> {
                        imageView.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                imageView.resources,
                                R.drawable.ic_hiking_24,
                                null
                            )
                        )
                        imageView.contentDescription = "Hiking tour"
                    }
                    Type.Climb -> {
                        imageView.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                imageView.resources,
                                R.drawable.ic_climbing_24,
                                null
                            )
                        )
                        imageView.contentDescription = "Climbing tour"
                    }
                }
            }
        }
    }


    @BindingAdapter("android:typeText")
    @JvmStatic
    fun setTypeText(textView: TextView, item: TourItem?) {
        item?.let { it ->
            when (it.type) {
                Type.Mountainbike -> {
                    textView.text = textView.resources.getString(R.string.mountainbike)
                }
                Type.Hike -> {
                    textView.text = textView.resources.getString(R.string.hike)
                }
                Type.Climb -> {
                    textView.text = textView.resources.getString(R.string.climb)
                }
                else -> Timber.e("Could not set tour type.")
            }
        }
    }

    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }

    @BindingAdapter("android:imageUrl")
    @JvmStatic
    fun bindImage(imgView: ImageView, imgUrl: String?) {
        if (imgUrl != null) {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            Glide.with(imgView.context)
                .load(imgUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_logo_mountains_256)
                        .error(R.drawable.ic_baseline_broken_image_256)
                )
                .into(imgView)
        }
        else{
            imgView.setImageDrawable(ResourcesCompat.getDrawable(imgView.resources, R.drawable.ic_logo_mountains_256, null))
        }
    }

    @BindingAdapter("android:tourType")
    @JvmStatic
    fun setTourType(chipGroup: ChipGroup, tourType: Type?) {
        tourType?.let {
            when (it) {
                Type.Mountainbike -> chipGroup.check(R.id.chip_mtb)
                Type.Hike -> chipGroup.check(R.id.chip_hike)
                Type.Climb -> chipGroup.check(R.id.chip_climb)
                else -> null
            }
        }
    }

    @InverseBindingAdapter(attribute = "android:tourType")
    @JvmStatic
    fun getTourType(chipGroup: ChipGroup) : Type? {
        return when (chipGroup.checkedChipId) {
            R.id.chip_mtb -> Type.Mountainbike
            R.id.chip_hike -> Type.Hike
            R.id.chip_climb -> Type.Climb
            else -> null
        }

    }

    @BindingAdapter("android:tourDifficulty")
    @JvmStatic
    fun setTourDifficulty(chipGroup: ChipGroup, tourDifficulty: String?) {
        tourDifficulty?.let {
            when (it) {
                "easy" -> chipGroup.check(R.id.chip_easy)
                "medium" -> chipGroup.check(R.id.chip_medium)
                "hard" -> chipGroup.check(R.id.chip_hard)
            }
        }
    }


    @InverseBindingAdapter(attribute = "android:tourDifficulty")
    @JvmStatic
    fun getTourDifficulty(chipGroup: ChipGroup): String? {
        return when (chipGroup.checkedChipId) {
            R.id.chip_easy -> "easy"
            R.id.chip_medium -> "medium"
            R.id.chip_hard -> "hard"
            else -> null
        }
    }

    @BindingAdapter("android:location")
    @JvmStatic
    fun setLocation(textView: TextView, tour: TourItem?) {
        tour?.let {
            tour.location?.let {
                textView.text = textView.context.getString(R.string.location_format)
                    .format(it.latitude, it.longitude)
            }
        }
    }

    @BindingAdapter("android:tourHm")
    @JvmStatic
    fun EditText.setTourHm(tourHm: Long?) {
        tourHm?.let {
            setText(it.toString())
        }
    }

    @InverseBindingAdapter(attribute = "android:tourHm", event = "android:textAttrChanged")
    @JvmStatic
    fun EditText.getTourHm(): Long {
        return text.toString().toLongOrNull() ?: 0
    }

    @InverseBindingAdapter(attribute = "android:tourKm", event = "android:textAttrChanged")
    @JvmStatic
    fun EditText.getTourKm(): Long {
        return text.toString().toLongOrNull() ?: 0
    }

    @BindingAdapter("android:tourKm")
    @JvmStatic
    fun EditText.setTourKm(tourKm: Long?) {
        tourKm?.let {
            setText(it.toString())
        }
    }

    @BindingAdapter("android:tourHm")
    @JvmStatic
    fun TextView.setTourHm(tourHm: Long?) {
        tourHm?.let {
            text = context.getString(R.string.hm_format).format(it)
        }
    }

    @BindingAdapter("android:tourKm")
    @JvmStatic
    fun TextView.setTourKm(tourKm: Long?) {
        tourKm?.let {
            text = context.getString(R.string.km_format).format(it)
        }
    }
}