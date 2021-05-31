package com.example.travellerapp

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.SurfaceView
import android.widget.ImageView

class CameraUtil(val cameraManager: CameraManager): CameraDevice.StateCallback(){
    val thread by lazy { HandlerThread("CameraInit").apply { start() } }
    val handler by lazy { Handler(thread.looper) }

    private var cameraDevice: CameraDevice? = null
    private var characteristics: CameraCharacteristics? = null
    private var surface: Surface? = null

    val captureCallback = object : CameraCaptureSession.CaptureCallback() {}

    val stateCallbackForPreview  = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {}

        override fun onConfigured(session: CameraCaptureSession) {
            surface?.let {
                val builder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                builder?.apply {
                    addTarget(it)
                    set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
                    set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
                }?.build()?.also {
                    session.setRepeatingRequest(it, captureCallback, handler)
                }
            }
        }
    }

    inner class StateCallbackForAcquire(val imageReader: ImageReader, imageView: ImageView)
        : CameraCaptureSession.StateCallback() {

        override fun onConfigureFailed(session: CameraCaptureSession) {}

        override fun onConfigured(session: CameraCaptureSession) {
            val builder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            builder?.apply {
                addTarget(imageReader.surface)
                set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO)
                set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
            }?.build()?.also { session.capture(it, captureCallback, handler) }
        }
    }

    @SuppressLint("MissingPermission")
    fun openCamera() {
        cameraManager.openCamera(setUpCameraId(), this, handler)
    }

    fun closeCamera() {
        cameraDevice?.close()
    }

    private fun setUpCameraId(): String {
        for (cameraId in cameraManager.cameraIdList) {
            characteristics = cameraManager.getCameraCharacteristics(cameraId)
            if (characteristics?.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                return cameraId
            }
        }
        throw IllegalStateException("Could not set Camera Id")
    }

    fun setupPreviewSession(surface: Surface) {
        this.surface = surface
        cameraDevice?.createCaptureSession(
                listOf(surface),
                stateCallbackForPreview,
                handler
        )
    }

    override fun onOpened(camera: CameraDevice) {
        cameraDevice = camera
    }

    fun acquire(iv: ImageView): Image{
        val size = characteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(ImageFormat.JPEG)?.get(2)?.let { Size(it.width, it.height) }
                ?: Size(860, 380)

        val imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 1)
        cameraDevice?.createCaptureSession(
                listOf(imageReader.surface),
                StateCallbackForAcquire(imageReader, iv),
                handler
        )
        while(true){
            try {
                val image = imageReader.acquireNextImage()
                return image
            }catch(e:Exception){}
        }
    }

    override fun onDisconnected(camera: CameraDevice) {
        cameraDevice = null
    }

    override fun onError(camera: CameraDevice, error: Int) {
    }

}