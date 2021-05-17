package com.example.travellerapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.travellerapp.model.Note

@Dao
interface NoteDao {

    @Insert
    fun save(vararg note: Note)

    @Query("SELECT * FROM Note;")
    fun getAll() : List<Note>
}