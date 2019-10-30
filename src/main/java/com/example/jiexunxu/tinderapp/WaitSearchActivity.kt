package com.example.jiexunxu.tinderapp

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button

import com.yelp.fusion.client.connection.YelpFusionApi
import com.yelp.fusion.client.connection.YelpFusionApiFactory
import com.yelp.fusion.client.models.Business
import com.yelp.fusion.client.models.Category
import com.yelp.fusion.client.models.SearchResponse

import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap

import retrofit2.Call


class WaitSearchActivity : AppCompatActivity() {
    // Yelp fusion API Key
    private val YelpFusionAPIKey = "8JjOowcYfC9pTdTSAecKpdk6rCAV1lDY-N01QtuDw_K33ML6o-DUmXErJuZujMoYcMdLFp7VK41ajShMXjNhdzgjaZIkdR2qAAToD68zo6my62RssH0sa3d9BT2sWnYx"
    internal var placeSearch: YelpFusionAPIQuery

    // Used for storing current places.
    internal var places: ArrayList<YelpPlace>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_for_search)
        initUI()
        initButtons()
    }

    // Disable back button on the phone
    override fun onBackPressed() {
        cancelSearch()
    }

    override fun onStart() {
        super.onStart()
        val params = intent.getSerializableExtra("params") as YelpFusionParams
        doYelpSearch(params)
    }

    private fun doYelpSearch(params: YelpFusionParams) {
        try {
            placeSearch = YelpFusionAPIQuery(YelpFusionAPIKey, applicationContext)
            placeSearch.execute(params)
        } catch (ex: Exception) {
            if (SettingsParams.debugMode)
                Log.d("Error", "==========FATAL ERROR: Unable to call yelp API=============")
            ErrorActivity.start(this, "Unable to call remote yelp API for search. Either there's no internet, the search quota has been full, or something else has gong wrong...")
        }

    }

    private fun initUI() {
        title = "Searching..."
        val layout = findViewById<ConstraintLayout>(R.id.waitSearchLayout)
        val opts = AppOptions.getUIOptions(SettingsParams.themeID)
        layout.setBackgroundColor(applicationContext.resources.getColor(opts.backgroundColor))
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(applicationContext.resources.getColor(opts.primaryColor)))
        val startOverButton = findViewById<Button>(R.id.startOverButton)
        startOverButton.setBackgroundResource(opts.buttonStyle)
        //   startOverButton.setCompoundDrawables(getApplicationContext().getResources().getDrawable( opts.buttonStyle), null, null, null);
    }

    private fun initButtons() {
        val startOverButton = findViewById<Button>(R.id.startOverButton)
        val gif = findViewById<pl.droidsonroids.gif.GifTextView>(R.id.waitSearchGifAnimation)
        startOverButton.setOnClickListener { cancelSearch() }
    }

    private fun cancelSearch() {
        placeSearch.cancelTask()
        val intent = Intent(this@WaitSearchActivity, MainActivity::class.java)
        this@WaitSearchActivity.startActivity(intent)
    }
}

internal class YelpFusionAPIQuery(APIKey: String, // Used to fire up the slideshowactivity on postExecute
                                  private val context: Context) : AsyncTask<YelpFusionParams, Void, ArrayList<YelpPlace>>() {
    var places: ArrayList<YelpPlace>? = null
        private set
    private var yelpAPI: YelpFusionApi? = null

    private var taskCanceled: Boolean = false

    init {
        try {
            yelpAPI = YelpFusionApiFactory().createAPI(APIKey)
        } catch (ex: IOException) {
            Log.d("Error", "Unable to create yelp fusion API")
        }

    }

    fun cancelTask() {
        taskCanceled = true
    }

    override fun doInBackground(vararg params: YelpFusionParams): ArrayList<YelpPlace>? {
        YelpFusionAPI_BusinessSearch(params[0])
        YelpPlace.sortKey = params[0].sortKey
        return places
    }

    override fun onPostExecute(result: ArrayList<YelpPlace>?) {
        super.onPostExecute(result)
        if (!taskCanceled) {
            if (result != null && result.size > 0) {
                if (YelpPlace.sortKey > 0)
                    Collections.sort(result)
                if (SettingsParams.debugMode)
                    for (i in places!!.indices)
                        Log.d("place", places!![i].toString())
                val intent = Intent(context, SlideShowActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val bundle = Bundle()
                bundle.putSerializable("places", result)
                intent.putExtra("bundle", bundle)
                context.startActivity(intent)
            } else {
                val intent = Intent(context, NoPlacesFoundActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
    }

    private fun YelpFusionAPI_BusinessSearch(params: YelpFusionParams) {
        try {
            val call: Call<SearchResponse>
            if (params.mustHaveFoodDelivery)
                call = yelpAPI!!.getTransactionSearch("delivery", params.getParams())
            else
                call = yelpAPI!!.getBusinessSearch(params.getParams())
            if (SettingsParams.debugMode)
                Log.d("Msg", "finish call")
            val response = call.execute().body()
            val businesses = response.businesses
            places = ArrayList<YelpPlace>(businesses.size)
            for (i in businesses.indices) {
                val business = businesses[i]
                val place = YelpPlace()
                place.formattedAddress = (business.location.address1 + business.location.address2 + business.location.address3
                        + "\n" + business.location.city + " " + business.location.state)
                place.lat = business.coordinates.latitude
                place.lng = business.coordinates.longitude
                place.rating = business.rating
                place.reviewCount = business.reviewCount
                place.name = business.name
                val categories = business.categories
                place.categories = Array<Array<String>>(categories.size) { arrayOfNulls(2) }
                for (j in categories.indices) {
                    place.categories[j][0] = categories[j].alias
                    place.categories[j][1] = categories[j].title
                }
                place.imageURL = business.imageUrl
                place.URL = business.url
                place.setPrice(business.price)
                if (params.mustHaveFoodDelivery) {
                    place.phone = business.phone
                    place.displayPhone = place.phone
                    place.setDistance(params.latitude, params.longitude, place.lat, place.lng)
                } else {
                    place.displayPhone = business.displayPhone
                    place.phone = business.phone
                    if (!params.getParams().containsKey("location"))
                        place.distance = business.distance
                    else
                        place.setDistance(params.latitude, params.longitude, place.lat, place.lng)
                }

                places!!.add(place)
            }
        } catch (ex: Exception) {
            if (SettingsParams.debugMode)
                Log.d("Error", "==========FATAL ERROR: Unable to call yelp API=============")
            ErrorActivity.start(context, "Unable to call remote yelp API for search. Either there's no internet, the search quota has been full, or something else has gong wrong...")
        }

    }
}
