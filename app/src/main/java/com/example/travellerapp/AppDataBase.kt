package com.example.travellerapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.travellerapp.model.Note

@Database(
    entities = [
        Note::class
    ],
    version = 1
)
abstract class AppDataBase : RoomDatabase(){
    abstract val note: NoteDao

}