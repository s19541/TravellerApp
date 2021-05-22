package com.example.travellerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NoteDto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val image: String,
    val latitude: Double,
    val longitude: Double
)
