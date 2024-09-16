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

    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE due_date = :date")
    LiveData<List<Task>> getTasksByDate(String date);

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);
    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    Task getTaskById(int id);
    @Query("SELECT * FROM tasks WHERE title = :title LIMIT 1")
    Task getTaskByTitle(String title);

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%'")
    LiveData<List<Task>> getTasksByKeyword(String keyword);
}
