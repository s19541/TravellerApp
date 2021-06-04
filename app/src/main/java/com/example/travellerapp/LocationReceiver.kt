package com.example.travellerapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.example.travellerapp.databinding.ActivityMainBinding
import com.google.android.gms.location.GeofencingEvent
import java.io.File
import java.io.FileInputStream

class LocationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val event = intent?.let {
            GeofencingEvent.fromIntent(it)
        }
        val image = event?.triggeringGeofences?.first()?.requestId

        var fis: FileInputStream
        try {
            fis = FileInputStream(File(image))
            val bitmap = BitmapFactory.decodeStream(fis)
            context?.let {
                val notification = NotificationCompat.Builder(it, "com.example.travellerapp.Geofence")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Found nearby photo")
                        .setLargeIcon(bitmap)
                        .setColor(Color.GREEN)

                val note = Shared.noteList.first{it.image == image}.text
                val photoWithNoteIntent = Intent(it, PhotoWithNoteActivity::class.java)
                photoWithNoteIntent.putExtra("note",note)
                photoWithNoteIntent.putExtra("photo",bitmap)
                photoWithNoteIntent.putExtra("fromNotification",true)

                val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(it)
                stackBuilder.addNextIntent(photoWithNoteIntent)
                val pi: PendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                notification.setContentIntent(pi)
                it.getSystemService(NotificationManager::class.java)?.notify(1, notification.build())
            }
            fis.close()
        } catch (e: Exception) { }

    }
}