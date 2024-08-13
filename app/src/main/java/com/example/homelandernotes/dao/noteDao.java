package com.example.homelandernotes.dao;

import android.provider.ContactsContract;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.homelandernotes.entities.note;

import java.util.List;

@Dao
public interface noteDao {
    @Query("Select * from notes ORDER by id DESC")
    List<note> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertNote(note nt);

    @Delete
    void DeleteNote(note nt);
}
