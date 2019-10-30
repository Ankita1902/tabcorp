package com.example.jiexunxu.tinderapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

import java.util.ArrayList

import pl.droidsonroids.gif.GifImageButton

class MainActivity : AppCompatActivity(), LocationListener {
    // GPS location related variables
    private var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val settings = SettingsParams()
        settings.readSettingsFromFile(this.applicationContext)
        initUI()
        initMainActivityButtons()
    }

    override fun onBackPressed() {
        this.finish()
    }

    private fun initUI() {
        title = "Pick a category"
        val layout = findViewById<ConstraintLayout>(R.id.mainLayout)
        val customSearchButton = findViewById<Button>(R.id.mainPageCustomzieSearchButton)
        val opts = AppOptions.getUIOptions(SettingsParams.themeID)
        layout.setBackgroundColor(applicationContext.resources.getColor(opts.backgroundColor))
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(applicationContext.resources.getColor(opts.primaryColor)))
        customSearchButton.setBackgroundResource(opts.buttonStyle)
    }

    private fun initMainActivityButtons() {
        val restaurantGifButton = findViewById<GifImageButton>(R.id.restaurantGif)
        val shoppingGifButton = findViewById<GifImageButton>(R.id.shoppingGif)
        val hotelsGifButton = findViewById<GifImageButton>(R.id.hotelsGif)
        val fitnessGifButton = findViewById<GifImageButton>(R.id.fitnessGif)
        val entertainmentGifButton = findViewById<GifImageButton>(R.id.entertainmentGif)
        val beautyGifButton = findViewById<GifImageButton>(R.id.beautyGif)
        val nightlifeGifButton = findViewById<GifImageButton>(R.id.nightlifeGif)
        val petsGifButton = findViewById<GifImageButton>(R.id.petsGif)
        val foodDeliveryButton = findViewById<GifImageButton>(R.id.foodDeliveryGif)
        val customSearchButton = findViewById<Button>(R.id.mainPageCustomzieSearchButton)

        customSearchButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            this@MainActivity.startActivity(intent)
        }

        restaurantGifButton.setOnClickListener { startSearch("food,restaurants") }

        shoppingGifButton.setOnClickListener { startSearch("shopping") }

        hotelsGifButton.setOnClickListener { startSearch("hotelstravel") }

        fitnessGifButton.setOnClickListener { startSearch("active") }

        entertainmentGifButton.setOnClickListener { startSearch("arts") }

        beautyGifButton.setOnClickListener { startSearch("beautysvc,health") }

        nightlifeGifButton.setOnClickListener { startSearch("nightlife") }

        petsGifButton.setOnClickListener { startSearch("pets") }

        foodDeliveryButton.setOnClickListener { startSearch("fooddelivery") }

        /*
        final GifImageButton servicesGifButton=findViewById(R.id.entertainmentGif);
        servicesGifButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startSearch("homeservices,localservices,professional,publicservicesgovt");
            }
        });
        */
    }

    private fun startSearch(category: String) {
        val settings = SettingsParams()
        settings.readSettingsFromFile(this.applicationContext)
        val params = settings.settingsToYelpParams()
        if (category == "fooddelivery")
            params.mustHaveFoodDelivery = true
        else
            params.setCategories(category)
        if (!params.getParams().containsKey("location")) {
            try {
                getDeviceLocation()
                params.setLatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
            } catch (ex: Exception) {
                ErrorActivity.start(this@MainActivity, "GPS service unavailable. Please specify a search address in advanced search to search around that area.")
            }

        }
        val intent = Intent(this@MainActivity, WaitSearchActivity::class.java)
        intent.putExtra("params", params)
        this@MainActivity.startActivity(intent)
    }

    private fun getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val location_context = Context.LOCATION_SERVICE
            val lm = getSystemService(location_context) as LocationManager
            val providers = lm.getProviders(true)
            for (provider in providers) {
                lm.requestLocationUpdates(provider, 1000, 0f, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        lastKnownLocation = location
                    }

                    override fun onProviderDisabled(provider: String) {}

                    override fun onProviderEnabled(provider: String) {}

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                })
                val l = lm.getLastKnownLocation(provider)
                if (lastKnownLocation == null || l != null && l.accuracy < lastKnownLocation!!.accuracy) {
                    lastKnownLocation = l
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onLocationChanged(loc: Location) {
        lastKnownLocation = loc
        if (SettingsParams.debugMode) {
            val Text = ("My current location is: " + "Latitud = "
                    + loc.latitude + "Longitud = " + loc.longitude)
            Log.d("loc", "onLocationChanged$Text")
        }
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
}
