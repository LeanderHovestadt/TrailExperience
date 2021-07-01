package com.example.android.trailexperience

import android.app.Application
import com.example.android.trailexperience.tours.data.local.ToursLocalDataSource
import com.example.android.trailexperience.tours.data.local.LocalDB
import com.example.android.trailexperience.tours.data.local.ToursLocalRepository
import com.example.android.trailexperience.tours.data.remote.ToursRemoteDataSource
import com.example.android.trailexperience.tours.data.remote.ToursRemoteService
import com.example.android.trailexperience.tours.detail.TourDetailViewModel
import com.example.android.trailexperience.tours.map.ToursMapViewModel
import com.example.android.trailexperience.tours.tourslist.ToursListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                ToursListViewModel(
                    get(),
                    get() as ToursLocalDataSource,
                    get() as ToursRemoteDataSource)
            }

            viewModel {
                ToursMapViewModel(get(),
                get() as ToursLocalDataSource)
            }

            viewModel {
                TourDetailViewModel(get(),
                get() as ToursLocalDataSource)
            }
            //Declare singleton definitions to be later injected using by inject()

            // ToursLocalRepository
            single {
                ToursLocalRepository(get())
            }
            // ReminderDataSource
            single<ToursLocalDataSource> {
                get<ToursLocalRepository>()
            }
            single { LocalDB.createToursDao(this@MyApp) }
            single {
                ToursRemoteService(get())
            }
            single<ToursRemoteDataSource> { get<ToursRemoteService>() }
        }

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(myModule))
        }
    }
}