package com.example.jiexunxu.tinderapp

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView

import com.squareup.picasso.Picasso


class NoPlacesFoundActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_places_found)
        initUI()
        initButtons()
    }

    private fun initUI() {
        title = "No Results Found :("
        val layout = findViewById<ConstraintLayout>(R.id.noPlacesFoundLayout)
        val backToMainButton = findViewById<Button>(R.id.noResultsBackButton)
        val opts = AppOptions.getUIOptions(SettingsParams.themeID)
        layout.setBackgroundColor(applicationContext.resources.getColor(opts.backgroundColor))
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(applicationContext.resources.getColor(opts.primaryColor)))
        backToMainButton.setBackgroundResource(opts.buttonStyle)
    }

    private fun initButtons() {
        val backToMainButton = findViewById<Button>(R.id.noResultsBackButton)
        backToMainButton.setOnClickListener {
            val intent = Intent(this@NoPlacesFoundActivity, MainActivity::class.java)
            this@NoPlacesFoundActivity.startActivity(intent)
        }
    }
}
