package com.example.jiexunxu.tinderapp

import android.location.Location

import java.io.Serializable


internal class YelpPlace : Serializable, Comparable<YelpPlace> {

    var formattedAddress: String? = null
    var lat: Double = 0.toDouble()
    var lng: Double = 0.toDouble()
    var imageURL: String? = null
    var URL: String? = null
    var phone: String? = null
    var displayPhone: String? = null
    var name: String? = null
    var openNow: Boolean = false
    var openingHours: String? = null
    var placeID: String? = null
    var price: String
    var rating: Double = 0.toDouble()
    var reviewCount: Int = 0
    var distance: Double = 0.toDouble() // in meters
    var categories: Array<Array<String>>? = null
    var priceDescription: String

    override fun toString(): String {
        return "{ Address=" + formattedAddress + ", \nlocation=[" + lat + ", " +
                lng + "], \nicon=" + imageURL + ", \nphone=" + phone + ", \nname=" + name +
                ", \nopenNow=" + openNow + ", \nopeningHours" + openingHours + ", \nplaceID=" +
                placeID + ", \nprice=" + price + ", \nrating=" + rating + ",\nreviewCount=" + reviewCount + "\ndistance=" + distance
    }

    override fun compareTo(other: YelpPlace): Int {
        when (sortKey) {
            1 -> {
                if (this.price.length > other.price.length)
                    return 1
                else if (this.price.length < other.price.length)
                    return -1
                return 0
            }
            2 -> {
                if (this.price.length < other.price.length)
                    return 1
                else if (this.price.length > other.price.length)
                    return -1
                return 0
            }
            3 -> {
                if (this.distance > other.distance)
                    return 1
                else if (this.distance < other.distance)
                    return -1
                return 0
            }
            4 -> {
                if (this.rating < other.rating)
                    return 1
                else if (this.rating > other.rating)
                    return -1
                return 0
            }
            5 -> {
                if (this.reviewCount < other.reviewCount)
                    return 1
                else if (this.reviewCount > other.reviewCount)
                    return -1
                return 0
            }
            else -> return 0
        }
    }

    fun setPrice(price: String) {
        this.price = price
        if (price.length == 1)
            priceDescription = " (<$10)"
        else if (price.length == 2)
            priceDescription = " ($10-$30)"
        else if (price.length == 3)
            priceDescription = " ($30-$60)"
        else if (price.length == 4)
            priceDescription = " (>$60)"
    }

    fun setDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double) {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = lng1
        val loc2 = Location("")
        loc2.latitude = lat1
        loc2.longitude = lng2
        val dist = loc1.distanceTo(loc2).toDouble()
        if (dist < 10000)
            distance = dist * 1.5
        else if (dist < 810000)
            distance = dist * (1.5 - (dist - 10000) * 0.5 / 800000)
        else
            distance = dist
    }

    fun distance2miles(): Double {
        return Math.round(distance / 1609.34 * 100.0) / 100.0
    }

    companion object {
        /* An integer indicating which of the following fields will be used as a sorting field for YelpPlace objects
       sortKey=1 uses price (low to high)
       sortKey=2 uses price (high to low)
       sortKey=3 uses distance
       sortKey=4 uses rating (high to low)
       sortKey=5 uses reviewCount
     */
        var sortKey: Int = 0
    }
}
