package com.example.travellerapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travellerapp.databinding.ActivityGalleryBinding
import java.io.File
import java.io.FileInputStream


class GalleryActivity : AppCompatActivity() {
    val binding by lazy { ActivityGalleryBinding.inflate(layoutInflater)}
    var photoNumber = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        updateImage()
    }
    fun updateImage(){
        binding.photoNumber.text = (photoNumber+1).toString() + "/" + Shared.noteList.size
        var fis: FileInputStream
        try {
            fis = FileInputStream(File(Shared.noteList[photoNumber].image))
            val bitmap = BitmapFactory.decodeStream(fis)
            binding.imageView.setImageBitmap(bitmap)

            binding.imageView.setOnClickListener{
                val photoWithNoteIntent = Intent(binding.root.context, PhotoWithNoteActivity::class.java)
                photoWithNoteIntent.putExtra("note",Shared.noteList[photoNumber].text)
                photoWithNoteIntent.putExtra("photo",bitmap)
                photoWithNoteIntent.putExtra("fromNotification",false)
                startActivity(photoWithNoteIntent)
            }
            fis.close()
        } catch (e: Exception) { }
    }
    fun nextPhoto(view: View){
        if(photoNumber == Shared.noteList.size-1)
            photoNumber = 0
        else
            photoNumber++
        updateImage()
    }
    fun previousPhoto(view: View){
        if(photoNumber == 0)
            photoNumber = Shared.noteList.size-1
        else
            photoNumber--
        updateImage()
    }
    fun returnPressed(view: View){
        finish()
    }
}