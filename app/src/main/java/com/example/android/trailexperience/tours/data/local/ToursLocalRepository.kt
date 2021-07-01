package com.example.android.trailexperience.tours.data.local

import com.example.android.trailexperience.tours.data.objects.TourDataItem
import com.example.android.trailexperience.utils.wrapEspressoIdlingResource
import com.example.android.trailexperience.tours.data.objects.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Concrete implementation of a data source as a db.
 *
 * The repository is implemented so that you can focus on only testing it.
 *
 * @param toursDao the dao that does the Room db operations
 * @param ioDispatcher a coroutine dispatcher to offload the blocking IO tasks
 */

class ToursLocalRepository(
    private val toursDao: ToursDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ToursLocalDataSource {

    /**
     * Get the tours list from the local db
     * @return Result the holds a Success with all the tours or an Error object with the error message
     */
    override suspend fun getTours(): Result<List<TourDataItem>> = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            Timber.d("getTours is called")
            return@withContext try {
                Result.Success(toursDao.getTours())
            } catch (ex: Exception) {
                Timber.e( "getTours has failed")
                Result.Error(ex.localizedMessage)
            }
        }
    }

    /**
     * Insert a reminder in the db.
     * @param reminder the reminder to be inserted
     */
    override suspend fun saveTour(tour: TourDataItem) = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            Timber.d( "saveTour is called")
            toursDao.saveTour(tour)
        }
    }

    /**
     * Get a reminder by its id
     * @param id to be used to get the reminder
     * @return Result the holds a Success object with the Reminder or an Error object with the error message
     */
    override suspend fun getTour(id: String): Result<TourDataItem> = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            try {
                Timber.d( "getTour is called for id $id")
                val tour = toursDao.getTourById(id)
                if (tour != null) {
                    Timber.i("getTour success for id $id")
                    return@withContext Result.Success(tour)
                } else {
                    Timber.e("getTour failure for id $id")
                    return@withContext Result.Error("Tour not found!")
                }
            } catch (e: Exception) {
                Timber.e("getTour exception for id $id")
                return@withContext Result.Error(e.localizedMessage)
            }
        }
    }

    /**
     * Deletes all the reminders in the db
     */
    override suspend fun deleteAllTours() = wrapEspressoIdlingResource {
        withContext(ioDispatcher) {
            Timber.d("deleteAllTours is called")
            toursDao.deleteAllTours()
        }
    }
}