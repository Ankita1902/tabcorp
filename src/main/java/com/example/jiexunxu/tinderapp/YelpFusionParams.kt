package com.example.jiexunxu.tinderapp

import android.location.Location

import java.io.Serializable
import java.util.ArrayList
import java.util.HashMap


internal class YelpFusionParams : Serializable {
    var params: HashMap<String, String>? = null
        private set
    var sortKey: Int = 0
    var mustHaveFoodDelivery: Boolean = false
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()

    fun setDefaultParams() {
        params = HashMap()
        params!!["latitude"] = "40.748838"
        params!!["longitude"] = "-73.985644"
        params!!["radius"] = "8000"
        params!!["sort_by"] = "best_match"
        params!!["limit"] = "25"
        params!!["price"] = "1,2,3,4"
        params!!["open_now"] = "false"
    }

    fun setLatLng(lat: Double, lng: Double) {
        latitude = lat
        longitude = lng
        if (!params!!.containsKey("location")) {
            params!!["latitude"] = java.lang.Double.toString(lat)
            params!!["longitude"] = java.lang.Double.toString(lng)
        }
    }

    /*
    void setSortingMethod(int sortBy){
        switch(sortBy){
            case 1:params.put("sort_by", "best_match");break;
            case 2:params.put("sort_by", "distance");break;
            case 3:params.put("sort_by", "rating");break;
            case 4:params.put("sort_by", "review_count");break;
            default:params.put("sort_by", "best_match");break;
        }

    }
*/
    fun setMaxResults(maxResults: Int) {
        params!!["limit"] = Integer.toString(maxResults)
    }

    fun setRadius(radius: Int) {
        params!!["radius"] = Integer.toString(radius)
    }

    // Supply additional keywords, such as "seafood, asian food" etc to the search
    fun setKeywordSearch(keyword: String?) {
        if (keyword != null && keyword.length > 0)
            params!!["term"] = keyword
        else
            params!!.remove("term")
    }

    // If this method is called, the default search location in params will be overwritten
    fun setLocationSearch(address: String?) {
        if (address != null && address.length > 0) {
            params!!.remove("latitude")
            params!!.remove("longitude")
            params!!["location"] = address
        } else {
            params!!.remove("location")
            params!!["latitude"] = "40.748838"
            params!!["longitude"] = "-73.985644"
        }
    }

    // Input is a variable number of categories string. This method will concatenate them together
    fun setCategories(vararg categories: String) {
        var cat = ""
        for (i in categories.indices)
            cat += categories[i] + ","
        cat = cat.substring(0, cat.length - 1)
        params!!["categories"] = cat
    }

    // Input are 4 booleans that corresponds to the four prices $, $$, $$$, $$$$. pi==true means the
    // i-th price will be considered
    fun setPrice(p1: Boolean, p2: Boolean, p3: Boolean, p4: Boolean) {
        var cat = ""
        if (p1)
            cat += "1,"
        if (p2)
            cat += "2,"
        if (p3)
            cat += "3,"
        if (p4)
            cat += "4,"
        if (!p1 && !p2 && !p3 && !p4)
            cat = "1,2,3,4,"
        cat = cat.substring(0, cat.length - 1)
        params!!["price"] = cat
    }

    fun setMustOpenNow(mustOpenNow: Boolean) {
        if (mustOpenNow)
            params!!["open_now"] = "true"
        else
            params!!["open_now"] = "false"
    }
}
