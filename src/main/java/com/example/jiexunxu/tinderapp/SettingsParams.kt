package com.example.jiexunxu.tinderapp

import android.content.Context
import android.media.audiofx.Equalizer
import android.util.Log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Serializable


internal class SettingsParams : Serializable {

    var keywords: String
    var address: String
    var maxResults: Int = 0
    var sortingMethod: Int = 0
    var searchRange: Int = 0
    var prices: BooleanArray
    var mustBeOpenNow: Boolean = false

    init {
        getDefaultSettings()
    }

    fun getDefaultSettings() {
        keywords = ""
        address = ""
        maxResults = 20
        sortingMethod = 0
        searchRange = 8000
        prices = BooleanArray(4)
        prices[0] = true
        prices[1] = true
        prices[2] = true
        prices[3] = true
        SettingsParams.themeID = 0
    }

    fun readSettingsFromFile(context: Context) {
        try {
            val inputStream = context.openFileInput("settings.txt")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            keywords = bufferedReader.readLine()
            address = bufferedReader.readLine()
            maxResults = Integer.parseInt(bufferedReader.readLine())
            sortingMethod = Integer.parseInt(bufferedReader.readLine())
            searchRange = Integer.parseInt(bufferedReader.readLine())
            prices = BooleanArray(4)
            prices[0] = bufferedReader.readLine()[0] == '1'
            prices[1] = bufferedReader.readLine()[0] == '1'
            prices[2] = bufferedReader.readLine()[0] == '1'
            prices[3] = bufferedReader.readLine()[0] == '1'
            mustBeOpenNow = bufferedReader.readLine()[0] == '1'
            SettingsParams.themeID = Integer.parseInt(bufferedReader.readLine())
            inputStream.close()
        } catch (e: Exception) {
            if (SettingsParams.debugMode)
                Log.e("Error", "Unable to read settings file, setting parameters to default")
            getDefaultSettings()
        }

    }

    /*
       A simple text file to store user settings. File format:
       1st line stores keywords
       2nd line stores address
       3rd line stores max results number (1, 5, 10, 20 or 50)
       4th line stores sorting method (0, 1, 2, or 3)
       5th line stores search range (1600, 8000, 16000 or 40000)
       6-10th line stores the four prices boolean value, mustBeOpenNow (0 or 1, one per line)
       11th line stores theme ID
       */
    fun writeSettingsToFile(context: Context) {
        try {
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput("settings.txt", context.MODE_PRIVATE))
            outputStreamWriter.write(keywords + "\n" + address + "\n" + maxResults + "\n" + sortingMethod + "\n" + searchRange +
                    "\n" + bool2Int(prices[0]) + "\n" + bool2Int(prices[1]) + "\n" + bool2Int(prices[2]) + "\n" + bool2Int(prices[3]) + "\n" +
                    bool2Int(mustBeOpenNow) + "\n" + themeID + "\n")
            outputStreamWriter.close()
        } catch (e: IOException) {
            if (SettingsParams.debugMode)
                Log.d("Error", "Unable to save settings file")
            ErrorActivity.start(context, "Unable to save your settings.")
        }

    }

    fun settingsToYelpParams(): YelpFusionParams {
        val params = YelpFusionParams()
        params.setDefaultParams()
        params.setKeywordSearch(keywords)
        params.setLocationSearch(address)
        params.setMaxResults(maxResults)
        params.sortKey = sortingMethod
        params.setRadius(searchRange)
        params.setPrice(prices[0], prices[1], prices[2], prices[3])
        params.setMustOpenNow(mustBeOpenNow)
        return params
    }

    private fun bool2Int(b: Boolean): Int {
        return if (b) 1 else 0
    }

    companion object {
        var themeID: Int = 0
        var debugMode = true
    }
}
