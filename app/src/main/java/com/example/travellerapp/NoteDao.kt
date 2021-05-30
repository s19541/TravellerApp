package com.example.travellerapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.travellerapp.model.NoteDto

@Dao
interface NoteDao {

    @Insert
    fun save(noteDto: NoteDto)

    @Query("SELECT * FROM NoteDto;")
    fun getAll() : List<NoteDto>

    @Query("DELETE FROM NoteDto WHERE image LIKE :image;")
    fun deleteNote(image: String)
}