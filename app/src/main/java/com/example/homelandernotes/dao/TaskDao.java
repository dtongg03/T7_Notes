package com.example.homelandernotes.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.homelandernotes.entities.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE date = :date")
    LiveData<List<Task>> getTasksByDate(String date);

    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> getAllTasks();
}
