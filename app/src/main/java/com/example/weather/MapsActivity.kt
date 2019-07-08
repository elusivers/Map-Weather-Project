package com.example.weather

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    companion object{

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location

    override fun onMarkerClick(p0: Marker?): Boolean {
        val detailsFragment = DetailsListDialogFragment.newInstance(4, p0!!.position)
        detailsFragment.show(supportFragmentManager, "DetailsListDialogFragment")
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //pobieramy naszą lokalizację
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        map = googleMap
        val centerOfPoland = LatLng(52.044925, 19.069815)

        setUpMap()

        map.setOnMarkerClickListener(this)

        //pobieramy naszą lokalizację
        map.isMyLocationEnabled = true //dodaje również przycisk wyśrodkowujący naszą lokalizację
        //nic
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if(location != null){
                val myLatLng = LatLng(location.latitude, location.longitude)
            }
        }

        // Add a marker in Sydney and move the camera

        val braavos = LatLng(52.3833318, 17.166666)
        val winterfell = LatLng(52.62481,18.52771)
        val kings_landing = LatLng(52.237049, 21.017532)
        val qarth = LatLng(52.409538, 16.931992)
        val valyria = LatLng(50.049683, 19.944544)
        val pentos = LatLng(51.107883, 17.038538)
        val yunkai = LatLng(51.759445, 19.457216)
        map.addMarker(MarkerOptions().position(braavos).title("Siekierki Wielkie")
            .icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN)))
        map.addMarker(MarkerOptions().position(winterfell).title("Radziejów")
            .icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_BLUE)))
        map.addMarker(MarkerOptions().position(kings_landing).title("Warszawa"))
        map.addMarker(MarkerOptions().position(qarth).title("Poznań"))
        map.addMarker(MarkerOptions().position(valyria).title("Kraków"))
        map.addMarker(MarkerOptions().position(pentos).title("Wrocław"))
        map.addMarker(MarkerOptions().position(yunkai).title("Łódź"))
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(lodz, 5.8f))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(centerOfPoland, 5.8f))

    }

    //nadanie zezwolenia naszej lokalizacji
    private fun setUpMap(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
    }
}
