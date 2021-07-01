package com.example.android.trailexperience.tours.tourslist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.android.trailexperience.R
import com.example.android.trailexperience.authentication.AuthenticationActivity
import com.example.android.trailexperience.databinding.FragmentToursListBinding
import com.example.android.trailexperience.tours.data.objects.TourItem
import com.example.android.trailexperience.utils.EspressoIdlingResource
import com.example.android.trailexperience.utils.setDisplayHomeAsUpEnabled
import com.example.android.trailexperience.utils.setTitle
import com.example.android.trailexperience.utils.setup
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ToursListFragment : Fragment() {

    //use Koin to retrieve the ViewModel instance
    val _viewModel: ToursListViewModel by viewModel()
    private lateinit var binding: FragmentToursListBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            FragmentToursListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.title_tours_list_fragment))

        binding.refreshLayout.setOnRefreshListener {
            _viewModel.fetchTours()
        }

        _viewModel.remoteToursList.observe(viewLifecycleOwner, {
            binding.refreshLayout.isRefreshing = false
            _viewModel.loadTours()
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.addReminderFAB.setOnClickListener {
            navigateToMapFragment()
        }
        navController = findNavController()

        _viewModel.showSnackBar.observe(viewLifecycleOwner, Observer {
            val snackBar = Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG)
            snackBar.addCallback(object : Snackbar.Callback() {
                override fun onShown(sb: Snackbar?) {
                    EspressoIdlingResource.increment()
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    EspressoIdlingResource.decrement()
                }
            })
            snackBar.show()
        })

        _viewModel.remoteToursList.observe(viewLifecycleOwner, { _viewModel.saveTours(it) })

        _viewModel.loadTours()
    }

    private fun navigateToMapFragment() {
        val listOfIds = mutableListOf<String>()
        _viewModel.filteredToursList.value?.forEach { unit ->
            if (unit.id != null){
                listOfIds.add(unit.id!!)
            }
            else {
                Timber.e("Invalid Id")
            }

        }
        navController.navigate(ToursListFragmentDirections.actionToursListFragmentToToursMapFragment(
            listOfIds.toTypedArray()
        ))
    }

    private fun navigateBackToLoginScreen() {
        val intent =
            Intent(activity, AuthenticationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent)
    }

    private fun navigateToDetailScreen(selectedTour: TourItem) {
        navController.navigate(ToursListFragmentDirections.actionToursListFragmentToTourDetailFragment(selectedTour.id))
    }

    private fun setupRecyclerView() {
        val adapter = ToursListAdapter { it
            -> navigateToDetailScreen(it)
        }

//        setup the recycler view using the extension function
        binding.toursListRecyclerView.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                Timber.i("Log out clicked")
                AuthUI.getInstance()
                    .signOut(requireContext())
                    .addOnCompleteListener {
                        navigateBackToLoginScreen()
                        Timber.i("Navigating back to login screen.")
                    }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        display logout as menu item
        inflater.inflate(R.menu.main_menu, menu)
    }
}