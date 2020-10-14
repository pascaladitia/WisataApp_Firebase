package com.pascal.wisataappfirebase.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pascal.wisataappfirebase.R
import com.pascal.wisataappfirebase.model.local.wisata.Wisata
import com.pascal.wisataappfirebase.model.online.WisataFirebase
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var item: Wisata? = null
    private var firebase: WisataFirebase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), PackageManager.PERMISSION_GRANTED
        )

        getParcel()
    }

    private fun getParcel() {
        item = intent?.getParcelableExtra("data")
        firebase = intent?.getParcelableExtra("firebase")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (item != null) {
            val kampung = LatLng(item?.latitude!!.toDouble(), item?.longtitude!!.toDouble())
            mMap.addMarker(MarkerOptions().position(kampung).title("Marker in ${item?.location}"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(kampung))

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kampung, 16f))
        } else {
            val kampung = LatLng(-6.8148909, 106.6771815)
            mMap.addMarker(MarkerOptions().position(kampung).title("Marker"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(kampung))

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kampung, 16f))
        }

        //setting zoom in/out
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        initButton()
    }

    private fun initButton() {
        btn_hybrid.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        }
        btn_satelit.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
        btn_terrain.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
        btn_normal.setOnClickListener {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        }

        //add to realtime firebase
        mMap.setOnMapClickListener {
            val lat = it.latitude
            val lon = it.longitude

            mMap.clear()

            val nama = convertCoordinat(lat, lon)
            maps_name.text = "$lat - $lon"
            maps_kordinat.text = nama

            mMap.addMarker(MarkerOptions().position(LatLng(lat, lon)).title("Marker in $nama"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, lon)))

            mapsRegister_save.setOnClickListener {
                saveData(nama, lat, lon)
            }
        }
    }

    private fun saveData(nama: String, lat: Double, lon: Double) {
        AlertDialog.Builder(this).apply {
            setTitle("Simpan")
            setMessage("Yakin ingin menyimpan Marker?")
            setCancelable(false)

            setPositiveButton("Ya") { dialog, which ->
                intentUI(lat, lon)
                finish()
            }
            setNegativeButton("Batal") { dialog, which ->
                dialog?.dismiss()
            }
        }.show()
    }

    private fun intentUI(lat: Double, lon: Double) {
        var intent = Intent(this, InputActivity::class.java)
        intent.putExtra("lat", lat.toString())
        intent.putExtra("lon", lon.toString())
        intent.putExtra("data", item)
        intent.putExtra("firebase", firebase)
        startActivity(intent)
    }

    private fun convertCoordinat(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this)
        val dataLocation = geocoder.getFromLocation(lat, lon, 1)
        val nameLocation = dataLocation.get(0).featureName

        return nameLocation
    }
}