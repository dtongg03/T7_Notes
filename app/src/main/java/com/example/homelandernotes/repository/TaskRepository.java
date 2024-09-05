package com.example.homelandernotes.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.homelandernotes.dao.TaskDao;
import com.example.homelandernotes.database.TaskDatabase;
import com.example.homelandernotes.entities.Task;

import java.util.List;

public class TaskRepository {

    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        TaskDatabase database = TaskDatabase.getDatabase(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getTasksByDate(String date) {
        return (LiveData<List<Task>>) taskDao.getTasksByDate(date);
    }

    public void insert(Task task) {
        TaskDatabase.getDatabaseWriteExecutor().execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        TaskDatabase.getDatabaseWriteExecutor().execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        TaskDatabase.getDatabaseWriteExecutor().execute(() -> taskDao.delete(task));
    }
}
