package com.example.android.trailexperience.tours.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.android.trailexperience.R
import com.example.android.trailexperience.databinding.FragmentAddBinding
import com.example.android.trailexperience.databinding.FragmentDetailTourBinding
import com.example.android.trailexperience.tours.data.objects.TourItem
import com.example.android.trailexperience.tours.data.objects.Type
import com.example.android.trailexperience.tours.detail.TourDetailFragmentArgs
import com.example.android.trailexperience.tours.detail.TourDetailViewModel
import com.example.android.trailexperience.utils.EspressoIdlingResource
import com.example.android.trailexperience.utils.setDisplayHomeAsUpEnabled
import com.example.android.trailexperience.utils.setTitle
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

val DEFAULT_TOUR = TourItem("", "", "medium", "", null, Type.Hike, null, null, null)

class ToursAddFragment : Fragment(){

    //use Koin to retrieve the ViewModel instance
    val _viewModel: ToursAddViewModel by viewModel()
    private lateinit var binding: FragmentAddBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            FragmentAddBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        val arguments = ToursAddFragmentArgs.fromBundle(requireArguments())
        if (arguments.tour != null) {
            binding.tour = arguments.tour
        }
        else
        {
            binding.tour = DEFAULT_TOUR
        }

        arguments.location?.let { location ->
            binding.tour?.let { tour ->
                tour.location = location
            }
        }

        binding.saveFab.setOnClickListener {
            binding.tour?.let { tour ->
                _viewModel.saveTour(tour)
            }
        }

        _viewModel.showMissingDetails.observe(viewLifecycleOwner, {
            if (it) {
                val snackBar = Snackbar.make(requireView(), getString(R.string.enter_missing_details), Snackbar.LENGTH_LONG)
                snackBar.addCallback(object : Snackbar.Callback() {
                    override fun onShown(sb: Snackbar?) {
                        EspressoIdlingResource.increment()
                    }

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        EspressoIdlingResource.decrement()
                        _viewModel.onMissingDetailsShown()
                    }
                })
                snackBar.show()
            }
        })

        _viewModel.onTourSaved.observe(viewLifecycleOwner, {
            Timber.i("onTourSaved: $it")
            if (it){
                reset()
                navController.navigate(ToursAddFragmentDirections.actionToursAddFragmentToToursListFragment())
            }
        })

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.title_tour_add_fragment))

        return binding.root
    }

    private fun reset() {
        binding.tour = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.selectLocationButton.setOnClickListener {
            var location: LatLng? = null
            binding.tour?.let { tour ->
                location = tour.location
            }

            navController.navigate(ToursAddFragmentDirections.actionToursAddFragmentToSelectLocationFragment(location))
        }
    }
}