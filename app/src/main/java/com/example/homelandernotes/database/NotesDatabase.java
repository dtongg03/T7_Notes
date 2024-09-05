package com.example.homelandernotes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.homelandernotes.dao.noteDao;
import com.example.homelandernotes.entities.note;
import com.example.homelandernotes.dao.TaskDao;
import com.example.homelandernotes.entities.Task;

@Database(entities = {note.class, Task.class}, version = 2, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase notesDatabase;

    public static synchronized NotesDatabase getNotesDatabase(Context context){
        if (notesDatabase == null){
            notesDatabase = Room.databaseBuilder(
                            context,
                            NotesDatabase.class,
                            "notes_db"
                    )
                    .fallbackToDestructiveMigration() // Dùng để tự động xoá và tạo lại CSDL khi có thay đổi phiên bản
                    .build();
        }
        return notesDatabase;
    }

    public abstract noteDao noteDao();
    public abstract TaskDao taskDao();
}
