package com.example.travellerapp

import android.graphics.*
import android.graphics.Paint.Align
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.travellerapp.databinding.ActivityPhotoBinding


class PhotoActivity : AppCompatActivity() {
    val binding by lazy { ActivityPhotoBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bitmapImage = intent.extras?.get("BitmapImage") as Bitmap

        val text = "hello hello"
        val paint = Paint()
        //paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = 15f
        paint.typeface = Typeface.create("Helvetica", Typeface.BOLD)
        paint.textAlign = Align.CENTER

        //val textRect = Rect()
        //paint.getTextBounds(text, 0, text.length, textRect)

        val canvas = Canvas(bitmapImage)
        canvas.drawText(text, canvas.width / 2f, canvas.height / 2f, paint)

        binding.photo.setImageBitmap(bitmapImage)
    }
    fun savePressed(view: View){
        finish()
    }
    fun returnPressed(view: View){
        finish()
    }
}