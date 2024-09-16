package com.example.homelandernotes.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.homelandernotes.dao.TaskDao;
import com.example.homelandernotes.database.NotesDatabase;
import com.example.homelandernotes.entities.Task;

import java.util.List;

public class TaskRepository {

    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;

    public TaskRepository(Application application) {
        NotesDatabase database = NotesDatabase.getDatabase(application);
        taskDao = database.taskDao();
        allTasks = taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getTasksByDate(String date) {
        return taskDao.getTasksByDate(date);
    }

    public void insert(Task task) {
        NotesDatabase.getDatabaseWriteExecutor().execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        NotesDatabase.getDatabaseWriteExecutor().execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        NotesDatabase.getDatabaseWriteExecutor().execute(() -> taskDao.delete(task));
    }
    public Task getTaskByTitle(String title) {
        final Task[] task = new Task[1];
        NotesDatabase.getDatabaseWriteExecutor().execute(() -> {
            task[0] = taskDao.getTaskByTitle(title);
        });
        return task[0];
    }
    public Task getTaskById(int taskId) {
        return taskDao.getTaskById(taskId);
    }
    public LiveData<List<Task>> getTasksByKeyword(String keyword) {
        return taskDao.getTasksByKeyword(keyword);
    }
}
