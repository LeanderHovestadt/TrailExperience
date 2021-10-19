package com.example.android.trailexperience.tours.add.selectlocation

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.android.trailexperience.R
import com.example.android.trailexperience.databinding.FragmentSelectLocationBinding
import com.example.android.trailexperience.databinding.FragmentToursListBinding
import com.example.android.trailexperience.tours.add.ToursAddFragmentArgs
import com.example.android.trailexperience.tours.add.ToursAddViewModel
import com.example.android.trailexperience.utils.setDisplayHomeAsUpEnabled
import com.example.android.trailexperience.utils.setTitle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class SelectLocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var pointOfInterest: PointOfInterest
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentSelectLocationBinding.inflate(inflater)
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.title_select_location_fragment))

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_select_location) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.saveFabSelectLocation.setOnClickListener {
            onLocationSelected()
        }
    }

    private fun onLocationSelected() {
        if (this::pointOfInterest.isInitialized) {
            Timber.i( "Successfully selected location.")
            navController.navigate(SelectLocationFragmentDirections.actionSelectLocationFragmentToToursAddFragment(null, pointOfInterest.latLng))
        } else {
            Timber.i( "Location has not yet been selected.")
            Snackbar.make(requireView(), getString(R.string.select_location_err), Snackbar.LENGTH_LONG).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            if (this::map.isInitialized) {
                Timber.i( "Normal map type selected.")
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            } else {
                Timber.w( "Map has not yet been initialized.")
                false
            }
        }
        R.id.hybrid_map -> {
            if (this::map.isInitialized) {
                Timber.i( "Hybrid map type selected.")
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            } else {
                Timber.w( "Map has not yet been initialized.")
                false
            }
        }
        R.id.satellite_map -> {
            if (this::map.isInitialized) {
                Timber.i( "Satellite map type selected.")
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            } else {
                Timber.w( "Map has not yet been initialized.")
                false
            }
        }
        R.id.terrain_map -> {
            if (this::map.isInitialized) {
                Timber.i( "Terrain map type selected.")
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            } else {
                Timber.w( "Map has not yet been initialized.")
                false
            }
        }
        else -> super.onOptionsItemSelected(item)
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
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        Timber.i( "Map is ready.")

        map = googleMap
        val zoomLevel = 15f

        val arguments = SelectLocationFragmentArgs.fromBundle(requireArguments())
        if (arguments.location != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(arguments.location!!, zoomLevel))
        }
        else {
            //Default location
            val latitude = 51.58333
            val longitude = 6.51667
            val alpen = LatLng(latitude, longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(alpen, zoomLevel))
        }

        setPoiClickListener(map)
        setOnMapClickListener(map)
        checkPermissionsAndEnableMyLocation()
    }

    private fun setPoiClickListener(map: GoogleMap) {
        Timber.i( "Setting POI click listener.")

        map.setOnPoiClickListener { poi ->

            map.clear()
            pointOfInterest = poi

            map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )?.showInfoWindow()

            map.addCircle(
                CircleOptions()
                    .center(poi.latLng)
                    .radius(200.0)
                    .strokeColor(Color.argb(255, 255, 0, 0))
                    .fillColor(Color.argb(64, 255, 0, 0)).strokeWidth(4F)

            )
        }
    }


    private fun setOnMapClickListener(map: GoogleMap) {
        Timber.i( "Setting click listener.")

        map.setOnMapClickListener { point ->

            map.clear()
            pointOfInterest = PointOfInterest(
                LatLng(point.latitude, point.longitude),
                UUID.randomUUID().toString(),
                "Point at %.2f/%.2f".format(point.latitude, point.longitude)
            )

            map.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
            )?.showInfoWindow()

            map.addCircle(
                CircleOptions()
                    .center(pointOfInterest.latLng)
                    .radius(200.0)
                    .strokeColor(Color.argb(255, 255, 0, 0))
                    .fillColor(Color.argb(64, 255, 0, 0)).strokeWidth(4F)

            )
        }
    }

    /**
     * Starts the permission check and Geofence process only if the Geofence associated with the
     * current hint isn't yet active.
     */
    private fun checkPermissionsAndEnableMyLocation() {
        Timber.d( "checkPermissionsAndStartGooglemap")
        if (foregroundLocationPermissionApproved()) {
            Timber.i( "foreground and background location permission approved")
            enableMyLocation()
        } else {
            Timber.i( "requesting foreground and background location permission")
            requestForegroundLocationPermissions()
        }
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
        Timber.d( "onRequestPermissionResult called.")

        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED
        ) {
            Timber.i( "permission request has been denied.")
            Snackbar.make(requireView(), getString(R.string.permission_denied_explanation), Snackbar.LENGTH_LONG).show()
        } else {
            Timber.i( "permission request has been approved.")
            enableMyLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        Timber.i( "setting isMyLocationEnabled to true.")
        map.isMyLocationEnabled = true
    }

    /*
 *  Determines whether the app has the appropriate permissions across Android 10+ and all other
 *  Android versions.
 */
    @TargetApi(29)
    private fun foregroundLocationPermissionApproved(): Boolean {
        Timber.d( "foregroundAndBackgroundLocationPermissionApproved called.")
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
        Timber.d( "requestForegroundAndBackgroundLocationPermissions called.")
        if (foregroundLocationPermissionApproved())
            return
        val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        Timber.i( "Requesting foreground location permission")
        requestPermissions(
            permissionsArray,
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )
    }
}

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val LOCATION_PERMISSION_INDEX = 0

