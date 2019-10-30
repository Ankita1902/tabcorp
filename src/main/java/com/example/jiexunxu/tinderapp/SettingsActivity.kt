package com.example.jiexunxu.tinderapp

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.CompoundButtonCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Spinner

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Serializable



class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setContentView(R.layout.activity_settings)
        initUI()
        initButtons()
    }

    override fun onStart() {
        super.onStart()
        val settings = SettingsParams()
        settings.readSettingsFromFile(this.applicationContext)
        setSettingValuesToUI(settings)
    }

    override fun onBackPressed() {
        saveAndBack()
    }

    private fun initUI() {
        title = "Custom Search"
        val layout = findViewById<ConstraintLayout>(R.id.settingsLayout)
        val keywordsEditText = findViewById<EditText>(R.id.keywordSearchTextbox)
        val addressEditText = findViewById<EditText>(R.id.addressTextbox)
        val maxResultsSpinner = findViewById<Spinner>(R.id.maxResultsSpinner)
        val sortBySpinner = findViewById<Spinner>(R.id.sortBySpinner)
        val searchRangeSpinner = findViewById<Spinner>(R.id.searchRangeSpinner)
        val themeSpinner = findViewById<Spinner>(R.id.colorThemeSpinner)
        val saveButton = findViewById<Button>(R.id.saveAndBackButton)
        val resetDefaultsButton = findViewById<Button>(R.id.resetDefaultButton)
        val price1CheckBox = findViewById<CheckBox>(R.id.price1CheckBox)
        val price2CheckBox = findViewById<CheckBox>(R.id.price2CheckBox)
        val price3CheckBox = findViewById<CheckBox>(R.id.price3CheckBox)
        val price4CheckBox = findViewById<CheckBox>(R.id.price4CheckBox)
        val openNowCheckBox = findViewById<CheckBox>(R.id.openNowCheckBox)
        val opts = AppOptions.getUIOptions(SettingsParams.themeID)
        layout.setBackgroundColor(applicationContext.resources.getColor(opts.backgroundColor))
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(applicationContext.resources.getColor(opts.primaryColor)))
        keywordsEditText.setBackgroundResource(opts.editTextStyle)
        addressEditText.setBackgroundResource(opts.editTextStyle)
        maxResultsSpinner.setBackgroundResource(opts.spinnerStyle)
        maxResultsSpinner.setPopupBackgroundResource(opts.spinnerPopupStyle)
        sortBySpinner.setBackgroundResource(opts.spinnerStyle)
        sortBySpinner.setPopupBackgroundResource(opts.spinnerPopupStyle)
        searchRangeSpinner.setBackgroundResource(opts.spinnerStyle)
        searchRangeSpinner.setPopupBackgroundResource(opts.spinnerPopupStyle)
        themeSpinner.setBackgroundResource(opts.spinnerStyle)
        themeSpinner.setPopupBackgroundResource(opts.spinnerPopupStyle)
        saveButton.setBackgroundResource(opts.buttonStyle)
        resetDefaultsButton.setBackgroundResource(opts.buttonStyle)
        CompoundButtonCompat.setButtonTintList(price1CheckBox, ColorStateList.valueOf(resources.getColor(opts.primaryColorDark)))
        CompoundButtonCompat.setButtonTintList(price2CheckBox, ColorStateList.valueOf(resources.getColor(opts.primaryColorDark)))
        CompoundButtonCompat.setButtonTintList(price3CheckBox, ColorStateList.valueOf(resources.getColor(opts.primaryColorDark)))
        CompoundButtonCompat.setButtonTintList(price4CheckBox, ColorStateList.valueOf(resources.getColor(opts.primaryColorDark)))
        CompoundButtonCompat.setButtonTintList(openNowCheckBox, ColorStateList.valueOf(resources.getColor(opts.primaryColorDark)))
    }

    private fun initButtons() {
        val maxResultsSpinner = findViewById<Spinner>(R.id.maxResultsSpinner)
        val maxResultsItems = arrayOf("1", "5", "10", "20", "50")
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, maxResultsItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        maxResultsSpinner.adapter = adapter
        val sortBySpinner = findViewById<Spinner>(R.id.sortBySpinner)
        val sortByItems = arrayOf("Best Match", "Price (low to high)", "Price (high to low)", "Distance", "Rating", "Review Count")
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortByItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortBySpinner.adapter = adapter
        val searchRangeSpinner = findViewById<Spinner>(R.id.searchRangeSpinner)
        val searchRangeItems = arrayOf("1", "5", "10", "25")
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, searchRangeItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        searchRangeSpinner.adapter = adapter
        val saveButton = findViewById<Button>(R.id.saveAndBackButton)
        val resetDefaultsButton = findViewById<Button>(R.id.resetDefaultButton)
        val themeSpinner = findViewById<Spinner>(R.id.colorThemeSpinner)
        val themeSpinnerItems = arrayOf("Default", "Ever Green", "Deep Blue", "Crimson Red")
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themeSpinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themeSpinner.adapter = adapter

        saveButton.setOnClickListener { saveAndBack() }

        resetDefaultsButton.setOnClickListener {
            val settings = SettingsParams()
            settings.getDefaultSettings()
            setSettingValuesToUI(settings)
        }
    }

    private fun saveAndBack() {
        val settings = UIToSettingsParams()
        settings.writeSettingsToFile(this.applicationContext)
        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
        this@SettingsActivity.startActivity(intent)
    }

    private fun UIToSettingsParams(): SettingsParams {
        val keywordsText = findViewById<EditText>(R.id.keywordSearchTextbox)
        val addressText = findViewById<EditText>(R.id.addressTextbox)
        val maxResultsSpinner = findViewById<Spinner>(R.id.maxResultsSpinner)
        val sortBySpinner = findViewById<Spinner>(R.id.sortBySpinner)
        val searchRangeSpinner = findViewById<Spinner>(R.id.searchRangeSpinner)
        val price1CheckBox = findViewById<CheckBox>(R.id.price1CheckBox)
        val price2CheckBox = findViewById<CheckBox>(R.id.price2CheckBox)
        val price3CheckBox = findViewById<CheckBox>(R.id.price3CheckBox)
        val price4CheckBox = findViewById<CheckBox>(R.id.price4CheckBox)
        val mustBeOpenCheckBox = findViewById<CheckBox>(R.id.openNowCheckBox)
        val themeSpinner = findViewById<Spinner>(R.id.colorThemeSpinner)

        val settings = SettingsParams()
        settings.keywords = keywordsText.text.toString()
        settings.address = addressText.text.toString()
        when (maxResultsSpinner.selectedItemPosition) {
            0 -> settings.maxResults = 1
            1 -> settings.maxResults = 5
            2 -> settings.maxResults = 10
            3 -> settings.maxResults = 20
            4 -> settings.maxResults = 50
            else -> settings.maxResults = 20
        }
        when (sortBySpinner.selectedItemPosition) {
            0 -> settings.sortingMethod = 0
            1 -> settings.sortingMethod = 1
            2 -> settings.sortingMethod = 2
            3 -> settings.sortingMethod = 3
            4 -> settings.sortingMethod = 4
            5 -> settings.sortingMethod = 5
            else -> settings.sortingMethod = 0
        }
        when (searchRangeSpinner.selectedItemPosition) {
            0 -> settings.searchRange = 1600
            1 -> settings.searchRange = 8000
            2 -> settings.searchRange = 16000
            3 -> settings.searchRange = 40000
            else -> settings.searchRange = 8000
        }
        settings.prices[0] = price1CheckBox.isChecked
        settings.prices[1] = price2CheckBox.isChecked
        settings.prices[2] = price3CheckBox.isChecked
        settings.prices[3] = price4CheckBox.isChecked
        settings.mustBeOpenNow = mustBeOpenCheckBox.isChecked
        when (themeSpinner.selectedItemPosition) {
            0 -> SettingsParams.themeID = 0
            1 -> SettingsParams.themeID = 1
            2 -> SettingsParams.themeID = 2
            3 -> SettingsParams.themeID = 3
            else -> SettingsParams.themeID = 0
        }
        return settings
    }

    private fun setSettingValuesToUI(settings: SettingsParams) {
        val keywordsText = findViewById<EditText>(R.id.keywordSearchTextbox)
        val addressText = findViewById<EditText>(R.id.addressTextbox)
        val maxResultsSpinner = findViewById<Spinner>(R.id.maxResultsSpinner)
        val sortBySpinner = findViewById<Spinner>(R.id.sortBySpinner)
        val searchRangeSpinner = findViewById<Spinner>(R.id.searchRangeSpinner)
        val price1CheckBox = findViewById<CheckBox>(R.id.price1CheckBox)
        val price2CheckBox = findViewById<CheckBox>(R.id.price2CheckBox)
        val price3CheckBox = findViewById<CheckBox>(R.id.price3CheckBox)
        val price4CheckBox = findViewById<CheckBox>(R.id.price4CheckBox)
        val mustBeOpenCheckBox = findViewById<CheckBox>(R.id.openNowCheckBox)
        val themeSpinner = findViewById<Spinner>(R.id.colorThemeSpinner)

        keywordsText.setText(settings.keywords)
        addressText.setText(settings.address)
        when (settings.maxResults) {
            1 -> maxResultsSpinner.setSelection(0)
            5 -> maxResultsSpinner.setSelection(1)
            10 -> maxResultsSpinner.setSelection(2)
            20 -> maxResultsSpinner.setSelection(3)
            50 -> maxResultsSpinner.setSelection(4)
            else -> maxResultsSpinner.setSelection(3)
        }
        when (settings.sortingMethod) {
            0 -> sortBySpinner.setSelection(0)
            1 -> sortBySpinner.setSelection(1)
            2 -> sortBySpinner.setSelection(2)
            3 -> sortBySpinner.setSelection(3)
            4 -> sortBySpinner.setSelection(4)
            5 -> sortBySpinner.setSelection(5)
            else -> sortBySpinner.setSelection(0)
        }
        when (settings.searchRange) {
            1600 -> searchRangeSpinner.setSelection(0)
            8000 -> searchRangeSpinner.setSelection(1)
            16000 -> searchRangeSpinner.setSelection(2)
            40000 -> searchRangeSpinner.setSelection(3)
            else -> searchRangeSpinner.setSelection(1)
        }
        price1CheckBox.isChecked = settings.prices[0]
        price2CheckBox.isChecked = settings.prices[1]
        price3CheckBox.isChecked = settings.prices[2]
        price4CheckBox.isChecked = settings.prices[3]
        mustBeOpenCheckBox.isChecked = settings.mustBeOpenNow
        when (SettingsParams.themeID) {
            0 -> themeSpinner.setSelection(0)
            1 -> themeSpinner.setSelection(1)
            2 -> themeSpinner.setSelection(2)
            3 -> themeSpinner.setSelection(3)
            else -> themeSpinner.setSelection(0)
        }
    }
}