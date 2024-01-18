package com.jakubsapplication.app.modules.mapview.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.VisibleRegion
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.jakubsapplication.app.R
import com.jakubsapplication.app.appcomponents.base.BaseActivity
import com.jakubsapplication.app.databinding.ActivityMapViewBinding
import com.jakubsapplication.app.modules.chatviewcontainer.ui.ChatViewContainerActivity
import com.jakubsapplication.app.modules.homeview.ui.HomeViewActivity
import com.jakubsapplication.app.modules.mapview.data.viewmodel.MapViewVM
import com.jakubsapplication.app.modules.profilesettingview.ui.ProfileSettingViewActivity
import com.jakubsapplication.app.modules.votingview.ui.VotingViewActivity
import java.util.Calendar
import java.util.Date

const val REQUEST_LOCATION_PERMISSION = 1
var zmail = ""
var ztitle = ""
var zlat = 0.0
var zlng = 0.0
var znacznik: Marker? = null
class MapViewActivity : BaseActivity<ActivityMapViewBinding>(R.layout.activity_map_view),
    GoogleMap.OnMyLocationButtonClickListener {
    val user = Firebase.auth.currentUser
    val email = user?.email
    private var isMyLocationEnabled = true
    private val viewModel: MapViewVM by viewModels<MapViewVM>()
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val handler = Handler(Looper.getMainLooper())
    private val odswiezanieRunnable: Runnable = object : Runnable {
        override fun run() {

            if(::mMap.isInitialized)
            {
                mMap.clear()
            }
            // Tutaj umieść kod, który ma być wykonywany co 30 sekund
            println("Odświeżanie znacznikow")
            val collectionReference = db.collection("marki")
            val currentTimestamp = Date().time
            collectionReference.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        val timestamp = document.getDate("timestamp")?.time
                        val elapsedTime = currentTimestamp - timestamp!!
                        if (elapsedTime > 20 * 60 * 1000) {
                            // Usuń dokument, jeśli czas od utworzenia jest dłuższy niż 20 minut
                            deleteDocument(document.id)
                        }
                        // Tutaj możesz uzyskać dostęp do danych dokumentu
                        val documentData = document.data
                        val Lan = documentData["Lat"]
                        val Long = documentData["Long"]
                        val email = documentData["email"]
                        val new = LatLng(Lan as Double, Long as Double)
                        if(documentData["Typ"] == "Policja") {
                            mMap.addMarker(
                                MarkerOptions().position(new).title("Policja").icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.policjasmall)
                                ).snippet("$email")
                            )
                        }
                        else if(documentData["Typ"] == "Spot")
                        {
                            mMap.addMarker(
                                MarkerOptions().position(new).title("Spot").icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.spotsmall)
                                ).snippet("$email")
                            )
                        }
                        else if(documentData["Typ"] == "Utrudnienia")
                        {
                            mMap.addMarker(
                                MarkerOptions().position(new).title("Utrudnienia").icon(
                                    BitmapDescriptorFactory.fromResource(R.drawable.wypadeksmall)
                                ).snippet("$email")
                            )
                        }
                            System.out.println("pozycje to $new")
                        // Przykład: val pole2 = documentData["pole2"]
                        // itd.
                    }
                }
                .addOnFailureListener { exception ->
                    // Obsłuż błąd pobierania danych
                }
            // Ponowne uruchomienie Runnable po 60 sekundach
            handler.postDelayed(this, 60000)
        }
    }
    private fun deleteDocument(documentId: String) {
        db.collection("marki")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                // Obsłuż pomyślne usunięcie dokumentu
            }
            .addOnFailureListener { e ->
                // Obsłuż błąd usuwania dokumentu
            }
    }
    override fun onInitialized() {
        viewModel.navArguments = intent.extras?.getBundle("bundle")
        binding.mapViewVM = viewModel
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        handler.post(odswiezanieRunnable)
        val locationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
        } else {
            setupMap()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



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
            var delete = findViewById<ImageView>(R.id.delete_button)
            googleMap.setOnCameraMoveListener {
                delete.visibility = View.GONE
                if(isMyLocationEnabled)
                {disableMyLocation()
                System.out.println("Wylaczono sledzenie")}
            }

            googleMap.setOnMarkerClickListener { marker ->
                // Odczytanie niestandardowych danych z markera
                    // Tutaj możesz wykorzystać niestandardowe informacje, ale nie wyświetlać ich użytkownikowi
                    Toast.makeText(this, "${marker.title}", Toast.LENGTH_SHORT).show()
                if(marker.snippet == email) {
                    delete.visibility = View.VISIBLE
                    zmail = marker.snippet
                    ztitle = marker.title
                    zlat = marker.position.latitude
                    zlng = marker.position.longitude
                    znacznik = marker
                }


                true
            }
            googleMap.isMyLocationEnabled = true
            // googleMap.uiSettings.isCompassEnabled = true
            startLocationUpdates()
            var spot = findViewById<ImageView>(R.id.spot_button)
            var policja = findViewById<ImageView>(R.id.police_button)
            var wypadek = findViewById<ImageView>(R.id.wypadek_button)
            val spotdarkmap = R.drawable.spot
            val policjadarkmap = R.drawable.policja
            val wypadekdarkmap = R.drawable.wypadek
            val spotmap = R.drawable.spotb
            val policjamap = R.drawable.policjab
            val wypadekmap = R.drawable.wypadekb
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (currentHour >= 16 || currentHour < 8) {
                spot.setImageResource(spotdarkmap)
                policja.setImageResource(policjadarkmap)
                wypadek.setImageResource(wypadekdarkmap)
            } else {
                spot.setImageResource(spotmap)
                policja.setImageResource(policjamap)
                wypadek.setImageResource(wypadekmap)
            }
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
            googleMap.setOnMyLocationButtonClickListener(this)
        }
    }
    override fun onMyLocationButtonClick(): Boolean {
        Handler(Looper.getMainLooper()).postDelayed({
            // Tutaj umieść kod, który ma zostać wykonany po opóźnieniu
            enableMyLocation()
        }, 1000)

        System.out.println("Rozpoczeto sledzenie")
        return false
    }
    private fun enableMyLocation() {
        Handler(Looper.getMainLooper()).postDelayed({
            isMyLocationEnabled = true
            startLocationUpdates()
        }, 2000)


    }

    private fun disableMyLocation() {
        // Wyłączanie śledzenia lokalizacji
        if (isMyLocationEnabled) {
            isMyLocationEnabled = false
            stopLocationUpdates()
        }
    }
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            interval = 2000 // Interwał aktualizacji lokalizacji w milisekundach (np. co 5 sekund)
            fastestInterval = 1000 // Najkrótszy możliwy interwał
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
        getLastLocation()
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
        handler.removeCallbacks(odswiezanieRunnable)
        stopLocationUpdates()
        super.onDestroy()
    }

    private fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            return false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    private fun getLastLocation() {
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
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Zapisz współrzędne lokalizacji do zmiennych
                    currentLatitude = it.latitude
                    currentLongitude = it.longitude

                    // Tutaj możesz wywołać dowolne inne funkcje lub operacje na pobranej lokalizacji
                }
            }
    }
    override fun setUpClicks() {
        binding.deleteButton.setOnClickListener{
            var delete = findViewById<ImageView>(R.id.delete_button)
            delete.visibility = View.GONE
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("marki")
                .whereEqualTo("Lat", zlat)
                .whereEqualTo("Long", zlng)
                .whereEqualTo("Typ", ztitle)
                .whereEqualTo("email", zmail)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Usuń znaleziony dokument
                        document.reference.delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Znacznik został poprawnie usunięty", Toast.LENGTH_SHORT).show()
                                znacznik?.remove()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Wystąpił błąd podczas usuwania znacznika", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    // Obsługa błędu
                    // ...
                }
        }
        binding.policeButton.setOnClickListener {
            getLastLocation()
            val firestore = FirebaseFirestore.getInstance()
            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                Toast.makeText(this,"Znacznik zapisany pomyślnie.", Toast.LENGTH_SHORT).show()
                // Nazwa kolekcji, w której chcesz dodać dokument
                val collectionName = "marki"
                // Dane do zapisania
                val newData = hashMapOf(
                    "Typ" to "Policja",
                    "Lat" to currentLatitude,
                    "Long" to currentLongitude,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "email" to email
                )
                // Dodanie nowego dokumentu do kolekcji
                firestore.collection(collectionName).add(newData)
                    .addOnSuccessListener { documentReference ->
                        println("Nowy dokument został pomyślnie dodany. ID dokumentu: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        println("Wystąpił błąd: ${e.message}")
                    }
            } else {
                Toast.makeText(this, "Poczekaj na załadowanie lokalizacji", Toast.LENGTH_SHORT).show()
            }
            val new = LatLng(currentLatitude as Double, currentLongitude as Double)
            mMap.addMarker(
                MarkerOptions().position(new).title("Patrol").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.policjasmall)
                ).snippet(email)
            )
        }
        binding.spotButton.setOnClickListener {
            getLastLocation()
            val firestore = FirebaseFirestore.getInstance()
            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                Toast.makeText(this,"Znacznik zapisany pomyślnie.", Toast.LENGTH_SHORT).show()
                // Nazwa kolekcji, w której chcesz dodać dokument
                val collectionName = "marki"
                // Dane do zapisania
                val newData = hashMapOf(
                    "Typ" to "Spot",
                    "Lat" to currentLatitude,
                    "Long" to currentLongitude,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "email" to email
                )
                // Dodanie nowego dokumentu do kolekcji
                firestore.collection(collectionName).add(newData)
                    .addOnSuccessListener { documentReference ->
                        println("Nowy dokument został pomyślnie dodany. ID dokumentu: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        println("Wystąpił błąd: ${e.message}")
                    }
            } else {
                Toast.makeText(this, "Poczekaj na załadowanie lokalizacji", Toast.LENGTH_SHORT).show()
            }
            val new = LatLng(currentLatitude as Double, currentLongitude as Double)
            mMap.addMarker(
                MarkerOptions().position(new).title("Spot").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.spotsmall)
                ).snippet(email)
            )
        }
        binding.wypadekButton.setOnClickListener {
            getLastLocation()
            val firestore = FirebaseFirestore.getInstance()
            if (currentLatitude != 0.0 && currentLongitude != 0.0) {
                Toast.makeText(this,"Znacznik zapisany pomyślnie.", Toast.LENGTH_SHORT).show()
                // Nazwa kolekcji, w której chcesz dodać dokument
                val collectionName = "marki"
                // Dane do zapisania
                val newData = hashMapOf(
                    "Typ" to "Utrudnienia",
                    "Lat" to currentLatitude,
                    "Long" to currentLongitude,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "email" to email
                )
                // Dodanie nowego dokumentu do kolekcji
                firestore.collection(collectionName).add(newData)
                    .addOnSuccessListener { documentReference ->
                        println("Nowy dokument został pomyślnie dodany. ID dokumentu: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        println("Wystąpił błąd: ${e.message}")
                    }
            } else {
                Toast.makeText(this, "Poczekaj na załadowanie lokalizacji", Toast.LENGTH_SHORT).show()
            }
            val new = LatLng(currentLatitude as Double, currentLongitude as Double)
            mMap.addMarker(
                MarkerOptions().position(new).title("Utrudnienia").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.wypadeksmall)
                ).snippet(email)
            )
        }
        binding.imageMenu.setOnClickListener {
            startActivity(ChatViewContainerActivity.getIntent(this, null))
        }
        binding.imageLineOne.setOnClickListener {
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
