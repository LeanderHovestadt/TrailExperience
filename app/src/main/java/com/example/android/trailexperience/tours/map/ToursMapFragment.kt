package com.example.android.trailexperience.tours.map

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.android.trailexperience.R
import com.example.android.trailexperience.databinding.FragmentMapsBinding
import com.example.android.trailexperience.tours.data.objects.TourItem
import com.example.android.trailexperience.tours.data.objects.Type
import com.example.android.trailexperience.utils.setDisplayHomeAsUpEnabled
import com.example.android.trailexperience.utils.setTitle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class ToursMapFragment : Fragment(), OnMapReadyCallback {

    //use Koin to retrieve the ViewModel instance
    val _viewModel: ToursMapViewModel by viewModel()
    private lateinit var binding: FragmentMapsBinding
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            FragmentMapsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.title_maps_fragment))

        // fetch tours from localDataSource
        val arguments = ToursMapFragmentArgs.fromBundle(requireArguments())
        _viewModel.setFilteredTours(arguments.tourIds)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        _viewModel.filteredTours.observe(viewLifecycleOwner, {
            tours ->
            if (tours.isNotEmpty()){
                Timber.i("refreshing googleMap")
                refreshMapMarkers(tours)
            }
            else{
                Timber.w("Will not update UI, tours list is empty")
            }
        })

        checkPermissionsAndEnableMyLocation()
    }

    private fun refreshMapMarkers(tours : List<TourItem>){
        // clear old markers
        mMap.clear()

        val boundBuilder = LatLngBounds.builder()

        tours.forEach { tour ->

            if (tour.location != null && tour.name != null && tour.difficulty != null && tour.type != null){
                val difficultyText = when(tour.difficulty) {
                    "easy" -> getString(R.string.difficulty_easy_text)
                    "medium" -> getString(R.string.difficulty_medium_text)
                    "hard" -> getString(R.string.difficulty_hard_text)
                    else -> {
                        Timber.e("Could not parse difficulty ${tour.difficulty}")
                        "Unknown"
                    }
                }
                val typeText = when(tour.type) {
                    Type.Mountainbike -> getString(R.string.mountainbiking)
                    Type.Hike -> getString(R.string.hiking)
                    Type.Climb -> getString(R.string.climbing)
                    else -> {
                        Timber.e("Could not parse type ${tour.type}")
                        "unknown"
                    }
                }
                val title = "${tour.name} - an $difficultyText $typeText tour"
                mMap.addMarker(MarkerOptions().position(tour.location!!).title(title))?.showInfoWindow()

                boundBuilder.include(tour.location!!)
            }
            else{
                Timber.e("tour is invalid")
            }
        }

        if (tours.isNotEmpty()){
            val bounds = boundBuilder.build();
            val padding = 0 // offset from edges of the map in pixels
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)

            mMap.animateCamera(cu);
        }
    else{
            Timber.e("No tours found")
        }
    }

    /**
     * Starts the permission check and enables my location on the google map
     */
    private fun checkPermissionsAndEnableMyLocation() {
        Timber.d("checkPermissionsAndStartGooglemap")
        if (foregroundLocationPermissionApproved()) {
            Timber.i("foreground and background location permission approved")
            enableMyLocation()
        } else {
            Timber.i("requesting foreground and background location permission")
            requestForegroundLocationPermissions()
        }
    }

    /*
*  Determines whether the app has the appropriate permissions across Android 10+ and all other
*  Android versions.
*/
    @TargetApi(29)
    private fun foregroundLocationPermissionApproved(): Boolean {
        Timber.d("foregroundAndBackgroundLocationPermissionApproved called.")
        return (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))
    }

    /*
 *  Requests ACCESS_FINE_LOCATION and (on Android 10+ (Q) ACCESS_BACKGROUND_LOCATION.
 */
    @TargetApi(29)
    private fun requestForegroundLocationPermissions() {
        Timber.d("requestForegroundAndBackgroundLocationPermissions called.")
        if (foregroundLocationPermissionApproved())
            return
        val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        Timber.i("Requesting foreground location permission")
        requestPermissions(
            permissionsArray,
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        Timber.i("setting isMyLocationEnabled to true.")
        mMap.isMyLocationEnabled = true
    }

    /*
 * In all cases, we need to have the location permission.  On Android 10+ (Q) we need to have
 * the background permission as well.
 */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Timber.d("onRequestPermissionResult called.")

        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED
        ) {
            Timber.i("permission request has been denied.")
            Snackbar.make(requireView(), getString(R.string.permission_denied_explanation), Snackbar.LENGTH_LONG).show()
        } else {
            Timber.i("permission request has been approved.")
            enableMyLocation()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            if (this::mMap.isInitialized) {
                Timber.i( "Normal map type selected.")
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            } else {
                Timber.w( "Map has not yet been initialized.")
                false
            }
        }
        R.id.hybrid_map -> {
            if (this::mMap.isInitialized) {
                Timber.i( "Hybrid map type selected.")
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            } else {
                Timber.w( "Map has not yet been initialized.")
                false
            }
        }
        R.id.satellite_map -> {
            if (this::mMap.isInitialized) {
                Timber.i("Satellite map type selected.")
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            } else {
                Timber.w( "Map has not yet been initialized.")
                false
            }
        }
        R.id.terrain_map -> {
            if (this::mMap.isInitialized) {
                Timber.i( "Terrain map type selected.")
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            } else {
                Timber.w( "Map has not yet been initialized.")
                false
            }
        }
        else -> super.onOptionsItemSelected(item)
    }
}

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val LOCATION_PERMISSION_INDEX = 0