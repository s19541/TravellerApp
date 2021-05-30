package com.example.travellerapp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.travellerapp.databinding.ActivityMainBinding
import com.example.travellerapp.model.Note
import com.example.travellerapp.model.NoteDto
import java.io.File
import java.io.FileInputStream
import kotlin.concurrent.thread

private const val REQ_LOCATION_PERMISSION = 1
class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    val cam by lazy {CameraUtil(getSystemService(Context.CAMERA_SERVICE) as CameraManager)}
    private val locman by lazy { getSystemService(LocationManager::class.java)}
    private val PROX_ALERT_INTENT = "android.intent.action.SCREEN_OFF"
    private val receiverList = mutableListOf<BroadcastReceiver>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shared.db = Room.databaseBuilder(this, AppDataBase::class.java, "notedb").build()
        val filter = IntentFilter(PROX_ALERT_INTENT)

        setContentView(binding.root)
        checkPermission()
        binding.surfaceView.holder.addCallback(this)
    }

    @SuppressLint("SetTextI18n", "MissingPermission")
    override fun onResume() {
        super.onResume()
        //locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0f, LocationListener { });
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
                var number = 1
                it?.map{

                    /*val receiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context, intent: Intent) {
                            println(it.latitude.toString() + " " + it.longitude)
                        }
                    }
                    receiverList.add(receiver)
                    registerReceiver(receiver,IntentFilter("receiver" + number))

                    val intent = Intent(PROX_ALERT_INTENT)
                    val proximityIntent = PendingIntent.getBroadcast(this, number, intent, 0)
                    val pi = PendingIntent.getBroadcast(applicationContext, number, Intent("receover"+number), 0)
                    locman.addProximityAlert(it.latitude, it.longitude, Shared.locationRadius.toFloat(), -1, pi)

                    number++*/
                }
            }
        }
        println(Shared.noteList)
        cam.openCamera()
    }
    @SuppressLint("MissingPermission")
    fun addGeoFence(note: NoteDto){
        val pi = PendingIntent.getBroadcast(
                applicationContext,
                1,
                Intent(this, LocationReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        LocationServices.getGeofencingClient(this)
    }

    override fun onPause() {
        //cam.closeCamera()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //for(receiver in receiverList)
         //unregisterReceiver(receiver)
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
    fun checkPermission(){
        val permissionStatusCamera = checkSelfPermission(CAMERA)
        if(permissionStatusCamera != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(CAMERA), 3)
        }
        val permissionStatusLocation = checkSelfPermission(ACCESS_FINE_LOCATION)
        if(permissionStatusLocation != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQ_LOCATION_PERMISSION)
        }
    }
}