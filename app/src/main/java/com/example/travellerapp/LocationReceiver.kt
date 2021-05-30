package com.example.travellerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LocationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Siema Garnuchu tu receiver")
    }
}