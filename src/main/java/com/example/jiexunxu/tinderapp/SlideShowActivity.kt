package com.example.jiexunxu.tinderapp

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView

import com.eftimoff.viewpagertransformers.*
import com.squareup.picasso.Picasso

import java.util.ArrayList

import pl.droidsonroids.gif.GifImageButton


class SlideShowActivity : AppCompatActivity() {
    private var places: ArrayList<YelpPlace>? = null
    private var adapter: SlideShowAdapter? = null
    private var selectedPlace: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slideshow)
        val args = intent.getBundleExtra("bundle")
        places = args.getSerializable("places") as ArrayList<YelpPlace>
        initUI()
        initButtons()
    }

    override fun onStart() {
        super.onStart()
        initSlideShow()
    }

    // Disable back button on the phone
    override fun onBackPressed() {
        backToMainMenu()
    }

    private fun initUI() {
        title = "Results"
        val layout = findViewById<ConstraintLayout>(R.id.slideshowLayout)
        val opts = AppOptions.getUIOptions(SettingsParams.themeID)
        layout.setBackgroundColor(applicationContext.resources.getColor(opts.backgroundColor))
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(applicationContext.resources.getColor(opts.primaryColor)))
    }

    private fun initButtons() {
        val callButton = findViewById<GifImageButton>(R.id.placePhoneCall)
        val directionsButton = findViewById<GifImageButton>(R.id.placeDirections)
        val websiteButton = findViewById<GifImageButton>(R.id.placeWebsite)
        val backButton = findViewById<GifImageButton>(R.id.placeGoBackToSearch)

        callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:" + places!![selectedPlace].phone)
            try {
                if (ContextCompat.checkSelfPermission(this@SlideShowActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@SlideShowActivity, arrayOf(Manifest.permission.CALL_PHONE), 1)
                } else {
                    startActivity(intent)
                }
            } catch (ex: Exception) {
                ErrorActivity.start(applicationContext, "Unable to make a phone call.")
            }
        }

        directionsButton.setOnClickListener {
            try {
                val googleMapStr = "http://maps.google.com/maps?daddr=" + places!![selectedPlace].lat + "," + places!![selectedPlace].lng
                val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(googleMapStr))
                startActivity(intent)
            } catch (ex: Exception) {
                ErrorActivity.start(applicationContext, "Unable to launch google map directions app.")
            }
        }

        websiteButton.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(places!![selectedPlace].URL)
                startActivity(intent)
            } catch (ex: Exception) {
                ErrorActivity.start(applicationContext, "Unable to launch webpage.")
            }
        }

        backButton.setOnClickListener { backToMainMenu() }

        val pager = findViewById<ViewPager>(R.id.viewpager)
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val place = places!![position]
                selectedPlace = position
                setTitle(place.name)
                var tags = ""
                for (i in 0 until place.categories.length)
                    tags += place.categories[i][1] + ", "
                tags = tags.substring(0, tags.length - 2)
                (findViewById<View>(R.id.placeTagsText) as TextView).text = "Tag(s): $tags"
                if (place.phone == null || place.phone.length() < 5)
                    (findViewById<View>(R.id.placePhoneNumberText) as TextView).text = "Phone: Unknown"
                else
                    (findViewById<View>(R.id.placePhoneNumberText) as TextView).text = "Phone: " + place.displayPhone
                (findViewById<View>(R.id.placeDistanceText) as TextView).text = "Distance: " + place.distance2miles() + " miles"
                (findViewById<View>(R.id.placePriceText) as TextView).text = "Price: " + place.price + place.priceDescription
                (findViewById<View>(R.id.placeReviewText) as TextView).text = "Review(" + place.reviewCount + "): "
                (findViewById<View>(R.id.placeRatingBar) as RatingBar).rating = place.rating
            }

            override fun onPageSelected(position: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun initSlideShow() {
        val imageURLs = arrayOfNulls<String>(places!!.size)
        for (i in places!!.indices)
            imageURLs[i] = places!![i].imageURL

        adapter = SlideShowAdapter(this, imageURLs)
        val pager = findViewById<ViewPager>(R.id.viewpager)
        pager.adapter = adapter
        when (java.util.Random().nextInt(9)) {
            0 -> pager.setPageTransformer(true, CubeOutTransformer())
            1 -> pager.setPageTransformer(true, BackgroundToForegroundTransformer())
            2 -> pager.setPageTransformer(true, DepthPageTransformer())
            3 -> pager.setPageTransformer(true, DrawFromBackTransformer())
            4 -> pager.setPageTransformer(true, FlipHorizontalTransformer())
            5 -> pager.setPageTransformer(true, ForegroundToBackgroundTransformer())
            6 -> pager.setPageTransformer(true, RotateDownTransformer())
            7 -> pager.setPageTransformer(true, RotateUpTransformer())
            8 -> pager.setPageTransformer(true, TabletTransformer())
            9 -> pager.setPageTransformer(true, ZoomOutSlideTransformer())
            else -> pager.setPageTransformer(true, DefaultTransformer())
        }
    }

    private fun backToMainMenu() {
        val intent = Intent(this@SlideShowActivity, MainActivity::class.java)
        this@SlideShowActivity.startActivity(intent)
    }
}

internal class SlideShowAdapter(var mContext: Context, var imageURLs: Array<String>) : PagerAdapter() {
    var mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return imageURLs.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.slideshow_item, container, false)
        val imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
        if (imageURLs[position] == "") {
            Picasso.get().load(R.drawable.slideshow_no_image).fit().centerCrop().into(imageView)
        } else {
            try {
                Picasso.get().load(imageURLs[position]).placeholder(R.drawable.slideshow_loading).fit().centerInside().into(imageView)//
            } catch (ex: Exception) {
                if (SettingsParams.debugMode)
                    Log.d("Error", "Unable to load image=" + imageURLs[position])
                Picasso.get().load(R.drawable.slideshow_no_image).fit().centerCrop().into(imageView)
            }

        }
        //   Glide.with(mContext).load(imageURLs[position]).apply((new RequestOptions()).centerCrop()).into(imageView);
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //  container.removeView((LinearLayout) object);
        var view: View? = `object` as View
        (container as ViewPager).removeView(view)
        view = null
    }
}
