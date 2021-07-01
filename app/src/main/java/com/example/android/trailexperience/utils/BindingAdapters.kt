package com.example.android.trailexperience.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.trailexperience.R
import com.example.android.trailexperience.tours.base.BaseRecyclerViewAdapter
import com.example.android.trailexperience.tours.data.objects.TourItem
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
                    "mtb" -> {
                        imageView.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                imageView.resources,
                                R.drawable.ic_mountainbike_24,
                                null
                            )
                        )
                        imageView.contentDescription = "Mountainbike tour"
                    }
                    "hike" -> {
                        imageView.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                imageView.resources,
                                R.drawable.ic_hiking_24,
                                null
                            )
                        )
                        imageView.contentDescription = "Hiking tour"
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
                "mtb" -> {
                    // TODO extract resources
                    textView.text = "Mountainbike"
                }
                "hike" -> {
                    textView.text = "Hike"
                }
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
        imgUrl?.let {
            val imgUri = it.toUri().buildUpon().scheme("https").build()
            Glide.with(imgView.context)
                .load(imgUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_logo_mountains_256)
                        .error(R.drawable.ic_baseline_broken_image_256)
                )
                .into(imgView)
        }
    }
}