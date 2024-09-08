package com.example.homelandernotes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.homelandernotes.dao.TaskDao;
import com.example.homelandernotes.dao.noteDao;
import com.example.homelandernotes.entities.Task;
import com.example.homelandernotes.entities.note;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class, note.class}, version = 2, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
    public abstract noteDao noteDao();

    private static volatile NotesDatabase INSTANCE;
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static NotesDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NotesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    NotesDatabase.class, "app_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static ExecutorService getDatabaseWriteExecutor() {
        return databaseWriteExecutor;
    }
}
