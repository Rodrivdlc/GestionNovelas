package com.example.novelas

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate called")

        dbHelper = DatabaseHelper(this)
        preferencesManager = PreferencesManager(this)

        if (savedInstanceState == null) {
            Log.d("MainActivity", "Adding NovelasListFragment")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, NovelasListFragment())
                .commit()
        }
    }

    fun showNovelaDetails(novela: Novela) {
        val fragment = NovelaDetailsFragment().apply {
            setNovela(novela)
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}