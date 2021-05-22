package com.example.travellerapp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraManager
import android.location.LocationManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.travellerapp.databinding.ActivityMainBinding
import com.example.travellerapp.model.Note
import kotlin.concurrent.thread

private const val REQ_LOCATION_PERMISSION = 1
class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    val cam by lazy {CameraUtil(getSystemService(Context.CAMERA_SERVICE) as CameraManager)}
    private val locman by lazy { getSystemService(LocationManager::class.java)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shared.db = Room.databaseBuilder(this, AppDataBase::class.java,"notedb").build()

        setContentView(binding.root)
        checkPermission()
        binding.surfaceView.holder.addCallback(this)
    }

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
        }
        println(Shared.noteList)
        cam.openCamera()
    }

    override fun onPause() {
        //cam.closeCamera()
        super.onPause()
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
        val permissionStatus = checkSelfPermission(ACCESS_FINE_LOCATION)
        if(permissionStatus != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), REQ_LOCATION_PERMISSION)
        }
        }
    }