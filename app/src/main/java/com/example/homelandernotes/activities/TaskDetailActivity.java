package com.example.homelandernotes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private static final int ADD_TASK_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Liên kết các View từ layout
        taskListView = findViewById(R.id.taskListView);
        dateTextView = findViewById(R.id.dateTextView);
        buttonBack = findViewById(R.id.buttonBack);

        // Khởi tạo TaskViewModel với TaskViewModelFactory
        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(getApplication())).get(TaskViewModel.class);

        // Lấy ngày được chọn từ Intent
        String selectedDate = getIntent().getStringExtra("selectedDate");
        dateTextView.setText("Ngày chọn: " + selectedDate);

        // Quan sát dữ liệu từ ViewModel và cập nhật ListView khi dữ liệu thay đổi
        taskViewModel.getAllTasks().observe(this, tasks -> {
            TaskAdapter adapter = new TaskAdapter(this, tasks);
            taskListView.setAdapter(adapter);
        });

        // Thiết lập sự kiện cho nút quay lại
        buttonBack.setOnClickListener(v -> finish()); // Kết thúc Activity hiện tại và quay lại màn hình trước
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            // Cập nhật danh sách khi task mới được thêm
            taskViewModel.getAllTasks().observe(this, tasks -> {
                TaskAdapter adapter = new TaskAdapter(this, tasks);
                taskListView.setAdapter(adapter);
            });
        }
    }
}
