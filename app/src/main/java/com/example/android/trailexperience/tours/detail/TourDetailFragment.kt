package com.example.android.trailexperience.tours.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.android.trailexperience.R
import com.example.android.trailexperience.databinding.FragmentDetailTourBinding
import com.example.android.trailexperience.databinding.FragmentMapsBinding
import com.example.android.trailexperience.utils.setDisplayHomeAsUpEnabled
import com.example.android.trailexperience.utils.setTitle
import org.koin.androidx.viewmodel.ext.android.viewModel

class TourDetailFragment : Fragment() {

    //use Koin to retrieve the ViewModel instance
    val _viewModel: TourDetailViewModel by viewModel()
    private lateinit var binding: FragmentDetailTourBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            FragmentDetailTourBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        val arguments = TourDetailFragmentArgs.fromBundle(requireArguments())

        _viewModel.setDetailTourFromId(arguments.tourId)

        _viewModel.item.observe(viewLifecycleOwner, {
            item -> binding.tour = item
        })

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.title_tour_detail_fragment))

        return binding.root
    }
}