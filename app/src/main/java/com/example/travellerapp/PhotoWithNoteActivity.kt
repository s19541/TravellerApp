package com.example.travellerapp

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.travellerapp.databinding.ActivityMainBinding
import com.example.travellerapp.databinding.ActivityPhotoWithNoteBinding

class PhotoWithNoteActivity : AppCompatActivity() {
    val binding by lazy { ActivityPhotoWithNoteBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val photo = intent.extras?.get("photo") as Bitmap
        binding.photo.setImageBitmap(photo)
        val note = intent.extras?.get("note")
        binding.note.text = note.toString()
    }
    fun returnPressed(view: View){
        if(intent.extras?.get("fromNotification") as Boolean)
            startActivity(Intent(binding.root.context, MainActivity::class.java))
        else
            finish()
    }
}