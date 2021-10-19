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
import com.example.android.trailexperience.tours.data.objects.Type
import com.example.android.trailexperience.utils.EspressoIdlingResource
import com.example.android.trailexperience.utils.setDisplayHomeAsUpEnabled
import com.example.android.trailexperience.utils.setTitle
import com.example.android.trailexperience.utils.setup
import com.firebase.ui.auth.AuthUI
import com.google.android.material.chip.ChipGroup
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
            Timber.i("remote tours have been updated, fetched ${it.size} items")
            binding.refreshLayout.isRefreshing = false
        })

        _viewModel.reset()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.mapFab.setOnClickListener {
            navigateToMapFragment()
        }

        binding.menuFab.setOnClickListener {
            _viewModel.toggleFabMenu()
        }

        binding.addFab.setOnClickListener {
            navigateToAddFragment()
        }

        binding.chipgroupType.setOnCheckedChangeListener { _: ChipGroup, i: Int ->
            when (i) {
                R.id.chip_climb -> _viewModel.updateTypeFilter(Type.Climb)
                R.id.chip_mtb -> _viewModel.updateTypeFilter(Type.Mountainbike)
                R.id.chip_hike -> _viewModel.updateTypeFilter(Type.Hike)
                View.NO_ID -> _viewModel.updateTypeFilter(Type.All)
            }
        }

        _viewModel.showFabMenu.observe(viewLifecycleOwner, {
            if (it) {
                openFabMenu()
            }
            else {
                closeFabMenu()
            }
        })


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
        _viewModel.remoteUpdateRequired.observe(viewLifecycleOwner, {
            if (it) {
                _viewModel.fetchTours()
            }
        })
        _viewModel.localUpdateRequired.observe(viewLifecycleOwner, {
            if (it) {
                binding.chipgroupType.clearCheck()
                _viewModel.loadTours(Type.All)
            }
        })

        _viewModel.loadTours(null)
    }

    private fun closeFabMenu() {
        Timber.d("closeFabMenu called")

        binding.addFab.bringToFront()
        binding.mapFab.bringToFront()
        binding.menuFab.bringToFront()

        binding.invalidateAll()

        binding.mapFab.animate().translationY(0F);
        binding.addFab.animate().translationY(0F);
    }

    private fun openFabMenu() {
        Timber.d("openFabMenu called")

        binding.addFab.visibility = View.VISIBLE
        binding.mapFab.visibility = View.VISIBLE

        binding.addFab.bringToFront()
        binding.mapFab.bringToFront()
        binding.menuFab.bringToFront()

        binding.invalidateAll()

        binding.mapFab.animate().translationY(-resources.getDimension(R.dimen.standard_55));
        binding.addFab.animate().translationY(-resources.getDimension(R.dimen.standard_105));
    }

    private fun navigateToAddFragment() {
        navController.navigate(ToursListFragmentDirections.actionToursListFragmentToToursAddFragment())
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