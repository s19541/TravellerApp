package com.example.travellerapp

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.hardware.camera2.CameraManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.travellerapp.databinding.ActivityMainBinding
import com.example.travellerapp.model.Note
import com.example.travellerapp.model.NoteDto
import com.google.android.gms.location.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.concurrent.thread

private const val REQ_LOCATION_PERMISSION = 1
class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    val cam by lazy {CameraUtil(getSystemService(Context.CAMERA_SERVICE) as CameraManager)}
    private val locman by lazy { getSystemService(LocationManager::class.java)}
    private val prefs by lazy {getSharedPreferences("prefs", Context.MODE_PRIVATE)}
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.takePhotoButton.isClickable = false
        println()
        checkPermission()
        Shared.db = Room.databaseBuilder(this, AppDataBase::class.java, "notedb").build()
        registerChannel()
        loadPreferences()

        setContentView(binding.root)
        binding.surfaceView.holder.addCallback(this)
    }
    private fun loadPreferences(){
        Shared.textSize = prefs.getInt("textSize", 15)
        Shared.textColor = prefs.getInt("textColor", Color.rgb(0,255,255))
        Shared.locationRadius = prefs.getInt("locationRadius", 1000)
    }
    private fun registerChannel() {
        getSystemService(NotificationManager::class.java).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nc = NotificationChannel(
                        "com.example.travellerapp.Geofence",
                        "Geofences",
                        NotificationManager.IMPORTANCE_DEFAULT
                )
                it.createNotificationChannel(nc)
            }
        }
    }

    @SuppressLint("SetTextI18n", "MissingPermission")
    override fun onResume() {
        super.onResume()
        thread {
            Shared.db?.note?.getAll().let {
                val newList = it?.map {
                    Note(
                            it.text,
                            it.image
                    )
                }
                Shared.noteList = newList as MutableList<Note>
            }
            for(note in Shared.noteList){
                var fis: FileInputStream
                try {
                    fis = FileInputStream(File(note.image))
                    fis.close()
                } catch (e: Exception) {
                    Shared.noteList.remove(note)
                    Shared.db?.note?.deleteNote(note.image)
                }
            }
            Shared.db?.note?.getAll().let{
                it?.map{
                    addGeoFence(it)
                }
            }
        }
        if(checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cam.openCamera()
            binding.takePhotoButton.isClickable = true
        }
    }
    @SuppressLint("MissingPermission")
    fun addGeoFence(note: NoteDto){
        val pi = PendingIntent.getBroadcast(
                applicationContext,
                1,
                Intent(this, LocationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val client = LocationServices.getGeofencingClient(this)
        client.removeGeofences(pi)
        client.addGeofences(
                genRequest(note.latitude, note.longitude, note.image),
                pi
        )
    }

    private fun genRequest(latitude: Double, longitude: Double, id: String): GeofencingRequest? {
        val geofence = Geofence.Builder()
                .setCircularRegion(latitude, longitude, Shared.locationRadius.toFloat())
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        return GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()
    }
    override fun surfaceCreated(holder: SurfaceHolder) {
        holder.surface?.let { cam.setupPreviewSession(it) }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }
    fun takePhotoPressed(view: View){
        val img = cam.acquire(binding.takePhotoButton as ImageView)
        val buffer = img.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix()
        matrix.postRotate(90f)
        val rotated = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true
        )
        var intent = Intent(binding.root.context, PhotoActivity::class.java)
        intent.putExtra("BitmapImage", rotated)
        startActivity(intent)
    }
    fun preferencesPressed(view: View){
        startActivity(Intent(binding.root.context, PreferencesActivity::class.java))
    }
    fun galleryPressed(view: View){
        startActivity(Intent(binding.root.context, GalleryActivity::class.java))
    }
    fun updateLocationPressed(view: View){
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, LocationListener { })
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkPermission(){
        val permissionStatusCamera = checkSelfPermission(CAMERA)
        if(permissionStatusCamera != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(CAMERA), 3)
        }
        val permissionStatusLocation = checkSelfPermission(ACCESS_FINE_LOCATION)
        val permissionStatusBackgroundLocation = checkSelfPermission(ACCESS_BACKGROUND_LOCATION)
        if(permissionStatusLocation != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, ACCESS_BACKGROUND_LOCATION), REQ_LOCATION_PERMISSION)
        }
        if(permissionStatusBackgroundLocation != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(ACCESS_BACKGROUND_LOCATION), REQ_LOCATION_PERMISSION)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 3) {
            if (!listOf(grantResults).contains(PackageManager.PERMISSION_DENIED)) {
                this.recreate()
            }
        }
    }
}