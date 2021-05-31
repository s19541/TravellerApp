package com.example.travellerapp

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Paint.Align
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.travellerapp.databinding.ActivityPhotoBinding
import com.example.travellerapp.model.NoteDto
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


@SuppressLint("SetTextI18n", "MissingPermission")
class PhotoActivity : AppCompatActivity() {
    val binding by lazy { ActivityPhotoBinding.inflate(layoutInflater)}
    var bitmapImage : Bitmap ?= null
    var lat: Double = 0.0
    var lng: Double = 0.0
    private val locman by lazy { getSystemService(LocationManager::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bitmapImage = intent.extras?.get("BitmapImage") as Bitmap

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy")
        val date = dateFormat.format(calendar.time)
        var text = "$date "

        val criteria = Criteria().apply {
            accuracy = Criteria.ACCURACY_FINE
        }
        if(checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val best = locman.getBestProvider(criteria, true) ?: ""
            locman.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0f,
                LocationListener { })
            val loc = locman.getLastKnownLocation(best)

            if (loc != null) {
                lat = loc.latitude
                lng = loc.longitude
                try {
                    if (Geocoder.isPresent()) {
                        text += Geocoder(this)
                            .getFromLocation(lat, lng, 1)
                            .first()
                            .locality
                        text += " "
                        text += Geocoder(this)
                            .getFromLocation(lat, lng, 1)
                            .first()
                            .countryName
                    }
                } catch (e: java.lang.Exception) {
                    println(e.message)
                }
            }
        }


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
        var fos: FileOutputStream
        try {
            fos = FileOutputStream(mypath)
            bitmapImage?.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
        } catch (e: Exception) { }
        val note = NoteDto(
            text = binding.editNote.text.toString(),
            image = mypath.toString(),
            latitude = lat,
            longitude = lng
        )
        thread {
            Shared.db?.note?.save(note)
        }
        finish()
    }
    fun returnPressed(view: View){
        finish()
    }
}