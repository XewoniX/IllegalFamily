package com.jakubsapplication.app.modules.mapview.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityMapViewBinding
import com.jakubsapplication.app.modules.chatviewcontainer.ui.ChatViewContainerActivity
import com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity
import com.jakubsapplication.app.modules.mapview.`data`.viewmodel.MapViewVM
import com.jakubsapplication.app.modules.profilesettingview.ui.ProfileSettingViewActivity
import com.jakubsapplication.app.modules.votingview.ui.VotingViewActivity
import java.util.Calendar

const val REQUEST_LOCATION_PERMISSION = 1

class MapViewActivity : BaseActivity<ActivityMapViewBinding>(R.layout.activity_map_view) {

  private val viewModel: MapViewVM by viewModels<MapViewVM>()
  private lateinit var mMap: GoogleMap

  override fun onInitialized() {
    viewModel.navArguments = intent.extras?.getBundle("bundle")
    binding.mapViewVM = viewModel
    window.decorView.systemUiVisibility =
      View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    val locationPermission =
      ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    if (locationPermission != PackageManager.PERMISSION_GRANTED) {
      requestLocationPermission()
    } else {
      setupMap()
    }



  }
  private fun requestLocationPermission() {
    ActivityCompat.requestPermissions(
      this,
      arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
      REQUEST_LOCATION_PERMISSION
    )
  }
  private fun setupMap() {
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync { googleMap ->
      mMap = googleMap
      googleMap.isMyLocationEnabled = true
      googleMap.uiSettings.isCompassEnabled = true
      startLocationUpdates()

      val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
      val mapStyle = if (currentHour >= 16 || currentHour < 8) {
        R.raw.dark_map
      } else {
        R.raw.standard_map
      }

      googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapStyle))

      val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

      // Zaktualizowany kod uzyskiwania dostępu do lokalizacji
      if (ActivityCompat.checkSelfPermission(
          this,
          Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
          this,
          Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
      ) {
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        location?.let {
          val currentLatLng = LatLng(it.latitude, it.longitude)
          googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        }
      }
    }
  }

  private val locationRequest: LocationRequest by lazy {
    LocationRequest.create().apply {
      interval = 5000 // Interwał aktualizacji lokalizacji w milisekundach (np. co 5 sekund)
      fastestInterval = 3000 // Najkrótszy możliwy interwał
      priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

  }

  private val locationCallback = object : LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
      locationResult.lastLocation?.let { location ->
        val currentLatLng = LatLng(location.latitude, location.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
      }
    }
  }
  private fun startLocationUpdates() {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    if (ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }
    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
  }
  private fun stopLocationUpdates() {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    fusedLocationClient.removeLocationUpdates(locationCallback)
  }
  override fun onPause() {
    super.onPause()
    stopLocationUpdates()
  }
  override fun onDestroy() {
    super.onDestroy()
    stopLocationUpdates()
  }
  override fun setUpClicks() {
    binding.imageMenu.setOnClickListener {
      startActivity(ChatViewContainerActivity.getIntent(this, null))
    }
    binding.frameCheckringligh.setOnClickListener {
      startActivity(VotingViewActivity.getIntent(this, null))
    }
    binding.imageUser.setOnClickListener {
      startActivity(ProfileSettingViewActivity.getIntent(this, null))
    }
    binding.imageHome.setOnClickListener {
      startActivity(HomeViewActivity.getIntent(this, null))
    }
  }
  companion object {
    const val TAG: String = "MAP_VIEW_ACTIVITY"
    fun getIntent(context: Context, bundle: Bundle?): Intent {
      val destIntent = Intent(context, MapViewActivity::class.java)
      destIntent.putExtra("bundle", bundle)
      return destIntent
    }
  }
}