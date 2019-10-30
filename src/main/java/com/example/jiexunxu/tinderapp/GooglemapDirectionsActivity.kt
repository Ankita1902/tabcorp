package com.example.jiexunxu.tinderapp

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import java.util.ArrayList


internal class GooglemapDirectionsActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private var map: GoogleMap? = null

    private var places: ArrayList<YelpPlace>? = null
    private var selectedPlace: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_googlemap_directions)
        mapView = findViewById(R.id.mapView)
        mapView!!.onCreate(null)
        mapView!!.getMapAsync(this)
        val backButton = findViewById<Button>(R.id.googleMapBackButton)
        backButton.setOnClickListener {
            val intent = Intent(this@GooglemapDirectionsActivity, SlideShowActivity::class.java)
            this@GooglemapDirectionsActivity.startActivity(intent)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(null)
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
        val args = intent.getBundleExtra("bundle")
        places = args.getSerializable("places") as ArrayList<YelpPlace>
        selectedPlace = intent.getIntExtra("selectedPlace", -1)
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onPause() {
        mapView!!.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView!!.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.setMinZoomPreference(15f)
        val place = places!![selectedPlace]
        map!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(place.lat, place.lng)))
        map!!.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
        map!!.addMarker(MarkerOptions().title(place.name).position(LatLng(place.lat, place.lng)))
    }
}
