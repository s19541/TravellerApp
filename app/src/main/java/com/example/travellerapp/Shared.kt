package com.example.travellerapp

import android.graphics.Color
import com.example.travellerapp.model.Note

object Shared {
    var noteList = mutableListOf<Note>()
    var db: AppDataBase? = null
    var textSize: Int = 15
    var textColor: Int = Color.rgb(0,0,0)
    var locationRadius: Int = 1000
}