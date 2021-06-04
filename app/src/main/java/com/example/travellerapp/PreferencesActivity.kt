package com.example.travellerapp

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.travellerapp.databinding.ActivityMainBinding
import com.example.travellerapp.databinding.ActivityPreferencesBinding
import java.security.AccessController.getContext

class PreferencesActivity : AppCompatActivity() {
    val binding by lazy { ActivityPreferencesBinding.inflate(layoutInflater)}
    var color = Color.rgb(0,0,0)
    private val prefs by lazy {getSharedPreferences("prefs", Context.MODE_PRIVATE)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.editTextSize.setText(Shared?.textSize.toString())
        binding.editLocationRadius.setText(Shared?.locationRadius.toString())
        binding.seekBarRed.progress = Shared.textColor.red
        binding.seekBarGreen.progress = Shared.textColor.green
        binding.seekBarBlue.progress = Shared.textColor.blue
        onSeekBarChange()
        binding.seekBarRed.max = 255
        binding.seekBarGreen.max = 255
        binding.seekBarBlue.max = 255
        setSeekBarListener(binding.seekBarRed)
        setSeekBarListener(binding.seekBarGreen)
        setSeekBarListener(binding.seekBarBlue)
    }
    fun returnPressed(view: View){
        finish()
    }
    fun setSeekBarListener(seekBar: SeekBar){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                onSeekBarChange()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }
    fun onSeekBarChange(){
        color = Color.rgb(binding.seekBarRed.progress,binding.seekBarGreen.progress,binding.seekBarBlue.progress)
        binding.colorView.setColorFilter(color)
    }
    fun savePreferencesPressed(view: View){
        if(binding.editTextSize.text.toString().toIntOrNull() == null || binding.editLocationRadius.text.toString().toIntOrNull() == null){
            Toast.makeText(this, "Nie wypełniono wszyskich pól", Toast.LENGTH_LONG).show()
            return
        }
        Shared.textSize = binding.editTextSize.text.toString().toInt()
        Shared.textColor = color
        Shared.locationRadius = binding.editLocationRadius.text.toString().toInt()

        prefs.edit()
            .putInt("textSize", Shared.textSize)
            .putInt("textColor", Shared.textColor)
            .putInt("locationRadius", Shared.locationRadius)
            .apply()

        finish()
    }
}