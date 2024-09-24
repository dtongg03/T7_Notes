package com.example.homelandernotes.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.homelandernotes.R;
import com.example.homelandernotes.adapters.TaskAdapter;
import com.example.homelandernotes.viewmodel.TaskViewModel;
import com.example.homelandernotes.viewmodel.TaskViewModelFactory;

public class TaskDetailActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private ListView taskListView;
    private TextView dateTextView;
    private ImageButton buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Liên kết các View từ layout
        taskListView = findViewById(R.id.taskListView);
        dateTextView = findViewById(R.id.dateTextView);
        buttonBack = findViewById(R.id.buttonBack);

        // Khởi tạo ViewModel
        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(getApplication())).get(TaskViewModel.class);

        // Lấy ngày được chọn từ Intent
        String selectedDate = getIntent().getStringExtra("selectedDate");
        dateTextView.setText("Ngày chọn: " + selectedDate);

        // Quan sát dữ liệu từ ViewModel và cập nhật ListView khi dữ liệu thay đổi
        taskViewModel.getAllTasks().observe(this, tasks -> {
            TaskAdapter adapter = new TaskAdapter(this, tasks);
            taskListView.setAdapter(adapter);
        });

        // Nút quay lại
        buttonBack.setOnClickListener(v -> onBackPressed());
    }
}
