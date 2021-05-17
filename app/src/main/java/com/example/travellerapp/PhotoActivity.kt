package com.example.travellerapp

import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.graphics.Paint.Align
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.travellerapp.databinding.ActivityPhotoBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class PhotoActivity : AppCompatActivity() {
    val binding by lazy { ActivityPhotoBinding.inflate(layoutInflater)}
    var bitmapImage : Bitmap ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bitmapImage = intent.extras?.get("BitmapImage") as Bitmap

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val date = dateFormat.format(calendar.time)

        val text = date
        val paint = Paint()
        //paint.style = Paint.Style.FILL
        paint.color = Shared.textColor
        paint.textSize = Shared.textSize.toFloat()
        paint.typeface = Typeface.create("Helvetica", Typeface.BOLD)
        paint.textAlign = Align.CENTER

        //val textRect = Rect()
        //paint.getTextBounds(text, 0, text.length, textRect)

        val canvas = Canvas(bitmapImage)
        canvas.drawText(text, canvas.width / 2f, canvas.height / 2f, paint)

        binding.photo.setImageBitmap(bitmapImage)
        this.bitmapImage = bitmapImage


    }
    fun savePressed(view: View){
        val cw = ContextWrapper(applicationContext)
        val directory: File = cw.getDir("photos", Context.MODE_PRIVATE)
        if (!directory.exists()) {
            directory.mkdir()
        }
        val calendar = Calendar.getInstance()
        val mypath = File(directory, "photo" + calendar.timeInMillis + ".png")
        println(mypath)
        var fos: FileOutputStream
        try {
            fos = FileOutputStream(mypath)
            bitmapImage?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
        } catch (e: Exception) { }
        finish()
    }
    fun returnPressed(view: View){
        finish()
    }
}