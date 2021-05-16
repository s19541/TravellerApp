package com.example.travellerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.travellerapp.databinding.ActivityMainBinding
import com.example.travellerapp.databinding.ActivityPreferencesBinding

class PreferencesActivity : AppCompatActivity() {
    val binding by lazy { ActivityPreferencesBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
    fun returnPressed(view: View){
        finish()
    }
}